# springdemo

一个用于验证本机 Java 开发环境的最小 Spring Boot 项目。

> 用途：Java 技术恢复计划（每天 2 小时版）· S1 / S2 的验证项目。
> 目标不是功能多，而是证明"从写代码到推到 GitHub、从打包到跑起来"这整条链路是通的。

## 技术栈

|组件|版本|
|-|-|
|JDK|21 (LTS, Temurin)|
|Spring Boot|3.5.16|
|构建工具|Maven 3.9.x|
|测试|JUnit 5 + MockMvc + AssertJ|

## 快速开始

### 前置条件

```bash
java -version    # 期望 openjdk 21.x
mvn -v           # 期望 Apache Maven 3.9.x，且 Java version 显示 21
```

### 运行

```bash
# 方式一：Maven 插件直接跑（开发时最常用）
mvn spring-boot:run

# 方式二：打包成可执行 jar 再跑（更接近生产）
mvn clean package -DskipTests
java -jar target/springdemo-0.0.1-SNAPSHOT.jar
```

启动成功的日志里会有：

```
Started DemoApplication in 1.234 seconds (process running for 1.891)
Tomcat started on port 8080 (http) with context path ''
```

### 接口

|方法|路径|说明|示例|
|-|-|-|-|
|GET|`/api/hello`|默认问候，返回 JSON|`curl "http://localhost:8080/api/hello"`|
|GET|`/api/hello?name=张三`|带参数问候|`curl "http://localhost:8080/api/hello?name=张三"`|
|GET|`/api/info`|返回运行时 JDK / JVM 信息|`curl "http://localhost:8080/api/info"`|

`/api/hello` 的响应示例：

```json
{
  "message": "Hello, World! 环境已就绪。",
  "time": "2026-10-26 18:30:12",
  "javaVersion": "21.0.9"
}
```

### 测试

```bash
mvn test
```

## 目录结构

```
springdemo/
├── pom.xml
├── src
│   ├── main
│   │   ├── java/com/example/demo
│   │   │   ├── DemoApplication.java        # 启动类（必须在最外层包）
│   │   │   └── web
│   │   │       ├── HelloController.java    # REST 接口
│   │   │       └── HelloResponse.java      # 返回体（record）
│   │   └── resources
│   │       └── application.yml             # 配置
│   └── test
│       └── java/com/example/demo/web
│           └── HelloControllerTest.java    # 接口测试
└── .gitignore
```

## 踩坑记录

> 这一节是给面试官看的，也是给自己复盘的。遇到问题就往下加一条。

* `%JAVA\_HOME%\\bin` 必须放在 PATH 里，且 `JAVA\_HOME` 要指向 JDK 根目录而不是 `bin`。
* 首次 `mvn` 构建要下载依赖，国内建议配阿里云镜像（见 `restart-week/config/maven-settings-aliyun.xml`）。
* Git 提交前确认 `.gitignore` 生效：`git status` 里不应该出现 `target/` 和 `.idea/`。
* \- 首次 push 报 `ERROR: Repository not found`：remote 地址里把 owner 写成了 GitHub 昵称（GGBond），实际 username 是 LnnNore。GitHub 对"无权访问"和"不存在"返回同一条错误，容易误判成权限问题；用 `ssh -T git@github.com` 可以让它自报真实用户名。

## 后续计划

* \[ ] S8–S12：加 `@Service` 分层、DTO + `@Valid` 参数校验、统一返回体 `Result<T>`、全局异常处理，做成内存版 task-api
* \[ ] S13–S17：接入数据库（MyBatis-Plus），加分页 / 事务 / 索引优化；接 springdoc-openapi，浏览器直接调试接口
* \[ ] S19–S22：做成完整项目，补架构说明、接口文档和测试

