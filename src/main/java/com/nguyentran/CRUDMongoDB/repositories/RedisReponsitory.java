package com.nguyentran.CRUDMongoDB.repositories;


import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisReponsitory {

//	public static final String HASH_KEY_NAME = "TOKEN";
	@Autowired
	private RedisTemplate redisTemplate;

	public void saveAccessToken(String id,String token) {		
		redisTemplate.opsForValue().set("access_token"+id, token, 1, TimeUnit.DAYS);
		
	}
	public void saveRefreshToken(String id,String token) {	
		redisTemplate.opsForValue().set("refresh_token"+id, token, 3, TimeUnit.DAYS);
		
	}
	public String findItemById(String id) {		
		return  (String) redisTemplate.opsForValue().get(id);
	}

	public void deleteTokenById(int id) {		
		redisTemplate.opsForValue().getAndDelete(id);	
	}
}
