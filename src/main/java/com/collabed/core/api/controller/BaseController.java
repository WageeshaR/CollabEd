package com.collabed.core.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Wageesha Rasanjana
 * @since 1.0
 */

@RestController
public class BaseController {

    @GetMapping
    public String index() {
        return "Greetings from CollabEd!";
    }
}
