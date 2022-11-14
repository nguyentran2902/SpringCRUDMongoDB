package com.nguyentran.CRUDMongoDB.models;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.nguyentran.CRUDMongoDB.models.ProductObject.ProdInf;

import lombok.Data;
import lombok.Generated;

@Data
public class Product {
	@Id
	private String _id;
	private String prodCode;
	private List<ProdInf> prodInf;
	
	private Date dateCreate;
	private String creator;
	
	private Date dateUpdate;
	private String updater;
	
	private int del_f;
}
