package com.bookssm.service;

import com.bookssm.dto.PageResult;
import com.bookssm.entity.StockBatch;
import com.bookssm.entity.StockRecord;

import java.math.BigDecimal;

/**
 * 库存服务接口
 */
public interface StockService {

    StockRecord inStock(Long bookId, Integer quantity, BigDecimal costPrice,
                        String supplier, String remark, Long operatorId);

    PageResult<StockBatch> listBatches(Long bookId, Integer page, Integer pageSize);

    PageResult<StockRecord> listRecords(Long bookId, String type, Integer page, Integer pageSize);

    Integer getTotalStock(Long bookId);
}
