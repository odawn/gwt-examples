#!/usr/bin/sh

get filename

unoconv --listener &

sleep 5

unoconv -f xls $FILENAME

kill -15 %-