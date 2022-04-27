package com.itmo.ktelnoy.tradesystem.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document("users")
public class UserEntity {
    @Id
    @NotBlank
    private String id;

    @NotBlank
    private String name;
    @NotBlank
    @Size(min = 64, max = 64)
    private String psdHash;

    @NotNull
    private Double moneyInDollarsHeld;
}
