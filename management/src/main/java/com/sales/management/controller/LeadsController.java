package com.sales.management.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sales.management.Repository.LeadsRepository;
import com.sales.management.Repository.TaskRepository;
import com.sales.management.dto.ApiResponse;
import com.sales.management.entity.LeadsEntity;
import com.sales.management.entity.TaskEntity;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class LeadsController {

    @Autowired
    private LeadsRepository leadsRepo;

    /* ================= CREATE LEAD ================= */

    @PostMapping("/addlead")
    public ApiResponse addLead(@RequestBody LeadsEntity lead) {
        try {
            leadsRepo.save(lead);
            return new ApiResponse(true, "Lead added successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(false, "Error adding lead");
        }
    }

    /* ================= GET ALL LEADS ================= */

    @GetMapping("/getallleads")
    public ApiResponse getAllLeads() {
        try {
            return new ApiResponse(true, "All leads", leadsRepo.findAll());
        } catch (Exception e) {
            return new ApiResponse(false, "Error fetching leads");
        }
    }

    /* ================= GET LEADS BY USER ================= */

    @GetMapping("/getleads/{mail}")
    public ApiResponse getLeadsByMail(@PathVariable String mail) {
        try {
            return new ApiResponse(
                true,
                "Leads by mail",
                leadsRepo.findByMail(mail)
            );
        } catch (Exception e) {
            return new ApiResponse(false, "Error fetching leads");
        }
    }

    /* ================= UPDATE LEAD ================= */

    @Autowired
    private TaskRepository taskRepo;

    @PutMapping("/updatelead")
    public ApiResponse updateLead(@RequestBody LeadsEntity lead) {
        try {
            if (lead.getId() == null) {
                return new ApiResponse(false, "Lead ID required");
            }

            // 1️⃣ Update lead
            leadsRepo.save(lead);

            // 2️⃣ CREATE or UPDATE task (single active task per lead)
            if (lead.getTaskType() != null && lead.getTaskDate() != null) {

                TaskEntity task =
                    taskRepo.findByLeadIdAndStatus(lead.getId(), "PENDING");

                if (task != null) {
                    // 🔁 UPDATE existing task
                    task.setTaskType(lead.getTaskType());
                    task.setTaskDate(lead.getTaskDate());
                    task.setNote(lead.getTaskNote());
                } else {
                    // 🆕 CREATE task (first time)
                    task = new TaskEntity();
                    task.setLeadId(lead.getId());
                    task.setClientName(lead.getClientName());
                    task.setSalesMail(lead.getMail());
                    task.setTaskType(lead.getTaskType());
                    task.setTaskDate(lead.getTaskDate());
                    task.setNote(lead.getTaskNote());
                }

                taskRepo.save(task);
            }

            return new ApiResponse(true, "Lead updated");

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(false, "Error updating lead");
        }
    }



    /* ================= TODAY LEADS ================= */

    @GetMapping("/leads/today")
    public ApiResponse getTodayLeads() {
        LocalDate today = LocalDate.now();
        return new ApiResponse(
            true,
            "Today's leads",
            leadsRepo.findByCreatedDate(today)
        );
    }

    /* ================= DATE RANGE LEADS ================= */

    @GetMapping("/leads/by-date")
    public ApiResponse getLeadsByDateRange(
            @RequestParam String start,
            @RequestParam String end) {

        return new ApiResponse(
            true,
            "Leads by date range",
            leadsRepo.findByCreatedDateBetween(
                LocalDate.parse(start),
                LocalDate.parse(end)
            )
        );
    }

    /* =======================================================
       🔥 NEW: COMBINED FILTER (MAIL + STATUS + DATE)
       ======================================================= */

    @GetMapping("/leads/filter")
    public ApiResponse filterLeads(
            @RequestParam(required = false) String mail,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end
    ) {
        try {
            LocalDate startDate =
                    (start != null && !start.isEmpty())
                    ? LocalDate.parse(start)
                    : null;

            LocalDate endDate =
                    (end != null && !end.isEmpty())
                    ? LocalDate.parse(end)
                    : null;

            List<LeadsEntity> leads =
                    leadsRepo.filterLeads(mail, status, startDate, endDate);

            return new ApiResponse(true, "Filtered leads", leads);

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(false, "Error filtering leads");
        }
    }
}
