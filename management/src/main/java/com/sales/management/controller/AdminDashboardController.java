package com.sales.management.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sales.management.Repository.LeadsRepository;
import com.sales.management.Repository.ProjectRepository;
import com.sales.management.dto.ApiResponse;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/admindashboard")
public class AdminDashboardController {

    @Autowired
    private LeadsRepository leadsRepo;

    @Autowired
    private ProjectRepository projectRepo;

    @GetMapping("/summary")
    public ApiResponse getSummary() {
        try {
            long totalLeads = leadsRepo.count();
            long assignedLeads = leadsRepo.countByStatus("Assigned");
            long totalProjects = projectRepo.count();
            long activeProjects = projectRepo.countByStatus("Active");

            Map<String, Object> data = new HashMap<>();
            data.put("totalLeads", totalLeads);
            data.put("assignedLeads", assignedLeads);
            data.put("totalProjects", totalProjects);
            data.put("activeProjects", activeProjects);

            return new ApiResponse(true, "Dashboard summary", data);

        } catch (Exception e) {
            return new ApiResponse(false, "Dashboard error");
        }
    }
    
    @GetMapping("/lead-summary")
    public ApiResponse leadFunnel() {
        try {
            Map<String, Long> data = new HashMap<>();
            data.put("Interested", leadsRepo.countByStatus("Interested"));
            data.put("Follow Up", leadsRepo.countByStatus("Follow Up"));
            data.put("Assigned", leadsRepo.countByStatus("Assigned"));
            data.put("Closed", leadsRepo.countByStatus("Closed"));

            return new ApiResponse(true, "Lead funnel", data);
        } catch (Exception e) {
            return new ApiResponse(false, "Error");
        }
    }

    
    @GetMapping("/sales-performance")
    public ApiResponse salesPerformance() {
        try {
            List<Map<String, Object>> result = new ArrayList<>();

            List<String> salesMails = leadsRepo.findDistinctMails();

            for (String mail : salesMails) {
                Map<String, Object> row = new HashMap<>();

                row.put("mail", mail);
                row.put("totalLeads", leadsRepo.countByMail(mail));
                row.put("assignedLeads", leadsRepo.countByMailAndStatus(mail, "Assigned"));
                row.put("closedLeads", leadsRepo.countByMailAndStatus(mail, "Closed"));
                row.put("totalProjects", projectRepo.countBySalesMail(mail));
                row.put("activeProjects", projectRepo.countBySalesMailAndStatus(mail, "Active"));

                result.add(row);
            }

            return new ApiResponse(true, "Sales performance", result);

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(false, "Error fetching performance");
        }
    }
    
    
    @GetMapping("/sales-performance/by-date")
    public ApiResponse salesPerformanceByDate(
            @RequestParam String start,
            @RequestParam String end) {

        try {
            List<Map<String, Object>> result = new ArrayList<>();

            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);

            List<String> salesMails = leadsRepo.findDistinctMails();

            for (String mail : salesMails) {
                Map<String, Object> row = new HashMap<>();

                row.put("mail", mail);
                row.put("totalLeads",
                    leadsRepo.countByMailAndCreatedDateBetween(mail, startDate, endDate));

                row.put("assignedLeads",
                    leadsRepo.countByMailAndStatusAndCreatedDateBetween(
                        mail, "Assigned", startDate, endDate));

                row.put("closedLeads",
                    leadsRepo.countByMailAndStatusAndCreatedDateBetween(
                        mail, "Closed", startDate, endDate));

                row.put("totalProjects",
                    projectRepo.countBySalesMailAndStartDateBetween(
                        mail, startDate, endDate));

                row.put("activeProjects",
                    projectRepo.countBySalesMailAndStatusAndStartDateBetween(
                        mail, "Active", startDate, endDate));

                result.add(row);
            }

            return new ApiResponse(true, "Sales performance by date", result);

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(false, "Error fetching performance by date");
        }
    }


    
    
}
