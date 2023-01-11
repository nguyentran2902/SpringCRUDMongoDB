package com.nguyentran.CRUDMongoDB.DTOs;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.nguyentran.CRUDMongoDB.entity.PersonObject.Email;
import com.nguyentran.CRUDMongoDB.entity.PersonObject.Info;
import com.nguyentran.CRUDMongoDB.entity.PersonObject.Language;
import com.nguyentran.CRUDMongoDB.entity.PersonObject.Phone;

import lombok.Data;

@Data
public class PersonDTO  implements Serializable{
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
