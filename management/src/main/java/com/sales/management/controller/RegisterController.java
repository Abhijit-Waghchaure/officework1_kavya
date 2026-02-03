package com.sales.management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sales.management.Repository.RegisterInterface;
import com.sales.management.dto.ApiResponse;
import com.sales.management.entity.RegisterEntity;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class RegisterController {

	@Autowired
	RegisterInterface registerrepo;
	
	@GetMapping("getalluser")
	public ApiResponse getalluser() {
	    try {
	        List<RegisterEntity> users = registerrepo.findAll();
	        return new ApiResponse(true, "Users fetched successfully", users);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ApiResponse(false, "Something went wrong while fetching users");
	    }
	}

	
	@PostMapping("registeruser")
	public ApiResponse registeruser(@RequestBody RegisterEntity user) {
	    try {
	        if (user == null) {
	            return new ApiResponse(false, "Invalid request body");
	        }

	        registerrepo.save(user);
	        return new ApiResponse(true, "User registered successfully");

	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ApiResponse(false, "Something went wrong while registering user");
	    }
	}

	
	@PutMapping("updateuser")
	public ApiResponse updateuser(@RequestBody RegisterEntity user) {
	    try {
	        if (user == null || user.getId() == null) {
	            return new ApiResponse(false, "User ID is required for update");
	        }

	        if (!registerrepo.existsById(user.getId())) {
	            return new ApiResponse(false, "User not found");
	        }

	        registerrepo.save(user);
	        return new ApiResponse(true, "User updated successfully");

	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ApiResponse(false, "Something went wrong while updating user");
	    }
	}



	@DeleteMapping("removeuser/{id}")
	public ApiResponse removeuser(@PathVariable Long id) {
	    try {
	        if (id == null) {
	            return new ApiResponse(false, "User ID is required");
	        }

	        if (!registerrepo.existsById(id)) {
	            return new ApiResponse(false, "User not found");
	        }

	        registerrepo.deleteById(id);
	        return new ApiResponse(true, "User removed successfully");

	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ApiResponse(false, "Something went wrong while deleting user");
	    }
	}



}
