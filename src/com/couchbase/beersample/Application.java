package com.couchbase.beersample;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.couchbase.client.core.logging.CouchbaseLoggerFactory;
import com.couchbase.client.core.logging.Log4JLoggerFactory;
import com.couchbase.client.deps.com.lmax.disruptor.EventPoller.Handler;
import com.couchbase.client.deps.io.netty.util.ResourceLeakDetector.Level;

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
