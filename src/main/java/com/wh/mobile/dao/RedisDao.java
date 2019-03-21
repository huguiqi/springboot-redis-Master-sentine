package com.wh.mobile.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;



@Repository
public class RedisDao <T>{

    @Autowired
    private RedisTemplate redisTemplate;

    // ----------------------------------------Object----------------------------------------
    /**
     * 设置对象
     *
     * @param key
     * @param object
     * @param timeout
     * @param clazz
     * @return
     * @throws Exception
     */
    public boolean addObject(final String key, final String object, final Long timeout, Class<?> clazz) {
        redisTemplate.opsForValue().set(key, object,timeout, TimeUnit.SECONDS);
        return true;
    }

    /**
     * 设置对象
     *
     * @param key
     * @param object
     * @param timeout
     * @param clazz
     * @return
     * @throws Exception
     */
    public boolean deleteAndAddObject(final String key, final String object, final Long timeout, Class<?> clazz) {
        this.delete(key);
        redisTemplate.opsForValue().set(key, object);
        return true;
    }

    /**
     * 获得对象
     *
     * @param key
     * @return
     */
    public Object getObject(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // ---------------------------------------String-----------------------------------------

    /**
     * 新增String ----setNX 不存在则增加 ------------------------------
     *
     * @param key
     *            键
     * @param value
     *            值
     * @param timeout
     *            超时(秒)
     * @return true 操作成功，false 已存在值
     */
    public boolean addString(final String key, final String value, final Long timeout) {
        boolean result = exectue(key, value, timeout);
        return result;
    }

    private boolean exectue(String key, String value, Long timeout) {
        return (boolean) redisTemplate.execute((RedisCallback<Boolean>) connection -> {
                Boolean result1 = connection.setNX(key.getBytes(), value.getBytes());
                if (result1 == false)
                    return result1;
                if (timeout != null && timeout > 0)
                    connection.expire(key.getBytes(), timeout);
                return result1;
            });
    }

    /**
     * 删除并新增String
     *
     * @param key
     *            键
     * @param value
     *            值
     * @param timeout
     *            超时(秒)
     * @return true 操作成功，false 已存在值
     */
    public boolean deleteAndAddString(final String key, final String value, final Long timeout) {
        this.delete(key);
        boolean result = exectue(key, value, timeout);
        return result;
    }

    /**
     * 批量新增String---setNx 不存在则增加
     *
     * @param keyValueList
     *            键值对的map
     * @param timeout
     *            超时处理
     * @return
     */
    public boolean addString(final Map<String, String> keyValueList, final Long timeout) {
        boolean result = (boolean) redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            for (String key : keyValueList.keySet()) {
                connection.setNX(key.getBytes(), keyValueList.get(key).getBytes());
                if (timeout != null && timeout > 0)
                    connection.expire(key.getBytes(), timeout);
            }
            return true;
        }, false, true);
        return result;
    }

    /**
     * 通过key获取单个
     *
     * @param key
     * @return
     */
    public String getString(final String key) {
        String value = (String) redisTemplate.execute((RedisCallback<String>) connection -> {
            byte[] result = connection.get(key.getBytes());
            if(result != null && result.length > 0)
                return new String(result);
            return null;
        });
        return value;
    }

    /**
     * 修改 String 重新Set等于Update
     *
     * @param key
     * @param value
     * @return
     */

	public boolean updateString(final String key, final String value) {
		if (getString(key) == null) {
			throw new NullPointerException("数据行不存在, key = " + key);
		}
		boolean result = (boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				connection.set(key.getBytes(), value.getBytes());
				return true;
			}
		});
		return result;
	}

	// ---------------------------------------List-----------------------------------------
	/**
	 * 新增Hash ----setNX 不存在则增加 ------------------------------
	 *
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param timeout
	 *            超时(秒)
	 * @return true 操作成功，false 已存在值
	 */
    public boolean addHash(final String key, final String field, final String value, final Long timeout) {
        boolean result = (boolean) redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            Boolean result1 = connection.hSetNX(key.getBytes(), field.getBytes(), value.getBytes());
            if (result1 == false)
                return result1;
            if (timeout != null && timeout > 0)
                connection.expire(key.getBytes(), timeout);
            return result1;
        });
        return result;
    }

    /**
     * 批量新增Hash ----setNX 不存在则增加 ------------------------------
     *
     * @param key
     *            键
     * @param fieldValueList
     *            值
     * @param timeout
     *            超时(秒)
     * @return true 操作成功，false 已存在值
     */
    public boolean addHash(final String key, final Map<String, String> fieldValueList, final Long timeout) {
        boolean result = (boolean) redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            for (String hashKey : fieldValueList.keySet()) {
                connection.hSetNX(key.getBytes(), hashKey.getBytes(), fieldValueList.get(hashKey).getBytes());
                if (timeout != null && timeout > 0)
                    connection.expire(key.getBytes(), timeout);
            }
            return true;
        });
        return result;
    }

    /**
     * 通过key获取单个
     *
     * @param key
     * @return
     */
    public Object getHashField(final String key, final String field) {
        String value = (String) redisTemplate.execute((RedisCallback<String>) connection -> {
            return new String(connection.hGet(key.getBytes(), field.getBytes()));
        });
        return value;
    }

    /**
     * 通过key获取整个Hash
     *
     * @param key
     * @return
     */
    public Map<byte[], byte[]> getHashAll(final String key, final String field) {
        Map<byte[], byte[]> value = (Map<byte[], byte[]>) redisTemplate.execute((RedisCallback<Map<byte[], byte[]>>) connection -> {
            return connection.hGetAll(key.getBytes());
        });
        return value;
    }

    //---------------------------------------------------通用删除-------------------------------------------------
    /**
     * 删除单个
     *
     * @param key
     */
    public void delete(final String key) {
        redisTemplate.execute((RedisCallback<Long>) connection -> {
            return connection.del(key.getBytes());
        });
    }


    //----------------------------------------------------队列操作--------------------------------------------------
    /**
     * 压栈
     *
     * @param key
     * @param value
     * @return
     */
    public Long push(String key, Object value,long minutes) {
        Long aLong = redisTemplate.opsForList().leftPush(key, JSONObject.toJSONString(value));
        redisTemplate.expire(key,minutes,TimeUnit.MINUTES);
        return aLong;
    }

    /**
     * 出栈
     * 弹出队列首元素
     * @param key
     * @return
     */
    public T popHead(String key,Class<T> tClass) {
        String jsonObj = (String) redisTemplate.opsForList().leftPop(key);
        if (StringUtils.hasText(jsonObj))
            return JSONObject.parseObject(jsonObj,tClass);
        return null;
    }


    /**
     * 出栈
     * 弹出队列尾元素,不阻塞
     * @param key
     * @return
     */
    public T popLast(String key,Class<T> tClass) {
        String jsonObj = (String) redisTemplate.opsForList().rightPop(key);
        return JSONObject.parseObject(jsonObj,tClass);
    }


    /**
     * 栈/队列长
     *
     * @param key
     * @return
     */
    public Long length(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 范围检索
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<T> range(String key, int start, int end,Class<T> clazz) {
        List<String> stringList = redisTemplate.opsForList().range(key, start, end);
        return stringList.stream().map(o -> {
            return JSONObject.parseObject(o, clazz);
        }).collect(Collectors.toList());
    }

    /**
     * 移除
     *
     * @param key
     * @param i
     * @param value
     */
    public void remove(String key, long i, String value) {
        redisTemplate.opsForList().remove(key, i, value);
    }

    /**
     * 检索
     *
     * @param key
     * @param index
     * @return
     */
    public String index(String key, long index) {
        return (String) redisTemplate.opsForList().index(key, index);
    }

    /**
     * 置值
     *
     * @param key
     * @param index
     * @param value
     */
    public void set(String key, long index, String value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 裁剪
     *
     * @param key
     * @param start
     * @param end
     */
    public void trim(String key, long start, int end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    //---------------------------------------------------SET-----------------------------------------------
    /**
     * 新增Set ----setNX 不存在则增加 ------------------------------
     *
     * @param key
     *            键
     * @param value
     *            值
     * @param timeout
     *            超时(秒)
     * @return true 操作成功，false 已存在值
     */
    public Long addSet(final String key, final String value, final Long timeout) {
        Long result = (Long) redisTemplate.execute((RedisCallback<Long>) connection -> {
            Long result1 = connection.sAdd(key.getBytes(), value.getBytes());
            if (result1 == 0)
                return result1;
            if (timeout != null && timeout > 0)
                connection.expire(key.getBytes(), timeout);
            return result1;
        });
        return result;
    }


    /**
     * 通过key获取单个Set
     *
     * @param key
     * @return
     */
    public Set<byte[]> getSet(final String key) {
        Set<byte[]> value = (Set<byte[]>) redisTemplate.execute((RedisCallback<Set<byte[]>>) connection -> {
            return connection.sMembers(key.getBytes());
        });
        return value;
    }

}
