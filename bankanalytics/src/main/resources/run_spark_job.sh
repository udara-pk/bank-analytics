#!/bin/bash
# Exit if any command fails
set -e

# Variables
CSV_PATH="src/test/resources/test_transactions.csv"
MAIN_CLASS="com.udara.bankanalytics.processor.SparkProcessor"

echo "Compiling project..."
mvn clean package -DskipTests

echo "Running Spark job..."
mvn exec:java -Dexec.mainClass="$MAIN_CLASS" -Dexec.args="$CSV_PATH"

echo "Job finished successfully!"
