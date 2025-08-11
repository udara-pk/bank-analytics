package com.udara.bankanalytics.processor;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.apache.spark.sql.functions.*;

public class SparkProcessor {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        if(args.length < 1){
            System.out.println("Usage: SparkProcessor <transactions_csv_path>");
            System.exit(1);
        }

        String filePath = args[0];

        // 1. Create Spark Session
        SparkSession spark = SparkSession.builder()
                .appName("Bank Transaction Analytics")
                .master("local[*]")// use all CPU cores
//                .config("spark.io.compression.codec", "lz4")
//                .config("spark.io.compression.lz4.disable.unsafe", "true")
                //.config("spark.io.compression.codec", "uncompressed")
                .getOrCreate();

        // 2. Load CSV as DataFrame
        Dataset<Row> transactions = spark.read()
                .option("header", "true")   // first row has column names
                .option("inferSchema", "true") // automatically detect data types
                .csv(filePath);

        // Register DataFrame as SQL temporary view
        transactions.createOrReplaceTempView("transactions");

        transactions.printSchema();
        transactions.show();

        //Total amount spent per user
        Dataset<Row> totalByUserSQL = spark.sql(
                "SELECT userId, ROUND(SUM(amount),2) AS total_spent "+
                "FROM transactions GROUP BY userId ORDER BY total_spent DESC"
        );
        System.out.println("=== Total Spent per User (SQL) ===");
        totalByUserSQL.show();

        // Top merchants by spending
        Dataset<Row> topMerchantsSQL = spark.sql(
                "SELECT merchant, ROUND(SUM(amount),2) AS total_spent "+
                        "FROM transactions GROUP BY merchant ORDER BY total_spent DESC"
        );
        System.out.println("=== Top Merchants by Spending (SQL) ===");
        topMerchantsSQL.show();

        // Spending by category
        Dataset<Row> spendingByCategorySQL= spark.sql(
             "SELECT category, ROUND(SUM(amount),2) AS total_spent "+
             "FROM transactions GROUP BY category ORDER BY total_spent DESC"
        );
        System.out.println("=== Spending by Category (SQL) ===");
        spendingByCategorySQL.show();

        // Average spend per transaction overall
        Dataset<Row> avgSpendSQL= spark.sql(
                "SELECT ROUND(AVG(amount),2) AS avg_spent FROM transactions"
        );
        System.out.println("=== Average Spend per Transactions (SQL) ===");
        avgSpendSQL.show();

        // Average spend per user
        Dataset<Row> avgSpendPerUserSQL= spark.sql(
                "SELECT userId, ROUND(SUM(amount),2) AS avg_spent "+
                        "FROM transactions GROUP BY userId ORDER BY avg_spent DESC"
        );
        System.out.println("=== Average Spend per User (SQL) ===");
        avgSpendPerUserSQL.show();


        // 3. Aggregations in Spark
//        System.out.println("=== Total Spent per User ===");
        Dataset<Row> totalByUser = transactions
                .groupBy("userId")
                .agg(round(sum("amount"), 2).alias("total_spent"))
                .orderBy(desc("total_spent"));

        Dataset<Row> spendingByCategory = transactions
                .groupBy("category")
                .agg(round(sum("amount"), 2).alias("total_spent"))
                .orderBy(desc("total_spent"));

        // 4. Convert Spark DataFrame to Java List for post-processing
        List<UserSpend> userSpendList = totalByUser.collectAsList().stream()
                        .map(row -> new UserSpend(
                                row.getAs("userId"),
                                row.getAs("total_spent")
                        )).toList();

        // 5. STREAMS & LAMBDAS - Find high spenders over 300
        System.out.println("=== High Spenders (>300) ===");
        userSpendList.stream()
                        .filter(us -> us.totalSpent > 300)
                        .sorted((a,b) -> Double.compare(b.totalSpent, a.totalSpent))
                                .forEach(us -> System.out.println(us.userId + " -> " + us.totalSpent));

        // 6. OPTIONAL - Safely find spending for a given user
        String searchUser = "U005"; //  Not in Dataset
        Optional<UserSpend> foundUser = userSpendList.stream()
                        .filter(us -> us.userId.equals(searchUser))
                .findFirst();
        System.out.println("User "+searchUser+" spending: "+
                foundUser.map(us -> (Double) us.totalSpent).orElse(Double.valueOf(0.0)));

        // 7. COMPLETEBLEFUTURE - Run category & merchant analysis in parallel
        CompletableFuture<Void> categoryFuture = CompletableFuture.runAsync(() -> {
            System.out.println("=== Spending by category ===");
            spendingByCategory.show();
        });

        CompletableFuture<Void> merchantFuture = CompletableFuture.runAsync(() -> {
            System.out.println("=== Top Merchants ===");
            transactions.groupBy("merchant")
                    .agg(round(sum("amount"), 2).alias("total_spent"))
                    .orderBy(desc("total_spent"))
                    .show();
        });

        CompletableFuture.allOf(categoryFuture, merchantFuture).get();

        // 8. Stop Spark
        spark.stop();
    }

    //Helper Class for Java processing
    static class UserSpend {
        String userId;
        double totalSpent;

        public UserSpend(String userId, double totalSpent) {
            this.userId = userId;
            this.totalSpent = totalSpent;
        }
    }
}
