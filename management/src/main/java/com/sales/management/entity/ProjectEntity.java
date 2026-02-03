package com.sales.management.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long leadid;

    /* Client & Project info */
    private String projectName;
    private String clientName;
    private String projectType;
    private String budget;

    private String salesMail;

    private String teamName;    // IT team assigned
    private String teamNo;

    /* Timeline */
    private LocalDate startDate;
    private LocalDate endDate;

    /* Status */
    private String status;      // Active, On Hold, Completed

    private LocalDate createdDate;

    @PrePersist
    public void onCreate() {
        this.createdDate = LocalDate.now();
    }
}
