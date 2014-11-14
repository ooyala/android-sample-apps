#!/bin/bash

SCRIPT_DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
BASE_DIR=${SCRIPT_DIR}/../
cd ${BASE_DIR}

echo "Copying OoyalaSDK into CompleteSampleApp"
cp vendor/ooyala/OoyalaSDK-Android/OoyalaSDK.jar CompleteSampleApp/libs/

echo "Copying OoyalaSDK into FreewheelSampleApp"
cp vendor/ooyala/OoyalaSDK-Android/OoyalaSDK.jar FreewheelSampleApp/libs/

echo "Copying OoyalaSDK into AdvancedPlaybackSampleApp"
cp vendor/ooyala/OoyalaSDK-Android/OoyalaSDK.jar AdvancedPlaybackSampleApp/libs/