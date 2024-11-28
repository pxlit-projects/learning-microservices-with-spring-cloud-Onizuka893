package be.pxl.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * CartServiceApplication
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class CartServiceApplication
{
    private final static Logger log = LoggerFactory.getLogger(CartServiceApplication.class);

    public static void main( String[] args )
    {
        SpringApplication.run(CartServiceApplication.class, args);
        log.info("Cart microservice started");
    }
}
