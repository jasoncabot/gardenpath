import commonjs from '@rollup/plugin-commonjs';
import resolve from '@rollup/plugin-node-resolve';
import typescript from '@rollup/plugin-typescript';
import { terser } from "rollup-plugin-terser";
import replace from '@rollup/plugin-replace';

export default {

    input: ['./src/index.ts'],

    output: {
        file: './dist/index.js',
        name: 'backend',
        format: 'cjs',
        sourcemap: false
    },

    plugins: [

        //  Parse our .ts source files
        resolve({
            extensions: ['.ts', '.tsx']
        }),

        replace({
            preventAssignment: true,
            'process.env.ALLOWED_ORIGIN': JSON.stringify('https://gardenpath.jasoncabot.me/')
        }),

        commonjs({ sourcemap: false }),

        //  See https://www.npmjs.com/package/rollup-plugin-typescript2 for config options
        typescript(),

        terser()
    ]
};