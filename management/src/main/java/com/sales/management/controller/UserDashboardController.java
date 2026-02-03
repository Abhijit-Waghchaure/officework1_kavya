package com.sales.management.controller;

import java.util.HashMap;
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
@RequestMapping("/userdashboard")
public class UserDashboardController {

    @Autowired
    private LeadsRepository leadsRepo;

    @Autowired
    private ProjectRepository projectRepo;

    @GetMapping("/summary")
    public ApiResponse getUserSummary(@RequestParam String mail) {
        try {
            long totalLeads = leadsRepo.countByMail(mail);
            long assignedLeads = leadsRepo.countByMailAndStatus(mail, "Assigned");
            long totalProjects = projectRepo.countBySalesMail(mail);
            long activeProjects = projectRepo.countBySalesMailAndStatus(mail, "Active");

            Map<String, Object> data = new HashMap<>();
            data.put("totalLeads", totalLeads);
            data.put("assignedLeads", assignedLeads);
            data.put("totalProjects", totalProjects);
            data.put("activeProjects", activeProjects);

            return new ApiResponse(true, "User dashboard summary", data);

        } catch (Exception e) {
            return new ApiResponse(false, "User dashboard error");
        }
    }

    @GetMapping("/lead-summary")
    public ApiResponse userLeadFunnel(@RequestParam String mail) {
        try {
            Map<String, Long> data = new HashMap<>();
            data.put("Interested", leadsRepo.countByMailAndStatus(mail, "Interested"));
            data.put("Follow Up", leadsRepo.countByMailAndStatus(mail, "Follow Up"));
            data.put("Assigned", leadsRepo.countByMailAndStatus(mail, "Assigned"));
            data.put("Closed", leadsRepo.countByMailAndStatus(mail, "Closed"));

            return new ApiResponse(true, "User lead funnel", data);
        } catch (Exception e) {
            return new ApiResponse(false, "Error");
        }
    }
}
