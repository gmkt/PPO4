package com.itmo.ktelnoy.stockexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoneyReceiptDto {
    @NotNull
    private Double amount;
    @NotBlank
    private String proof;
}
