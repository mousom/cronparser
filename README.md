# Motivation
This document describes how to compile and run cronparser in OSX or Linux or Unix. 
cronparser is a command line application that parses a cron string and expands each
field to show the times at which it will run.

For example, the following input argument:

    */15 0 1,15 * 1-5 /usr/bin/find

Should yield the following output:

    minute        0 15 30 45
    hour          0
    day of month  1 15
    month         1 2 3 4 5 6 7 8 9 10 11 12
    day of week   1 2 3 4 5
    command       /usr/bin/find

# Prerequisites
1. java 1.8 or higher.
2. gradle if you want to compile.
3. zip to unzip the source code archive.

# How can I compile and execute?
1. clone this repo.
2. cd cronparser
3. ./gradlew build
4. java -cp build/libs/parser-1.0-SNAPSHOT.jar com.parser.CronParser \<cron-config\>
