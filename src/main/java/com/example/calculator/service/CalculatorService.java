package com.example.calculator.service;

import com.example.calculator.model.CalculationHistory;
import com.example.calculator.repository.CalculationHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 计算器业务逻辑层：负责调用表达式求值、操作数据库
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
    public CalculationHistory calculate(String expression) {
        // 去掉空格后再计算
        String expr = expression.replaceAll("\\s+", "");
        double value = ExpressionEvaluator.evaluate(expr);

        CalculationHistory history = new CalculationHistory();
        history.setExpression(expr);
        history.setResult(ExpressionEvaluator.formatResult(value));
        return historyRepository.save(history);
    }

    /** 查询全部历史记录，最新的在前面 */
    public List<CalculationHistory> getAllHistory() {
        return historyRepository.findAllByOrderByCreatedAtDesc();
    }

    /** 根据 id 删除一条历史记录，返回是否真的删除了 */
    public boolean deleteHistory(Long id) {
        if (!historyRepository.existsById(id)) {
            return false;
        }
        historyRepository.deleteById(id);
        return true;
    }

    /** 清空全部历史记录 */
    public void clearAllHistory() {
        historyRepository.deleteAll();
    }
}
