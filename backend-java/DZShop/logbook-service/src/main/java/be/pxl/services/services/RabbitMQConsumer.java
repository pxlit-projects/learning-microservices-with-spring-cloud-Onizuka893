package be.pxl.services.services;

import be.pxl.services.config.RabbitMQConfig;
import be.pxl.services.domain.EntryRequest;
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

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        log.info("Received message: {}", message);
        EntryRequest request = EntryRequest.builder()
                .message(message)
                .producer("test")
                .build();
        entryService.addEntry(request);
    }
}
