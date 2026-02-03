package com.sales.management.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sales.management.Repository.LeadsRepository;
import com.sales.management.Repository.ProjectRepository;
import com.sales.management.dto.ApiResponse;
import com.sales.management.entity.LeadsEntity;
import com.sales.management.entity.ProjectEntity;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepo;
    
    
    @Autowired
    private LeadsRepository leadrepo;

    /* ================= ASSIGN / CREATE PROJECT ================= */

    @PostMapping("/assignproject")
    public ApiResponse assignProject(@RequestBody ProjectEntity project) {
        try {
            if (project == null) {
                return new ApiResponse(false, "Invalid project data");
            }
            
            Optional<LeadsEntity> lead = leadrepo.findById(project.getLeadid());
            lead.get().setStatus("Assigned");
            leadrepo.save(lead.get());
            
            projectRepo.save(project);
            return new ApiResponse(true, "Project assigned successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(false, "Something went wrong while assigning project");
        }
    }
    
    
    
    /* ================= GET PROJECTS BY USER MAIL ================= */

    @GetMapping("/getprojects/by-mail/{mail}")
    public ApiResponse getProjectsByMail(@PathVariable String mail) {
        try {
            if (mail == null || mail.isEmpty()) {
                return new ApiResponse(false, "Mail is required");
            }

            List<ProjectEntity> projects = projectRepo.findBySalesMail(mail);
            return new ApiResponse(true, "User projects fetched successfully", projects);

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(false, "Something went wrong while fetching user projects");
        }
    }

    
    

    /* ================= GET ALL PROJECTS (ADMIN) ================= */

    @GetMapping("/getallprojects")
    public ApiResponse getAllProjects() {
        try {
            List<ProjectEntity> projects = projectRepo.findAll();
            return new ApiResponse(true, "Projects fetched successfully", projects);

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(false, "Something went wrong while fetching projects");
        }
    }

    /* ================= UPDATE PROJECT ================= */

    @PutMapping("/updateproject")
    public ApiResponse updateProject(@RequestBody ProjectEntity project) {
        try {
            if (project == null || project.getId() == null) {
                return new ApiResponse(false, "Project ID is required");
            }

            if (!projectRepo.existsById(project.getId())) {
                return new ApiResponse(false, "Project not found");
            }

            projectRepo.save(project);
            return new ApiResponse(true, "Project updated successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(false, "Something went wrong while updating project");
        }
    }

    /* ================= DELETE PROJECT (OPTIONAL) ================= */

    @DeleteMapping("/removeproject/{id}")
    public ApiResponse removeProject(@PathVariable Long id) {
        try {
            if (id == null) {
                return new ApiResponse(false, "Project ID is required");
            }

            if (!projectRepo.existsById(id)) {
                return new ApiResponse(false, "Project not found");
            }

            projectRepo.deleteById(id);
            return new ApiResponse(true, "Project removed successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(false, "Something went wrong while deleting project");
        }
    }
}
