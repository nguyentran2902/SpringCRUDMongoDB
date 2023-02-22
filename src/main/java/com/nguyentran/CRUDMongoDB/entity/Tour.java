package com.nguyentran.CRUDMongoDB.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nguyentran.CRUDMongoDB.entity.TourObject.Info;

import lombok.Data;

@Data
@Document(collection = "tour")
public class Tour {
	
	@Id
	private ObjectId _id;
	private List<Info> infos;
	private Integer slot;

	
}
