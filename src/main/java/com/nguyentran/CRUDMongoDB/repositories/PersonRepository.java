package com.nguyentran.CRUDMongoDB.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.BsonNull;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Facet;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.nguyentran.CRUDMongoDB.DTOs.PersonDTO;
import com.nguyentran.CRUDMongoDB.entity.Person;
import com.nguyentran.CRUDMongoDB.entity.PersonObject.Info;
import com.nguyentran.CRUDMongoDB.entity.PersonObject.Language;

@Repository
public class PersonRepository {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private MongoDatabase mongoDatabase;


	private MongoCollection<Person> mongoCollection;

	@Autowired
	public void PersonRepository() {
		CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries
						.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		mongoCollection = mongoDatabase.getCollection("person", Person.class).withCodecRegistry(pojoCodecRegistry);
		;

	}

	//get all
	public void getAllPersons(List<PersonDTO> personDTOs, int pageNo, int pageSize) {
		List<Bson> pipeline = new ArrayList<Bson>();
		pipeline.add(Aggregates.limit(pageSize));
		pipeline.add(Aggregates.skip((pageNo - 1) * pageSize));
		mongoCollection.aggregate(pipeline, PersonDTO.class).into(personDTOs);
	}

	// get by id
	public Person getPersonById(String id) {
		Bson match = Filters.eq("_id", new ObjectId(id));
		Person p = mongoCollection.find(match).first();
		return p;
	}

	//save person
	public Boolean savePerson(PersonDTO pDTO) {
		Person p = modelMapper.map(pDTO, Person.class);
		InsertOneResult ior = mongoCollection.insertOne(p);
		return ior.wasAcknowledged();
	}

	//delete person
	public DeleteResult deletePerson(String id) {
		Bson filter = Filters.eq("_id", new ObjectId(id));
		DeleteResult dr = mongoCollection.deleteOne(filter);
		return  dr;

	}

	//update person
	public Person updatePerson(PersonDTO pDTO) {
		Person p = modelMapper.map(pDTO, Person.class);
		Person pSave = mongoTemplate.save(p);
		return pSave;
	}

	//search person by name
	public List<PersonDTO> searchPerson(String name, int pageNo, int pageSize) {
		Bson regexFilter = new Document("$or",
				Arrays.asList(new Document("firstName", new Document("$regex", name).append("$option", "i")),
						new Document("lastName", new Document("$regex", name).append("$option", "i"))));

		List<Bson> pipeline = new ArrayList<Bson>();
		pipeline.add(regexFilter);
		pipeline.add(Aggregates.limit(pageSize));
		pipeline.add(Aggregates.skip((pageNo - 1) * pageSize));
		List<PersonDTO> personDTOs = new ArrayList<PersonDTO>();
		mongoCollection.aggregate(pipeline, PersonDTO.class).into(personDTOs);
		return  personDTOs;
		
	}

	//add language for person
	public UpdateResult addLanguageforPerson(String id, Language lang) {
		Bson filter = Filters.eq("_id", new ObjectId(id));
		Bson update = Updates.set("languages", lang);
//		UpdateOptions uo = new UpdateOptions().upsert(true);
		
		UpdateResult ur = mongoCollection.updateOne(filter, update);
		return ur;
	}

	//remove language for person
	public UpdateResult removeLangOfPerson(String id, Language lang) {
		Bson filter = Filters.eq("_id", new ObjectId(id));
		Bson update = Updates.pull("languages", lang);
		UpdateResult ur = mongoCollection.updateOne(filter, update);
		return ur;
	}

	//add info for person
	public UpdateResult addInfPerson(String id, Info inf) {
		Bson filter = Filters.eq("_id", new ObjectId(id));

		Bson update = Updates.addToSet("infos", inf);
//		UpdateOptions uo = new UpdateOptions().upsert(true);

		UpdateResult ur = mongoCollection.updateOne(filter, update);
		return ur;
	}

	//remove info for person
	public UpdateResult removeInfOfPerson(String id, Info inf) {
		Bson filter = Filters.eq("_id", new ObjectId(id));
		Bson update = Updates.pull("infos", inf);
//		UpdateOptions uo = new UpdateOptions().upsert(true);

		UpdateResult ur = mongoCollection.updateOne(filter, update);
		return ur;
	}

	//update sex for all persons
	public UpdateResult updateSexPerson() {
		Bson filterSex = Filters.ne("sex", 2);
		Bson update = Updates.set("sex", 2);
		UpdateOptions uOptions = new UpdateOptions().upsert(true);
		UpdateResult ur = mongoCollection.updateMany(filterSex, update, uOptions);
		return ur;
	}

	// 9.Viết query đếm trong collection person có bao nhiêu sdt
	public Document countTotalPhone() {
		List<Bson> pipeline = new ArrayList<>();
		// check phonePerson null?
		Bson filterNull = new Document("$match", new Document("phones.0", new Document("$exists", true)));

		Bson project = new Document("$project", new Document("totalcount", new Document("$size", "$phones")));

		Bson group = new Document("$group",
				new Document("_id", new BsonNull()).append("totalPhones", new Document("$sum", "$totalcount")));

		pipeline.add(filterNull);
		pipeline.add(project);
		pipeline.add(group);

		Document d = mongoCollection.aggregate(pipeline, Document.class).first();
		return d;
	}

	// 10. Viết query get toàn bộ language hiện có trong collection person (kết quả
		// ko được trùng nhau)
	public List<Document> getAllLang(int pageNo,int pageSize) {
		
		  List<Bson> pipeline = new ArrayList<>();

	        Bson limit = Aggregates.limit(pageSize);
	        Bson skip = Aggregates.skip((pageNo - 1) * pageSize);

	        Bson unwind = new Document("$unwind", "$langs");
	        Bson group = new Document("$group", new Document("_id", "$langs"));

	        pipeline.add(unwind);
	        pipeline.add(group);
	        pipeline.add(skip);
	        pipeline.add(limit);

	        List<Document> documents = new ArrayList<>();
	        documents = mongoCollection.aggregate(pipeline, Document.class).into(documents);
	        return documents;

	}

	// 11.Viết query get những person có tên chứa "Nguyễn" và ngày sinh trong khoảng
		// tháng 2~ tháng 10
	public List<Document> getPersonsByNameAndMonth(String name, Integer monthStart, Integer monthEnd, int pageNo, int pageSize) {
		List<Bson> pipeline = new ArrayList<>();

		Bson limit = Aggregates.limit(pageSize);
		Bson skip = Aggregates.skip((pageNo - 1) * pageSize);
		Bson project = new Document("$project",
				new Document("fullname", new Document("$concat", Arrays.asList("$firstName", " ", "$lastName")))
						.append("month", new Document("$month", "$dayOfBirth")));

		Bson match = new Document("$match",
				new Document("fullname", new Document("$regex", Pattern.compile(name + "(?i)"))).append("month",
						new Document("$gte", monthStart).append("$lte", monthEnd)));

		pipeline.add(project);
		pipeline.add(match);
		pipeline.add(limit);
		pipeline.add(skip);
		List<Document> docs = new ArrayList<>();
		mongoCollection.aggregate(pipeline, Document.class).into(docs);
		return docs;
		
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
		List<Bson> pipeline = new ArrayList<>();
		Bson limit = Aggregates.limit(pageSize);
		Bson skip = Aggregates.skip((pageNo - 1) * pageSize);
		Bson match = new Document("$match", new Document("languages.language", "vi/vn").append("sex", 0));

		Bson project = new Document("$project",
				new Document("fullName", new Document("$concat", Arrays.asList("$firstName", " ", "$lastName")))
						.append("phones", 1L)
						.append("languages",
								new Document("$filter",
										new Document("input", "$languages").append("as", "languageVN").append("cond",
												new Document("$eq", Arrays.asList("$$languageVN.language", "vi/vn")))))
						.append("emails",
								new Document("$filter",
										new Document("input", "$emails").append("as", "emails").append("cond",
												new Document("$regexMatch", new Document("input", "$$emails.email")
														.append("regex", Pattern.compile("@gmail.com(?i)")))))));
		pipeline.add(match);
		pipeline.add(project);
		pipeline.add(limit);
		pipeline.add(skip);

		
		List<Document> docs = new ArrayList<>();
		mongoCollection.aggregate(pipeline, Document.class).into(docs);
		return docs;
	}

	// .13
	public List<Document> getPersonsAndCountByCond(int pageNo, int pageSize) {
		List<Bson> pipeline = new ArrayList<>();

		// face 1
		Bson limit = Aggregates.limit(pageSize);
		Bson skip = Aggregates.skip((pageNo - 1) * pageSize);

		Bson filterCond = new Document("$project",
				new Document("fullName", new Document("$concat", Arrays.asList("$firstName", " ", "$lastName")))
						.append("phones", 1L)
						.append("languages",
								new Document("$filter",
										new Document("input", "$languages").append("as", "languageVN").append("cond",
												new Document("$eq", Arrays.asList("$$languageVN.language", "vi/vn")))))
						.append("emails",
								new Document("$filter",
										new Document("input", "$emails").append("as", "emails").append("cond",
												new Document("$regexMatch", new Document("input", "$$emails.email")
														.append("regex", Pattern.compile("@gmail.com(?i)")))))));

		List<Bson> personBySexAndLans = new ArrayList<>();
		personBySexAndLans.add(filterCond);
		personBySexAndLans.add(limit);
		personBySexAndLans.add(skip);

		// faced 2
		List<Bson> countRecordCorrect = new ArrayList<>();

		Bson match = new Document("$match", new Document("languages.language", "vi/vn").append("sex", 0));
		Bson count = Aggregates.count("totalRecordCorrect");
		countRecordCorrect.add(match);
		countRecordCorrect.add(count);

		// face {}
		Bson faced = Aggregates.facet(new Facet("personBySexAndLans", personBySexAndLans),
				new Facet("countRecordCorrect", countRecordCorrect),
				new Facet("countRecord", Aggregates.count("totalRecord")));

		pipeline.add(faced);

		
		List<Document> docs = new ArrayList<>();
		mongoCollection.aggregate(pipeline, Document.class).into(docs);
		return docs;
	}

	
	// 5.update 1 CMND của 1 person thành deactive (ko còn sử dụng nữa)
	public UpdateResult updateInfPersonToDeactive(String id, Info inf) {
		Bson filter = Filters.eq("_id", new ObjectId(id));
		Bson filter2 = Filters.eq("infos.numberId", inf.getNumberId());
		Bson filterBoth = Filters.and(filter, filter2);

		Bson update = Updates.set("infos.$[elem].status", 0);
		UpdateOptions uo = new UpdateOptions()
				.arrayFilters(Arrays.asList(new Document("elem.type", 1).append("elem.numberId", inf.getNumberId())));
		UpdateResult ur = mongoCollection.updateOne(filterBoth, update, uo);
		return ur;
	}

	// 14
		// Viết query update thông tin của 1 person, gồm:
		// sex
		// infor: thêm mới 1 infor
		// langs: xoá 1 lang
	public UpdateResult updateMultiField(PersonDTO pDTO, String id) {
		Bson filter = Filters.eq("_id", new ObjectId(id));

		Bson updateSet = Updates.set("sex", pDTO.getSex());
		Bson updateAddToSet = Updates.addToSet("infos", pDTO.getInfos().get(0));
		Bson updatePullLang = Updates.pull("languages",
				new Document("language", pDTO.getLanguages().get(0).getLanguage())

		);
		Bson updatePullPhones = Updates.pull("phones", new Document("number", pDTO.getPhones().get(0).getNumber()));
		Bson updates = Updates.combine(updateSet, updateAddToSet, updatePullLang, updatePullPhones);

		UpdateOptions uOption = new UpdateOptions().upsert(true);

		UpdateResult ur = mongoCollection.updateOne(filter, updates, uOption);
		return ur;
	};
	
	// getPerson by Id and lang
	public Person getPersonByIdAndLang(String id, Language lang) {
		Bson filter = Filters.eq("_id", new ObjectId(id));
		Bson filter2 = Filters.eq("languages.language", lang.getLanguage());
		Bson filterBoth = Filters.and(filter, filter2);

		return mongoCollection.find(filterBoth).first();
		
		
	}

	// getPerson by Id and info
	public Person getPersonByIdAndInfo(String id, Info inf) {
		Bson filter = Filters.eq("_id", new ObjectId(id));
		Bson filter2 = Filters.eq("infos.numberId", inf.getNumberId());
		Bson filterBoth = Filters.and(filter, filter2);
		return mongoCollection.find(filterBoth).first();
	}


	
}
