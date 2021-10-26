/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.configurations;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 *
 * @author Пользователь
 */
//@Configuration
//@Order(SecurityProperties.BASIC_AUTH_ORDER)
//public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
//
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        super.getHttp()
//                .httpBasic()
////                .and()
////                .csrf()
////                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
////                .and()
////                .authorizeRequests()
////                .anyRequest().authenticated()
////                .and()
////                .authorizeRequests()
////                .antMatchers("/app/static").permitAll();
//
//    }
//}
