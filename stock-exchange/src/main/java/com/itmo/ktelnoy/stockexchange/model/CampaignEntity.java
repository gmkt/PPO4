package com.itmo.ktelnoy.stockexchange.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document("campaigns")
public class CampaignEntity {
    @Id
    @NotBlank
    private String id;

    private String name;

}
