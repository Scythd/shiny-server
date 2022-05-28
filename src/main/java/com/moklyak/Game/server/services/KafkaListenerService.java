package com.moklyak.Game.server.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moklyak.Game.server.DAOs.KafkaDao;
import com.moklyak.Game.server.controllers.BullCowController;
import com.moklyak.Game.server.models.BullCowGameDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;


@Controller
public class KafkaListenerService {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "BULL-COW-OUT", groupId = "bull-cow-group-mapper")
    public static void listenGroupFoo(String message) {
        try {
            KafkaDao mesObj = objectMapper.readValue(message, KafkaDao.class);
            BullCowController.responses.put(mesObj.getId(), new ResponseEntity<BullCowGameDto>(mesObj.getObject(), HttpStatus.valueOf(mesObj.getResponseCode())));
        } catch (Exception ex){
            String s = ex.getClass().toString();
        }

    }
}
