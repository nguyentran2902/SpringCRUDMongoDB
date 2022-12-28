package com.nguyentran.CRUDMongoDB.services;


import java.util.List;

import org.bson.Document;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.nguyentran.CRUDMongoDB.DTOs.ProductDTO;
import com.nguyentran.CRUDMongoDB.entity.Product;
import com.nguyentran.CRUDMongoDB.repositories.ProductRepository;

@Service
public class ProductService {


	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ModelMapper modelMapper;

	

	// get all
	public List<ProductDTO> getAllProducts(int pageNo, int pageSize) {
		
		List<ProductDTO> productDTO = productRepository.getAllProducts(pageNo,pageSize);

		return productDTO;
	}

	// get product by id
	public ProductDTO getProductById(String id) {

		Product p = productRepository.getProductById(id);
		return modelMapper.map(p, ProductDTO.class);
	}

	////* Viết query tìm kiếm những product có tên là "productNameEN" + ngôn ngữ là
	//// "en"
	public List<ProductDTO> getProductByNameAndLang(String name, String lang, int pageNo, int pageSize) {
	
		
		List<ProductDTO> productDTOs = productRepository.getProductByNameAndLang(name,lang,pageNo,pageSize);
		

		return productDTOs;
	}

	// * Viết query get toàn bộ product, kết quả trả về gồm id/code/prodInf,
	// với prodInf được filter theo ngôn ngữ (vn),
	// nếu product ko có ngôn ngữ "vi" thì trả về ngôn ngữ đầu tiên được tạo trong
	// prodInf
	public List<Document> getProductByLangFilter(String lang, int pageNo, int pageSize) {
		
		List<Document> docs = productRepository.getProductByLangFilter(lang,pageNo,pageSize);

		return docs;
	}

}
