 package com.nguyentran.CRUDMongoDB.controllers;


import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nguyentran.CRUDMongoDB.DTOs.PersonDTO;
import com.nguyentran.CRUDMongoDB.entity.PersonObject.Info;
import com.nguyentran.CRUDMongoDB.entity.PersonObject.Language;
import com.nguyentran.CRUDMongoDB.services.PersonService;


@RestController
@RequestMapping("/admin/person")
public class PersonController {

	@Autowired
	private PersonService personService;

	// get all person
	@GetMapping("/getAllPerson")
	public ResponseEntity<?> getAllPersons(
			@RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "6", required = false) int pageSize) {

		try {
			List<PersonDTO> personsDTO = personService.getAllPersons(pageNo, pageSize);
			if (personsDTO != null && personsDTO.size() > 0)
				return ResponseEntity.ok(personsDTO);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error: Not found any person in data");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

	// get person by id
	@GetMapping("/getPersonById/{id}")
	public ResponseEntity<?> getPersonById(@PathVariable String id) {
		if (id == null || !ObjectId.isValid(id)) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error: invalid id: " + id);
		}

		try {
			PersonDTO personDTO = personService.getPersonById(id);

			if (personDTO != null)
				return ResponseEntity.ok(personDTO);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error: Not found any person");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

	// add new person
	@PostMapping("/createNewPerson")
	public ResponseEntity<?> savePerson(@RequestBody PersonDTO pDTO) {

		if (pDTO == null) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error: Invalid input data!");
		}

		try {
			Boolean result = personService.savePerson(pDTO);

			if (result)
				return ResponseEntity.status(HttpStatus.CREATED).body("save success");

			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error: save failed!");

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

	// update person
	@PostMapping("/update/{id}")
	public ResponseEntity<?> updatePerson(@RequestBody PersonDTO pDTO, @PathVariable String id) {

		if (id == null || !ObjectId.isValid(id)) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error: invalid id: " + id);
		}
		if (pDTO == null) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error: Invalid input data!");
		}

		try {
			int resultCode = personService.updatePerson(pDTO, id);

			if (resultCode == 0)
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Error: Not found any person with id = " + id);
			if (resultCode == 1)
				return ResponseEntity.status(HttpStatus.OK).body("update success");

			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error: update failed!");

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

		

	}

	// x??a 1 person
	@GetMapping("/delete/{id}")
	public ResponseEntity<?> deletePerson(@PathVariable String id) {

		if (id == null || !ObjectId.isValid(id)) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error: invalid id: " + id);
		}

		try {
			int resultCode = personService.deletePerson(id);

			if (resultCode == 0)
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Error: Not found any person with id = " + id);
			if (resultCode == 1)
				return ResponseEntity.status(HttpStatus.OK).body("delete success");

			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error: Delete failed");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

	// search person by name
	@GetMapping("/search/{name}")
	public ResponseEntity<?> searchPersonByName(
			@RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "6", required = false) int pageSize,
			@PathVariable(required = false) String name) {

		try {
			List<PersonDTO> personDTOs = personService.searchPerson(name, pageNo, pageSize);
			if (personDTOs != null && personDTOs.size() >= 0)
				return ResponseEntity.ok(personDTOs);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error: Not found any person with name =" + name);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

	// 2.Vi???t query update th??m 1 language c???a 1 person
	@PostMapping("/lang/update/{id}")
	public ResponseEntity<?> updateLangOfPerson(@RequestBody Language lang, @PathVariable String id) {

		if (id == null || !ObjectId.isValid(id)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: invalid id: " + id);
		}

		if (lang == null || lang.getLanguage() == null || lang.getType() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid input data!!");
		}

		try {
			int resultCode = personService.addLanguageforPerson(id, lang);

			if (resultCode == 0) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Error: Not found any person with id = " + id);
			}

			if (resultCode == 1) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Error: lang is existed in this person");
			}

			if (resultCode == 2) {
				return ResponseEntity.status(HttpStatus.OK).body("update success");
			}

			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error:update failed");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

	// 3.Vi???t query xo?? 1 language c???a 1 person
	@PostMapping("/lang/delete/{id}")
	public ResponseEntity<?> RemoveLangOfPerson(@RequestBody Language lang, @PathVariable String id) {
		if (id == null || !ObjectId.isValid(id)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: invalid id: " + id);
		}

		if (lang == null || lang.getLanguage() == null || lang.getType() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid input data!!");
		}

		try {
			int resultCode = personService.removeLangOfPerson(id, lang);

			if (resultCode == 0) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Error: Not found any person with id = " + id);
			}

			if (resultCode == 1) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Error: lang not existed in this person");
			}

			if (resultCode == 2) {
				return ResponseEntity.status(HttpStatus.OK).body("delete success");
			}

			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error:delete failed");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

	// 4 Vi???t query update th??m 1 info c???a 1 person
	@PostMapping("/info/update/{id}")
	public ResponseEntity<?> updateInfPerson(@RequestBody Info inf, @PathVariable String id) {

		if (id == null || !ObjectId.isValid(id)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: invalid id: " + id);
		}
		if (inf == null || inf.getNumberId() == null || inf.getType() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid input data!!");
		}

		try {
			int resultCode = personService.addInfPerson(id, inf);

			if (resultCode == 0) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Error: Not found any person with id = " + id);
			}
			if (resultCode == 1) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Error: info were exist in this person");
			}
			// ok
			if (resultCode == 2) {
				return ResponseEntity.status(HttpStatus.OK).body("update success");
			}
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error:update failed");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

	// 5.update 1 CMND c???a 1 person th??nh deactive (ko c??n s??? d???ng n???a)
	@PostMapping("/info/updateToDeactive/{id}")
	public ResponseEntity<?> updateInfPersonToDeactive(@RequestBody Info inf, @PathVariable String id) {
		if (id == null || !ObjectId.isValid(id)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: invalid id");
		}

		if (inf == null || inf.getNumberId() == null || inf.getType() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid input data!!");
		}

		try {
			int resultCode = personService.updateInfPersonToDeactive(id, inf);

			if (resultCode == 0) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Error: Not found any person with id = " + id);
			}

			if (resultCode == 1) {
				return ResponseEntity.status(HttpStatus.ACCEPTED)
						.body("all info was updated before OR do not match any info in this person");
			}

			if (resultCode == 2) {
				return ResponseEntity.status(HttpStatus.CREATED).body("update success");
			}

			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error:update failed");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

	// 6.X??a 1 info c???a 1 person (theo lo???i th??? v?? m?? th???)
	@PostMapping("/info/delete/{id}")
	public ResponseEntity<?> RemoveInfOfPerson(@RequestBody Info inf, @PathVariable String id) {

		if (id == null || !ObjectId.isValid(id)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: invalid id");
		}

		if (inf == null || inf.getNumberId() == null || inf.getType() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid input data!!");
		}

		try {
			int resultCode = personService.removeInfOfPerson(id, inf);

			if (resultCode == 0)
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Error: Not found any person with id = " + id);
			if (resultCode == 1)
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Error: inf not existed in this person");

			if (resultCode == 2)
				return ResponseEntity.status(HttpStatus.OK).body("delete success");

			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error:delete failed");

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

	// 8. update gi???i t??nh to??n b??? person sang N/A (2)
	@GetMapping("/sex/update")
	public ResponseEntity<?> updateSexOfPersons() {

		try {
			int resultCode = personService.updateSexPerson();
			if (resultCode == 0)
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("all person's gender were updated before");

			if (resultCode == 1)
				return ResponseEntity.status(HttpStatus.OK).body("update success");

			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error: update failed");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

	// 9.Vi???t query ?????m trong collection person c?? bao nhi??u sdt
	@GetMapping("/countTotalPhone")
	public ResponseEntity<?> countTotalPhone() {
		try {
			Document d = personService.countTotalPhone();
			if (d != null)
				return ResponseEntity.status(HttpStatus.OK).body(d);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error:get failed");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

	// 10. Vi???t query get to??n b??? language hi???n c?? trong collection person (k???t qu???
	// ko ???????c tr??ng nhau)
	@GetMapping("/getallLang")
	public ResponseEntity<?> getAllLang(@RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "6", required = false) int pageSize) {
		try {
			List<Document> langs = personService.getAllLang(pageNo,pageSize);
			if (langs != null && langs.size() > 0)
				return ResponseEntity.status(HttpStatus.OK).body(langs);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error:get failed");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

	// 11.Vi???t query get nh???ng person c?? t??n ch???a "Nguy???n" v?? ng??y sinh trong kho???ng
	// th??ng 2~ th??ng 10
	@GetMapping("/getPersonsByNameAndMonth")
	public ResponseEntity<?> getPersonsByNameAndMonth(
			@RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "6", required = false) int pageSize,
			@RequestParam(required = false, defaultValue = "") String name,
			@RequestParam(required = false, defaultValue = "1") int monthStart,
			@RequestParam(required = false, defaultValue = "12") int monthEnd) {

		if (monthStart <= 0 || monthStart > 12 || monthEnd <= 0 || monthEnd > 12 || monthStart > monthEnd) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid param");
		}

		try {
			List<Document> docs = personService.getPersonsByNameAndMonth(name, monthStart, monthEnd, pageNo, pageSize);
			if (docs != null && docs.size() > 0)
				return ResponseEntity.ok(docs);
			else
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Not found any person in data");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

	// 12. Vi???t query get th??ng tin c???a to??n b??? person c?? gi???i t??nh l?? nam +
	// language l?? "Ti???ng Vi???t", y??u c???u:
	// - Group theo fullname (h??? + t??n)
	// - K???t qu??? tr??? v??? bao g???m:
	// + fullname (h??? + t??n)
	// + sdt
	// + language (ch??? hi???n th??? language "Ti???ng Vi???t")
	// + email (ch??? hi???n th??? nh???ng email c?? ??u??i l?? @gmail.com)
	@GetMapping("/getPersonsByCond")
	public ResponseEntity<?> getPersonsByCond(
			@RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "6", required = false) int pageSize) {

		try {
			List<Document> docs = personService.getPersonsByCond(pageNo, pageSize);

			if (docs != null && docs.size() > 0)
				return ResponseEntity.ok(docs);
			else
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error: Not found any person in data");

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

	// 13
	@GetMapping("/getPersonsAndCountByCond")
	public ResponseEntity<?> getPersonsAndCountByCond(
			@RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "6", required = false) int pageSize) {

		try {
			List<Document> docs = personService.getPersonsAndCountByCond(pageNo, pageSize);
			if (docs != null && docs.size() > 0)
				return ResponseEntity.ok(docs);

			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error: Not found any person in data");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}
	// 14
	// Vi???t query update th??ng tin c???a 1 person, g???m:
	// sex
	// infor: th??m m???i 1 infor
	// langs: xo?? 1 lang

	@PostMapping("/updateMultiField/{id}")
	public ResponseEntity<?> updateMultiField(@RequestBody PersonDTO pDTO, @PathVariable String id) {

		if (id == null || !ObjectId.isValid(id)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: invalid id");
		}

		if (pDTO == null || pDTO.getSex() == null || pDTO.getInfos() == null || pDTO.getInfos().get(0).getType() == null
				|| pDTO.getInfos().get(0).getNumberId() == null || pDTO.getLanguages() == null
				|| pDTO.getLanguages().size() <= 0 || pDTO.getLanguages().get(0).getLanguage() == null
				|| pDTO.getPhones() == null || pDTO.getPhones().size() <= 0
				|| pDTO.getPhones().get(0).getNumber() == null) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid input data!");
		}

		try {
			int resultCode = personService.updateMultiField(pDTO, id);

			if (resultCode == 0) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Error: Not found any person with id = " + id);
			}
			if (resultCode == 1) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body("Error:???????");
			}

			if (resultCode == 2) {
				return ResponseEntity.status(HttpStatus.OK).body("update success");
			}

			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error:update failed");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

}
