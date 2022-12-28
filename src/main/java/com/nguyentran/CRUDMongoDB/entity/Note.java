package com.nguyentran.CRUDMongoDB.entity;

import java.util.Map;

import lombok.Data;

@Data
public class Note {
	private String subject;
	private String content;
	private String token;
	private Map<String, String> data;
	private String toppic;
}
