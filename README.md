# 前后端分离计算器系统 —— 后端

## 项目简介

本仓库是前后端分离计算器系统的后端部分，负责接收前端发来的计算表达式，在后端完成解析与计算，并把计算历史保存到数据库中。

## 技术栈

- Java 17+
- Spring Boot 3.5
- Spring Data JPA
- H2 Database（文件数据库，无需安装）

## 运行环境

- JDK 17 或更高版本
- Maven 3.6 或更高版本（使用 IntelliJ IDEA 自带 Maven 也可以）

## 项目结构

```
StudentID_calculator_backend/
├── src/
│   └── main/
│       ├── java/com/example/calculator/
│       │   ├── CalculatorApplication.java   启动类
│       │   ├── controller/
│       │   │   └── CalculatorController.java  接口层，接收前端请求
│       │   ├── service/
│       │   │   ├── CalculatorService.java     业务逻辑层
│       │   │   └── ExpressionEvaluator.java   表达式解析与计算（调度场算法）
│       │   ├── model/
│       │   │   └── CalculationHistory.java    计算历史实体类
│       │   └── repository/
│       │       └── CalculationHistoryRepository.java  数据库操作
│       └── resources/
│           └── application.properties         配置文件
├── pom.xml
├── README.md
└── codestyle.md
```

## 安装与启动

1. 克隆本仓库
2. 在项目根目录执行：

```bash
mvn spring-boot:run
```

或者先打包再运行：

```bash
mvn clean package
java -jar target/calculator-backend-1.0.0.jar
```

启动成功后服务运行在 `http://localhost:8080`。

## 数据库说明

使用 H2 文件数据库，无需安装任何数据库软件。首次启动时会自动在项目目录下创建 `data/calculatordb` 文件，并自动建表。

表结构：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT | 主键，自增 |
| username | VARCHAR | 用户名，用于隔离不同用户的历史记录 |
| expression | VARCHAR | 计算表达式 |
| result | VARCHAR | 计算结果 |
| created_at | TIMESTAMP | 计算时间 |

## API 接口

所有接口都需要携带用户名，每个用户只能查询、删除自己的历史记录。

| 方法 | 地址 | 说明 |
| --- | --- | --- |
| POST | /api/calculate | 计算表达式并保存历史（请求体带 username） |
| GET | /api/history?username=xxx | 查询该用户的历史记录 |
| DELETE | /api/history/{id}?username=xxx | 删除该用户的指定历史记录 |
| DELETE | /api/history?username=xxx | 清空该用户的全部历史记录 |

计算接口请求示例：

```json
{
  "username": "zhangsan",
  "expression": "(1+2)*3"
}
```

成功响应：

```json
{
  "success": true,
  "expression": "(1+2)*3",
  "result": "9"
}
```

失败响应：

```json
{
  "success": false,
  "message": "除数不能为零"
}
```

## 前后端连接方式

前端页面通过 `fetch` 请求 `http://localhost:8080/api/...` 下的接口，后端已通过 `@CrossOrigin` 允许跨域访问，前端直接用浏览器打开 HTML 文件即可与后端通信。
