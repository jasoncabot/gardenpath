import 'phaser';

const uuidv4: () => string = () => {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

class UserController extends Phaser.Events.EventEmitter {
    constructor() {
        super();

        const id = localStorage.getItem("userId") || uuidv4();
        localStorage.setItem("userId", id);
        this.userId = id;

        this.name = localStorage.getItem("userName") || "";
    }

    userId: string
    name: string | undefined

    storeName: (name: string) => (void) = (name: string) => {
        this.name = name;
        localStorage.setItem("userName", name);
    }
}

export {
    UserController
};

