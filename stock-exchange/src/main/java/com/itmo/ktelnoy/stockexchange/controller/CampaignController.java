package com.itmo.ktelnoy.stockexchange.controller;

import com.itmo.ktelnoy.stockexchange.model.CampaignEntity;
import com.itmo.ktelnoy.stockexchange.model.StockEntity;
import com.itmo.ktelnoy.stockexchange.mongo.StockRepository;
import com.itmo.ktelnoy.stockexchange.mongo.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/campaign", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
public class CampaignController {
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private StockRepository stockRepository;

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CampaignEntity> createCampaign(@Valid @RequestBody CampaignEntity campaignDto) {
        return ResponseEntity.ok(campaignRepository.save(campaignDto));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CampaignEntity> getCampaign(@PathVariable("id") String id) {
        return ResponseEntity.of(campaignRepository.findById(id));
    }

    @GetMapping(value = "/{id}/stocks", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StockEntity>> getStocksForCampaign(@PathVariable("id") String campaignId) {
        return ResponseEntity.ok(stockRepository.findByCampaignId(campaignId).stream()
                .filter(Objects::nonNull).collect(Collectors.toList()));
    }
}
