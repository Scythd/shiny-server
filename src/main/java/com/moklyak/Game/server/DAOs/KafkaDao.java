package com.moklyak.Game.server.DAOs;

import com.moklyak.Game.server.models.BullCowGameDto;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.math.BigInteger;

@Data
public class KafkaDao{
    long id;
    int responseCode;
    BullCowGameDto object;
}
