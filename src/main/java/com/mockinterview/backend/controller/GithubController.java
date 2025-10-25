package com.mockinterview.backend.controller;

import com.mockinterview.backend.service.GithubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class GithubController {
    @Autowired
    private GithubService githubService;

    @GetMapping(value = "/admin/fetchContent")
    public void updateQuestions(){
        String response=githubService.fetchGithubFiles();
        System.out.println(response);
    }

}
