package com.nguyentran.CRUDMongoDB.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.nguyentran.CRUDMongoDB.DTOs.PersonDTO;
import com.nguyentran.CRUDMongoDB.models.Person;
import com.nguyentran.CRUDMongoDB.models.PersonObject.Info;
import com.nguyentran.CRUDMongoDB.models.PersonObject.Language;

public interface PersonService {

	Boolean savePerson(PersonDTO p);

	List<PersonDTO> getAllPersons(int pageNo,int pageSize);

	PersonDTO  getPersonById(String id);

	int deletePerson(String id);

	List<PersonDTO> searchPerson(String name, int pageNo, int pageSize);

	int updatePerson(PersonDTO pDTO, String id);
	
	//Viết query update thêm 1 language của 1 person
	int addLanguageforPerson(String id,Language lang);

	int removeLangOfPerson(String id, Language lang);

	int addInfPerson(String id, Info inf);

	int removeInfOfPerson(String id, Info inf);

	int updateSexPerson();

	Document countTotalPhone();

	List<String> getAllLang();

	List<Document> getPersonsByNameAndMonth(int pageNo, int pageSize);

	List<Document> getPersonsByCond(int pageNo, int pageSize);

	List<Document> getPersonsAndCountByCond(int pageNo, int pageSize);

	int updateInfPersonToDeactive(String id,Info inf);

	Person getPersonByIdAndLang(String id, Language lang);

	Person getPersonByIdAndInfo(String id, Info inf);

	int updateMultiField(PersonDTO pDTO, String id);

}
