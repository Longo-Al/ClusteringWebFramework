#!/bin/bash

cat /etc/passwd

service mysql start
service ssh start
catalina.sh run
