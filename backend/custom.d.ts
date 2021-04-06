type User = string;

declare namespace Express {
    export interface Request {
        user?: User;
    }
}
