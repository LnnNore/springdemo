package com.example.demo.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 控制器层测试。
 *
 * <p>@WebMvcTest 只启动 Web 层（Controller + 参数解析 + JSON 序列化），
 * 不启动数据库、不启动整个容器，所以很快。这是写接口测试的首选方式。
 *
 * <p>注意：@WebMvcTest 不会扫描 @Service / @Repository，
 * 如果 Controller 依赖了它们，需要用 @MockitoBean 注入替身。
 */
@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/hello 不传参数时返回默认问候")
    void helloWithDefaultName() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("Hello, World")))
                .andExpect(jsonPath("$.javaVersion").isNotEmpty());
    }

    @Test
    @DisplayName("GET /api/hello?name=xxx 带上参数时回显名字")
    void helloWithName() throws Exception {
        mockMvc.perform(get("/api/hello").param("name", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("Hello, Java")));
    }

    @Test
    @DisplayName("GET /api/info 返回运行时 JDK 信息")
    void infoReturnsJdkInfo() throws Exception {
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("java")));
    }
}
