package com.ikkiking.controller;


import com.ikkiking.api.response.InitResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class DefaultController {

    private final InitResponse initResponse;

    @Autowired
    public DefaultController(InitResponse initResponse) {
        this.initResponse = initResponse;
    }

    @RequestMapping("/")
    public String index(Model model) {
        System.out.println("TEST");
        return "index";
    }
}
