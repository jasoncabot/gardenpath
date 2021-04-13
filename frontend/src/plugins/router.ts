import CreateGameScene from "../scenes/creategame";
import GameScene from "../scenes/game";
import JoinGameScene from "../scenes/joingame";
import MainMenuScene from "../scenes/mainmenu";
import StartGameScene from "../scenes/startgame";

class RouterPlugin extends Phaser.Plugins.BasePlugin {

    constructor(pluginManager: Phaser.Plugins.PluginManager) {
        super(pluginManager);
    }

    init = (data: any) => {
        super.init(data);

        const currentWindow: Window = data.window;

        // using the browser to navigate performs the appropriate routing
        currentWindow.addEventListener('popstate', (_event) => this.showSceneMatchingCurrentLocation(), false);
        currentWindow.addEventListener('pushstate', (_event) => this.showSceneMatchingCurrentLocation(), false);

        this.registerAllScenes();
    }

    start = () => {
        super.start();

        // by default go to the scene for whatever the current pathname is
        this.showSceneMatchingCurrentLocation();
    }

    // TODO: extract out scene list and mappings to make this plugin generic across all projects

    registerAllScenes: () => (void) = () => {
        [CreateGameScene, GameScene, JoinGameScene, MainMenuScene, StartGameScene].forEach(scene => {
            this.game.scene.add(scene.key, scene);
        });
    }

    showSceneMatchingCurrentLocation: () => (void) = () => {
        const path = location.pathname;

        const [_, resource, id, subresource] = path.split('/');

        // Stop any old scenes before starting the new one
        // this is required as we 'should' be calling scene.start(<key>)
        // from the current scene, not this.game - so we don't know which
        // is the current scene that requires being stopped
        this.game.scene.getScenes(true).forEach(scene => scene.scene.stop());

        if (!resource || resource.length === 0) {
            return this.game.scene.start(MainMenuScene.key);
        }

        if (resource === 'games' && !id) {
            return this.game.scene.start(JoinGameScene.key);
        }

        if (resource === 'games' && id === 'new') {
            return this.game.scene.start(CreateGameScene.key);
        }

        if (resource === 'games' && subresource === 'players') {
            return this.game.scene.start(StartGameScene.key, { id });
        }

        if (resource === 'games' && !subresource) {
            return this.game.scene.start(GameScene.key, { id });
        }
        throw new Error('Unable to match route from ' + JSON.stringify({
            resource, id, subresource
        }, null, 2));
    }

    navigate: (path: string) => (void) = (path: string) => {
        if (location.pathname !== path) {
            history.pushState({}, 'GardenPath', path);
            this.showSceneMatchingCurrentLocation();
        }
    }
}

export { RouterPlugin };
