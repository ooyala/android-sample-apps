#!/bin/bash

SCRIPT_DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
BASE_DIR=${SCRIPT_DIR}/../
cd ${BASE_DIR}


echo "Copying OoyalaFreewheelSDK into FreewheelSampleApp"
cp vendor/ooyala/OoyalaFreewheelSDK-Android/OoyalaFreewheelSDK.jar FreewheelSampleApp/libs/

echo "Copying OoyalaFreewheelSDK into CompleteSampleApp"
cp vendor/ooyala/OoyalaFreewheelSDK-Android/OoyalaFreewheelSDK.jar CompleteSampleApp/libs/

echo "Copying FWAdManager into FreewheelSampleApp"
cp vendor/Freewheel/Android_AdManagerDistribution/FWAdManager.jar FreewheelSampleApp/libs/

echo "Copying FWAdManager into CompleteSampleApp"
cp vendor/Freewheel/Android_AdManagerDistribution/FWAdManager.jar CompleteSampleApp/libs/
