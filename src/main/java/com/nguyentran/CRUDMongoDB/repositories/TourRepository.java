package com.nguyentran.CRUDMongoDB.repositories;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.nguyentran.CRUDMongoDB.entity.DateOpen;
import com.nguyentran.CRUDMongoDB.entity.PriceTour;
import com.nguyentran.CRUDMongoDB.entity.Tour;

@Repository
public class TourRepository {
	@Autowired
	private MongoDatabase mongoDatabase;

	private MongoCollection<Tour> tourCollection;
	private MongoCollection<PriceTour> priceTourCollection;
	private MongoCollection<DateOpen> dateOpenCollection;

	@Autowired
	public void TourRepository() {
		CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries
						.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		tourCollection = mongoDatabase.getCollection("tour", Tour.class).withCodecRegistry(pojoCodecRegistry);
		priceTourCollection = mongoDatabase.getCollection("priceTour", PriceTour.class)
				.withCodecRegistry(pojoCodecRegistry);
		dateOpenCollection = mongoDatabase.getCollection("dateOpen", DateOpen.class)
				.withCodecRegistry(pojoCodecRegistry);
		;

	}

	//  Get list dateOpen filter by date and listTourId
	public List<Document> getListDateOpenFilter(String date,Set<String> listId) {
		List<Bson> DateOpenPipeline = new ArrayList<Bson>();
		Bson match = new Document("$match", new Document("dateAvailable", date)
				.append("status", 1)
				.append("tourId", new Document("$in", listId)));
		DateOpenPipeline.add(match);

		List<Document> dateOpenDocs = new ArrayList<>();
		dateOpenCollection.aggregate(DateOpenPipeline, Document.class).into(dateOpenDocs);
		return dateOpenDocs;
	}

	// get list priceTour filter by date and listIdDateOpen
	public List<Document> getListPriceTourFilter(String date, List<String> listIdDateOpen, int pageNo,
			int pageSize) {
		List<Bson> priceTourPipeline = new ArrayList<Bson>();

//		Bson match = new Document("$match",new Document("dateApplyStart",
//				new Document("$lte",new Document("$dateFromString",new Document("dateString",date)))
//				.append("dateApplyEnd", new Document("$gte",new Document("$dateFromString",new Document("dateString",date))))
//				.append("tourId", new Document("$in",listIdDateOpen))));

		Bson match = new Document("$match",
				new Document("$and",
						Arrays.asList(new Document("dateApplyStart", new Document("$lte", LocalDate.parse(date))),
								new Document("dateApplyEnd", new Document("$gte", LocalDate.parse(date))),
								new Document("tourId", new Document("$in", listIdDateOpen)))));
		
		Bson project = new Document("$project",new Document("tourId",1)
				.append("currency", 1)
				.append("price", 1)
				.append("_id", 0));

		priceTourPipeline.add(match);
		priceTourPipeline.add(project);
		priceTourPipeline.add(Aggregates.limit(pageSize));
		priceTourPipeline.add(Aggregates.skip((pageNo - 1) * pageSize));
		List<Document> priceTourDocs = new ArrayList<>();
		priceTourCollection.aggregate(priceTourPipeline, Document.class).into(priceTourDocs);
		return priceTourDocs;
	}

	// Get list Tour filter by numSlot and lang
	public List<Document> getInfosTour( Integer numSlot, String lang) {
		List<Bson> tourPipeline = new ArrayList<Bson>();
		Bson match = new Document("$match", new Document("slot", new Document("$gte", numSlot)));
		Bson project = new Document("$project",
				new Document("slot", 1).append("infos",
						new Document("$cond",
								Arrays.asList(new Document("$in", Arrays.asList(lang, "$infos.lang")),
										new Document("$filter",
												new Document("input", "$infos").append("as", "infosMap").append("cond",
														new Document("$eq", Arrays.asList("$$infosMap.lang", lang)))),
										new Document("$first", "$infos")))));

		tourPipeline.add(match);
		tourPipeline.add(project);
		List<Document> tourDocs = new ArrayList<>();
		tourCollection.aggregate(tourPipeline, Document.class).into(tourDocs);
		return tourDocs;
	}

}
