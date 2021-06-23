package com.example.webwar244;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <br><br>
 * Created 2021-04-15 10:02
 *
 * @author Gary Clayburg
 */
@RestController
@RequestMapping(value = "/hello")
public class HelloController {

    @GetMapping("/world")
    public String hello() {
        return "hello world";
    }
}
