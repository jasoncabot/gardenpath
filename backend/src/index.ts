import { load as loadExpress } from "loaders/expressLoader";
import { registerRoutes } from "controllers/games";

const initialise = async () => {
    const port = process.env.PORT || 8080;
    console.log(`Loading express with allowed origin: ${process.env.ALLOWED_ORIGIN}`);
    const express = await loadExpress(process.env.ALLOWED_ORIGIN!);
    console.log('Express loaded');

    registerRoutes(express);

    express.listen(port, () => {
        console.log(`Listening for connections on port ${port}`);
    });
}

initialise()
    .then(() => {
        console.log(`Application initialised`);
    })
    .catch((e) => {
        console.error(`Failed to initialise`);
        console.error(e);
    });
