/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.controllers;

import com.moklyak.Game.server.models.AuthenticationRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Пользователь
 */
@RestController
@RequestMapping(value = "/api/auth")
public class RegistrationContoller {

    @PostMapping(value = "/login")
    public ResponseEntity login(@RequestBody AuthenticationRequestDto requestDto) {
        
    }
}
