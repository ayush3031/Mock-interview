package com.mockinterview.backend.repository;

import com.mockinterview.backend.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//repositories work as interface between actual and spring-boot . helps by writing sql queries wrt to java code in repo like find by name's sql query in written automatically
public interface CompanyRepository extends JpaRepository<Company,Long>{
    //to find by name if company is there in db
    Optional<Company> findByName(String name);

}
