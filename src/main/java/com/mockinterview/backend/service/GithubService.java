package com.mockinterview.backend.service;

import com.mockinterview.backend.model.GithubFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class GithubService {

    @Value("${GITHUB_TOKEN}")
    public String gitHubToken;

    private static final String BaseURL="https://api.github.com/repos/krishnadey30/LeetCode-Questions-CompanyWise/contents";
    private static final Logger log = LoggerFactory.getLogger(GithubService.class);
    private final RestTemplate restTemplate=new RestTemplate();

    public void fetchContent(){
        HttpHeaders header=new HttpHeaders();
        header.set("Authorization","Bearer "+gitHubToken);
        header.set("Accept","application/vnd.github.v3+json");
        HttpEntity<String>entity=new HttpEntity<>(header);
        ResponseEntity<GithubFile[]>response=restTemplate.exchange(BaseURL, HttpMethod.GET,entity,GithubFile[].class);
        GithubFile[] files=response.getBody();
        if(files==null)return;
        System.out.println(files[0].name);
        log.info(Arrays.toString(files));
        System.out.println("fetching");
    }


}
