package com.couchbase.beersample;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    }
}
