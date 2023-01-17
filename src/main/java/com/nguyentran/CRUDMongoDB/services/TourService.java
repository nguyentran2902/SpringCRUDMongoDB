package com.nguyentran.CRUDMongoDB.services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nguyentran.CRUDMongoDB.exceptionhandler.InternalServerException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.NoContentException;
import com.nguyentran.CRUDMongoDB.repositories.TourRepository;

@Service
public class TourService {

	@Autowired
	private TourRepository tourRepository;

	public List<Document> getInfosTour(Integer numSlot, String lang, String date, String currency, int pageNo,
			int pageSize) {

		try {
			// 1.Get list filter Tour
			List<Document> tourDocs = tourRepository.getInfosTour(numSlot, lang);

			if (tourDocs == null || tourDocs.size() == 0) {
				throw new NoContentException("");
			}

			// 1.2 put list Tour to hashmap
			HashMap<String, Document> tourMaps = new HashMap<String, Document>();
			for (Document tourDoc : tourDocs) {			
				tourMaps.put(tourDoc.get("_id").toString(), tourDoc);
			}

			// 2.1 Get list filter dateOpen
			List<Document> dateOpenDocs = tourRepository.getListDateOpenFilter(date, tourMaps.keySet());
			if (dateOpenDocs == null || dateOpenDocs.size() == 0) {
				throw new NoContentException("");
			}
			// 2.2 Get list dateOpenId
			List<String> listDateOpenId = new ArrayList<String>();
			for (Document d : dateOpenDocs) {
				listDateOpenId.add(d.get("tourId").toString());

			}

			// 3. get list filter priceTour
			List<Document> priceTourDocs = tourRepository.getListPriceTourFilter(date, listDateOpenId, pageNo,
					pageSize);

			if (priceTourDocs == null || priceTourDocs.size() == 0) {
				throw new NoContentException("");
			}

			// 4.put price and currency to list filter priceTour
			for (Document dPriceTour : priceTourDocs) {

				// get document from tour
				Document tourDoc = tourMaps.get(dPriceTour.get("tourId").toString());

				dPriceTour.put("slotInput", numSlot);
				dPriceTour.put("dateOpen", date);

				if (tourDoc != null) {
					dPriceTour.put("infos", tourDoc.get("infos"));
					dPriceTour.put("slot", tourDoc.get("slot"));
				}

				if (dPriceTour.get("currency") != null && dPriceTour.get("price") != null) {
					dPriceTour.put("availablePrice",
							convertCurency(numSlot, currency, dPriceTour.get("currency"), dPriceTour.get("price")));
				} else
					dPriceTour.put("availablePrice", "not found");

			}

			return priceTourDocs;
		} catch (Exception e) {
			throw new InternalServerException(e.getMessage());
		}

	}

	// convert price by currency
	private String convertCurency(Integer numSlot, String currencyInput, Object currency, Object price) {

		Double rateVND = 25000.d;
		Double rateUSD = 0.00004;
		Double priceConvert = (double) 0L;

		if (currencyInput.equalsIgnoreCase(currency.toString())) {
			priceConvert = (Double) price;
		} else if (currencyInput.equalsIgnoreCase("vnd")) {
			priceConvert = (Double) price * rateVND;
		} else if (currencyInput.equalsIgnoreCase("usd")) {
			priceConvert = (Double) price * rateUSD;
		}

		DecimalFormat df = new DecimalFormat("#.###");
		return df.format(numSlot * priceConvert);
	}

	

}
