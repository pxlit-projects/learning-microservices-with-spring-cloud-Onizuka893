package be.pxl.services.services;

import be.pxl.services.config.RabbitMQConfig;
import be.pxl.services.domain.LogbookEntryRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConsumer.class);
    private final EntryService entryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(byte[] messageBytes) {
        try {
            // Deserialize the byte[] to a LogbookEntryRequest
            LogbookEntryRequest message = objectMapper.readValue(messageBytes, LogbookEntryRequest.class);

            log.info("Received message: {}", message);

            // Process the message
            LogbookEntryRequest request = LogbookEntryRequest.builder()
                    .message(message.getMessage())
                    .producer(message.getProducer())
                    .build();

            entryService.addEntry(request);
        } catch (Exception e) {
            log.error("Failed to process message", e);
        }
    }
}
