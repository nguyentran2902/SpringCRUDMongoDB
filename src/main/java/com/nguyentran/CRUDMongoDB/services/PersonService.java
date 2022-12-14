package com.nguyentran.CRUDMongoDB.services;

import java.util.ArrayList;

import java.util.List;




import org.bson.Document;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

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
	public List<PersonDTO> getAllPersons(int pageNo, int pageSize) {
		List<PersonDTO> personDTOs = new ArrayList<PersonDTO>();
		personRepository.getAllPersons(personDTOs, pageNo, pageSize);

		return personDTOs;
	}

	// find by ID
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
	public int deletePerson(String id) {
		if (getPersonById(id) == null) {
			return 0;
		}

		DeleteResult dr = personRepository.deletePerson(id);

		//check delete success
		if (dr.getDeletedCount() >= 1)
			return 1;

		return 2;
	}

	// update person
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

		if(personDTOs !=null || personDTOs.size()>0) {
			
			return personDTOs;
		}
		return null;
	}

	// Vi???t query update th??m 1 language c???a 1 person
	public int addLanguageforPerson(String id, Language lang) {

		UpdateResult ur = personRepository.addLanguageforPerson(id, lang);

		//kh??ng t??m th???y person n??o c?? id th???a ??k 
		if (ur.getMatchedCount() == 0) {
			return 0;
		}

		//T??m th???y person nh??ng kh??ng c?? update n??o ??c th???c hi???n (=> lang is exist)
		if (ur.getModifiedCount() == 0) {
			return 1;
		}

		//t??m th???y person th???a ??k v?? c?? update (th??nh c??ng)
		if (ur.getModifiedCount() >= 1) {
			return 2;
		}
		
		return 3;
	}

	public int removeLangOfPerson(String id, Language lang) {

		UpdateResult ur = personRepository.removeLangOfPerson(id,lang);

		//kh??ng t??m th???y person n??o c?? id th???a ??k 
		if (ur.getMatchedCount() == 0) {
			return 0;
		}

		//T??m th???y person nh??ng kh??ng c?? update n??o ??c th???c hi???n (=> lang is not exist)
		if (ur.getModifiedCount() == 0) {
			return 1;
		}

		//t??m th???y person th???a ??k v?? ???? th???c hi???n remove (th??nh c??ng)
		if (ur.getModifiedCount() >= 1) {
			return 2;
		}

		return 3;
	}

	// 4 Vi???t query update th??m 1 info c???a 1 person
	public int addInfPerson(String id, Info inf) {
		
		UpdateResult ur = personRepository.addInfPerson(id,inf);
		
		//kh??ng t??m th???y person n??o c?? id th???a ??k 
		if (ur.getMatchedCount() == 0) {
			return 0;
		}
		//T??m th???y person nh??ng kh??ng c?? update n??o ??c th???c hi???n (=> info is exist)
		if (ur.getModifiedCount() == 0) {
			return 1;
		}

		//t??m th???y person th???a ??k v?? ???? th???c hi???n add (th??nh c??ng)
		if (ur.getModifiedCount() >= 1) {
			return 2;
		}
		return 3;
	}

	// 6.X??a 1 info c???a 1 person (theo lo???i th??? v?? m?? th???)
	public int removeInfOfPerson(String id, Info inf) {
		
		UpdateResult ur = personRepository.removeInfOfPerson(id,inf);

		//kh??ng t??m th???y person n??o c?? id th???a ??k 
		if (ur.getMatchedCount() == 0)
			return 0;
		//T??m th???y person nh??ng kh??ng c?? update n??o ??c th???c hi???n (=> info is not exist)
		if (ur.getModifiedCount() == 0)
			return 1;
		//t??m th???y person th???a ??k v?? ???? th???c hi???n remove (th??nh c??ng)
		if (ur.getModifiedCount() >= 1)
			return 2;

		return 3;
	}

	// 8. update gi???i t??nh to??n b??? person sang N/A (2)
	public int updateSexPerson() {
		
		UpdateResult ur = personRepository.updateSexPerson();
		//update faile
		if (ur.getModifiedCount() == 0)
			return 0;

		//update success
		if (ur.getModifiedCount() >= 1)
			return 1;

		return 2;
	}

	// 9.Vi???t query ?????m trong collection person c?? bao nhi??u sdt
	public Document countTotalPhone() {
		
		Document d = personRepository.countTotalPhone();
		if(d!=null ) {
			return d;
		}
		return null;
	}

	// 10. Vi???t query get to??n b??? language hi???n c?? trong collection person (k???t qu???
	// ko ???????c tr??ng nhau)
	public List<Document> getAllLang(int pageNo,int pageSize) {

		
		List<Document> listD  = personRepository.getAllLang(pageNo,pageSize);
		if(listD!=null || listD.size()>0) {
			return listD;
		}
		return null;
	}

	// 11.Vi???t query get nh???ng person c?? t??n ch???a "Nguy???n" v?? ng??y sinh trong kho???ng
	// th??ng 2~ th??ng 10
	public List<Document> getPersonsByNameAndMonth(String name, Integer monthStart, Integer monthEnd, int pageNo,
			int pageSize) {
		

		
		List<Document> docs = personRepository.getPersonsByNameAndMonth(name,monthStart,monthEnd,pageNo,pageSize);
		

		if(docs!=null || docs.size()>0) {
			return docs;
		}
		return null;
	}

	// 12. Vi???t query get th??ng tin c???a to??n b??? person c?? gi???i t??nh l?? nam +
	// language l?? "Ti???ng Vi???t", y??u c???u:
	// - Group theo fullname (h??? + t??n)
	// - K???t qu??? tr??? v??? bao g???m:
	// + fullname (h??? + t??n)
	// + sdt
	// + language (ch??? hi???n th??? language "Ti???ng Vi???t")
	// + email (ch??? hi???n th??? nh???ng email c?? ??u??i l?? @gmail.com)
	public List<Document> getPersonsByCond(int pageNo, int pageSize) {
		
		List<Document> docs =  personRepository.getPersonsByCond(pageNo,pageSize);
		
		if(docs!=null || docs.size()>0) {
			return docs;
		}
		return null;
	}

	// .13
	public List<Document> getPersonsAndCountByCond(int pageNo, int pageSize) {
		
		List<Document> docs = personRepository.getPersonsAndCountByCond(pageNo,pageSize);

		if(docs!=null || docs.size()>0) {
			return docs;
		}
		return null;
	}

	// 5.update 1 CMND c???a 1 person th??nh deactive (ko c??n s??? d???ng n???a)
	public int updateInfPersonToDeactive(String id, Info inf) {

		if (getPersonById(id) == null) {
			return 0;
		}

		UpdateResult ur = personRepository.updateInfPersonToDeactive(id,inf);

		if (ur.getModifiedCount() == 0) {
			return 1;
		}

		if (ur.getModifiedCount() >= 1) {
			return 2;
		}

		return 3;
	}

	// 14
	// Vi???t query update th??ng tin c???a 1 person, g???m:
	// sex
	// infor: th??m m???i 1 infor
	// langs: xo?? 1 lang
	public int updateMultiField(PersonDTO pDTO, String id) {
		if (getPersonById(id) == null) {
			return 0;
		}

		UpdateResult ur = personRepository.updateMultiField(pDTO,id);
		if (ur.getModifiedCount() == 0) {
			return 1;
		}

		if (ur.getModifiedCount() >= 1) {
			return 2;
		}

		return 3;

	}


}
