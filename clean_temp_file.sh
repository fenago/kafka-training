#!/usr/bin/env bash

# Used to clean temp files for zip production
find . -name "build" | xargs rm -rf
find . -name ".gradle" | xargs rm -rf
find . -name "gradle" | xargs rm -rf
find . -name "out" | xargs rm -rf
find . -name ".idea" | xargs rm -rf
find . -name ".DS_Store" | xargs rm -rf
