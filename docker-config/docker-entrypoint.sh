#!/bin/bash
set -e

service mysql start
service ssh start
catalina.sh run
