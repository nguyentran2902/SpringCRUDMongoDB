package com.nguyentran.CRUDMongoDB.services;


import java.util.List;


import org.bson.Document;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import com.nguyentran.CRUDMongoDB.DTOs.CompanyDTO;
import com.nguyentran.CRUDMongoDB.entity.Company;
import com.nguyentran.CRUDMongoDB.repositories.CompanyRepository;

@Service
public class CompanyService {



	@Autowired
	private CompanyRepository companyRepository;
	@Autowired
	private ModelMapper modelMapper;


	// get all company
	public List<CompanyDTO> getAllCompanies(int pageNo, int pageSize) {
		List<CompanyDTO> companyDTOs = companyRepository.getAllCompanies(pageNo,pageSize);
		return companyDTOs;
	}

	// get company by id
	public CompanyDTO getCompanyById(String id) {
		
		
		Company p = companyRepository.getCompanyById(id);
		
		return modelMapper.map(p, CompanyDTO.class);
	}

	// 1. Thống kê có bao nhiêu công ty, số lượng nhân viên của mỗi công ty
	public Document countCompanyAndEmployee(int pageNo, int pageSize) {
	
		Document doc = companyRepository.countCompanyAndEmployee(pageNo,pageSize);
		return doc;
	}

	// 2. Thống kê công ty A , vào năm 2022 có bao nhiêu nhân viên vào làm, tổng mức
	// lương phải trả cho những nhân viên đó là bao nhiêu
	public Document countEmployeeAndGetSalary(String id, int year) {

		Document doc = companyRepository.countEmployeeAndGetSalary(id,year);
		return doc;
	}

	// 3. Thống kê tổng số tiền các công ty phải trả cho những người đăng ký vào làm
			// trong năm 2022
	public Document GetAllSalaryInCompanyByYear(Integer year) {
		
		Document doc = companyRepository.GetAllSalaryInCompanyByYear(year);
		return doc;
	}

	// 4. Thống kê tổng số tiền các công ty IT phải trả cho những người đăng ký vào
	// làm trong các năm từ 2020 ~ 2022
	public Document GetAllSalaryInCompanyBetweenYears(Integer yearStart, Integer yearEnd) {
		
		Document doc = companyRepository.GetAllSalaryInCompanyBetweenYears(yearStart,yearEnd);
		return doc;
	}

}
