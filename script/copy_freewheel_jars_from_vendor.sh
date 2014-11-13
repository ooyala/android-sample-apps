#!/bin/bash

SCRIPT_DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
BASE_DIR=${SCRIPT_DIR}/../
cd ${BASE_DIR}

cp vendor/Freewheel/Android_AdManagerDistribution/FWAdManager.jar FreewheelSampleApp/libs/
cp vendor/Freewheel/Android_AdManagerDistribution/FWAdManager.jar CompleteSampleApp/libs/
