package com.itmo.ktelnoy.stockexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockPurchaseRequestDTO {

    @NotNull
    private Integer stockNumber;

    @NotNull
    private MoneyReceiptDto receipt;

}
