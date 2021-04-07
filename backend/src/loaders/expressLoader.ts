import express from "express";
import cors from 'cors';

const load = async (origin: string) => {
    const app = express();
    app.disable('x-powered-by');
    //options for cors midddleware
    const options: cors.CorsOptions = { origin };
    app.use(cors(options));
    app.use(express.json());

    app.get('/status', (_req, res) => { res.status(200).end(); });
    app.head('/status', (_req, res) => { res.status(200).end(); });
    return app;
}

export { load };
