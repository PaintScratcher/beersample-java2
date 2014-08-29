package com.couchbase.beersample;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import com.couchbase.client.core.logging.CouchbaseLoggerFactory;
import com.couchbase.client.core.logging.Log4JLoggerFactory;

@ComponentScan
@Controller
@EnableAutoConfiguration
public class Application {
	
    @RequestMapping("/")
    String home() {
        return "index";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
        CouchbaseLoggerFactory.setDefaultFactory(new Log4JLoggerFactory());
    }
}
