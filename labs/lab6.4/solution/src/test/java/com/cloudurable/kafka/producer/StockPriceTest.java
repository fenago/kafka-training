package com.fenago.kafka.producer;

import com.fenago.kafka.model.StockPrice;
import org.junit.Test;

import static org.junit.Assert.*;

public class StockPriceTest {

    @Test
    public void testStockPrice() {
        StockPrice stockPrice = new StockPrice("IBM", 5, 30);
        String json = stockPrice.toJson();
        StockPrice stockPrice2 = new StockPrice(json);
        assertEquals(stockPrice.getName(), stockPrice2.getName());
        assertEquals(stockPrice.getCents(), stockPrice2.getCents());
        assertEquals(stockPrice.getDollars(), stockPrice2.getDollars());
    }
}