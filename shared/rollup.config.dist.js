import commonjs from 'rollup-plugin-commonjs';
import resolve from 'rollup-plugin-node-resolve';
import { terser } from 'rollup-plugin-terser';
import typescript from 'rollup-plugin-typescript2';

export default {

    input: ['./src/index.ts'],

    output: {
        file: './dist/index.js',
        name: 'shared',
        format: 'esm',
        sourcemap: false
    },

    plugins: [

        //  Parse our .ts source files
        resolve({
            extensions: ['.ts', '.tsx']
        }),

        commonjs({
            sourceMap: false,
            ignoreGlobal: true
        }),

        //  See https://www.npmjs.com/package/rollup-plugin-typescript2 for config options
        typescript(),

        terser()
    ]
};