package com.bookssm.controller;

import com.bookssm.dto.PageResult;
import com.bookssm.dto.Result;
import com.bookssm.entity.StockBatch;
import com.bookssm.entity.StockRecord;
import com.bookssm.service.StockService;
import com.bookssm.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;

/**
 * 库存管理控制器
 */
@RestController
@RequestMapping("/api/stocks")
public class StockController {

    @Autowired
    private StockService stockService;

    @PostMapping("/in")
    public Result<StockRecord> inStock(
            @RequestParam("bookId") Long bookId,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("costPrice") BigDecimal costPrice,
            @RequestParam(value = "supplier", required = false) String supplier,
            @RequestParam(value = "remark", required = false) String remark,
            HttpSession session) {
        Long operatorId = SessionUtil.getLoginUserId(session);
        return Result.success("入库成功", stockService.inStock(bookId, quantity, costPrice,
                supplier, remark, operatorId));
    }

    @GetMapping("/batches")
    public Result<PageResult<StockBatch>> batches(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(stockService.listBatches(null, page, pageSize));
    }

    @GetMapping("/books/{bookId}/batches")
    public Result<PageResult<StockBatch>> bookBatches(
            @PathVariable("bookId") Long bookId,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(stockService.listBatches(bookId, page, pageSize));
    }

    @GetMapping("/records")
    public Result<PageResult<StockRecord>> records(
            @RequestParam(value = "bookId", required = false) Long bookId,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(stockService.listRecords(bookId, type, page, pageSize));
    }

    @GetMapping("/books/{bookId}/total")
    public Result<Integer> totalStock(@PathVariable("bookId") Long bookId) {
        return Result.success(stockService.getTotalStock(bookId));
    }
}
