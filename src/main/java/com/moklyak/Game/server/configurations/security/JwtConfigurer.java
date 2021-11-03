/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.configurations.security;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *
 * @author Пользователь
 */
public class JwtConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtConfigurer(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        JwtTokenBeforeFilter jwtTokenFilter = new JwtTokenBeforeFilter(jwtTokenProvider);
        httpSecurity.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        
        JwtTokenAfterFilter jwtTokenAfterFilter = new JwtTokenAfterFilter(jwtTokenProvider);
        httpSecurity.addFilterAfter(jwtTokenAfterFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
