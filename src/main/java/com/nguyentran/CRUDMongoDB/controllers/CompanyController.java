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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nguyentran.CRUDMongoDB.DTOs.CompanyDTO;
import com.nguyentran.CRUDMongoDB.DTOs.PersonDTO;
import com.nguyentran.CRUDMongoDB.services.CompanyService;

@RestController
@RequestMapping("/company")
public class CompanyController {

	@Autowired
	private CompanyService companyService;

	@GetMapping("getAll")
	public ResponseEntity<?> getAllCompanies(
			@RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "6", required = false) int pageSize) {

		List<CompanyDTO> companyDTOs = companyService.getAllCompanies(pageNo, pageSize);
		if (companyDTOs.size() > 0)
			return ResponseEntity.ok(companyDTOs);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Not found any company in data");
	}

	// 1. Thống kê có bao nhiêu công ty, số lượng nhân viên của mỗi công ty
	@GetMapping("/countCompanyAndEmployee")
	public ResponseEntity<?> countCompanyAndEmployee(@RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "6", required = false) int pageSize) {
		Document doc = companyService.countCompanyAndEmployee(pageNo, pageSize);
		if (doc != null)
			return ResponseEntity.ok(doc);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: get failed");
	}

	// 2. Thống kê công ty A , vào năm 2022 có bao nhiêu nhân viên vào làm, tổng mức
	// lương phải trả cho những nhân viên đó là bao nhiêu

	@GetMapping("/countEmployeeAndGetSalaryByYear/{id}")
	public ResponseEntity<?> countEmployeeAndGetSalary(@PathVariable String id, @RequestParam Integer year) {

		if (id == null || !ObjectId.isValid(id)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: invalid id: " + id);
		}

		if (year == null || year < 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: invalid year: " + year);
		}

		if (companyService.getCompanyById(id) == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Not found any company with id = " + id);
		}

		Document doc = companyService.countEmployeeAndGetSalary(id, year);
		if (doc != null)
			return ResponseEntity.ok(doc);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: get failed");
	}

	// 3. Thống kê tổng số tiền các công ty phải trả cho những người đăng ký vào làm
	// trong năm 2022
	@GetMapping("/GetAllSalaryInCompanyByYear")
	public ResponseEntity<?> GetAllSalaryInCompanyByYear(@RequestParam Integer year) {

		if (year == null || year < 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: invalid year: " + year);
		}

		Document doc = companyService.GetAllSalaryInCompanyByYear(year);
		if (doc != null)
			return ResponseEntity.ok(doc);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: get failed");
	}

	// 4. Thống kê tổng số tiền các công ty IT phải trả cho những
	// người đăng ký vào làm trong các năm từ 2020 ~ 2022
	//
	@GetMapping("/GetAllSalaryInCompanyBetweenYears")
	public ResponseEntity<?> GetAllSalaryInCompanyByYear(@RequestParam Integer yearStart,
			@RequestParam Integer yearEnd) {

		if (yearStart == null || yearStart < 0 || yearEnd == null || yearEnd < 0 || yearStart > yearEnd) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: invalid year");
		}

		Document doc = companyService.GetAllSalaryInCompanyBetweenYears(yearStart, yearEnd);
		if (doc != null)
			return ResponseEntity.ok(doc);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: get failed");
	}

}
