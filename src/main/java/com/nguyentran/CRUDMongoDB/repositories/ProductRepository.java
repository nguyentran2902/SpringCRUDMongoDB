package com.nguyentran.CRUDMongoDB.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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
import com.mongodb.client.model.Filters;
import com.nguyentran.CRUDMongoDB.DTOs.ProductDTO;
import com.nguyentran.CRUDMongoDB.entity.Product;

@Repository
public class ProductRepository {

	@Autowired
	private MongoDatabase mongoDatabase;


	private MongoCollection<Product> productCollection;

	@Autowired
	public void ProductRepository() {
		CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries
						.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		productCollection = mongoDatabase.getCollection("product", Product.class).withCodecRegistry(pojoCodecRegistry);
		;
	}

	public List<ProductDTO> getAllProducts(int pageNo, int pageSize) {
		List<ProductDTO> productDTO = new ArrayList<ProductDTO>();
		List<Bson> pipeline = new ArrayList<Bson>();
		pipeline.add(Aggregates.limit(pageSize));
		pipeline.add(Aggregates.skip((pageNo - 1) * pageSize));
		productCollection.aggregate(pipeline, ProductDTO.class).into(productDTO);
		return productDTO;
	}

	public Product getProductById(String id) {
		Bson match = Filters.eq("_id", new ObjectId(id));
		Product p = productCollection.find(match).first();

		return p;
	}

////* Viết query tìm kiếm những product có tên là "productNameEN" + ngôn ngữ là
	//// "en"
	public List<ProductDTO> getProductByNameAndLang(String name, String lang, int pageNo, int pageSize) {
		List<Bson> pipeline = new ArrayList<>();

		Bson match = new Document("$match",
				new Document("prodInf",
						new Document("$elemMatch",
								new Document("productName", new Document("$regex", Pattern.compile(name + "(?i)")))
										.append("lang", lang))));

		pipeline.add(match);
		pipeline.add(Aggregates.limit(pageSize));
		pipeline.add(Aggregates.skip((pageNo - 1) * pageSize));
		List<ProductDTO> productDTOs = new ArrayList<>();
		productCollection.aggregate(pipeline, ProductDTO.class).into(productDTOs);
		return productDTOs;
	}

	// * Viết query get toàn bộ product, kết quả trả về gồm id/code/prodInf,
		// với prodInf được filter theo ngôn ngữ (vn),
		// nếu product ko có ngôn ngữ "vi" thì trả về ngôn ngữ đầu tiên được tạo trong
		// prodInf
	public List<Document> getProductByLangFilter(String lang, int pageNo, int pageSize) {
		List<Bson> pipeline = new ArrayList<>();

		Bson project = new Document("$project",
				new Document("prodCode", 1).append("prodInf",
						new Document("$cond", Arrays.asList(new Document("$in", Arrays.asList( lang,"$prodInf.lang")),
								new Document("$filter",
										new Document("input", "$prodInf").append("as", "prodInfMap").append("cond",
												new Document("$eq", Arrays.asList("$$prodInfMap.lang", lang)))),
								new Document("$first", "$prodInf")))));

		pipeline.add(project);
		pipeline.add(Aggregates.limit(pageSize));
		pipeline.add(Aggregates.skip((pageNo - 1) * pageSize));
		
		List<Document> docs = new ArrayList<>();
		productCollection.aggregate(pipeline, Document.class).into(docs);
		return docs;
	}

}
