#!/bin/bash

SOURCE_PATH="/home/evangelos/Desktop/HadoopAssignment/dna/Dna.java"

# Hadoop User's workspace
WORKSPACE="$HOME/bda"

# Class and Package info
PACKAGE_NAME="dna"
MAIN_CLASS="${PACKAGE_NAME}.Dna"

# HDFS Parameters
HDFS_INPUT="/input/task_2"
HDFS_OUTPUT="/output/task_2"

# Stop script on any error
set -e

echo "[1/6] Setting up Hadoop User workspace..."

mkdir -p $WORKSPACE
rm -rf $WORKSPACE/*.java
rm -rf $WORKSPACE/*.jar


echo "[2/6] Synchronizing code from User 'evangelos'..."
# Copy the source file into our workspace
cp $SOURCE_PATH $WORKSPACE/

if [ ! -f "$WORKSPACE/Dna.java" ]; then
    echo "❌ Error: Could not copy file. Check permissions!"
    exit 1
fi

echo "[3/6] Compiling Java Code..."
# Compile
cd $WORKSPACE
export CLASSPATH=$(hadoop classpath)
javac -d . Dna.java

echo "[4/6] Creating JAR..."
# Package the directory matching the package name (dna)
jar cf wc.jar $PACKAGE_NAME

echo "[5/6] Cleaning HDFS Output Directory..."
hdfs dfs -rm -r $HDFS_OUTPUT 2>/dev/null || true

echo "[6/6] Executing Hadoop Job..."
echo "----------------------------------------------------"
hadoop jar wc.jar $MAIN_CLASS $HDFS_INPUT $HDFS_OUTPUT
echo "----------------------------------------------------"
echo "Job Complete!"
echo "Files created:"

hdfs dfs -cat $HDFS_OUTPUT/part-r-00000