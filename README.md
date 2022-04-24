---
title: Java-web-第一阶段-综合案例
date: 2022-04-18
tags: java-web
typora-root-url: ..

---

前面学习了Java基础和Java-web相关知识，包括：数据库相关的MySQL数据库、SQL语言、JDBC（Java数据库连接相关API）、Mybatis（Java持久层框架）；Web服务相关的Servlet、Filter以及常用框架Tomcat；Java版本管理和依赖管理工具Maven；前端相关的HTML、CSS、JavaScript语言，以及Vue、Element框架；HTTP协议相关基础知识。最后，通过这个综合案例来使用以上所学过的知识完成一个网站的搭建，对知识进行复习和巩固。
<!-- more -->

## 案例描述

一个网页列表的增删改查实现，包括前端和后端。

## 环境准备

### 工程创建

* 创建Maven工程，选择从“骨架”创建，并选择`maven-archetype-webapp`

  ![image-20220418162822269](/images/image-20220418162822269.png)

* 修改`pom.xml`添加外部依赖，包括servlet、mysql以及mybatis还有fastjson（下面的插件其实是多余的，可以删掉）。

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
  
    <groupId>xyz.xmcode</groupId>
    <artifactId>Stated1Result</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>war</packaging>
  
    <name>Stated1Result Maven Webapp</name>
    <!-- FIXME change it to the project's website -->
    <url>http://www.example.com</url>
  
    <properties>
      <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
      <maven.compiler.source>1.7</maven.compiler.source>
      <maven.compiler.target>1.7</maven.compiler.target>
    </properties>
  
    <dependencies>
      <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.11</version>
        <scope>test</scope>
      </dependency>
  
      <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>javax.servlet-api</artifactId>
        <version>3.1.0</version>
        <scope>provided</scope>
      </dependency>
  
      <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis</artifactId>
        <version>3.5.5</version>
      </dependency>
  	<!-- mysql驱动版本要和系统使用的mysql兼容，不然会报错 -->
      <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.28</version>
      </dependency>
        
       <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <version> 1.2.70</version>
      </dependency>
    </dependencies>
  
    <build>
      <finalName>Stated1Result</finalName>
      <pluginManagement><!-- lock down plugins versions to avoid using Maven defaults (may be moved to parent pom) -->
        <plugins>
          <plugin>
            <artifactId>maven-clean-plugin</artifactId>
            <version>3.1.0</version>
          </plugin>
          <!-- see http://maven.apache.org/ref/current/maven-core/default-bindings.html#Plugin_bindings_for_war_packaging -->
          <plugin>
            <artifactId>maven-resources-plugin</artifactId>
            <version>3.0.2</version>
          </plugin>
          <plugin>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.0</version>
          </plugin>
          <plugin>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>2.22.1</version>
          </plugin>
          <plugin>
            <artifactId>maven-install-plugin</artifactId>
            <version>2.5.2</version>
          </plugin>
          <plugin>
            <artifactId>maven-deploy-plugin</artifactId>
            <version>2.8.2</version>
          </plugin>
        </plugins>
      </pluginManagement>
    </build>
  </project>
  ```

* 建立正确的目录结构（考虑mybatis的文件依赖关系），并导入前端相关的源程序依赖（包括vue.js和element-ui，都可以在官网下载）。

  ![image-20220418172013665](/images/image-20220418172013665.png)

### 数据库准备

* mybatis配置文件修改（`mybatis-config.xml`）

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <!DOCTYPE configuration
          PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <configuration>
      <!--    配置环境-->
      <environments default="mysql">
          <!--        配置mysql环境
                     id要与default一致
          -->
          <environment id="mysql">
              <!--            配置事务的类型-->
              <transactionManager type="JDBC"></transactionManager>
              <!--            配置连接池-->
              <dataSource type="POOLED">
                  <!-- 配置连接数据库的基本信息,注意8.0版本以后要加cj-->
                  <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                  <property name="url" value="jdbc:mysql://localhost:3306/stated_result?serverTimezone=UTC&amp;useSSL=false"/>
                  <property name="username" value="root"/>
                  <property name="password" value="qwertyuiop"/>
              </dataSource>
          </environment>
      </environments>
      <!--    指定映射配置文件的位置，映射配置文件指的是每个dao独立的配置文件-->
      <mappers>
          <package name ="xyz.xmcode.mapper"/>
      </mappers>
  </configuration>
  ```

* 建立两张数据表，分别记录账户和商标信息，SQL代码如下：

  ```sql
  -- 创建数据库
  Create database stated_result;
  -- 进入数据库
  USE stated_result;
  -- 创建账户表
  CREATE TABLE Accounts(
                           id int primary key auto_increment,
                           username varchar(10),
                           password varchar(20)
  );
  -- 创建商标表
  CREATE TABLE Brands(
                         id int primary key auto_increment,
                         brand_name varchar(20),
                         company_name varchar(20),
                         orderd int,
                         description varchar(100),
                         status int
  );
  
  -- 添加数据
  insert into Accounts (username, password)
  values ('xiaoming', '1234567890'), ('xiaohong', '2234567890');
  
  insert into Brands (brand_name, company_name, orderd, description, status)
  values
      ('华为', '华为技术有限公司',100, '万物互联',1),
      ('小米', '小米科技有限公司',189, 'are you ok',1),
      ('华为', '华为技术有限公司',100, '万物互联',1),
      ('华为', '华为技术有限公司',100, '万物互联',1),
      ('华为', '华为技术有限公司',100, '万物互联',1),
      ('华为', '华为技术有限公司',100, '万物互联',1),
      ('华为', '华为技术有限公司',100, '万物互联',1),
      ('华为', '华为技术有限公司',100, '万物互联',1);
  
  select *from Accounts;
  select *from Brands;
  ```

##  功能实现

### 后端

#### 数据库连接

* 建立Brand类，表示数据库中的一列（pojo目录下）

  ```java
  package xyz.xmcode.pojo;
  
  public class Brand {
      private Integer id;
      private String brandName;
      private String companyName;
      private Integer order;
      private String description;
      private Integer status;
  
      public Integer getId() {
          return id;
      }
  
      public void setId(int id) {
          this.id = id;
      }
  
      public String getBrandName() {
          return brandName;
      }
  
      public void setBrandName(String brandName) {
          this.brandName = brandName;
      }
  
      public String getCompanyName() {
          return companyName;
      }
  
      public void setCompanyName(String companyName) {
          this.companyName = companyName;
      }
  
      public Integer getOrder() {
          return order;
      }
  
      public void setOrder(int order) {
          this.order = order;
      }
  
      public String getDescription() {
          return description;
      }
  
      public void setDescription(String description) {
          this.description = description;
      }
  
      public Integer getStatus() {
          return status;
      }
  
      public void setStatus(int status) {
          this.status = status;
      }
  }
  ```

* 使用Mybatis建立数据库到Brand实现类的映射（mapper目录下）

  * 建立BrandMapper接口（主要作用在于绑定方法与sql语句）

    ```java
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
    ```

  * 配置`BrandMapper.xml`文件

    ```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE mapper
            PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
            "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
    <mapper namespace="xyz.xmcode.mapper.BrandMapper">
    
        <resultMap id="brandResultMap" type="xyz.xmcode.pojo.Brand">
            <result property="brandName" column="brand_name"/>
            <result property="companyName" column="company_name"/>
            <result property="order" column="orderd"/>
        </resultMap>
        <update id="updateOne">
            update Brands set
                 brand_name = #{brand.brandName},
                 company_name = #{brand.companyName},
                 description = #{brand.description},
                 orderd = #{brand.order},
                 status = #{brand.status}
            where id = #{brand.id}
        </update>
    
        <delete id="deleteByIds">
            delete from Brands where id in
            <foreach collection="ids" item="id" separator="," open="(" close=")">
                #{id}
            </foreach>
        </delete>
    
        <select id="selectByCondition" resultMap="brandResultMap">
            select * from Brands
    
            <where>
                <if test="brand.brandName != null and brand.brandName.length != 0">
                    and brand_name like #{brand.brandName}
                </if>
                <if test="brand.companyName != null and brand.companyName.length != 0">
                    and company_name like #{brand.companyName}
                </if>
                <if test="brand.status != null">
                    and status = #{brand.status}
                </if>
            </where>
    
        </select>
    </mapper>
    ```

* 接口函数的具体实现（service目录下）

  * 定义Brand服务接口：

    ```java
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
    ```

  * 定义BrandService实现类：

    ```java
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
    ```

  * 定义工具类实现SqlSessionFactory工厂对象的创建

    ```java
    package xyz.xmcode.util;
    
    import org.apache.ibatis.io.Resources;
    import org.apache.ibatis.session.SqlSessionFactory;
    import org.apache.ibatis.session.SqlSessionFactoryBuilder;
    
    import java.io.InputStream;
    
    public class SqlSessionFactoryUtil{
        private static SqlSessionFactory sqlSessionFactory;
        static{
            //静态代码块会随着类的加载而自动执行，且只执行一次
            try{
                String resource = "mybatis-config.xml";
                InputStream inputStream = Resources.getResourceAsStream(resource);
                sqlSessionFactory = new
                        SqlSessionFactoryBuilder().build(inputStream);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        public static SqlSessionFactory getSqlSessionFactory(){
            return sqlSessionFactory;
        }
    }
    ```

    **使用接口和实现类而不直接在servlet中调用的目的：**实现类与类之间、层与层之间的解耦。

    **使用用工具类创建工厂对象而不直接调用建立的目的：**工具类中使用了静态代码块，使得工厂对象的创建只会在类加载时发生一次，从而可以节约资源。

* 定义需要实现的servlet:

  * SelectAllServlet:

    ```java
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
    import java.util.List;
    
    @WebServlet("/selectAllServlet")
    public class SelectAllServlet extends HttpServlet {
    
        private BrandService brandService = new BrandServiceImpl();
    
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            //1.调用Service查询
            List<Brand> brands = brandService.selectAll();
            //2.转为Json
            String jsonString = JSON.toJSONString(brands);
            //3.写数据
            resp.setContentType("text/json;charset=utf-8");
            resp.getWriter().write(jsonString);
        }
    
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            super.doPost(req, resp);
        }
    }
    ```

* Servlet优化：

  * 优化方法：如果和上面一样，针对每一个url来建立一个servlet会导致代码很乱很复杂，不够简洁，因此使用反射的方法，通过路由来调用该路由对应的方法。具体实现方法为：建立一个继承自HttpServlet的基础类（BaseServlet），并在该类中重写其service()方法，获取请求中的路由信息来调用对应的方法；之后针对一个对象（比如本例子中的Brand）可以只实现一个Servlet（BrandServlet）继承自BaseServlet，匹配url为/Brand/*， *中的内容由方法名称匹配。

  * BaseServlet:

    ```java
    package xyz.xmcode.web.servlet;
    
    import javax.servlet.ServletException;
    import javax.servlet.http.HttpServlet;
    import javax.servlet.http.HttpServletRequest;
    import javax.servlet.http.HttpServletResponse;
    import java.io.IOException;
    import java.lang.reflect.InvocationTargetException;
    import java.lang.reflect.Method;
    
    public class BaseServlet extends HttpServlet {
        //根据请求路径进行方法分发
        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String uri = req.getRequestURI();
            String methodName = uri.substring(uri.lastIndexOf('/') + 1);
            //获取类对象
            Class<? extends BaseServlet> aClass = this.getClass();
            //获取方法对象
            try {
                Method method = aClass.getMethod(methodName, HttpServletRequest.class,HttpServletResponse.class);
                //执行方法
                method.invoke(this, req, resp);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    ```

  * BrandServlet:

    ```java
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
    ```

### 前端

前端使用html + css +javaScript实现，用到的框架有Vue、axios和Element-ui。

#### 前端框架的引入

* 具体引入方法可以去官网查询：
  * Vue官网：https://v3.cn.vuejs.org/
  * Element-ui官网：https://element.eleme.cn/
  * axios官网：http://www.axios-js.com/
* 本项目中下载了三个框架的源文件，有需要可以去仓库中下载。

#### 页面展示

![image-20220424143123847](/images/image-20220424143123847.png)

#### 实现代码

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <!-- import CSS -->
    <link rel="stylesheet" href="element-ui/lib/theme-chalk/index.css">
    <title>登录页面</title>
</head>

<style>
    .el-table .warning-row {
        background: oldlace;
    }

    .el-table .success-row {
        background: #f0f9eb;
    }
</style>

<body>
<div id="app">
    <el-form :inline="true" :model="checkBrand" class="demo-form-inline">
        <el-form-item label="当前状态">
            <el-select v-model="checkBrand.status" placeholder="当前状态">
                <el-option label="开启" value= 1></el-option>
                <el-option label="禁用" value= 0></el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="企业名称">
            <el-input v-model="checkBrand.companyName" placeholder="企业名称"></el-input>
        </el-form-item>
        <el-form-item label="品牌名称">
            <el-input v-model="checkBrand.brandName" placeholder="品牌名称"></el-input>
        </el-form-item>

        <el-form-item>
            <el-button type="primary" @click="checkByCondition">查询</el-button>
        </el-form-item>
    </el-form>

    <el-row>
        <el-button type="success" plain @click="dialogFormVisible = true"> 新  增</el-button>
        <el-button type="warning" plain @click="deleteBySelect">批量删除</el-button>
    </el-row>

    <el-dialog
            title="提示"
            :visible.sync="deleteDialogVisible"
            width="30%">
        <span>确定进行批量删除操作？</span>
        <span slot="footer" class="dialog-footer">
    <el-button @click="deleteDialogVisible = false">取 消</el-button>
    <el-button type="warning" @click="deleteByIds">确定删除</el-button>
  </span>
    </el-dialog>

    <el-dialog title="新增选项" :visible.sync="dialogFormVisible" center>
        <el-form :model="newBrand" label-position = 'left'>
            <el-form-item label="品牌名称" :label-width="formLabelWidth">
                <el-input v-model="newBrand.brandName" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="企业名称" :label-width="formLabelWidth">
                <el-input v-model="newBrand.companyName" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="排序":label-width="formLabelWidth">
                <el-input v-model="newBrand.order" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="状态":label-width="formLabelWidth" >
                <el-select v-model="newBrand.status" placeholder="当前状态">
                    <el-option label="开启" value= 1></el-option>
                    <el-option label="禁用" value= 0></el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="品牌描述">
                <el-input type="textarea" v-model="newBrand.description"></el-input>
            </el-form-item>

        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="dialogFormVisible = false">取  消</el-button>
            <el-button type="primary" @click="addNewBrand">确定新增</el-button>
        </div>
    </el-dialog>

    <el-dialog title="更新选项" :visible.sync="updateDialogVisible" center>
        <el-form :model="updateBrand" label-position = 'right'>
            <el-form-item label="品牌名称" :label-width="formLabelWidth">
                <el-input v-model="updateBrand.brandName" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="企业名称" :label-width="formLabelWidth">
                <el-input v-model="updateBrand.companyName" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="排序":label-width="formLabelWidth">
                <el-input v-model="updateBrand.order" autocomplete="off"></el-input>
            </el-form-item>
            <el-form-item label="状态":label-width="formLabelWidth" >
                <el-select v-model="updateBrand.status" placeholder="当前状态">
                    <el-option label="开启" value= 1></el-option>
                    <el-option label="禁用" value= 0></el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="品牌描述">
                <el-input type="textarea" v-model="updateBrand.description"></el-input>
            </el-form-item>

        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="updateDialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="updateTheBrand">确 定</el-button>
        </div>
    </el-dialog>

    <el-table
            :data="tableData"
            style="width: 100%"
            :row-class-name="tableRowClassName"
            @selection-change="handleSelectionChange">
        <el-table-column
                type = selection
                width = 50px
                align = "center">
        </el-table-column>
        <el-table-column
                type = index
                width = 50px
                align = "center">
        </el-table-column>
        <el-table-column
                prop="brandName"
                label="品牌名称"
                align = "center">
        </el-table-column>
        <el-table-column
                prop="companyName"
                align = "center"
                label="企业名称">
        </el-table-column>
        <el-table-column
                prop="order"
                align = "center"
                label="排序">
        </el-table-column>
        <el-table-column
                prop="description"
                align = "center"
                label="企业描述">
        </el-table-column>
        <el-table-column
                prop="status"
                align = "center"
                label="当前状态">
        </el-table-column>
        <el-table-column label = "操作">
            <template slot-scope="scope">
                <el-button type="success" @click="updateOne(scope.row)">修改</el-button>
                <el-button type="warning" @click="deleteOne(scope.row)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
    <div class="block">

        <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="currentPageNumber"
                :page-sizes="[5, 10, 20, 30, 40]"
                :page-size="100"
                layout="total, sizes, prev, pager, next, jumper"
                :total="allData.length">
        </el-pagination>
    </div>

</div>
</body>


<!-- import Vue before Element -->
<script src="js/vue.js"></script>

<!-- import JavaScript -->
<script src="element-ui/lib/index.js"></script>
<script src="js/axios/dist/axios.min.js"></script>

<script>
    new Vue({
        el: '#app',
        mounted(){
            //console.log(axios);
            this.selectAll();
        },
        methods: {
            selectAll(){
                let _this = this;
                axios({
                    method:"get",
                    url:"http://localhost/Stated1Result_war/Brand/selectAll"
                }).then(function (resp){
                    _this.allData = resp.data;
                    _this.tableData = [];
                    for(let i = 0; i< _this.tablePageSize; i ++){
                        let aBrand = _this.allData[(_this.currentPageNumber - 1) * _this.tablePageSize + i];
                        if(aBrand == null) break;
                        _this.tableData[i] = aBrand;
                    }
                })
            },
            checkByCondition(){
                let _this = this;
                axios(
                    {
                        method:"post",
                        url:"http://localhost/Stated1Result_war/Brand/selectByCondition",
                        data: _this.checkBrand
                    }
                ).then(function (resp) {
                    _this.allData = resp.data;
                    _this.setTableData();
                })
            },
            addNewBrand(){
                let _this = this;
                axios(
                    {
                        method:"post",
                        url:"http://localhost/Stated1Result_war/Brand/addNew",
                        data: _this.newBrand
                    }
                ).then(function (resp) {
                    if(resp.data == "success"){
                        _this.dialogFormVisible = false;
                        _this.selectAll();
                    }
                    else{
                        console.log("some problem happened!")
                    }
                })

            },
            deleteByIds(ids){
                this.$confirm('此操作将永久删除数据, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let selectedIds = ids;
                    let _this = this;
                    axios({
                        method:"post",
                        url:"http://localhost/Stated1Result_war/Brand/delete",
                        data: selectedIds
                    }).then(resp => {
                        if(resp.data == "deleted"){
                            _this.selectAll();
                            _this.$message({
                                type: 'success',
                                message: '删除成功！'
                            });
                        }
                        else{
                            console.log("some problem happened!")
                        }
                    })
                }).catch(() => {
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    });
                });
            },
            deleteBySelect(){
                let selectedIds = [];
                for (let i = 0; i < this.multipleSelection.length; i++) {
                    selectedIds[i] = this.multipleSelection[i].id;
                }
                this.deleteByIds(selectedIds);
            },
            deleteOne(row){
                let selectedIds = [];
                selectedIds[0] = row.id;
                this.deleteByIds(selectedIds);
            },
            updateOne(row){
                this.updateBrand = row;
                this.updateDialogVisible = true;
            },
            updateTheBrand(){
                let _this = this;
                axios(
                    {
                        method:"put",
                        url:"http://localhost/Stated1Result_war/Brand/updateOne",
                        data: _this.updateBrand
                    }
                ).then(function (resp) {
                    if(resp.data.id == _this.updateBrand.id
                        &&resp.data.companyName == _this.updateBrand.companyName
                        &&resp.data.brandName == _this.updateBrand.brandName
                        &&resp.data.status == _this.updateBrand.status
                        &&resp.data.order == _this.updateBrand.order
                        &&resp.data.description == _this.updateBrand.description){

                        _this.updateDialogVisible = false;
                        _this.selectAll();
                        _this.$message({
                            type: 'success',
                            message: '更新成功！'
                        });
                    }
                    else{
                        console.log("some problem happened!");
                        _this.updateDialogVisible = false;
                        _this.selectAll();
                        _this.$message({
                            type: 'info',
                            message: '更新数据出现了问题！'
                        });
                    }
                })
            },
            tableRowClassName({row, rowIndex}) {
                if (rowIndex %2 === 1) {
                    return 'warning-row';
                } else if (rowIndex %2 === 0) {
                    return 'success-row';
                }
                return '';
            },
            handleSelectionChange(val) {
                this.multipleSelection = val;
            },
            handleSizeChange(val) {
                this.tablePageSize = val ;
                this.setTableData();
            },
            handleCurrentChange(val) {
                this.currentPageNumber = val;
                this.setTableData();
            },
            setTableData(){
                this.tableData = [];
                for(let i = 0; i< this.tablePageSize; i ++){
                    let aBrand = this.allData[(this.currentPageNumber - 1) * this.tablePageSize + i];
                    if(aBrand == null) break;
                    this.tableData[i] = aBrand;
                }
            },
        },
        data() {
            return {
                //新增表单输入框宽度
                formLabelWidth: '80px',
                //新增表单对话框可见性
                dialogFormVisible: false,
                //批量删除确认框
                deleteDialogVisible: false,
                //更新数据填写框
                updateDialogVisible: false,
                //新建表单数据
                newBrand:{
                    brandName:'',
                    companyName:'',
                    order: '',
                    description:'',
                    status: '',
                },
                //搜索表单数据
                checkBrand:{
                    brandName:'',
                    companyName:'',
                    status: '',
                },
                //更新表单书据
                updateBrand:{
                    brandName:'',
                    companyName:'',
                    order: '',
                    description:'',
                    status: '',
                },
                //复选框数据
                multipleSelection: [],
                //全部数据
                allData:[],
                //表格数据
                tableData: [],
                currentPageNumber : 1,
                tablePageSize: 5
            }
        },
    })
</script>
</html>
```

### 项目启动的相关配置

* 首先要安装Tomcat，直接去官网下载压缩包，然后解压放到合适位置即可。
* 安装完成后在bin目录下运行startup.bat即可运行服务，注意如果出现cmd窗口闪一下关闭的情况，是因为环境变量配置的问题，添加环境变量JAVA_HOME指定到你的java.jdk目录。
* 运行后浏览器访问本地8080端口即可看到tomcat的欢迎页面。
* 尝试没有问题后在IDEA工程中新建Application，选择本地Tomcat即可，IDEA会自动识别系统中安装的Tomcat。

## 踩坑记录

* MySQL的版本问题：pom.xml中引入的mysql-connector依赖版本要和自己安装的mysql版本一致，建议安装8.0以后的，以前版本对中文兼容有问题。（注意该成8.0以后要注意JDBC驱动链接，链接中要加上cj，详细看上面mybatis的核心配置文件）
* `BrandMapper.xml`文件中的namespace中要写全类名，即要包含包名信息
* 注意`BrandMapper.xml`文件中书写语句的语法问题，何时要转义，何时不需要
* 对数据库的“查”操作，可以不提交事务，但“增删改”操作执行sql后一定要commit
* axios()中不能直接调用this，需要在axios前设置变量（let _this = this），然后使用该变量调用。
