package com.nguyentran.CRUDMongoDB.repositories;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nguyentran.CRUDMongoDB.models.Person;

@Repository

public interface PersonRepository extends MongoRepository<Person, String> {
	
//	 List<Person> findAll();
//	 Optional<Person> findByPerSonId(ObjectId id);
//	 Person deleteByPerSonId(String id);
	
}
