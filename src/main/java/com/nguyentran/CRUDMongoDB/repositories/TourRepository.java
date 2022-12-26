package com.nguyentran.CRUDMongoDB.repositories;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	// 1. Get listId filter dateOpen
	public List<Document> getListDateOpenFilter(String date) {
		List<Bson> DateOpenPipeline = new ArrayList<Bson>();
		Bson match = new Document("$match", new Document("dateAvailable", date).append("status", 1));
		DateOpenPipeline.add(match);

		List<Document> dateOpenDocs = new ArrayList<>();
		dateOpenCollection.aggregate(DateOpenPipeline, Document.class).into(dateOpenDocs);
		return dateOpenDocs;
	}

	// 2.get list filter priceTour
	public List<Document> getListDateOpenFilter(String date, List<String> listIdDateOpen) {
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

		priceTourPipeline.add(match);
		List<Document> priceTourDocs = new ArrayList<>();
		priceTourCollection.aggregate(priceTourPipeline, Document.class).into(priceTourDocs);
		return priceTourDocs;
	}

	// 3.Get list filter Tour
	public List<Document> getInfosTour(List<ObjectId> listIdPriceTour, Integer numSlot, String lang, int pageNo,
			int pageSize) {
		List<Bson> tourPipeline = new ArrayList<Bson>();
		Bson match = new Document("$match", new Document("slot", new Document("$gte", numSlot)).append("_id",
				new Document("$in", listIdPriceTour)));
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
		tourPipeline.add(Aggregates.limit(pageSize));
		tourPipeline.add(Aggregates.skip((pageNo - 1) * pageSize));
		List<Document> tourDocs = new ArrayList<>();
		tourCollection.aggregate(tourPipeline, Document.class).into(tourDocs);
		return tourDocs;
	}

}
