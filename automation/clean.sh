#!/bin/sh

cd ..
rm .DS_Store

cd sandbox
./gradlew clean
rm -rf .DS_Store .gradle .idea
rm gradle/.DS_Store
cd buildSrc
rm -rf .DS_Store .gradle build
cd ../groovy
rm -rf .DS_Store .cxx
cd ../kts
rm -rf .DS_Store .cxx

cd ../../library
./gradlew clean
# Enabling (-s) the following sh options: 
# - extglob: to support the extended wildcard expressions - the parentheses in the current case;
# - dotglob: to include hidden files starting with dots into wildcards.
shopt -s extglob dotglob
rm -rf .DS_Store .gradle .idea/!(runConfigurations)
