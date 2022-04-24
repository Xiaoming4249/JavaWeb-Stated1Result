package xyz.xmcode.mapper;

import org.apache.ibatis.annotations.*;
import xyz.xmcode.pojo.Brand;

import java.util.List;

public interface BrandMapper {
    /**
     * 按id查询
     */
    @Select("select * from Brands where id = #{id}")
    @ResultMap("brandResultMap")
    Brand selectById(@Param("id") int id);
    /**
     * 查询全部
     * @return
     */
    @Select("select * from Brands")
    @ResultMap("brandResultMap")
    List<Brand> selectAll();

    /**
     * 添加一条新数据
     * @param brand
     */
    @Insert("insert into Brands value(null,#{brandName},#{companyName},#{order},#{description},#{status} )")
    void addNewBrand(Brand brand);

    /**
     * 按数组ids中的id进行删除
     * @param ids
     */
    void deleteByIds(@Param("ids") int[] ids);

    /**
     * 按条件查询
     */
    List<Brand> selectByCondition(@Param("brand") Brand brand);

    /**
     * 更新一条数据
     */
    void updateOne(@Param("brand") Brand brand);
}
