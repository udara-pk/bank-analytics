package com.udara.bankanalytics.processor;

import org.apache.spark.sql.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SparkProcessorTest {
    private static SparkSession spark;

    @BeforeAll
    static void setup(){
        spark = SparkSession.builder()
                .appName("BankAnalyticsTest")
                .master("local[*]")
                .getOrCreate();
    }

    @AfterAll
    static void tearDown(){
        if(spark != null) spark.stop();
    }
    @Test
    void  testTotalSpentPerUser(){
        Dataset<Row> transactions = spark.read()
                .option("header","true")
                .option("inferSchema","true")
                .csv("src/test/resources/test_transactions.csv");

        transactions.createOrReplaceTempView("transactions");

        Dataset<Row> result = spark.sql(
                "select userId, ROUND(SUM(amount),2) AS total_spent "+
                        "from transactions GROUP BY  userId ORDER BY userId"
        );

        Row[] rows = (Row[]) result.collect();
        assertEquals(3, rows.length);
        assertEquals(300.50, rows[0].getAs("total_spent"));
        assertEquals(50.00, rows[1].getAs("total_spent"));
        assertEquals(75.25, rows[2].getAs("total_spent"));

    }
}
