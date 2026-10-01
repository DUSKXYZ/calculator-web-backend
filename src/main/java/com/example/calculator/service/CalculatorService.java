package com.example.calculator.service;

import com.example.calculator.model.CalculationHistory;
import com.example.calculator.repository.CalculationHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 计算器业务逻辑层：负责调用表达式求值、操作数据库
 * 历史记录按用户名隔离，每个人只能看到和管理自己的记录
 */
@Service
public class CalculatorService {

    private final CalculationHistoryRepository historyRepository;

    public CalculatorService(CalculationHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /**
     * 计算表达式并把结果保存到数据库，返回保存好的历史记录
     */
    public CalculationHistory calculate(String username, String expression) {
        // 去掉空格后再计算
        String expr = expression.replaceAll("\\s+", "");
        double value = ExpressionEvaluator.evaluate(expr);

        CalculationHistory history = new CalculationHistory();
        history.setUsername(username);
        history.setExpression(expr);
        history.setResult(ExpressionEvaluator.formatResult(value));
        return historyRepository.save(history);
    }

    /** 查询某个用户的历史记录，最新的在前面 */
    public List<CalculationHistory> getHistory(String username) {
        return historyRepository.findByUsernameOrderByCreatedAtDesc(username);
    }

    /**
     * 删除一条历史记录。
     * 只有记录存在并且属于这个用户时才会删除，防止删掉别人的记录。
     */
    public boolean deleteHistory(Long id, String username) {
        Optional<CalculationHistory> record = historyRepository.findById(id);
        if (record.isEmpty()) {
            return false;
        }
        if (!record.get().getUsername().equals(username)) {
            return false; // 不是自己的记录，不允许删除
        }
        historyRepository.deleteById(id);
        return true;
    }

    /** 清空某个用户的全部历史记录 */
    @Transactional
    public void clearHistory(String username) {
        historyRepository.deleteByUsername(username);
    }
}
