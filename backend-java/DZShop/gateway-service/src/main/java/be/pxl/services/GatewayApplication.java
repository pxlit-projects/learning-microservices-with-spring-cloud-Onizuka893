package be.pxl.services;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication
{
    public static void main( String[] args )
    {
        new SpringApplicationBuilder(GatewayApplication.class).run(args);
    }
}
