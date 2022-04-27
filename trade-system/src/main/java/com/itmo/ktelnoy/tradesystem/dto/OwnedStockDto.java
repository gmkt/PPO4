package com.itmo.ktelnoy.tradesystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnedStockDto {
    private String stockId;
    private Integer numberHeld;
    private Double stockPrice;
}
