package com.sales.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sales.management.Repository.RegisterInterface;
import com.sales.management.dto.ApiResponse;
import com.sales.management.dto.LoginResponse;
import com.sales.management.entity.RegisterEntity;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class LoginController {

    @Autowired
    private RegisterInterface registerrepo;

    @PostMapping("/login")
    public ApiResponse login(@RequestBody RegisterEntity request) {
        try {
            if (request.getMail() == null || request.getPassword() == null) {
                return new ApiResponse(false, "Email and password are required");
            }

            RegisterEntity user = registerrepo.findByMail(request.getMail());

            if (user == null) {
                return new ApiResponse(false, "Wrong credentials");
            }

            if (!user.getPassword().equals(request.getPassword())) {
                return new ApiResponse(false, "Wrong credentials");
            }

            LoginResponse loginResponse = new LoginResponse(
                user.getMail(),
                user.getRole()
            );

            return new ApiResponse(
                true,
                "Login successful",
                loginResponse
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(false, "Something went wrong during login");
        }
    }

}
