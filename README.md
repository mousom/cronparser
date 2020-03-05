# Motivation
This document describes how to compile and run cronparser in OSX or Linux or Unix. 

# Prerequisites
1. java 1.8 or higher.
2. gradle if you want to compile.
3. zip to unzip the source code archive.

# How can I run the cronparser?
1. unzip cronparser.zip
2. cd cronparser
3. java -cp build/libs/parser-1.0-SNAPSHOT.jar com.parser.CronParser <cron-config>
   Example
   java -cp build/libs/parser-1.0-SNAPSHOT.jar com.parser.CronParser "0-10/2 * * 1 1-3 /usr/bin/ls"

# How can I compile and execute?
1. unzip cronparser.zip
2. cd cronparser
3. ./gradlew build
4. java -cp build/libs/parser-1.0-SNAPSHOT.jar com.parser.CronParser <cron-config>
