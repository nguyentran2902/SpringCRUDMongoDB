package com.nguyentran.CRUDMongoDB.models.CompanyObject;

import lombok.Data;

@Data
public class PaymentCurrency {
	private int type;
	private String paymentCurrency;
}
