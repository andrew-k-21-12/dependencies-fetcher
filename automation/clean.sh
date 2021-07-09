#!/bin/sh

cd ..
rm .DS_Store

cd library
./gradlew clean
rm -rf .DS_Store .gradle .idea

cd ../sandbox
rm -rf .DS_Store .gradle .idea build
cd app
rm -rf .DS_Store .cxx build dependencies
