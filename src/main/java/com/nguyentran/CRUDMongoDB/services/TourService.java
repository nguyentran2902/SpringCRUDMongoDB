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
		
		// 1.1 Get list filter dateOpen
		List<Document> dateOpenDocs = tourRepository.getListDateOpenFilter(date);
		if (dateOpenDocs == null || dateOpenDocs.size() == 0) {
			return null;
		}

		// 1.2 Get listId filter dateOpen
		List<String> listIdDateOpen = new ArrayList<String>();
		for (Document d : dateOpenDocs) {
			listIdDateOpen.add(d.get("tourId").toString());

		}

		// 2.1 get list filter priceTour
		List<Document> priceTourDocs = tourRepository.getListDateOpenFilter(date,listIdDateOpen);
		

		if (priceTourDocs == null || priceTourDocs.size() == 0) {
			return null;
		}
		// 2.2 Get listId filter priceTour
		List<ObjectId> listIdPriceTour = new ArrayList<ObjectId>();
		for (Document d : priceTourDocs) {
			listIdPriceTour.add(new ObjectId((String) d.get("tourId")));
		}

		// 3.Get list filter Tour
		List<Document> tourDocs = tourRepository.getInfosTour(listIdPriceTour, numSlot,  lang,  pageNo,
				 pageSize);
		

		if (tourDocs == null || tourDocs.size() == 0) {
			return null;
		}
		
		//put list priceTour to hashmap
		HashMap<String, Document> priceTourMap = new HashMap<String, Document>();
		for (Document dPriceTour : priceTourDocs) {
			priceTourMap.put( dPriceTour.get("tourId").toString(), dPriceTour);
		}

		// 4.put price and curency to list filter tour
		for (Document dTour : tourDocs) {
			
			//get document from tourPrice
			Document docPriceTour = priceTourMap.get(dTour.get("_id").toString());
			
					dTour.replace("_id", docPriceTour.get("tourId"));
					dTour.put("dateOpen", date);
					dTour.put("slotInput", numSlot);
					dTour.put("currency", currency);
					dTour.put("availablePrice",
							convertCurency(numSlot, currency, docPriceTour.get("currency"), docPriceTour.get("price")));
		}
		
		return tourDocs;

	}

	// convert price by currency
	private String convertCurency(Integer numSlot, String currencyInput, Object currency, Object price) {

		Double priceConvert = (double) 0L;
		Double rateVND = 25000.d;
		Double rateUSD = 0.00004;

		if (currencyInput.equalsIgnoreCase(currency.toString())) {
			priceConvert = (Double) price;
		} else if (currencyInput.equalsIgnoreCase("vnd")) {
			priceConvert = (Double) price * rateVND;
		} else if (currencyInput.equalsIgnoreCase("usd")) {
			priceConvert = (Double) price * rateUSD;
			;
		}

		DecimalFormat df = new DecimalFormat("#.###");
		return df.format(numSlot * priceConvert);
	}

}
