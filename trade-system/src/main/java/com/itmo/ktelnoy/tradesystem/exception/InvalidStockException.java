package com.itmo.ktelnoy.tradesystem.exception;

import org.springframework.http.HttpStatus;

public class InvalidStockException extends TradeSystemBaseException {
    private static final String INVALID_STOCK_DATA = "Invalid stock data";

    public InvalidStockException() {
        super(INVALID_STOCK_DATA, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
