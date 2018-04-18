#!/bin/bash

# Find all build.gradle files and replace the ExoPlayer version
oldExoPlayerVersion=r2.5.3
newExoPlayerVersion=2.6.1
find ./../ -name "**build.gradle" -exec sed -i '' -e "s/$oldExoPlayerVersion/$newExoPlayerVersion/g" {} \;
