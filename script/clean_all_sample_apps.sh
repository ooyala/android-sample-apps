#!/bin/bash

echo "Cleaning CompleteSampleApp"
echo "Removing old files from CompleteSampleApp"
rm -r CompleteSampleApp/src/com/ooyala/sample/players
rm -r CompleteSampleApp/src/com/ooyala/sample/lists

echo "Making CompleteSampleApp directories if necessary"
mkdir CompleteSampleApp/src/com/ooyala/sample/players
mkdir CompleteSampleApp/src/com/ooyala/sample/lists

echo "Removing libs in CompleteSampleApp"
rm -rf CompleteSampleApp/libs/*

echo "Removing libs in AdvancedPlaybackSampleApp"
rm -rf AdvancedPlaybackSampleApp/libs/*

echo "Removing libs in FreewheelSampleApp"
rm -rf FreewheelSampleApp/libs/*
