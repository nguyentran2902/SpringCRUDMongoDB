package com.nguyentran.CRUDMongoDB.DTOs;

import java.util.Date;
import java.util.List;

import com.nguyentran.CRUDMongoDB.models.ProductObject.ProdInf;

import lombok.Data;

@Data
public class ProductDTO {
	private String _id;
	private String prodCode;
	private List<ProdInf> prodInf;
	
	private Date dateCreate;
	private String creator;
	
	private Date dateUpdate;
	private String updater;
	
	private int del_f;
}
