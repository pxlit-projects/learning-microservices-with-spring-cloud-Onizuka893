package be.pxl.services.controller;

import be.pxl.services.services.RabbitMQProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessagingController {

    private static final Logger log = LoggerFactory.getLogger(MessagingController.class);
    private final RabbitMQProducer rabbitMQProducer;

    @GetMapping("/send")
    public String sendMessage(@RequestParam String message) {
        log.info("Message endpoint hit {}", message);
        rabbitMQProducer.sendMessage(message);
        return "Message sent: " + message;
    }
}
