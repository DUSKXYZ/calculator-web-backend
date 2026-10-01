package com.example.calculator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * 计算历史记录实体类，对应数据库中的 calculation_history 表
 */
@Entity
@Table(name = "calculation_history")
public class CalculationHistory {

    /** 主键，自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 计算表达式，例如 (1+2)*3 */
    @Column(nullable = false)
    private String expression;

    /** 计算结果，用字符串保存，避免 20 显示成 20.0 的问题 */
    @Column(nullable = false)
    private String result;

    /** 计算时间 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 这条记录属于哪个用户，用来隔离不同人的历史记录 */
    @Column(nullable = false)
    private String username;

    /** 保存之前自动填入当前时间 */
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
