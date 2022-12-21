package com.nguyentran.CRUDMongoDB.controllers;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nguyentran.CRUDMongoDB.DTOs.ProductDTO;
import com.nguyentran.CRUDMongoDB.services.ProductService;

@RestController
@RequestMapping("/admin/product")
public class ProductController {

	@Autowired
	private ProductService productService;

	// get all
	@GetMapping("/getAllProduct")
	public ResponseEntity<?> getAllPersons(
			@RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "6", required = false) int pageSize) {

		try {
			List<ProductDTO> productDTOs = productService.getAllProducts(pageNo, pageSize);

			if (productDTOs != null && productDTOs.size() > 0) {
				return ResponseEntity.ok(productDTOs);
			}
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error: Not found any person in data");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

	//// * Viết query tìm kiếm những product có tên là "productNameEN" + ngôn ngữ là
	//// "en"
	@GetMapping("/getProductByNameAndLang")
	public ResponseEntity<?> getProductByNameAndLang(
			@RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "6", required = false) int pageSize,
			@RequestParam(required = false) String name, @RequestParam(required = false) String lang) {

		if (name == null || name.trim().isEmpty() || lang == null || lang.trim().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invavid param");
		}
		try {
			List<ProductDTO> productDTOs = productService.getProductByNameAndLang(name, lang, pageNo, pageSize);

			if (productDTOs != null && productDTOs.size() > 0) {
				return ResponseEntity.ok(productDTOs);
			}
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error: Not found any person in data");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

	// * Viết query get toàn bộ product, kết quả trả về gồm id/code/prodInf,
	// với prodInf được filter theo ngôn ngữ (vn),
	// nếu product ko có ngôn ngữ "vi" thì trả về ngôn ngữ đầu tiên được tạo trong
	// prodInf

	@GetMapping("/getProductByLangFilter")
	public ResponseEntity<?> getProductByLangFilter(
			@RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "6", required = false) int pageSize,
			@RequestParam String lang) {

		if (lang == null || lang.trim().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invavid param");
		}
		try {
			List<Document> docs = productService.getProductByLangFilter(lang, pageNo, pageSize);

			if (docs != null && docs.size() > 0) {
				return ResponseEntity.ok(docs);
			}
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error: Not found any person in data");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}

	}

}
