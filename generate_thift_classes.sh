#!/usr/bin/env bash

thrift --gen java:private-members src/main/thrift/moradanen.thrift

rm -rf src/main/java/com/sopovs/moradanen/thrift
mv gen-java/com/sopovs/moradanen/thrift src/main/java/com/sopovs/moradanen/thrift
