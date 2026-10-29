package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用入口。
 *
 * <p>@SpringBootApplication 是三个注解的组合：
 * <ul>
 *   <li>@SpringBootConfiguration —— 声明这是一个配置类</li>
 *   <li>@EnableAutoConfiguration —— 开启自动配置（根据 classpath 猜你要什么）</li>
 *   <li>@ComponentScan —— 扫描本包及子包下的 @Component/@Service/@RestController 等</li>
 * </ul>
 * 所以这个类必须放在所有业务代码的<b>最外层包</b>，否则扫描不到。
 */
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
