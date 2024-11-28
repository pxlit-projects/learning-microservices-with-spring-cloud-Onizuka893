package be.pxl.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MessagingApplication
{
    private static final Logger log = LoggerFactory.getLogger(MessagingApplication.class);

    public static void main( String[] args )
    {
        new SpringApplicationBuilder(MessagingApplication.class).run(args);
        log.info("Messaging Application started");
    }
}
