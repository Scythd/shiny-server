/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.configurations.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 *
 * @author Пользователь
 */
public class JwtTokenAfterFilter extends GenericFilterBean {

    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenAfterFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            token = jwtTokenProvider.refreshToken(token);
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(httpRequest);
                httpResponse.addHeader("Authorization", "Bearer_" + token);
                mutableRequest.putHeader("Authorization", token);
                chain.doFilter(mutableRequest, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}

final class MutableHttpServletRequest extends HttpServletRequestWrapper {

    // holds custom header and value mapping
    private final Map<String, String> customHeaders;

    public MutableHttpServletRequest(HttpServletRequest request) {
        super(request);
        this.customHeaders = new HashMap<String, String>();
    }

    public void putHeader(String name, String value) {
        this.customHeaders.put(name, value);
    }

    public String getHeader(String name) {
        // check the custom headers first
        String headerValue = customHeaders.get(name);

        if (headerValue != null) {
            return headerValue;
        }
        // else return from into the original wrapped object
        return ((HttpServletRequest) getRequest()).getHeader(name);
    }

    public Enumeration<String> getHeaderNames() {
        // create a set of the custom header names
        java.util.Set<String> set = new java.util.HashSet<>(customHeaders.keySet());

        // now add the headers from the wrapped request object
        @SuppressWarnings("unchecked")
        Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
        while (e.hasMoreElements()) {
            // add the names of the request headers into the list
            String n = e.nextElement();
            set.add(n);
        }

        // create an enumeration from the set and return
        return Collections.enumeration(set);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        Set<String> headerValues = new HashSet<>();
        if (this.customHeaders.get(name) != null) {
            headerValues.add(this.customHeaders.get(name));
        } else {
            Enumeration<String> underlyingHeaderValues = ((HttpServletRequest) getRequest()).getHeaders(name);
            while (underlyingHeaderValues.hasMoreElements()) {
                headerValues.add(underlyingHeaderValues.nextElement());
            }
        }
        return Collections.enumeration(headerValues);
    }
}
