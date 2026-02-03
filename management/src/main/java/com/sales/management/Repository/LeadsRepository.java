package com.sales.management.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sales.management.entity.LeadsEntity;

public interface LeadsRepository extends JpaRepository<LeadsEntity, Long> {

    /* ================= EXISTING METHODS (UNCHANGED) ================= */

    List<LeadsEntity> findByMail(String mail);

    long countByStatus(String status);

    long countByMail(String mail);

    long countByMailAndStatus(String mail, String status);

    @Query("SELECT DISTINCT l.mail FROM LeadsEntity l")
    List<String> findDistinctMails();

    long countByCreatedDate(LocalDate date);

    List<LeadsEntity> findByCreatedDate(LocalDate date);

    List<LeadsEntity> findByCreatedDateBetween(LocalDate start, LocalDate end);

    long countByMailAndCreatedDateBetween(
        String mail, LocalDate start, LocalDate end
    );

    long countByMailAndStatusAndCreatedDateBetween(
        String mail, String status, LocalDate start, LocalDate end
    );

    /* ================================================================
       🔥 NEW: COMBINED FILTER (MAIL + STATUS + DATE RANGE)
       ================================================================ */

    @Query("""
        SELECT l FROM LeadsEntity l
        WHERE (:mail IS NULL OR l.mail = :mail)
          AND (:status IS NULL OR l.status = :status)
          AND (:startDate IS NULL OR l.createdDate >= :startDate)
          AND (:endDate IS NULL OR l.createdDate <= :endDate)
        ORDER BY l.createdDate DESC
    """)
    List<LeadsEntity> filterLeads(
        @Param("mail") String mail,
        @Param("status") String status,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
