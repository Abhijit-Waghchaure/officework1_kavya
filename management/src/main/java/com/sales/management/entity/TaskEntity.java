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
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long leadId;          // reference to lead
    private String salesMail;     // who has to do the task
    private String clientName;

    private String taskType;      // FOLLOW_UP | CALL | QUOTATION
    private String note;

    private LocalDate taskDate;   // when task must be done
    private String status;        // PENDING | DONE

    private LocalDate createdDate;

    @PrePersist
    public void onCreate() {
        this.createdDate = LocalDate.now();
        this.status = "PENDING";
    }
}
