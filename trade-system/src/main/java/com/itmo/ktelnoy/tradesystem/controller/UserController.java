package com.itmo.ktelnoy.tradesystem.controller;

import com.itmo.ktelnoy.stockexchange.dto.MoneyReceiptDto;
import com.itmo.ktelnoy.tradesystem.model.UserEntity;
import com.itmo.ktelnoy.tradesystem.mongo.UserRepository;
import com.itmo.ktelnoy.tradesystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/user", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
public class UserController {

    @Autowired
    private UserRepository repository;
    @Autowired
    private UserService userService;

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserEntity> createUser(@RequestBody @Valid UserEntity userDTO) {
        return ResponseEntity.ok(repository.save(userDTO));
    }

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserEntity> getUser(@PathVariable String userId) {
        return ResponseEntity.of(repository.findById(userId));
    }

    @Transactional
    @PostMapping(value = "/{userId}/addMoney", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addMoney(@PathVariable String userId, @RequestBody @Valid MoneyReceiptDto receiptDto) {
        UserEntity user = repository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(String.format("User %s not found", userId), HttpStatus.NOT_FOUND);
        }
        userService.addMoney(user, receiptDto);

        return ResponseEntity.ok("Successfully added money");
    }

}
