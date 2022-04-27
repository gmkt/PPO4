package com.itmo.ktelnoy.stockexchange.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StockResponseDto {
    private String message;
    private StockReceiptDto receipt;

    public StockResponseDto(String message) {
        this.message = message;
    }

    public StockResponseDto(StockReceiptDto receipt) {
        this.receipt = receipt;
    }
}
