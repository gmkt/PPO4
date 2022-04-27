package com.itmo.ktelnoy.tradesystem.exception;

public class StockNotFoundException extends TradeSystemBaseException {
    private static final String STOCK_NOT_FOUND = "Stock %s not found";

    public StockNotFoundException(String stockId) {
        super(String.format(STOCK_NOT_FOUND, stockId));
    }
}
