package com.cloudurable.kafka.consumer;

import org.junit.Test;

import java.sql.Connection;
import java.util.List;

import static com.cloudurable.kafka.consumer.DatabaseUtilities.getConnection;
import static org.junit.Assert.*;

public class DatabaseUtilitiesTest {

    @Test
    public void saveStockPrice() throws Exception {

        DatabaseUtilities.initDB();

        final Connection connection = getConnection();

        final StockPriceRecord stockPriceRecord = new StockPriceRecord("foo", 5, 100L, "IBMM", 5, 5, true);
        DatabaseUtilities.saveStockPrice(stockPriceRecord, connection);


        connection.close();

        final List<StockPriceRecord> records = DatabaseUtilities.readDB();

        assertTrue(records.size()>0);

    }

}