package com.itmo.ktelnoy.tradesystem.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document("owned_stocks")
public class OwnedStockEntity {
    @Id
    @NotNull
    private CompositeOwnedStockId id;

    @NotNull
    private Integer numberHeld;
    @NotBlank
    private String proof;

    @Data
    @AllArgsConstructor
    public static class CompositeOwnedStockId {
        @NotBlank
        private String stockId;
        @NotBlank
        private String userId;
    }
}
