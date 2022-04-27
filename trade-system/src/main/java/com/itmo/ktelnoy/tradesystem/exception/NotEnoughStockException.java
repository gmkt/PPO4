package com.itmo.ktelnoy.tradesystem.exception;

public class NotEnoughStockException extends TradeSystemBaseException {
    private static final String NOT_ENOUGH_STOCK = "Not enough of stock %s";

    public NotEnoughStockException(String stockId) {
        super(String.format(NOT_ENOUGH_STOCK, stockId));
    }
}
