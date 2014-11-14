#!/bin/bash

SCRIPT_DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
BASE_DIR=${SCRIPT_DIR}/../
cd ${BASE_DIR}


echo "Copying OoyalaIMASDK into IMASampleApp"
cp vendor/ooyala/OoyalaIMASDK-Android/OoyalaIMASDK.jar IMASampleApp/libs/

echo "Copying OoyalaIMASDK into CompleteSampleApp"
cp vendor/ooyala/OoyalaIMASDK-Android/OoyalaIMASDK.jar CompleteSampleApp/libs/

echo "Copying FWAdManager into IMASampleApp"
cp vendor/Google/ima-android-sdk-beta8.jar IMASampleApp/libs/

echo "Copying FWAdManager into CompleteSampleApp"
cp vendor/Google/ima-android-sdk-beta8.jar CompleteSampleApp/libs/
