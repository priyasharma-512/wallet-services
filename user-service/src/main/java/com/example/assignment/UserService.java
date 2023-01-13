package com.example.assignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    private final String REDIS_PREFIX_USER = "user::"; //to differentialte service in redis

    private final String KAFKA_TOPIC = "create_wallet";
    public void addUser(UserRequest userRequest) {

        User user = User.builder().
                userName(userRequest.getUserName()).
                email(userRequest.getEmail()).
                name(userRequest.getName()).
                age(userRequest.getAge()).
                build();
        userRepository.save(user);
        //after saving in db we save it in cache for faster retrieval
        saveInCache(user);

        //kafka
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("username", user.getUserName());

        //converting jsonobject to string
        String message = jsonObject.toString();
        kafkaTemplate.send(KAFKA_TOPIC ,message);

    }

    public void saveInCache(User user)
    {
        //saving user  in redis
        //convert user to map
        Map map = objectMapper.convertValue(user, Map.class);
        redisTemplate.opsForHash().putAll(REDIS_PREFIX_USER + user.getUserName(),map);
    }
    public User getUserByUserName(String userName) throws Exception
    {
        Map map =  redisTemplate.opsForHash().entries(REDIS_PREFIX_USER+userName);

        if(map == null || map.size() == 0) {
            //if it is not in cache u need to now search in db basically it is a cache miss
            User user = userRepository.findByUserName(userName);

            if (user != null) {
                saveInCache(user);
            }
            else {
                throw new UserNotFoundException();
            }
            return user;
        }
        else
            return objectMapper.convertValue(map, User.class);
    }
}
