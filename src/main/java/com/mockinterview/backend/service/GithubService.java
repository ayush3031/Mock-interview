package com.mockinterview.backend.service;

import com.mockinterview.backend.model.Company;
import com.mockinterview.backend.model.GithubFile;
import com.mockinterview.backend.model.Questions;
import com.mockinterview.backend.repository.CompanyRepository;
import com.mockinterview.backend.repository.QuestionRepository;
import com.opencsv.CSVReader;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;
import java.util.*;

@Service
public class GithubService {

    @Value("${GITHUB_TOKEN}")
    public String gitHubToken;

    private static final String BaseURL="https://api.github.com/repos/krishnadey30/LeetCode-Questions-CompanyWise/contents";
    private static final Logger log = LoggerFactory.getLogger(GithubService.class);
    private final RestTemplate restTemplate=new RestTemplate();

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public String fetchGithubFiles()
    {
        HttpHeaders header=new HttpHeaders();
        header.set("Authorization","Bearer "+gitHubToken);
        header.set("Accept","application/vnd.github.v3+json");

        HttpEntity<String>entity=new HttpEntity<>(header);
        ResponseEntity<GithubFile[]>response=restTemplate.exchange(BaseURL, HttpMethod.GET,entity,GithubFile[].class);
        GithubFile[] files=response.getBody();
        if(files==null)
        {
            return "No files found";
        }
        else
        {
            for(GithubFile file:files)
            {
                String name=file.name.toLowerCase();
                if(name!=null && name.endsWith("_alltime.csv"))
                {
                    String companyName=extractCompanyName(name);
                    String downloadUrl=file.download_url;
                    List<Pair<String,String>>questionsList=extractQuestions(companyName,downloadUrl,header);
                    saveQuestions(companyName,questionsList);
                }
            }
        }
        return "Fetched Successfully";
    }

    public List<Pair<String,String>> extractQuestions(String name, String downloadUrl,HttpHeaders header)
    {
        List<Pair<String,String>> questionList=new ArrayList<>();
        try
        {
            HttpEntity entity = new HttpEntity(header);
            ResponseEntity<String> csvResponse=restTemplate.exchange(downloadUrl,HttpMethod.GET,entity,String.class);
            String csvContent=csvResponse.getBody();
            if (csvContent != null && !csvContent.isEmpty()) {
                try (CSVReader csvReader = new CSVReader(new StringReader(csvContent))) {
                    String[] line;
                    //headers
                    csvReader.readNext();
                    while ((line = csvReader.readNext()) != null) {
                        String question = line[1];
                        String difficulty = line[3];
                        String hashedQuestion = DigestUtils.sha256Hex(question.toLowerCase().trim());
                        //check if each question exists already for this company or not
                        if (!questionRepository.existsByQuestionHashAndCompanyName(hashedQuestion, name)) {
                            questionList.add(Pair.of(question, difficulty));
                        }
                    }
                    log.atInfo().log("Added questions to list");
                } catch (Exception e) {
                    log.atError().log("Error in csv reader");
                    throw new RuntimeException(e);
                }
                if (companyRepository.findByName(name).isEmpty()) {
                    Company company = new Company(name);
                    companyRepository.save(company);
                }

            }
            return questionList;
        } catch (Exception e) {
            log.atError().log("Error fetching from download url");
            throw new RuntimeException(e);
        }
    }

    private void saveQuestions(String name,List<Pair<String,String>>questionsList)
    {
        Optional<Company> company = companyRepository.findByName(name);
        if(company.isEmpty())
        {
            return;
        }
        Company company1=company.get();
        for(Pair<String,String>pair:questionsList)
        {
            Questions question = new Questions();
            question.setDifficulty(pair.getValue());
            question.setCompany(company1);
            question.setQuestionText(pair.getKey());
            String hashedQuestion=DigestUtils.sha256Hex(pair.getKey().toLowerCase().trim());
            question.setQuestionHash(hashedQuestion);
            questionRepository.save(question);
        }
    }

    private String extractCompanyName(String fileName) {
        return fileName.split("_")[0].trim();
    }


}
