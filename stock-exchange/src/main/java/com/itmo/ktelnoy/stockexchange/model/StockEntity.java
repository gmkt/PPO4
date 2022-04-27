package com.itmo.ktelnoy.stockexchange.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document("stocks")
public class StockEntity {
    @Id
    @NotBlank
    private String id;

    @NotNull
    @Min(0)
    private Integer numberAvailable;
    @NotNull
    @Min(0)
    private Double priceInDollars;
    @NotBlank
    private String campaignId;
}
