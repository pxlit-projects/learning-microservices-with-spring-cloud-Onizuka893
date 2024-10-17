package be.pxl.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * CartServiceApplication
 */
@SpringBootApplication
@EnableDiscoveryClient
public class CartServiceApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(CartServiceApplication.class, args);
    }
}
