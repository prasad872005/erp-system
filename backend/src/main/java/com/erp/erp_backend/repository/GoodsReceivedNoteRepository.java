package com.erp.erp_backend.repository;

import com.erp.erp_backend.entity.GoodsReceivedNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodsReceivedNoteRepository
        extends JpaRepository<GoodsReceivedNote, Long> {

    List<GoodsReceivedNote> findByPurchaseOrderId(Long purchaseOrderId);

    List<GoodsReceivedNote> findByProductId(Long productId);
}