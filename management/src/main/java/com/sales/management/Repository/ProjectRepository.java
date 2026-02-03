package com.sales.management.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sales.management.entity.ProjectEntity;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    List<ProjectEntity> findBySalesMail(String salesMail);

    List<ProjectEntity> findByStatus(String status);
    long countByStatus(String status);

    long countBySalesMail(String salesMail);
    long countBySalesMailAndStatus(String salesMail, String status);

    long countBySalesMailAndStartDateBetween(
    	    String mail, LocalDate start, LocalDate end);

    	long countBySalesMailAndStatusAndStartDateBetween(
    	    String mail, String status, LocalDate start, LocalDate end);
    
}
