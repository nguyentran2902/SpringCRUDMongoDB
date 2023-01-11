package com.nguyentran.CRUDMongoDB;

import java.io.IOException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

@SpringBootApplication
@EnableMongoRepositories
@EnableCaching
public class CrudMongoDbApplication extends AbstractMongoClientConfiguration {

	@Value("${spring.data.mongodb.host}")
	private String mongoHost;

	@Value("${spring.data.mongodb.port}")
	private String mongopPort;

	@Value("${spring.data.mongodb.database}")
	private String mongoDB;

	public static void main(String[] args) {
	
		SpringApplication.run(CrudMongoDbApplication.class, args);
	}

	@Bean
	public  MongoClient mongoClient() {
		return MongoClients.create();
	}
	
	@Bean
	public  MongoDatabase mongoDatabase() {
		return mongoClient().getDatabase(mongoDB);
	}
	

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setSkipNullEnabled(true);
		return modelMapper;
	}
	
	
	
//	@Bean
//	public FirebaseMessaging firebaseMessaging() throws IOException {
//	    GoogleCredentials googleCredentials = GoogleCredentials
//	            .fromStream(new ClassPathResource("firebase-service-account.json").getInputStream());
//	    FirebaseOptions firebaseOptions = FirebaseOptions
//	            .builder()
//	            .setCredentials(googleCredentials)
//	            .build();
//	    
//	    FirebaseApp app = null ;
//	    if (FirebaseApp.getApps().isEmpty()) {
//	    	 app = FirebaseApp.initializeApp(firebaseOptions);
//        }
//	    
//	    return FirebaseMessaging.getInstance(app);
//	}
	
//	@Bean
//	public FirebaseApp firebaseApp(GoogleCredentials credentials) {
//	    FirebaseOptions options = FirebaseOptions.builder()
//	      .setCredentials(credentials)
//	      .build();
//
//	    return FirebaseApp.initializeApp(options);
//	}

	

	
	

//	@Bean
//	public MappingMongoConverter mappingMongoConverter() {
//
//		DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDatabaseFactory());
//		MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
//		converter.setTypeMapper(new DefaultMongoTypeMapper(null));
//
//		return converter;
//	}

	@Override
	protected String getDatabaseName() {
		return mongoDB;
	}

}
