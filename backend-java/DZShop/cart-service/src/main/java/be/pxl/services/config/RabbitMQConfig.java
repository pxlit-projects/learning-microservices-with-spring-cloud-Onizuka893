package be.pxl.services.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "example_queue";

    @Bean
    public Queue exampleQueue() {
        return new Queue(QUEUE_NAME, true); // Durable queue
    }
}