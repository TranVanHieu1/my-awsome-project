package com.ojt.mockproject.repository;

import com.ojt.mockproject.entity.Enum.OrderStatusEnum;
import com.ojt.mockproject.entity.Orderr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orderr, Integer> {
    List<Orderr> findByStatus(OrderStatusEnum status);

    List<Orderr> findByAccountId(Integer accountId);

    List<Orderr> findByAccountIdAndStatus(Integer accountId, OrderStatusEnum status);

    List<Orderr> findByCoursesContaining(String coursesId);

    List<Orderr> findByStatusAndIsDeleted(OrderStatusEnum status, boolean b);

    List<Orderr> findByAccountIdAndIsDeleted(Integer accountId, boolean b);

    List<Orderr> findByAccountIdAndStatusAndIsDeleted(Integer accountId, OrderStatusEnum status, boolean b);

    List<Orderr> findAllByStatusAndCreateAtBetweenAndIsDeleted(OrderStatusEnum orderStatusEnum, LocalDateTime startDate, LocalDateTime endDate, boolean b);
}
