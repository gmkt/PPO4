package com.itmo.ktelnoy.tradesystem.exception;

import org.springframework.http.HttpStatus;

public class IntegrationStockExchangeException extends TradeSystemBaseException {
    private static final String INVALID_STOCK_DATA = "Stock exchange integration exception: %s";

    public IntegrationStockExchangeException(String remoteMessage) {
        super(String.format(INVALID_STOCK_DATA, remoteMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
