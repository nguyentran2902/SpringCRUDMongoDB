package com.nguyentran.CRUDMongoDB;

import org.bson.Document;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@SpringBootApplication
@EnableMongoRepositories
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
