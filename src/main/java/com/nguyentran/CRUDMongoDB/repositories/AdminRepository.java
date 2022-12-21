package com.nguyentran.CRUDMongoDB.repositories;

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
import com.mongodb.client.result.InsertOneResult;
import com.nguyentran.CRUDMongoDB.entity.Admin;

@Repository
public class AdminRepository {
	@Autowired
	private MongoDatabase mongoDatabase;
	

	private MongoCollection<Admin> adminCollection;

	@Autowired
	public void AdminRepository() {
		CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries
						.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		adminCollection = mongoDatabase.getCollection("admin", Admin.class).withCodecRegistry(pojoCodecRegistry);
		;
	}

	public Admin loadAdminByUsername(String username) {
		Bson match = new Document("username", username);
		Admin admin = adminCollection.find(match).first();
		return admin;
	}

	public Admin getUserById(String id) {
		Bson match = new Document("_id", new ObjectId(id));
		Admin admin = adminCollection.find(match).first();
		return admin;
	}

	public InsertOneResult saveAdmin(Admin admin) {
		
		InsertOneResult ior = adminCollection.insertOne(admin);
		return ior;
	}

}
