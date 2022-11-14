package com.nguyentran.CRUDMongoDB.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nguyentran.CRUDMongoDB.models.Company;

@Repository
public interface CompanyRepository extends MongoRepository<Company, String> {

}
