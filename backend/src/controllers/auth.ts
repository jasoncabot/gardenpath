import { Request, Response, NextFunction } from "express";

const requireUser = (request: Request, response: Response, next: NextFunction) => {
    const token = (request.headers.authorization || "").split(' ')[1] || "";
    if (token.length == 0) {
        return response.status(403).json({ error: 'No user found' });
    }

    request.user = Buffer.from(token, 'base64').toString('ascii');
    next();
}

export { requireUser }