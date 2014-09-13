#!/bin/bash
echo cleaning old class files...
rm *.class 2>/dev/null || true

for java_file in $(ls *.java); do
 echo compiling... $java_file
 javac -g $java_file
done
