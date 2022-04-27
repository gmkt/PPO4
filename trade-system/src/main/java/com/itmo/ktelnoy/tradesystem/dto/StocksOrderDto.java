package com.itmo.ktelnoy.tradesystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StocksOrderDto {
    @NotBlank
    private String stockId;
    @NotNull
    private Integer numberToPurchase;
}
