import { load as loadExpress } from "loaders/expressLoader";
import { registerJobs } from "controllers/jobs";

const initialise = async () => {
    console.log(`Loading express with allowed origin: ${process.env.ALLOWED_ORIGIN}`);
    const express = await loadExpress(process.env.ALLOWED_ORIGIN!);
    console.log('Express loaded');

    registerJobs();
    console.log('Jobs registered');
}

initialise()
    .then(() => {
        console.log(`Application initialised`);
    })
    .catch((e) => {
        console.error(`Failed to initialise`);
        console.error(e);
    });
