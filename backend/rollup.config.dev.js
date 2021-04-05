import commonjs from '@rollup/plugin-commonjs';
import resolve from '@rollup/plugin-node-resolve';
import typescript from '@rollup/plugin-typescript';
import run from '@rollup/plugin-run';

export default {

    input: ['./src/index.ts'],

    output: {
        file: './dist/index.js',
        name: 'backend',
        format: 'cjs',
        sourcemap: true
    },

    plugins: [

        //  Parse our .ts source files
        resolve({
            extensions: ['.ts', '.tsx']
        }),

        commonjs({ sourcemap: true }),

        //  See https://www.npmjs.com/package/rollup-plugin-typescript2 for config options
        typescript(),

        run()
    ]
};