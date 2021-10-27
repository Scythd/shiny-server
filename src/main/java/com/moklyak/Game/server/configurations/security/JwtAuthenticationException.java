/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.configurations.security;

import org.springframework.security.core.AuthenticationException;

/**
 *
 * @author Пользователь
 */
public class JwtAuthenticationException extends AuthenticationException{
    
    public JwtAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
    public JwtAuthenticationException(String msg) {
        super(msg);
    }
}
