package xyz.xmcode.service.impl;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import xyz.xmcode.mapper.BrandMapper;
import xyz.xmcode.pojo.Brand;
import xyz.xmcode.service.BrandService;
import xyz.xmcode.util.SqlSessionFactoryUtil;

import java.util.ArrayList;
import java.util.List;

public class BrandServiceImpl implements BrandService {
    //1、创建SqlSessionFactory工厂对象
    static SqlSessionFactory factory = SqlSessionFactoryUtil.getSqlSessionFactory();

    @Override
    public Brand selectById(int id) {
        //2.获取SqlSession对象
        SqlSession sqlSession = factory.openSession();
        //3.获取BrandMapper
        BrandMapper mapper = sqlSession.getMapper(BrandMapper.class);

        Brand brand = mapper.selectById(id);
        return brand;
    }

    @Override
    public List<Brand> selectAll() {
        //2.获取SqlSession对象
        SqlSession sqlSession = factory.openSession();
        //3.获取BrandMapper
        BrandMapper mapper = sqlSession.getMapper(BrandMapper.class);
        //4.调用方法
        List<Brand> brands = mapper.selectAll();
        //5.释放资源
        sqlSession.close();
        return brands;
    }

    @Override
    public void addNewBrand(Brand brand) {
        //2.获取SqlSession对象
        SqlSession sqlSession = factory.openSession();
        //3.获取BrandMapper
        BrandMapper mapper = sqlSession.getMapper(BrandMapper.class);
        mapper.addNewBrand(brand);
        sqlSession.commit();
        sqlSession.close();
    }

    @Override
    public void deleteBrand(int[] ids) {
        //2.获取SqlSession对象
        SqlSession sqlSession = factory.openSession();
        //3.获取BrandMapper
        BrandMapper mapper = sqlSession.getMapper(BrandMapper.class);
        mapper.deleteByIds(ids);
        sqlSession.commit();
        sqlSession.close();
    }

    @Override
    public List<Brand> selectByCondition(Brand brand) {
        //2.获取SqlSession对象
        SqlSession sqlSession = factory.openSession();
        //3.获取BrandMapper
        BrandMapper mapper = sqlSession.getMapper(BrandMapper.class);

        List<Brand> brands = mapper.selectByCondition(brand);
        System.out.println(mapper);
        sqlSession.close();
        return brands;
    }

    @Override
    public void updateOne(Brand brand) {
        //2.获取SqlSession对象
        SqlSession sqlSession = factory.openSession();
        //3.获取BrandMapper
        BrandMapper mapper = sqlSession.getMapper(BrandMapper.class);

        System.out.println("update: "+brand);
        mapper.updateOne(brand);
        //!!!!!涉及对数据库的增删改，一定要提交事务
        sqlSession.commit();
        sqlSession.close();
    }
}
