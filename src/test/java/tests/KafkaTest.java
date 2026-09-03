package tests;

import config.KafkaConsumerConfig;
import config.KafkaProducerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import service.KafkaConsumerService;
import service.KafkaProducerService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = {
        KafkaConsumerService.class,
        KafkaProducerService.class,
        KafkaConsumerConfig.class,
        KafkaProducerConfig.class,
})
@DirtiesContext
class KafkaTest {

    @Autowired
    private KafkaProducerService producerService;
    @Autowired
    private KafkaConsumerService consumerService;

    @Test
    void testSendAndReceiveMessage() throws InterruptedException {

        Thread.sleep(3000);

        String testMessage = "Заказ 12312412 оформлен";

        producerService.sendMessage(testMessage);

        Thread.sleep(3000);

        assertThat(consumerService.getReceivedMessage()).isEqualTo("Заказ 12312412 оформлен");
    }
}