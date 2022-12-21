package com.nguyentran.CRUDMongoDB.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.nguyentran.CRUDMongoDB.entity.ProductObject.ProdInf;

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
