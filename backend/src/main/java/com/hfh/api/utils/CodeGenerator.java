package com.hfh.api.utils;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.util.Collections;

/**
 * MyBatis-Plus 代码生成器
 * <p>
 * 使用前请确保数据库已启动，并根据实际情况修改下方配置项
 */
public class CodeGenerator {

    // ==================== 数据库配置 ====================
    // 敏感信息（密码）从环境变量读取，避免硬编码入库
    // 本地可在运行前设置环境变量，或直接修改下方的 USERNAME/PASSWORD
    private static final String URL = "jdbc:mysql://localhost:3306/mydb?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false";
    private static final String USERNAME = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "your_db_password");

    // ==================== 生成配置 ====================
    // 父包名
    private static final String PARENT_PACKAGE = "com.hfh.api";
    // 模块名（生成的文件将放在此模块下，如 controller、service 等）
    private static final String MODULE_NAME = "";
    // 输出目录（默认项目根目录下的 src/main/java）
    private static final String OUTPUT_DIR = System.getProperty("user.dir") + "/backend/src/main/java";
    // Mapper XML 输出目录
    private static final String XML_OUTPUT_DIR = System.getProperty("user.dir") + "/backend/src/main/resources/mapper";

    // ==================== 表配置 ====================
    // 需要生成代码的表名，多个表用逗号分隔
    private static final String[] TABLE_NAMES = {"sys_user", "account_book", "category", "transaction", "reminder"};
    // 表名前缀（生成时会自动去除）
    private static final String TABLE_PREFIX = "";

    // ==================== 作者信息 ====================
    private static final String AUTHOR = "hfh";

    public static void main(String[] args) {
        FastAutoGenerator.create(URL, USERNAME, PASSWORD)
                // 全局配置
                .globalConfig(builder -> {
                    builder.author(AUTHOR)                           // 作者
                            .outputDir(OUTPUT_DIR)                   // 输出路径
                            .dateType(DateType.ONLY_DATE)            // 日期策略：只使用 java.util.Date
                            .commentDate("yyyy-MM-dd")               // 注释日期格式
                            .disableOpenDir()                        // 生成后不自动打开文件夹
                            .enableSwagger()                         // 开启 Swagger 注解
                    ;
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent(PARENT_PACKAGE)                   // 父包名
                            .moduleName(MODULE_NAME)                 // 模块名
                            .entity("entity")                        // Entity 包名
                            .mapper("mapper")                        // Mapper 包名
                            .service("service")                      // Service 包名
                            .serviceImpl("service.impl")             // ServiceImpl 包名
                            .controller("controller")                // Controller 包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, XML_OUTPUT_DIR)) // Mapper XML 路径
                    ;
                })
                // 策略配置
                .strategyConfig(builder -> {
                    builder.addInclude(TABLE_NAMES)                  // 需要生成代码的表
                            .addTablePrefix(TABLE_PREFIX)            // 表名前缀过滤
                            // Entity 策略
                            .entityBuilder()
                            .naming(NamingStrategy.underline_to_camel)   // 数据库映射下划线转驼峰
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .idType(IdType.ASSIGN_ID)                    // 主键策略：雪花算法
                            .enableLombok()                              // 启用 Lombok
                            .enableChainModel()                          // 链式调用
                            .logicDeleteColumnName("deleted")            // 逻辑删除字段
                            .versionColumnName("version")               // 乐观锁字段
                            .formatFileName("%sEntity")                 // 文件命名格式
                            // Controller 策略
                            .controllerBuilder()
                            .enableRestStyle()                           // Rest 风格
                            .formatFileName("%sController")
                            // Service 策略
                            .serviceBuilder()
                            // Mapper 策略
                            .mapperBuilder()
                    ;
                })
                // 模板引擎配置（使用 Velocity）
                .templateEngine(new VelocityTemplateEngine())
                // 执行生成
                .execute();

        System.out.println("========== 代码生成完成 ==========");
    }
}
