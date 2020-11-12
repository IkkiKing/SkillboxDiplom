package com.ikkiking.controller;


import com.ikkiking.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
public class DefaultController
{
    @Autowired
    private PostRepository postRepository;

    @RequestMapping("/")
    public String index()
    {

        return "index";
    }
}
