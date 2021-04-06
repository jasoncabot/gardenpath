import express from "express";
import cors from 'cors';

const load = async (origin: string) => {
    const app = express();
    app.disable('x-powered-by');
    //options for cors midddleware
    const options: cors.CorsOptions = {
        allowedHeaders: [
            'Origin',
            'X-Requested-With',
            'Content-Type',
            'Accept',
            'X-Access-Token',
        ],
        credentials: true,
        methods: 'GET,HEAD,OPTIONS,PUT,PATCH,POST,DELETE',
        origin: origin,
        preflightContinue: false,
    };
    app.use(cors(options));
    app.use(express.json());

    app.get('/status', (_req, res) => { res.status(200).end(); });
    app.head('/status', (_req, res) => { res.status(200).end(); });
    return app;
}

export { load };
