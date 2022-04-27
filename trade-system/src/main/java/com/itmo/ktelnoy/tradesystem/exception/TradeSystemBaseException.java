package com.itmo.ktelnoy.tradesystem.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class TradeSystemBaseException extends RuntimeException {
    private HttpStatus status;

    public TradeSystemBaseException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    public TradeSystemBaseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
