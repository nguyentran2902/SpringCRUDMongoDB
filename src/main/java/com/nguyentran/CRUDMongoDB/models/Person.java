package com.nguyentran.CRUDMongoDB.models;


import java.util.Date;
import java.util.List;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.nguyentran.CRUDMongoDB.models.PersonObject.Email;
import com.nguyentran.CRUDMongoDB.models.PersonObject.Info;
import com.nguyentran.CRUDMongoDB.models.PersonObject.Language;
import com.nguyentran.CRUDMongoDB.models.PersonObject.Phone;

import lombok.*;


@Data

@Document(collection = "person")
public class Person {
	
	@Id
	private String _id;
	
	private String firstName;
	private String lastName;
	private Integer sex;
	private Date dayOfBirth;
	
	private List<Phone> phones;
	private List<Email> emails;
	private List<Info> infos;
	private List<Language> languages;
	
}
