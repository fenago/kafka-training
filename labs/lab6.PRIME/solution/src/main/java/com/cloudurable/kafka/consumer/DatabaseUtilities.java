package com.cloudurable.kafka.consumer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtilities {


    public static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:hsqldb:file:/tmp/stockPricesTable");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void startJdbcTransaction(final Connection connection) throws SQLException {
        connection.setAutoCommit(false);
    }


    public static void saveStockPrice(final StockPriceRecord stockRecord,
                                       final Connection connection) throws SQLException {

        final PreparedStatement preparedStatement = getUpsertPreparedStatement(
                                        stockRecord.getName(), connection);



        //Save partition, offset and topic in database.
        preparedStatement.setLong(1, stockRecord.getOffset());
        preparedStatement.setLong(2, stockRecord.getPartition());
        preparedStatement.setString(3, stockRecord.getTopic());

        //Save stock price, name, dollars, and cents into database.
        preparedStatement.setInt(4, stockRecord.getDollars());
        preparedStatement.setInt(5, stockRecord.getCents());
        preparedStatement.setString(6, stockRecord.getName());

        //Save the record with offset, partition, and topic.
        preparedStatement.execute();

    }


    private static PreparedStatement getUpsertPreparedStatement(String stockName, Connection connection) throws SQLException {
        final PreparedStatement findPrevious = connection.prepareStatement(
                "select * from STOCK_PRICE where stockName = ?"
        );
        findPrevious.setString(1, stockName);

        boolean recordExists = findPrevious.executeQuery().next();
        PreparedStatement preparedStatement;

        if (recordExists) {
            preparedStatement = connection.prepareStatement(
                    "update STOCK_PRICE SET " +
                            "   offset=?, partition=?, topic=?, " +
                            "   dollars=?, cents=? " +
                            "   where stockName=? "
            );
        } else {
            preparedStatement = connection.prepareStatement(
                    "insert into STOCK_PRICE (" +
                            "   offset, partition, topic, " +
                            "   dollars, cents, stockName) " +
                            "   values(?, ?, ?, ?, ?, ?)"
            );
        }
        return preparedStatement;
    }

    public static List<StockPriceRecord> readDB()  {
        final Connection connection = getConnection();
        try {
            final List<StockPriceRecord> records = new ArrayList<>();
            final Statement statement = connection.createStatement();

            try {

                final ResultSet resultSet = statement.executeQuery(
                        "select * from STOCK_PRICE");

                while (resultSet.next()) {
                    final long offset = resultSet.getLong("offset");
                    final int partition = resultSet.getInt("partition");
                    final String topic = resultSet.getString("topic");
                    final String stockName = resultSet.getString("stockName");
                    final int dollars = resultSet.getInt("dollars");
                    final int cents = resultSet.getInt("cents");
                    records.add(new StockPriceRecord(topic, partition, offset,
                            stockName, dollars, cents, true));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            finally {
                statement.close();
                connection.close();
            }
            return records;
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void initDB() throws Exception {

        Class.forName("org.hsqldb.jdbcDriver");

        final Connection connection = getConnection();

        connection.createStatement().execute("" +
                "   CREATE TABLE IF NOT EXISTS      " +
                "   STOCK_PRICE(                    " +
                "       offset      bigint,         " +
                "       partition   int,            " +
                "       topic       varchar(20),    " +
                "       stockName   varchar(20),    " +
                "       dollars     int,           " +
                "       cents       int,            " +
                "       PRIMARY KEY (stockName)     " +
                "    )                              ");

        connection.close();

    }
}
