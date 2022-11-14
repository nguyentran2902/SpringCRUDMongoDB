package com.nguyentran.CRUDMongoDB.DTOs;

import java.util.Date;
import java.util.List;

import com.nguyentran.CRUDMongoDB.models.PersonObject.Email;
import com.nguyentran.CRUDMongoDB.models.PersonObject.Info;
import com.nguyentran.CRUDMongoDB.models.PersonObject.Language;
import com.nguyentran.CRUDMongoDB.models.PersonObject.Phone;

import lombok.Data;

@Data
public class PersonDTO {
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
