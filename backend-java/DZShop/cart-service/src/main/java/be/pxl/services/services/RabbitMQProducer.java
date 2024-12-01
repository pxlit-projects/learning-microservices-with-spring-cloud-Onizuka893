package be.pxl.services.services;

import be.pxl.services.config.RabbitMQConfig;
import be.pxl.services.model.LogbookEntryRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQProducer {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQProducer.class);
    private final RabbitTemplate rabbitTemplate;
    private static final String EXCHANGE_NAME = "Cart-Service";

    private final ObjectMapper objectMapper = new ObjectMapper(); // For JSON serialization

    public void sendMessage(String message) {
        try {
            // Build the LogbookEntryRequest object
            LogbookEntryRequest logbookEntryRequest = LogbookEntryRequest.builder()
                    .producer(EXCHANGE_NAME)
                    .message(message)
                    .build();

            // Serialize the object to byte[]
            byte[] messageBytes = objectMapper.writeValueAsBytes(logbookEntryRequest);

            log.info("Sending log message: {}", logbookEntryRequest);

            // Send the serialized byte array to RabbitMQ
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, messageBytes);
        } catch (Exception e) {
            log.error("Failed to serialize and send message", e);
        }
    }
}