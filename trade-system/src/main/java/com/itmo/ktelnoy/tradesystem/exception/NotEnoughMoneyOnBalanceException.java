package com.itmo.ktelnoy.tradesystem.exception;

public class NotEnoughMoneyOnBalanceException extends TradeSystemBaseException {
    private static final String NOT_ENOUGH_MONEY_ON_BALANCE = "Not enough money on balance of user %s";

    public NotEnoughMoneyOnBalanceException(String userId) {
        super(String.format(NOT_ENOUGH_MONEY_ON_BALANCE, userId));
    }
}
