package com.itmo.ktelnoy.stockexchange.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MoneyResponseDto {
    private String message;
    private MoneyReceiptDto receipt;

    public MoneyResponseDto(String message) {
        this.message = message;
    }

    public MoneyResponseDto(MoneyReceiptDto receipt) {
        this.receipt = receipt;
    }
}
