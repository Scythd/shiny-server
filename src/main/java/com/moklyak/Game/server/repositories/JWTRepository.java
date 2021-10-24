/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.repositories;

import com.nimbusds.jose.Payload;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Пользователь
 */

@Repository
public class JWTRepository implements CsrfTokenRepository{

    @Getter
    private String secret;

    public JWTRepository() {
        this.secret = "springrest";
       
    }
    
    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        String id = UUID.randomUUID().toString().replace("-", "");
        Date now = new Date();
        Date exp = Date.from(LocalDateTime.now().plusMinutes(30)
                .atZone(ZoneId.systemDefault()).toInstant());

        String token = "";
        token = Jwt.withTokenValue("1")
                   .expiresAt(exp.toInstant())
                   .issuedAt(now.toInstant())
                   .jti(id)
                   .notBefore(now.toInstant())
                   .build().getTokenValue();
        System.out.println("TOKEN " + token + "TOKEN!!!!!");
        return new DefaultCsrfToken("x-csrf-token", "_csrf", token);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        return null;
    }
    
}
