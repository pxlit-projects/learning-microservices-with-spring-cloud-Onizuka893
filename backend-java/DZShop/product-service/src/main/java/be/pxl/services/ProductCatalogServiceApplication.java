package be.pxl.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ProductCatalogServiceApplication
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ProductCatalogServiceApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(ProductCatalogServiceApplication.class, args);
    }
}