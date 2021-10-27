/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Пользователь
 */
@Controller
@RequestMapping(path = "/")
public class InitController {
    
    @GetMapping
    public String index(){
        
        return "index";
       
    }
    
    @PostMapping
    public String index(Model model){
        model.addAttribute("attr1", "Kappa");
        return "index";
       
    }
}