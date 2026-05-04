package service;

import lombok.Getter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Getter
@Service
public class KafkaConsumerService {
    private String receivedMessage;

    @KafkaListener(topics = "my_topic", groupId = "group_id")
    public void consume(String message) {
        System.out.println("Message received: " + message);
        this.receivedMessage = message;
    }

}

