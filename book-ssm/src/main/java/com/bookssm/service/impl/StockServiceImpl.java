package com.bookssm.service.impl;

import com.bookssm.dto.PageResult;
import com.bookssm.entity.Book;
import com.bookssm.entity.StockBatch;
import com.bookssm.entity.StockRecord;
import com.bookssm.exception.BusinessException;
import com.bookssm.mapper.BookMapper;
import com.bookssm.mapper.StockMapper;
import com.bookssm.service.StockService;
import com.bookssm.util.BatchNoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 库存服务实现
 */
@Service
public class StockServiceImpl implements StockService {

    private static final int MAX_BATCH_NO_RETRY = 5;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private BookMapper bookMapper;

    @Override
    @Transactional
    public StockRecord inStock(Long bookId, Integer quantity, BigDecimal costPrice,
                               String supplier, String remark, Long operatorId) {
        if (bookId == null) {
            throw new BusinessException(400, "图书ID不能为空");
        }
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(400, "入库数量必须大于0");
        }
        if (costPrice == null || costPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "入库成本价必须大于0");
        }
        if (operatorId == null) {
            throw new BusinessException(401, "请先登录");
        }

        Book book = bookMapper.findById(bookId);
        if (book == null) {
            throw new BusinessException(404, "图书不存在");
        }

        Integer beforeStock = stockMapper.findBookStockForUpdate(bookId);
        if (beforeStock == null) {
            throw new BusinessException(404, "图书不存在");
        }
        Integer afterStock = beforeStock + quantity;

        StockBatch batch = new StockBatch();
        batch.setBookId(bookId);
        batch.setQuantity(quantity);
        batch.setRemainQuantity(quantity);
        batch.setCostPrice(costPrice);
        batch.setSupplier(trimToNull(supplier));
        batch.setOperatorId(operatorId);
        batch.setRemark(trimToNull(remark));
        insertBatchWithRetry(batch);

        stockMapper.updateBookStock(bookId, afterStock);

        StockRecord record = new StockRecord();
        record.setBookId(bookId);
        record.setBatchId(batch.getId());
        record.setType("IN");
        record.setQuantity(quantity);
        record.setBeforeStock(beforeStock);
        record.setAfterStock(afterStock);
        record.setOperatorId(operatorId);
        record.setRemark(trimToNull(remark));
        stockMapper.insertRecord(record);

        return stockMapper.findRecordById(record.getId());
    }

    @Override
    public PageResult<StockBatch> listBatches(Long bookId, Integer page, Integer pageSize) {
        int currentPage = normalizePage(page);
        int currentPageSize = normalizePageSize(pageSize);
        int offset = (currentPage - 1) * currentPageSize;

        long total = stockMapper.countBatches(bookId);
        List<StockBatch> list = stockMapper.findBatches(bookId, offset, currentPageSize);
        return new PageResult<>(total, currentPage, currentPageSize, list);
    }

    @Override
    public PageResult<StockRecord> listRecords(Long bookId, String type, Integer page, Integer pageSize) {
        int currentPage = normalizePage(page);
        int currentPageSize = normalizePageSize(pageSize);
        int offset = (currentPage - 1) * currentPageSize;
        String recordType = trimToNull(type);

        long total = stockMapper.countRecords(bookId, recordType);
        List<StockRecord> list = stockMapper.findRecords(bookId, recordType, offset, currentPageSize);
        return new PageResult<>(total, currentPage, currentPageSize, list);
    }

    @Override
    public Integer getTotalStock(Long bookId) {
        if (bookId == null) {
            throw new BusinessException(400, "图书ID不能为空");
        }
        Integer stock = stockMapper.findTotalStock(bookId);
        if (stock == null) {
            throw new BusinessException(404, "图书不存在");
        }
        return stock;
    }

    private void insertBatchWithRetry(StockBatch batch) {
        for (int i = 0; i < MAX_BATCH_NO_RETRY; i++) {
            batch.setBatchNo(BatchNoUtil.generate());
            try {
                stockMapper.insertBatch(batch);
                return;
            } catch (DuplicateKeyException e) {
                if (i == MAX_BATCH_NO_RETRY - 1) {
                    throw new BusinessException(500, "生成库存批次号失败，请重试");
                }
            }
        }
    }

    private int normalizePage(Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    private int normalizePageSize(Integer pageSize) {
        int value = pageSize == null || pageSize < 1 ? 10 : pageSize;
        return Math.min(value, 100);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
