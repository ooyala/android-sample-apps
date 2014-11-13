#!/bin/bash

SCRIPT_DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
BASE_DIR=${SCRIPT_DIR}/../
cd ${BASE_DIR}

echo "Removing old files from CompleteSampleApp"
rm CompleteSampleApp/src/com/ooyala/sample/players/*
rm CompleteSampleApp/src/com/ooyala/sample/lists/*

echo "Copying AdvancedPlaybackSampleApp into CompleteSampleApp"
cp AdvancedPlaybackSampleApp/src/com/ooyala/sample/players/* CompleteSampleApp/src/com/ooyala/sample/players/
cp AdvancedPlaybackSampleApp/src/com/ooyala/sample/lists/* CompleteSampleApp/src/com/ooyala/sample/lists/
