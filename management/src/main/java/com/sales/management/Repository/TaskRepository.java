package com.sales.management.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sales.management.entity.TaskEntity;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    List<TaskEntity> findBySalesMailAndTaskDate(String salesMail, LocalDate taskDate);

    List<TaskEntity> findByTaskDate(LocalDate taskDate);

    List<TaskEntity> findBySalesMail(String salesMail);

    List<TaskEntity> findBySalesMailAndTaskDateBetween(
        String salesMail,
        LocalDate start,
        LocalDate end
    );
    
    TaskEntity findByLeadIdAndStatus(Long leadId, String status);

    long countBySalesMail(String salesMail);

    long countBySalesMailAndTaskDate(String salesMail, LocalDate taskDate);

    long countBySalesMailAndTaskDateAndStatus(
        String salesMail,
        LocalDate taskDate,
        String status
    );

}
