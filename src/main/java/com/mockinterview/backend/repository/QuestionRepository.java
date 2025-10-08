package com.mockinterview.backend.repository;

import com.mockinterview.backend.model.Questions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface QuestionRepository extends JpaRepository<Questions,Long> {
//its argument <Questions,Long> tells spring to create full repository of entity question and its primary key is type is Long <EntityClass,IDType>
    List<Questions>findQuestionsByCompanyName(String name);
}
