package com.nguyentran.CRUDMongoDB.services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nguyentran.CRUDMongoDB.repositories.TourRepository;

@Service
public class TourService {

	@Autowired
	private TourRepository tourRepository;

	public List<Document> getInfosTour(Integer numSlot, String lang, String date, String currency, int pageNo,
			int pageSize) {

		// 1.Get list filter Tour
		List<Document> tourDocs = tourRepository.getInfosTour(numSlot, lang);

		if (tourDocs == null || tourDocs.size() == 0) {
			return null;
		}

		// 1.2 Get list tourId
		List<String> listTourId = new ArrayList<String>();
		for (Document d : tourDocs) {
			listTourId.add(d.get("_id").toString());

		}

		// 2.1 Get list filter dateOpen
		List<Document> dateOpenDocs = tourRepository.getListDateOpenFilter(date, listTourId);
		if (dateOpenDocs == null || dateOpenDocs.size() == 0) {
			return null;
		}
		// 2.2 Get list dateOpenId
		List<String> listDateOpenId = new ArrayList<String>();
		for (Document d : dateOpenDocs) {
			listDateOpenId.add(d.get("tourId").toString());

		}

		// 3. get list filter priceTour
		List<Document> priceTourDocs = tourRepository.getListPriceTourFilter(date, listDateOpenId, pageNo, pageSize);

		if (priceTourDocs == null || priceTourDocs.size() == 0) {
			return null;
		}

		

		// put list Tour to hashmap
		HashMap<String, Document> tourMap = new HashMap<String, Document>();
		for (Document docTour : tourDocs) {
			tourMap.put(docTour.get("_id").toString(), docTour);
		}

		// 4.put price and curency to list filter priceTour
		for (Document dPriceTour : priceTourDocs) {

			// get document from tourPrice
			Document docTour = tourMap.get(dPriceTour.get("tourId").toString());
			
			if(docTour!=null) {
				dPriceTour.put("infos", docTour.get("infos"));
				dPriceTour.put("slot", docTour.get("slot"));
			}
			dPriceTour.put("slotInput", numSlot);
			dPriceTour.put("dateOpen", date);
			if(dPriceTour.get("currency")!=null) {
				dPriceTour.put("availablePrice",
						convertCurency(numSlot, currency, dPriceTour.get("currency"), dPriceTour.get("price")));
			} else dPriceTour.put("availablePrice",
					"not found");
			
		}

		return priceTourDocs;

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
