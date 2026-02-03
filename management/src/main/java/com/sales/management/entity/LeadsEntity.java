package com.sales.management.entity;

import java.time.LocalDate;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class LeadsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate createdDate;

    // Client / Company details
    private String clientName;
    private String companyName;

    // Sales info
    private String projectType;
    private String budgetRange;
    private String timeline;
    private String reference;

    // Ownership & status
    private String mobileno;
    private String status;   // Interested, Follow Up, Closed etc.

    // Additional info
    private String description;

    // User (sales person)
    private String mail;

    /* ================= TASK INPUT (NOT STORED IN LEADS TABLE) ================= */

    @Transient
    private String taskType;     // FOLLOW_UP | CALL | QUOTATION

    @Transient
    private LocalDate taskDate;  // when task should be done

    @Transient
    private String taskNote;

    @PrePersist
    public void onCreate() {
        this.createdDate = LocalDate.now();
    }
}
