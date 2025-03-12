package com.ojt.mockproject.repository;

import com.ojt.mockproject.dto.WalletLog.Response.WalletLogAccountResponseDTO;
import com.ojt.mockproject.dto.WalletLog.Response.WalletLogResponseDTO;
import com.ojt.mockproject.entity.Enum.WalletLogTypeEnum;
import com.ojt.mockproject.entity.WalletLog;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalletLogRepository extends JpaRepository<WalletLog, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<WalletLog> findByWalletId(Integer walletId);

    //find wallet-logs by accountId (in Wallet)
    @Query("SELECT new com.ojt.mockproject.dto.WalletLog.Response.WalletLogResponseDTO(" +
            "wl.id, wl.wallet.id, wl.type, wl.amount, wl.createAt, wl.isDeleted) " +
            "FROM WalletLog wl " +
            "WHERE wl.wallet.account.id = :accountId")
    List<WalletLogResponseDTO> findAllByAccountId(Integer accountId);

    @Query("SELECT new com.ojt.mockproject.dto.WalletLog.Response.WalletLogAccountResponseDTO(" +
            "wl.id, wl.wallet.id, wl.transaction.orderr.id,wl.transaction.id,wl.type, wl.amount, wl.createAt, wl.isDeleted) " +
            "FROM WalletLog wl " +
            "WHERE wl.wallet.account.id = :accountId")
    List<WalletLogAccountResponseDTO> findAllByAccountId1(Integer accountId);

    //find by month
    @Query("SELECT wl FROM WalletLog wl WHERE MONTH(wl.createAt) = :month AND YEAR(wl.createAt) = :year")
    List<WalletLog> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    List<WalletLog> findByWalletIdAndTypeAndCreateAtAfter(Integer walletId, WalletLogTypeEnum type, LocalDateTime createAt);

    @Query("SELECT wl FROM WalletLog wl WHERE DAY(wl.createAt) = :day AND MONTH(wl.createAt) = :month AND YEAR(wl.createAt) = :year")
    List<WalletLog> findWalletLogsByDate(@Param("day") int day, @Param("month") int month, @Param("year") int year);


    // total amount, count sale in 12 days before
    @Query("SELECT DATE(wl.createAt) as date, COUNT(wl) as count, SUM(wl.amount) as totalAmount " +
            "FROM WalletLog wl WHERE wl.createAt BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(wl.createAt) ORDER BY DATE(wl.createAt) DESC")
    List<Object[]> findLast12Days(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<WalletLog> findByWalletIdAndIsDeleted(Integer walletId, boolean b);
}