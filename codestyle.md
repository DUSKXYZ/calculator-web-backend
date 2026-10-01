# 后端代码规范（codestyle.md）

本项目的代码规范主要参考 **《阿里巴巴 Java 开发手册》**，同时参考 **Google Java Style Guide**。

- 阿里巴巴 Java 开发手册：https://github.com/alibaba/p3c
- Google Java Style Guide：https://google.github.io/styleguide/javaguide.html

以下为本项目遵循的主要规范。

## 1. 命名规范

1. 类名使用大驼峰（UpperCamelCase），例如 `CalculatorController`。
2. 方法名、变量名使用小驼峰（lowerCamelCase），例如 `calculate`、`historyRepository`。
3. 常量使用全大写加下划线，例如 `MAX_SIZE`。
4. 包名全部小写，例如 `com.example.calculator.service`。
5. 命名要见名知意，不使用拼音，不使用单个字母（循环计数变量除外）。

## 2. 格式规范

1. 缩进使用 4 个空格，不使用 Tab。
2. 左大括号不换行，右大括号单独一行。
3. 运算符两侧各加一个空格，例如 `a + b`。
4. 逗号后面加一个空格。
5. 每行长度尽量不超过 120 个字符。
6. 文件使用 UTF-8 编码。

## 3. 注释规范

1. 每个类都要有 Javadoc 注释，说明类的用途。
2. 公开的方法要有 Javadoc 注释，说明参数和返回值。
3. 关键的、不容易看懂的逻辑要加单行注释说明原因。
4. 注释要与代码保持一致，修改代码时同步修改注释。

## 4. 编程实践

1. 不允许使用 `eval` 之类的方式直接执行用户输入，防止代码注入。
2. 对用户的所有输入都要进行校验，不信任前端传来的数据。
3. 异常要分类处理，业务错误返回 400，服务器错误返回 500。
4. 分层开发：controller 只负责接收请求，service 负责业务逻辑，repository 负责数据库操作。
5. 类成员变量使用 private，通过 getter/setter 访问。
6. 数据库相关的对象不要直接暴露无关字段。
