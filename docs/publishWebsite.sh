#!/bin/sh
scriptdir="$(dirname "$0")"

#(cd "$scriptdir/.." ; sbt -mem 2048 -J-Xmx5120m "clean; fastOptJS::webpack")
[[ -z $(git status -uall --porcelain) ]] && {
  cp "$scriptdir/../modules/04-webapp/target/scala-3.0.0/scalajs-bundler/main/fmgp-geometry-webapp-fastopt-bundle.js" "$scriptdir" &&
  git branch gh-pages &&
  git checkout gh-pages &&
  #git pull &&
  git add --force "$scriptdir/fmgp-geometry-webapp-fastopt-bundle.js" &&
  git commit -m "gh-pages update at $(date '+%Y%m%d-%H%M%S')" &&
  git push --set-upstream origin gh-pages -f &&
  git checkout master &&
  git branch -d gh-pages # delete branch locally
} || echo "This branch is NOT clean."

#Now check https://fabiopinheiro.github.io/fmgp-generative-design/
#aka https://geo.fmgp.app/
