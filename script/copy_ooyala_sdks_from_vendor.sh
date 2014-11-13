#!/bin/bash

SCRIPT_DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
BASE_DIR=${SCRIPT_DIR}/../
cd ${BASE_DIR}

cp vendor/ooyala/OoyalaFreewheelSDK-Android/OoyalaFreewheelSDK.jar FreewheelSampleApp/libs/
cp vendor/ooyala/OoyalaFreewheelSDK-Android/OoyalaFreewheelSDK.jar CompleteSampleApp/libs/
