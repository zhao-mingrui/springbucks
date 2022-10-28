package geektime.spring.springbucks.service;

import geektime.spring.springbucks.mapper.CoffeeMapper;
import geektime.spring.springbucks.model.Coffee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class CoffeeService {
    private static final String CACHE = "springbucks-coffee";

    @Autowired
    private RedisTemplate<String, Coffee> redisTemplate;
    @Autowired(required = false)
    private CoffeeMapper coffeeMapper;

    /*查询所有咖啡*/
    public List<Coffee> findAllCoffee(int pagenum,int pagesize) {
        HashOperations<String, String, Coffee> hashall = redisTemplate.opsForHash();
        List<Coffee> coffees = coffeeMapper.findAllWithParam(pagenum,pagesize);
        log.info("Coffee Found: {}", coffees);
        for (int i = 0; i < coffees.size(); i++) {
            log.info("Put coffee {} to Redis.", coffees.get(i).getName());
            hashall.put(CACHE,coffees.get(i).getName(),coffees.get(i));
        }
        redisTemplate.expire(CACHE, 10, TimeUnit.MINUTES);
        return coffees;
    }

    /*根据咖啡名称查询咖啡*/
    public Optional<Coffee> selectByName(String name) {
        HashOperations<String, String, Coffee> hashOperations = redisTemplate.opsForHash();
        if (redisTemplate.hasKey(CACHE) && hashOperations.hasKey(CACHE, name)) {
            log.info("Get coffee {} from Redis.", name);
            return Optional.of(hashOperations.get(CACHE, name));
        }
        Optional<Coffee> coffee= coffeeMapper.selectByName(name);
        log.info("Coffee Found: {}", coffee);
        if (coffee.isPresent()) {
            log.info("Put coffee {} to Redis.", name);
            hashOperations.put(CACHE, name, coffee.get());
            redisTemplate.expire(CACHE, 10, TimeUnit.MINUTES);
        }
        return coffee;
    }
    /*根据主键查询咖啡*/
    public Coffee selectByID(Long id) {
        Coffee s = coffeeMapper.selectByPrimaryKey(id);
        return s;
    }
    /*根据主键批量查询咖啡*/
    public List<Coffee> selectByManyID(List<Long> list) {
        List<Coffee> coffees = coffeeMapper.selectByManyPrimaryKey(list);
        return coffees;
    }
    @Transactional(rollbackOn = {RuntimeException.class})
    /*根据主键删除咖啡*/
    public Integer deleteByID(Long id) throws RuntimeException{
        int i = coffeeMapper.deleteByPrimaryKey(id);
        return i;
    }
    @Transactional(rollbackOn = {RuntimeException.class})
    /*根据主键更新咖啡*/
    public Integer updateByID(Coffee coffee) throws RuntimeException{
        int i = coffeeMapper.updateByPrimaryKey(coffee);
        return i;
    }
    @Transactional(rollbackOn = {RuntimeException.class})
    /*添加咖啡*/
    public Integer insertCoffee(Coffee coffee) throws RuntimeException{
        int i = coffeeMapper.insert(coffee);
        return  i;
    }
}
