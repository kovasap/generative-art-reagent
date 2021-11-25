#!/bin/bash

npx shadow-cljs release app

cp -r public/* release/
rm release/index.html
git add release/**
git commit -m "new release"
git push
