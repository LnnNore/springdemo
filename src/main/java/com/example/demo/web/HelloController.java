package com.example.demo.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Day 1 的验证用接口。
 *
 * <p>@RestController = @Controller + @ResponseBody，返回值会被 Jackson 序列化成 JSON。
 * 这是 Spring Boot 写 HTTP 接口最常用的注解。
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 构造器注入（现在推荐的方式，比 @Autowired 字段注入更好测试、更明确依赖） */
    private final Environment env;
    private final String appName;

    public HelloController(Environment env,
                           @Value("${spring.application.name:springdemo}") String appName) {
        this.env = env;
        this.appName = appName;
    }

    /**
     * GET /api/hello?name=张三
     *
     * <p>@RequestParam 的 required = false 表示参数可以不传，不传就用默认值 "World"。
     */
    @GetMapping("/hello")
    public HelloResponse hello(@RequestParam(value = "name", required = false, defaultValue = "World") String name) {
        return new HelloResponse(
                "Hello, " + name + "! 环境已就绪。",
                LocalDateTime.now().format(FMT),
                env.getProperty("java.version")
        );
    }

    /**
     * GET /api/info
     *
     * <p>返回运行时的 JDK 信息——用来验证 Maven / IDEA 到底用了哪个 JDK。
     * 如果这里显示 21，说明整条链路都对上了。
     */
    @GetMapping("/info")
    public String info() {
        return """
                app       : %s
                java      : %s (%s)
                jvm       : %s
                encoding  : %s
                """.formatted(
                appName,
                env.getProperty("java.version"),
                env.getProperty("java.vendor"),
                env.getProperty("java.vm.name"),
                env.getProperty("file.encoding")
        );
    }
}
