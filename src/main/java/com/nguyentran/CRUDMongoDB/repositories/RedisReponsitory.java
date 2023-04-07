package com.nguyentran.CRUDMongoDB.repositories;


import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisReponsitory {

//	public static final String HASH_KEY_NAME = "TOKEN";
	@Autowired
	private RedisTemplate<String, HashMap<String, HashMap<String, Object>>> redisTemplate;

	public void saveToken(String id, HashMap<String, HashMap<String, Object>> token) {	
		 redisTemplate.opsForValue().set(id, token, 3, TimeUnit.DAYS);
		
	}
	public  HashMap<String, HashMap<String, Object>> findItemById(String id) {		
		return redisTemplate.opsForValue().get(id);
	}

	public Boolean deleteTokenById(String id) {		
		return redisTemplate.delete(id);
	}
}
