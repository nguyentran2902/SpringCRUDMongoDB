package com.nguyentran.CRUDMongoDB.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.BsonNull;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Facet;
import com.mongodb.client.model.Filters;
import com.nguyentran.CRUDMongoDB.DTOs.CompanyDTO;
import com.nguyentran.CRUDMongoDB.entity.Company;

@Repository
public class CompanyRepository {
	@Autowired
	private MongoDatabase mongoDatabase;

	private MongoCollection<Company> companyCollection;

	@Autowired
	public void CompanyRepository() {
		CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries
						.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		companyCollection = mongoDatabase.getCollection("company", Company.class).withCodecRegistry(pojoCodecRegistry);
		;
	}

	public List<CompanyDTO> getAllCompanies(int pageNo, int pageSize) {
		List<CompanyDTO> companyDTOs = new ArrayList<>();
		List<Bson> pipeline = new ArrayList<Bson>();
		pipeline.add(Aggregates.limit(pageSize));
		pipeline.add(Aggregates.skip((pageNo - 1) * pageSize));
		companyCollection.aggregate(pipeline, CompanyDTO.class).into(companyDTOs);
		return companyDTOs;
	}

	public Company getCompanyById(String id) {
		Bson match = Filters.eq("_id", new ObjectId(id));
		Company p = companyCollection.find(match).first();
		return p;
	}
	
	
	// 1. Thống kê có bao nhiêu công ty, số lượng nhân viên của mỗi công ty
	public Document countCompanyAndEmployee(int pageNo, int pageSize) {
		// face 1
				Bson countCompany = new Document("$count", "totalCompany");

				// face 2
				List<Bson> countEmployeeInCompany = new ArrayList<Bson>();
				Bson lookup = new Document("$lookup", new Document().append("from", "employee").append("localField", "_id")
						.append("foreignField", "idCompany").append("as", "employees"));

				Bson project = new Document("$project",
						new Document().append("companyName", 1L).append("totalEmployees", new Document("$size", "$employees")));
				countEmployeeInCompany.add(lookup);
				countEmployeeInCompany.add(project);
				countEmployeeInCompany.add(Aggregates.limit(pageSize));
				countEmployeeInCompany.add(Aggregates.skip((pageNo - 1) * pageSize));

				// faced {}
				Bson facet = Aggregates.facet(new Facet("countCompany", countCompany),
						new Facet("countEmployeeInCompany", countEmployeeInCompany));

				List<Bson> pipeline = new ArrayList<Bson>();
				pipeline.add(facet);
		Document doc = companyCollection.aggregate(pipeline, Document.class).first();
		
		return doc;
	}

	// 2. Thống kê công ty A , vào năm 2022 có bao nhiêu nhân viên vào làm, tổng mức
		// lương phải trả cho những nhân viên đó là bao nhiêu
	public Document countEmployeeAndGetSalary(String id, int year) {
		List<Bson> pipeline = new ArrayList<Bson>();

		Bson filterCompany = new Document("$match", new Document("_id", new ObjectId(id)));
		Bson lookup = new Document("$lookup", new Document("from", "employee").append("localField", "_id")
				.append("foreignField", "idCompany").append("as", "employees"));

		Bson unwind = new Document("$unwind", "$employees");

		Bson filterYear = new Document("$match", new Document("$expr",
				new Document("$eq", Arrays.asList(new Document("$year", "$employees.timeJoin"), year))));

		Bson group = new Document("$group",
				new Document("_id", new BsonNull())
						.append("totalSalary", new Document("$sum", "$employees.salary.wage"))
						.append("totalEmployees", new Document("$sum", 1)));

		pipeline.add(filterCompany);
		pipeline.add(lookup);
		pipeline.add(unwind);
		pipeline.add(filterYear);
		pipeline.add(group);
		Document doc = companyCollection.aggregate(pipeline, Document.class).first();
		return doc;
	}

	// 3. Thống kê tổng số tiền các công ty phải trả cho những người đăng ký vào làm
		// trong năm 2022
	public Document GetAllSalaryInCompanyByYear(Integer year) {
		List<Bson> pipeline = new ArrayList<Bson>();

		Bson lookup = new Document("$lookup", new Document("from", "employee").append("localField", "_id")
				.append("foreignField", "idCompany").append("as", "employees"));

		Bson unwind = new Document("$unwind", "$employees");

		Bson filterYear = new Document("$match", new Document("$expr",
				new Document("$eq", Arrays.asList(new Document("$year", "$employees.timeJoin"), year))));

		Bson group = new Document("$group",
				new Document("_id", "$employees.IdCompany").append("data", new Document("$first", "$$ROOT"))
						.append("totalSalary", new Document("$sum", "$employees.salary.wage")));

		Bson project = new Document("$project",
				new Document("companyName", "$data.companyName").append("totalSalary", 1));

		pipeline.add(lookup);
		pipeline.add(unwind);
		pipeline.add(filterYear);
		pipeline.add(group);
		pipeline.add(project);
		Document doc = companyCollection.aggregate(pipeline, Document.class).first();
		return doc;
	}

	// 4. Thống kê tổng số tiền các công ty IT phải trả cho những người đăng ký vào
		// làm trong các năm từ 2020 ~ 2022
	public Document GetAllSalaryInCompanyBetweenYears(Integer yearStart, Integer yearEnd) {
		List<Bson> pipeline = new ArrayList<Bson>();

		Bson filterPurpose = new Document("$match", new Document("purposes.purpose", "IT"));

		Bson lookup = new Document("$lookup", new Document("from", "employee").append("localField", "_id")
				.append("foreignField", "idCompany").append("as", "employees"));

		Bson unwind = new Document("$unwind", "$employees");

		Bson addFields = new Document("$addFields", new Document("year", new Document("$year", "$employees.timeJoin")));

		Bson filterYear = new Document("$match",
				new Document("year", new Document("$gte", yearStart).append("$gte", yearEnd)));

		Bson group = new Document("$group",
				new Document("_id", "$employees.IdCompany").append("data", new Document("$first", "$$ROOT"))
						.append("totalSalary", new Document("$sum", "$employees.salary.wage")));

		Bson project = new Document("$project",
				new Document("companyName", "$data.companyName").append("totalSalary", 1));

		pipeline.add(filterPurpose);
		pipeline.add(lookup);
		pipeline.add(unwind);
		pipeline.add(addFields);
		pipeline.add(filterYear);
		pipeline.add(group);
		pipeline.add(project);
		
		Document doc = companyCollection.aggregate(pipeline, Document.class).first();
		return doc;
	}

}
