/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.controllers;


import com.moklyak.Game.server.models.BullCowGameDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Пользователь
 */
@RestController
@RequestMapping(value = "/api/game/bullcow")
public class BullCowController {
    
    @PostMapping(value = "/init")
    public ResponseEntity<BullCowGameDto> gameInit(){

        
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
    
    @PostMapping(value = "/state")
    public ResponseEntity<BullCowGameDto> gameState(){
        
        
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
    
}
