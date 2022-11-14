package com.nguyentran.CRUDMongoDB.services;

import java.util.List;

import org.bson.Document;

import com.nguyentran.CRUDMongoDB.DTOs.CompanyDTO;

public interface CompanyService {

	List<CompanyDTO> getAllCompanies(int pageNo, int pageSize);

	Document countCompanyAndEmployee(int pageNo, int pageSize);

	Document countEmployeeAndGetSalary(String id, int year);

	CompanyDTO getCompanyById(String id);

	Document GetAllSalaryInCompanyByYear(Integer year);

	Document GetAllSalaryInCompanyBetweenYears(Integer yearStart, Integer yearEnd);

}
