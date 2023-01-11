package com.nguyentran.CRUDMongoDB.redisConfig;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
class RedisConfig {

	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {
		LettuceConnectionFactory lcf = new LettuceConnectionFactory();
		lcf.setHostName("localhost");
		lcf.setPort(6379);
		lcf.afterPropertiesSet();
		return lcf;

	}

//	@Bean
//	public JedisConnectionFactory redisConnectionFactory() {
//	  JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
//
//	  // Defaults
//	  redisConnectionFactory.setHostName("127.0.0.1");
//	  redisConnectionFactory.setPort(6379);
//	  return redisConnectionFactory;
//	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		// tạo ra một RedisTemplate
		// Với Key là Object
		// Value là Object
		// RedisTemplate giúp chúng ta thao tác với Redis
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory());

		template.setKeySerializer(new StringRedisSerializer());
	    template.setHashKeySerializer(new StringRedisSerializer());
	    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//	    template.setHashValueSerializer(new LdapFailAwareRedisObjectSerializer());
//	    template.setEnableDefaultSerializer(true);
		template.afterPropertiesSet();
		return template;
	}

	@Bean
	@Primary
	public RedisCacheManager cacheManager10m() {

		return RedisCacheManager.builder(redisConnectionFactory()) //
				.cacheDefaults(redisCacheConfiguration(60000)) //
				.build();
	}
	
	@Bean
	public RedisCacheManager cacheManager1h() {
		return RedisCacheManager.builder(redisConnectionFactory()) //
				.cacheDefaults(redisCacheConfiguration(3600000)) //
				.build();
	}
	
	
	public RedisCacheConfiguration redisCacheConfiguration(int dur) {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig() //
//				.prefixCacheNameWith(this.getClass().getPackageName()) 
				.disableCachingNullValues()
				.entryTtl(Duration.ofMillis(dur))
				.computePrefixWith(CacheKeyPrefix.simple())
	            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
	            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
	RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory())
	            .build();//

		return config;
	}


//	public class LdapFailAwareRedisObjectSerializer implements RedisSerializer<Object> {
//
//		private Converter<Object, byte[]> serializer = new SerializingConverter();
//		private Converter<byte[], Object> deserializer = new DeserializingConverter();
//
//		final byte[] EMPTY_ARRAY = new byte[0];
//
//		public Object deserialize(byte[] bytes) {
//			if (isEmpty(bytes)) {
//				return null;
//			}
//
//			try {
//				return deserializer.convert(bytes);
//			} catch (Exception ex) {
//				throw new SerializationException("Cannot deserialize", ex);
//			}
//		}
//
//		public byte[] serialize(Object object) {
//			if (object == null) {
//				return EMPTY_ARRAY;
//			}
//
//			try {
//				return serializer.convert(object);
//			} catch (Exception ex) {
//				return EMPTY_ARRAY;
//				// TODO add logic here to only return EMPTY_ARRAY for known conditions
//				// else throw the SerializationException
//				// throw new SerializationException("Cannot serialize", ex);
//			}
//		}
//
//		private boolean isEmpty(byte[] data) {
//			return (data == null || data.length == 0);
//		}
//	}
}