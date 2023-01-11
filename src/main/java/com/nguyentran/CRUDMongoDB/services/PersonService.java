package com.nguyentran.CRUDMongoDB.services;

import java.util.ArrayList;

import java.util.List;

import org.bson.Document;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.nguyentran.CRUDMongoDB.DTOs.PersonDTO;
import com.nguyentran.CRUDMongoDB.entity.Person;
import com.nguyentran.CRUDMongoDB.entity.PersonObject.Info;
import com.nguyentran.CRUDMongoDB.entity.PersonObject.Language;
import com.nguyentran.CRUDMongoDB.repositories.PersonRepository;

@Service
public class PersonService {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PersonRepository personRepository;

	// get ListP
	@Cacheable(value="listPerson", cacheManager ="cacheManager10m")
	public List<PersonDTO> getAllPersons(int pageNo, int pageSize) {
		List<PersonDTO> personDTOs = new ArrayList<PersonDTO>();
		personRepository.getAllPersons(personDTOs, pageNo, pageSize);
		return personDTOs;
	}

	// find by ID
	@Cacheable(value="person",key="#id", cacheManager ="cacheManager1h")
	public PersonDTO getPersonById(String id) {
		Person p = personRepository.getPersonById(id);
		if (p != null)
			return modelMapper.map(p, PersonDTO.class);
		return null;
	}

	// Save Person
	public Boolean savePerson(PersonDTO pDTO) {

		Boolean result = personRepository.savePerson(pDTO);

		return result;
	}

	// delete Person
	@CacheEvict(value="person",key="#id",allEntries=true)
	public int deletePerson(String id) {
		if (getPersonById(id) == null) {
			return 0;
		}

		DeleteResult dr = personRepository.deletePerson(id);

		// check delete success
		if (dr.getDeletedCount() >= 1)
			return 1;

		return 2;
	}

	// update person
	@CachePut(value="person",key="#id")
	public int updatePerson(PersonDTO pDTO, String id) {
		if (getPersonById(id) == null) {
			return 0;
		}
		Person pSave = personRepository.updatePerson(pDTO);

		if (pSave != null) {
			return 1;
		}
		return 2;
	}

	// search person by name
	public List<PersonDTO> searchPerson(String name, int pageNo, int pageSize) {

		List<PersonDTO> personDTOs = personRepository.searchPerson(name, pageNo, pageSize);

		if (personDTOs != null || personDTOs.size() > 0) {

			return personDTOs;
		}
		return null;
	}

	// Viết query update thêm 1 language của 1 person
	public int addLanguageforPerson(String id, Language lang) {

		UpdateResult ur = personRepository.addLanguageforPerson(id, lang);

		// không tìm thấy person nào có id thỏa đk
		if (ur.getMatchedCount() == 0) {
			return 0;
		}

		// Tìm thấy person nhưng không có update nào đc thực hiện (=> lang is exist)
		if (ur.getModifiedCount() == 0) {
			return 1;
		}

		// tìm thấy person thỏa đk và có update (thành công)
		if (ur.getModifiedCount() >= 1) {
			return 2;
		}

		return 3;
	}

	public int removeLangOfPerson(String id, Language lang) {

		UpdateResult ur = personRepository.removeLangOfPerson(id, lang);

		// không tìm thấy person nào có id thỏa đk
		if (ur.getMatchedCount() == 0) {
			return 0;
		}

		// Tìm thấy person nhưng không có update nào đc thực hiện (=> lang is not exist)
		if (ur.getModifiedCount() == 0) {
			return 1;
		}

		// tìm thấy person thỏa đk và đã thực hiện remove (thành công)
		if (ur.getModifiedCount() >= 1) {
			return 2;
		}

		return 3;
	}

	// 4 Viết query update thêm 1 info của 1 person
	public int addInfPerson(String id, Info inf) {

		UpdateResult ur = personRepository.addInfPerson(id, inf);

		// không tìm thấy person nào có id thỏa đk
		if (ur.getMatchedCount() == 0) {
			return 0;
		}
		// Tìm thấy person nhưng không có update nào đc thực hiện (=> info is exist)
		if (ur.getModifiedCount() == 0) {
			return 1;
		}

		// tìm thấy person thỏa đk và đã thực hiện add (thành công)
		if (ur.getModifiedCount() >= 1) {
			return 2;
		}
		return 3;
	}

	// 6.Xóa 1 info của 1 person (theo loại thẻ và mã thẻ)
	public int removeInfOfPerson(String id, Info inf) {

		UpdateResult ur = personRepository.removeInfOfPerson(id, inf);

		// không tìm thấy person nào có id thỏa đk
		if (ur.getMatchedCount() == 0)
			return 0;
		// Tìm thấy person nhưng không có update nào đc thực hiện (=> info is not exist)
		if (ur.getModifiedCount() == 0)
			return 1;
		// tìm thấy person thỏa đk và đã thực hiện remove (thành công)
		if (ur.getModifiedCount() >= 1)
			return 2;

		return 3;
	}

	// 8. update giới tính toàn bộ person sang N/A (2)
	public int updateSexPerson() {

		UpdateResult ur = personRepository.updateSexPerson();
		// update faile
		if (ur.getModifiedCount() == 0)
			return 0;

		// update success
		if (ur.getModifiedCount() >= 1)
			return 1;

		return 2;
	}

	// 9.Viết query đếm trong collection person có bao nhiêu sdt
	public Document countTotalPhone() {

		Document d = personRepository.countTotalPhone();
		if (d != null) {
			return d;
		}
		return null;
	}

	// 10. Viết query get toàn bộ language hiện có trong collection person (kết quả
	// ko được trùng nhau)
	public List<Document> getAllLang(int pageNo, int pageSize) {

		List<Document> listD = personRepository.getAllLang(pageNo, pageSize);
		if (listD != null || listD.size() > 0) {
			return listD;
		}
		return null;
	}

	// 11.Viết query get những person có tên chứa "Nguyễn" và ngày sinh trong khoảng
	// tháng 2~ tháng 10
	public List<Document> getPersonsByNameAndMonth(String name, Integer monthStart, Integer monthEnd, int pageNo,
			int pageSize) {

		List<Document> docs = personRepository.getPersonsByNameAndMonth(name, monthStart, monthEnd, pageNo, pageSize);

		if (docs != null || docs.size() > 0) {
			return docs;
		}
		return null;
	}

	// 12. Viết query get thông tin của toàn bộ person có giới tính là nam +
	// language là "Tiếng Việt", yêu cầu:
	// - Group theo fullname (họ + tên)
	// - Kết quả trả về bao gồm:
	// + fullname (họ + tên)
	// + sdt
	// + language (chỉ hiển thị language "Tiếng Việt")
	// + email (chỉ hiển thị những email có đuôi là @gmail.com)
	public List<Document> getPersonsByCond(int pageNo, int pageSize) {

		List<Document> docs = personRepository.getPersonsByCond(pageNo, pageSize);

		if (docs != null || docs.size() > 0) {
			return docs;
		}
		return null;
	}

	// .13
	public List<Document> getPersonsAndCountByCond(int pageNo, int pageSize) {

		List<Document> docs = personRepository.getPersonsAndCountByCond(pageNo, pageSize);

		if (docs != null || docs.size() > 0) {
			return docs;
		}
		return null;
	}

	// 5.update 1 CMND của 1 person thành deactive (ko còn sử dụng nữa)
	public int updateInfPersonToDeactive(String id, Info inf) {

		if (getPersonById(id) == null) {
			return 0;
		}

		UpdateResult ur = personRepository.updateInfPersonToDeactive(id, inf);

		if (ur.getModifiedCount() == 0) {
			return 1;
		}

		if (ur.getModifiedCount() >= 1) {
			return 2;
		}

		return 3;
	}

	// 14
	// Viết query update thông tin của 1 person, gồm:
	// sex
	// infor: thêm mới 1 infor
	// langs: xoá 1 lang
	public int updateMultiField(PersonDTO pDTO, String id) {
		if (getPersonById(id) == null) {
			return 0;
		}

		UpdateResult ur = personRepository.updateMultiField(pDTO, id);
		if (ur.getModifiedCount() == 0) {
			return 1;
		}

		if (ur.getModifiedCount() >= 1) {
			return 2;
		}

		return 3;

	}

}
