package com.example.demo.web;

/**
 * 接口返回体。
 *
 * <p>用 record 而不是普通 class：省掉 getter / equals / hashCode / toString，
 * 一行就能表达"这是个不可变的数据载体"。Java 16 起的正式特性，Spring Boot 3 完全支持。
 *
 * <p>注意：record 只适合做 DTO / 返回值，不要拿它当 JPA 实体。
 */
public record HelloResponse(
        String message,
        String time,
        String javaVersion
) {
}
