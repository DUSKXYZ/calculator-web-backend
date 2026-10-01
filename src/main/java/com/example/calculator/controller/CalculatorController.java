package com.example.calculator.controller;

import com.example.calculator.model.CalculationHistory;
import com.example.calculator.service.CalculatorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 计算器接口层，给前端提供 HTTP API
 * 前端是单独的 HTML 页面，所以这里加了 @CrossOrigin 允许跨域访问。
 * 所有接口都需要带上 username 参数，历史记录按用户名隔离。
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CalculatorController {

    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    /**
     * 计算接口：POST /api/calculate
     * 请求体: {"username": "zhangsan", "expression": "(1+2)*3"}
     * 成功返回: {"success": true, "expression": "(1+2)*3", "result": "9"}
     * 失败返回: {"success": false, "message": "错误原因"}
     */
    @PostMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculate(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        String username = body.get("username");
        String expression = body.get("expression");
        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "请先输入用户名");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (expression == null || expression.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "表达式不能为空");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            CalculationHistory history = calculatorService.calculate(username.trim(), expression);
            response.put("success", true);
            response.put("expression", history.getExpression());
            response.put("result", history.getResult());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // 表达式非法、除零等业务错误
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "服务器内部错误");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 查询某个用户的历史记录：GET /api/history?username=zhangsan
     */
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getHistory(@RequestParam(required = false) String username) {
        Map<String, Object> response = new HashMap<>();
        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "请先输入用户名");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        List<CalculationHistory> list = calculatorService.getHistory(username.trim());
        response.put("success", true);
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除指定历史记录：DELETE /api/history/{id}?username=zhangsan
     * 只能删除自己的记录
     */
    @DeleteMapping("/history/{id}")
    public ResponseEntity<Map<String, Object>> deleteHistory(@PathVariable Long id,
                                                             @RequestParam(required = false) String username) {
        Map<String, Object> response = new HashMap<>();
        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "请先输入用户名");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        boolean deleted = calculatorService.deleteHistory(id, username.trim());
        if (deleted) {
            response.put("success", true);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "记录不存在或不属于当前用户");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 清空某个用户的全部历史记录：DELETE /api/history?username=zhangsan
     */
    @DeleteMapping("/history")
    public ResponseEntity<Map<String, Object>> clearHistory(@RequestParam(required = false) String username) {
        Map<String, Object> response = new HashMap<>();
        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "请先输入用户名");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        calculatorService.clearHistory(username.trim());
        response.put("success", true);
        response.put("message", "已清空你的全部历史记录");
        return ResponseEntity.ok(response);
    }
}
