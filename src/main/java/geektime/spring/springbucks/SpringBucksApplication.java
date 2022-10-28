package geektime.spring.springbucks;

import com.github.pagehelper.PageInfo;
import geektime.spring.springbucks.model.Coffee;
import geektime.spring.springbucks.service.CoffeeService;
import lombok.extern.slf4j.Slf4j;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@EnableTransactionManagement
@SpringBootApplication
@EnableJpaRepositories
@MapperScan("mapper.geektime.spring.springbucks.mapper")
public class SpringBucksApplication implements ApplicationRunner {
	@Autowired
	private CoffeeService coffeeService;

	public static void main(String[] args) {
		SpringApplication.run(SpringBucksApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		insertOneCoffee();/*增加一种咖啡*/
        //findAllCoffee();/*查询所有咖啡*/
        //findCoffeeByName();/*根据名称查询咖啡*/
		//updateOneCoffee();/*更新一个咖啡*/
		//delteCoffees();/*删除一个咖啡*/
		findCoffeeByManyID();/*根据id批量查询咖啡*/
	}

	public List<Coffee> findAllCoffee(){
        List<Coffee> allCoffee = coffeeService.findAllCoffee(1,3);
        PageInfo page = new PageInfo(allCoffee);
        log.info("PageInfo: {}", page);
        log.info("Coffee {}", allCoffee.size());
	    return allCoffee;
    }

    public Optional<Coffee> findCoffeeByName(){
        Optional<Coffee> coffee = coffeeService.selectByName("zmr");
        return coffee;
    }
    public void findCoffeeByManyID(){
		List<Long> arrays = new ArrayList<>();
		arrays.add(1L);
		arrays.add(2L);
		List<Coffee> coffees = coffeeService.selectByManyID(arrays);
		for (Coffee coffees1:coffees){
			System.out.println(coffees1);
		}
	}

    public void  insertOneCoffee(){
		Coffee zmr = new Coffee();
		zmr.setName("zmr");
		zmr.setPrice(Money.of(CurrencyUnit.of("CNY"), 100.0));
		zmr.setCreateTime(new Date());
		zmr.setUpdateTime(new Date());
        Coffee espresso = new Coffee();
        espresso.setName("espresso");
        espresso.setPrice(Money.of(CurrencyUnit.of("CNY"), 20.0));
        espresso.setCreateTime(new Date());
        espresso.setUpdateTime(new Date());
        Integer integer = coffeeService.insertCoffee(zmr);
		Integer integer1 = coffeeService.insertCoffee(espresso);
        log.info("Coffee {}", integer);
        log.info("Coffee {}", integer1);
        Coffee s = coffeeService.selectByID(1L);
        Coffee s1 = coffeeService.selectByID(2L);
        log.info("Coffee {}", s);
        log.info("Coffee {}", s1);
	}

	public void updateOneCoffee(){
		Optional<Coffee> coffee = findCoffeeByName();
		Coffee coffee1 = new Coffee();
		coffee1.setId(coffee.get().getId());
		coffee1.setName("zmr");
		coffee1.setPrice(Money.of(CurrencyUnit.of("CNY"), 100.0));
		coffee1.setCreateTime(coffee.get().getCreateTime());
		coffee1.setUpdateTime(new Date());

		Integer i = coffeeService.updateByID(coffee1);
		log.info("Coffee {}", i);
	}

	public void delteCoffees(){
		Integer i = coffeeService.deleteByID(1L);
		log.info("Coffee {}", i);
	}
	@Bean
	public RedisTemplate<String, Coffee> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Coffee> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

}

