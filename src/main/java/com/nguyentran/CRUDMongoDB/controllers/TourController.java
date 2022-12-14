package com.nguyentran.CRUDMongoDB.controllers;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nguyentran.CRUDMongoDB.services.TourService;

@RestController
@RequestMapping("/admin/tour")
public class TourController {

	@Autowired
	private TourService tourService;

	@GetMapping("/getInfosTour")
	public ResponseEntity<?> getInfosTour(
			@RequestParam(value = "numSlot", defaultValue = "1", required = false) Integer numSlot,
			@RequestParam(value = "lang", required = false) String lang,
			@RequestParam(value = "date", required = false) String date,
			@RequestParam(value = "currency", defaultValue = "vnd", required = false) String currency,
			@RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "6", required = false) int pageSize)
			throws ParseException {

		// check input
		if (numSlot == null || numSlot <= 0 ) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error: Invalid input data: numSlot!");
		}
		HashMap<String, Object> reponse = new HashMap<>();
		try {
			// get infos
			List<Document> infosTour = tourService.getInfosTour(numSlot, lang, date, currency, pageNo, pageSize);
			if (infosTour != null && infosTour.size() > 0) {
				reponse.put("message", "success");
				reponse.put("data", infosTour);
				return ResponseEntity.ok(reponse);
			}
			reponse.put("message", "success");
			reponse.put("data", "Not found any info tour in data");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(reponse);
		} catch (Exception e) {
			reponse.put("message", "error");
			reponse.put("data", "Error: " + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(reponse);
		}

	}

}
