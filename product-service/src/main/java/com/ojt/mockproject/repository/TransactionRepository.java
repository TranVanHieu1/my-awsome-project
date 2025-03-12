package com.ojt.mockproject.repository;

import com.ojt.mockproject.dto.Transaction.Responses.GetCashoutTransactionResponse;
import com.ojt.mockproject.entity.Enum.TransactionTypeEnum;
import com.ojt.mockproject.entity.Enum.WalletLogTypeEnum;

import com.ojt.mockproject.entity.Orderr;
import com.ojt.mockproject.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository  extends JpaRepository<Transaction, Integer> {
    List<Transaction> findTransactionByAccountIdAndType(Integer accountId, TransactionTypeEnum type);
    List<Transaction> findByOrderrAndCreateAtBetween(Orderr orderr, LocalDateTime start, LocalDateTime end);
}
