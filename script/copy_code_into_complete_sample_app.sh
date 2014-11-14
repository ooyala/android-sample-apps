#!/bin/bash

SCRIPT_DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
BASE_DIR=${SCRIPT_DIR}/../
cd ${BASE_DIR}

echo "Copying AdvancedPlaybackSampleApp into CompleteSampleApp"
cp AdvancedPlaybackSampleApp/src/com/ooyala/sample/players/* CompleteSampleApp/src/com/ooyala/sample/players/
cp AdvancedPlaybackSampleApp/src/com/ooyala/sample/lists/* CompleteSampleApp/src/com/ooyala/sample/lists/

echo "Copying FreewheelSampleApp into CompleteSampleApp"
cp FreewheelSampleApp/src/com/ooyala/sample/players/* CompleteSampleApp/src/com/ooyala/sample/players/
cp FreewheelSampleApp/src/com/ooyala/sample/lists/* CompleteSampleApp/src/com/ooyala/sample/lists/

echo "Copying IMASampleApp into CompleteSampleApp"
cp IMASampleApp/src/com/ooyala/sample/players/* CompleteSampleApp/src/com/ooyala/sample/players/
cp IMASampleApp/src/com/ooyala/sample/lists/* CompleteSampleApp/src/com/ooyala/sample/lists/
