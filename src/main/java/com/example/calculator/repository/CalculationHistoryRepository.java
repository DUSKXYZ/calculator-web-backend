package com.example.calculator.repository;

import com.example.calculator.model.CalculationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 计算历史的数据库操作接口
 * JpaRepository 已经自带了 save、findAll、deleteById 等常用方法
 */
@Repository
public interface CalculationHistoryRepository extends JpaRepository<CalculationHistory, Long> {

    /** 按用户名查询历史记录，按计算时间倒序，让最新的记录显示在最前面 */
    List<CalculationHistory> findByUsernameOrderByCreatedAtDesc(String username);

    /** 删除某个用户的全部历史记录 */
    void deleteByUsername(String username);
}
