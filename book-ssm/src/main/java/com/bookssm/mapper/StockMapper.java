package com.bookssm.mapper;

import com.bookssm.entity.StockBatch;
import com.bookssm.entity.StockRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库存 Mapper 接口
 */
public interface StockMapper {

    Integer findBookStockForUpdate(@Param("bookId") Long bookId);

    int updateBookStock(@Param("bookId") Long bookId, @Param("stock") Integer stock);

    int insertBatch(StockBatch batch);

    int insertRecord(StockRecord record);

    StockRecord findRecordById(@Param("id") Long id);

    List<StockBatch> findAvailableBatchesForUpdate(@Param("bookId") Long bookId);

    int updateBatchRemain(@Param("id") Long id,
                          @Param("beforeRemain") Integer beforeRemain,
                          @Param("afterRemain") Integer afterRemain);

    long countBatches(@Param("bookId") Long bookId);

    List<StockBatch> findBatches(@Param("bookId") Long bookId,
                                 @Param("offset") int offset,
                                 @Param("pageSize") int pageSize);

    long countRecords(@Param("bookId") Long bookId,
                      @Param("type") String type);

    List<StockRecord> findRecords(@Param("bookId") Long bookId,
                                  @Param("type") String type,
                                  @Param("offset") int offset,
                                  @Param("pageSize") int pageSize);

    Integer findTotalStock(@Param("bookId") Long bookId);
}
