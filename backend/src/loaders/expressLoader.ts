import express from "express";
import cors from 'cors';
import { registerRoutes } from "controllers/games";

interface ExpressLoadResult {
    express: any
    server: any
}

const load: (origin: string) => (Promise<ExpressLoadResult>) = async (origin: string) => {
    const app = express();
    app.disable('x-powered-by');
    //options for cors midddleware
    const options: cors.CorsOptions = { origin };
    app.use(cors(options));
    app.use(express.json());

    app.get('/status', (_req, res) => { res.status(200).end(); });
    app.head('/status', (_req, res) => { res.status(200).end(); });

    registerRoutes(app);

    const port = process.env.PORT || 8080;
    const server = app.listen(port, () => {
        console.log(`Listening for connections on port ${port}`);
    });

    // shutdown handling
    const onShutdownReceived = () => {
        if (!server.listening) process.exit(0)
        server.close(err => {
            if (err) {
                console.error(err)
                return process.exit(1)
            }
            process.exit(0)
        })
    }

    process.on('SIGINT', onShutdownReceived);
    process.on('SIGTERM', onShutdownReceived);
    process.on('SIGHUP', onShutdownReceived);

    return {
        express: app,
        server: server
    };
}

export { load };
