package com.sales.management.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sales.management.Repository.TaskRepository;
import com.sales.management.dto.ApiResponse;
import com.sales.management.entity.TaskEntity;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepo;
    
    
 // 🔹 ADMIN: all tasks (PENDING + DONE)
    @GetMapping("/admin/all")
    public ApiResponse adminAllTasks() {
        return new ApiResponse(
            true,
            "All tasks",
            taskRepo.findAll()
        );
    }


    // 🔹 USER: Today's tasks
    @GetMapping("/today")
    public ApiResponse todayTasks(
            @RequestParam String mail
    ) {
        return new ApiResponse(
            true,
            "Today's tasks",
            taskRepo.findBySalesMailAndTaskDate(mail, LocalDate.now())
        );
    }

    // 🔹 USER: tasks by date
    @GetMapping("/by-date")
    public ApiResponse tasksByDate(
            @RequestParam String mail,
            @RequestParam String date
    ) {
        return new ApiResponse(
            true,
            "Tasks by date",
            taskRepo.findBySalesMailAndTaskDate(
                mail,
                LocalDate.parse(date)
            )
        );
    }

    // 🔹 ADMIN: all tasks today
    @GetMapping("/admin/today")
    public ApiResponse adminTodayTasks() {
        return new ApiResponse(
            true,
            "All tasks today",
            taskRepo.findByTaskDate(LocalDate.now())
        );
    }
    
 // 🔹 ADMIN: all tasks by selected date
    @GetMapping("/admin/by-date")
    public ApiResponse adminTasksByDate(
            @RequestParam String date
    ) {
        return new ApiResponse(
            true,
            "All tasks by date",
            taskRepo.findByTaskDate(LocalDate.parse(date))
        );
    }

 // 🔹 USER: all tasks
    @GetMapping("/by-mail")
    public ApiResponse userAllTasks(
            @RequestParam String mail
    ) {
        return new ApiResponse(
            true,
            "All user tasks",
            taskRepo.findBySalesMail(mail)
        );
    }
    
    
    @PutMapping("/mark-done/{id}")
    public ApiResponse markTaskDone(@PathVariable Long id) {

        TaskEntity task = taskRepo.findById(id).orElse(null);

        if (task == null) {
            return new ApiResponse(false, "Task not found");
        }

        if ("DONE".equals(task.getStatus())) {
            return new ApiResponse(false, "Task already completed");
        }

        task.setStatus("DONE");
        taskRepo.save(task);

        return new ApiResponse(true, "Task marked as DONE");
    }
    
    
 // 🔹 ADMIN / USER: tasks by mail + date
    @GetMapping("/filter")
    public ApiResponse filterTasks(
            @RequestParam(required = false) String mail,
            @RequestParam(required = false) String date
    ) {
        try {
            if (mail != null && date != null) {
                return new ApiResponse(
                    true,
                    "Tasks by mail and date",
                    taskRepo.findBySalesMailAndTaskDate(
                        mail,
                        LocalDate.parse(date)
                    )
                );
            }

            if (mail != null) {
                return new ApiResponse(
                    true,
                    "Tasks by mail",
                    taskRepo.findBySalesMail(mail)
                );
            }

            if (date != null) {
                return new ApiResponse(
                    true,
                    "Tasks by date",
                    taskRepo.findByTaskDate(LocalDate.parse(date))
                );
            }

            return new ApiResponse(
                true,
                "All tasks",
                taskRepo.findAll()
            );

        } catch (Exception e) {
            return new ApiResponse(false, "Error filtering tasks");
        }
    }



    
}
