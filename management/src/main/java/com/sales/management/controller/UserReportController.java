package com.sales.management.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sales.management.Repository.LeadsRepository;
import com.sales.management.Repository.ProjectRepository;
import com.sales.management.Repository.TaskRepository;
import com.sales.management.dto.ApiResponse;
import com.sales.management.dto.UserDailyReportDto;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/user/report")
public class UserReportController {

    @Autowired
    private LeadsRepository leadsRepo;

    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private ProjectRepository projectRepo;

    // 🔹 USER DAILY REPORT
    @GetMapping("/daily")
    public ApiResponse userDailyReport(
            @RequestParam String mail,
            @RequestParam(required = false) String date
    ) {
        LocalDate reportDate =
                (date != null && !date.isEmpty())
                        ? LocalDate.parse(date)
                        : LocalDate.now();

        UserDailyReportDto dto = new UserDailyReportDto();

        // ===== LEADS =====
        dto.setTotalLeads(
                leadsRepo.countByMail(mail)
        );

        dto.setLeadsToday(
                leadsRepo.countByMailAndCreatedDateBetween(
                        mail,
                        reportDate,
                        reportDate
                )
        );

        // ===== TASKS =====
        dto.setTotalTasks(
                taskRepo.countBySalesMail(mail)
        );

        dto.setTasksToday(
                taskRepo.countBySalesMailAndTaskDate(
                        mail,
                        reportDate
                )
        );

        dto.setCompletedTasksToday(
                taskRepo.countBySalesMailAndTaskDateAndStatus(
                        mail,
                        reportDate,
                        "DONE"
                )
        );

        // ===== PROJECTS =====
        dto.setTotalProjects(
                projectRepo.countBySalesMail(mail)
        );

        dto.setActiveProjects(
                projectRepo.countBySalesMailAndStatus(
                        mail,
                        "Active"
                )
        );

        return new ApiResponse(
                true,
                "User daily report",
                dto
        );
    }
}
