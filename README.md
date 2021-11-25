# Generative Art with Quil and Reagent

Taken originally from https://github.com/yogthos/quil-reagent-demo.

## Development Commands

### Development mode
```
npm install
npx shadow-cljs watch app
```
start a ClojureScript REPL
```
npx shadow-cljs browser-repl
```
### Building for production

```
./release.bash
```

This will put all the final artifacts in the "release" directory.
