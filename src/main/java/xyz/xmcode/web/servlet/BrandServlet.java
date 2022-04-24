package xyz.xmcode.web.servlet;

import com.alibaba.fastjson.JSON;
import xyz.xmcode.pojo.Brand;
import xyz.xmcode.service.BrandService;
import xyz.xmcode.service.impl.BrandServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@WebServlet ("/Brand/*")
public class BrandServlet extends BaseServlet {
    private BrandService brandService = new BrandServiceImpl();
    public void selectAll(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
        //1.调用Service查询
        List<Brand> brands = brandService.selectAll();
        //2.转为Json
        String jsonString = JSON.toJSONString(brands);
        //3.写数据
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().write(jsonString);
    }
    public void addNew(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Brand brand = JSON.parseObject(request.getReader().readLine(), Brand.class);
        brandService.addNewBrand(brand);
        response.getWriter().write("success");
    }

    public void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int[] params = JSON.parseObject(request.getReader().readLine(), int[].class);
        brandService.deleteBrand(params);
        response.getWriter().write("deleted");
    }

    public void updateOne(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Brand brand = JSON.parseObject(request.getReader().readLine(), Brand.class);
        brandService.updateOne(brand);
        Brand newBrand = brandService.selectById(brand.getId());
        String jsonString = JSON.toJSONString(newBrand);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().write(jsonString);
    }

    public void selectByCondition(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Brand brand = JSON.parseObject(request.getReader().readLine(), Brand.class);
        System.out.println(brand);
        if(brand.getBrandName() != null && brand.getBrandName() != "")
            brand.setBrandName("%" + brand.getBrandName() +"%");
        if(brand.getCompanyName() != null && brand.getCompanyName() != "")
            brand.setCompanyName("%" + brand.getCompanyName() +"%");
        System.out.println(brand);

        List<Brand> brands = brandService.selectByCondition(brand);
        String jsonString = JSON.toJSONString(brands);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().write(jsonString);
    }
}
