package com.collabed.core;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("collabed/index")
public class BaseController {

    @GetMapping
    public String index() {
        return "Greetings from CollabEd!";
    }
}
