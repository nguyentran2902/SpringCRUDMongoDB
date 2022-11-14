package com.nguyentran.CRUDMongoDB.services.Impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.nguyentran.CRUDMongoDB.DTOs.CompanyDTO;
import com.nguyentran.CRUDMongoDB.DTOs.ProductDTO;
import com.nguyentran.CRUDMongoDB.models.Company;
import com.nguyentran.CRUDMongoDB.models.Product;
import com.nguyentran.CRUDMongoDB.services.ProductService;

@Service
public class ProductSericeImpl implements ProductService {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MongoDatabase mongoDatabase;

	@Autowired
	private ModelMapper modelMapper;

	private MongoCollection<Product> productCollection;

	@Autowired
	public void ProductSericeImpl() {
		CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry(), org.bson.codecs.configuration.CodecRegistries
						.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		productCollection = mongoDatabase.getCollection("product", Product.class).withCodecRegistry(pojoCodecRegistry);
		;
	}

	// get all
	@Override
	public List<ProductDTO> getAllProducts(int pageNo, int pageSize) {
		List<ProductDTO> productDTO = new ArrayList();

		List<Bson> pipeline = new ArrayList();
		pipeline.add(Aggregates.limit(pageSize));
		pipeline.add(Aggregates.skip((pageNo - 1) * pageSize));
		productCollection.aggregate(pipeline, ProductDTO.class).into(productDTO);

		return productDTO;
	}

	// get product by id
	@Override
	public ProductDTO getProductById(String id) {
		Bson match = Filters.eq("_id", new ObjectId(id));
		Product p = productCollection.find(match).first();
		return modelMapper.map(p, ProductDTO.class);
	}

	//// * Viết query tìm kiếm những product có tên là "productNameEN" + ngôn ngữ là
	//// "en"
	@Override
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
	@Override
	public List<Document> getProductByLangFilter(String lang, int pageNo, int pageSize) {
		List<Bson> pipeline = new ArrayList<>();

		Bson project = new Document("$project",
				new Document("prodCode", 1).append("prodInf",
						new Document("$cond", Arrays.asList(new Document("$eq", Arrays.asList("$prodInf.lang", lang)),
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
