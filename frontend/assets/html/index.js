import { name, description, homepage, author } from "../../package.json";

const htmlTemplate = ({ files, title }) => `<!DOCTYPE html>

<html lang="en">

    <head>
        <meta charset="UTF-8"/>

        <meta name="viewport" content="minimum-scale=1, initial-scale=1, width=device-width, shrink-to-fit=no"/>
        <meta name="author" content="${author}"/>
        <meta name="description" content="${description}"/>

        <!-- Open Graph protocol -->
        <meta property="og:title" content="${name}"/>
        <meta property="og:type" content="website"/>
        <meta property="og:url" content="${homepage}"/>
        <meta property="og:description" content="${description}"/>

        <title>${title}</title>

    </head>

    <body>
        <div id="root">
        </div>
        ${files.js.map(f => `<script src="/${f.fileName}"></script>`)}
    </body>

</html>
`;

export default htmlTemplate;