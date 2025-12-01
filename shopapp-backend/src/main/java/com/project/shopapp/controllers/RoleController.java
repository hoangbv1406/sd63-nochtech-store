package com.project.shopapp.controllers;

import com.project.shopapp.models.Role;
import com.project.shopapp.shared.base.ResponseObject;
import com.project.shopapp.services.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Roles retrieved successfully.")
                .status(HttpStatus.OK)
                .data(roles)
                .build()
        );
    }

}
