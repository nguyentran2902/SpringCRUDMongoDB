package com.nguyentran.CRUDMongoDB.services;

import java.util.List;

import org.bson.Document;
import org.springframework.http.ResponseEntity;

import com.nguyentran.CRUDMongoDB.DTOs.ProductDTO;


public interface ProductService {

	List<ProductDTO> getAllProducts(int pageNo, int pageSize);
	
	ProductDTO getProductById(String id);

	List<ProductDTO> getProductByNameAndLang(String name, String lang, int pageNo, int pageSize);

	List<Document> getProductByLangFilter(String lang, int pageNo, int pageSize);

}
