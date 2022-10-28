package geektime.spring.springbucks.mapper;

import geektime.spring.springbucks.model.Coffee;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CoffeeMapper {

    /*查询所有咖啡*/
    @Select("select * from t_coffee order by id")
    List<Coffee> findAllWithParam(@Param("pageNum") int pageNum,
                                  @Param("pageSize") int pageSize);

    /*根据咖啡名称查询咖啡*/
    @Select({
            "select",
            "ID, NAME, PRICE, CREATE_TIME, UPDATE_TIME",
            "from T_COFFEE",
            "where NAME = #{name,jdbcType=VARCHAR}"
    })
    Optional<Coffee> selectByName(String name);

    /*根据主键批量查询咖啡*/

    List<Coffee> selectByManyPrimaryKey(List<Long> list);


    /*根据主键查询咖啡*/
    @Select({
            "select",
            "ID, NAME, PRICE, CREATE_TIME, UPDATE_TIME",
            "from T_COFFEE",
            "where ID = #{id,jdbcType=BIGINT}"
    })
    Coffee selectByPrimaryKey(Long id);

    /*新增咖啡*/
    @Insert({
            "insert into T_COFFEE (NAME, PRICE, ",
            "CREATE_TIME, UPDATE_TIME)",
            "values (#{name,jdbcType=VARCHAR}, #{price,jdbcType=BIGINT,typeHandler=geektime.spring.springbucks.handler.MoneyTypeHandler}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    @SelectKey(statement="CALL IDENTITY()", keyProperty="id", before=false, resultType=Long.class)
    int insert(Coffee record);

    /*根据主键删除咖啡*/
    @Delete({
            "delete from T_COFFEE",
            "where ID = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    /*根据主键更新咖啡*/
    @Update({
            "update T_COFFEE",
            "set NAME = #{name,jdbcType=VARCHAR},",
            "PRICE = #{price,jdbcType=BIGINT,typeHandler=geektime.spring.springbucks.handler.MoneyTypeHandler},",
            "CREATE_TIME = #{createTime,jdbcType=TIMESTAMP},",
            "UPDATE_TIME = #{updateTime,jdbcType=TIMESTAMP}",
            "where ID = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(Coffee record);

}
