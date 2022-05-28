package com.moklyak.Game.server.DAOs;

import lombok.Data;

import java.math.BigInteger;

@Data
public class KafkaDao2 {
    long id;
    String method;
    String token;
    String arg;
}
