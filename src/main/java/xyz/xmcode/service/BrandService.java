package xyz.xmcode.service;

import xyz.xmcode.pojo.Brand;
import java.util.List;

public interface BrandService {
    Brand selectById(int id);
    List<Brand> selectAll();
    void addNewBrand(Brand brand);
    void deleteBrand(int[] ids);
    List<Brand> selectByCondition(Brand brand);
    void updateOne(Brand brand);
}
