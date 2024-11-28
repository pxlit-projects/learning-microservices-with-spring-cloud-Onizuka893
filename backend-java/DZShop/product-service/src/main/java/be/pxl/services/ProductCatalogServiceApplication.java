package be.pxl.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
   private static final Logger log = LoggerFactory.getLogger(ProductCatalogServiceApplication.class);

    public static void main( String[] args )
    {
        SpringApplication.run(ProductCatalogServiceApplication.class, args);
        log.info("Product Catalog micro service started");
    }
}
