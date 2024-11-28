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
public class LogbookApplication
{
    private static final Logger log = LoggerFactory.getLogger(LogbookApplication.class);

    public static void main( String[] args )
    {
        new SpringApplicationBuilder(LogbookApplication.class).run(args);
        log.info("Logbook Application started");
    }
}
