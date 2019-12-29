#!/bin/sh

(cd .. ; sbt "clean; fastOptJS::webpack")
cp ../demo/target/scala-2.13/scalajs-bundler/main/demo-fastopt-bundle.js .

git branch gh-pages
git add --force docs/demo-fastopt-bundle.js