# Bank Analytics with Java + Apache Spark

## 📌 Overview
This project processes **bank transaction data** using **Java 17** and **Apache Spark** on top of HDFS (or local file system for development).  
It demonstrates:
- Big Data processing with Spark
- Modern Java features (Streams, Lambdas, Futures)
- Analytics: total spend per customer, top merchants, top categories, average spend
- CI/CD with GitHub Actions
- Unit & integration testing with JUnit 5
- Linux scripting and SQL usage

---

## 🛠 Tech Stack
- **Java 17**
- **Apache Spark 4.0**
- **Maven**
- **JUnit 5** & **Mockito**
- **HDFS (optional)** — can run locally without Hadoop
- **GitHub Actions** for CI/CD

---

## 📂 Project Structure
bankanalytics/
├── src/main/java/com/udara/bankanalytics/model/Transaction.java
├── src/main/java/com/udara/bankanalytics/processor/SparkProcessor.java
├── src/main/resources/transactions.csv
├── src/test/java/com/udara/bankanalytics/processor/SparkProcessorTest.java
├── pom.xml
└── README.md

## 🚀 How to Run Locally

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/your-username/bankanalytics.git
cd bankanalytics

### 2️⃣ Build & Run
```bash
mvn clean compile exec:java \
 -Dexec.mainClass="com.udara.bankanalytics.processor.SparkProcessor"

### 3️⃣ Sample Dataset (src/main/resources/transactions.csv)
transactionId,customerId,amount,timestamp,merchant,category
T001,C001,120.50,2025-08-01T10:15:30,Amazon,Shopping
T002,C002,85.00,2025-08-01T11:00:00,Starbucks,Food
T003,C001,220.00,2025-08-02T14:30:00,Apple,Electronics
T004,C003,15.75,2025-08-03T09:45:00,Starbucks,Food

### Console Logs
yaml
INFO  SparkProcessor - Loading transactions.csv...
INFO  SparkProcessor - Total spend per customer:
C001: 340.50
C002: 85.00
C003: 15.75

INFO  SparkProcessor - Top merchants:
1. Starbucks - 2 transactions
2. Amazon - 1 transaction
3. Apple - 1 transaction

INFO  SparkProcessor - Average spend per transaction: 110.42


D:\Java Projects\bank-analytics\bankanalytics>mvn clean compile
D:\Java Projects\bank-analytics\bankanalytics>mvn exec:java -Dexec.mainClass=com.udara.bankanalytics.processor.SparkProcessor
