package com.project.shopapp.controllers;

import com.project.shopapp.components.SecurityUtils;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.dtos.UserLoginDTO;
import com.project.shopapp.dtos.UserUpdateDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidPasswordException;
import com.project.shopapp.models.Token;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.user.LoginResponse;
import com.project.shopapp.responses.user.UserResponse;
import com.project.shopapp.services.auth.AuthService;
import com.project.shopapp.services.token.TokenService;
import com.project.shopapp.services.user.UserService;
import com.project.shopapp.utils.FileUtils;
import com.project.shopapp.utils.ValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;
    private final AuthService authService;
    private final SecurityUtils securityUtils;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllUser() {
        List<User> user = userService.getAllUsers();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Users retrieved successfully.")
                .status(HttpStatus.OK)
                .data(user)
                .build()
        );
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result
    ) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(errorMessages.toString())
                    .build()
            );
        }
        if (userDTO.getEmail() == null || userDTO.getEmail().trim().isBlank()) {
            if (userDTO.getPhoneNumber() == null || userDTO.getPhoneNumber().isBlank()) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .data(null)
                        .message("At least email or phone number is required")
                        .build()
                );
            } else {
                if (!ValidationUtils.isValidPhoneNumber(userDTO.getPhoneNumber())) {
                    throw new Exception("Invalid phone number");
                }
            }
        } else {
            if (!ValidationUtils.isValidEmail(userDTO.getEmail())) {
                throw new Exception("Invalid email format");
            }
        }
        if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message("Passwords must match.")
                    .build()
            );
        }
        User user = userService.createUser(userDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(UserResponse.fromUser(user))
                .message("User registered successfully.")
                .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            HttpServletRequest request
    ) throws Exception {
        String token = authService.login(userLoginDTO);
        User userDetail = userService.getUserDetailsFromToken(token);

        String userAgent = request.getHeader("User-Agent");
        Token jwtToken = tokenService.addToken(userDetail, token, isMobileDevice(userAgent));

        LoginResponse loginResponse = LoginResponse.builder()
                .message("Login successfully")
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .id(userDetail.getId())
                .build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("User logged in successfully.")
                .data(loginResponse)
                .status(HttpStatus.OK)
                .build()
        );
    }

    @PostMapping("/login/social")
    public ResponseEntity<ResponseObject> loginSocial(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            HttpServletRequest request
    ) throws Exception {
        String token = userService.loginSocial(userLoginDTO);
        User userDetail = userService.getUserDetailsFromToken(token);

        String userAgent = request.getHeader("User-Agent");
        Token jwtToken = tokenService.addToken(userDetail, token, isMobileDevice(userAgent));

        LoginResponse loginResponse = LoginResponse.builder()
                .message("Login social successfully")
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .id(userDetail.getId())
                .build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Login social successfully")
                .data(loginResponse)
                .status(HttpStatus.OK)
                .build()
        );
    }

    @PostMapping("/details")
    public ResponseEntity<ResponseObject> getUserDetails(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws Exception {
        String extractedToken = authorizationHeader.substring(7);
        User user = userService.getUserDetailsFromToken(extractedToken);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("User details retrieved successfully.")
                .data(UserResponse.fromUser(user))
                .status(HttpStatus.OK)
                .build()
        );
    }

    @PutMapping("/details/{userId}")
    public ResponseEntity<ResponseObject> updateUserDetails(
            @PathVariable("userId") Long userId,
            @RequestBody UserUpdateDTO userUpdateDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws Exception {
        String extractedToken = authorizationHeader.substring(7);
        User user = userService.getUserDetailsFromToken(extractedToken);
        if (user.getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User updatedUser = userService.updateUser(userId, userUpdateDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("User details updated successfully. userId = " + userId)
                .data(UserResponse.fromUser(updatedUser))
                .status(HttpStatus.OK)
                .build()
        );
    }

    @PostMapping(value = "/upload-profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseObject> uploadProfileImage(
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        User loginUser = securityUtils.getLoggedInUser();
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("Image file is required.")
                    .build()
            );
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(ResponseObject.builder()
                    .message("Image file size exceeds the allowed limit of 10MB.")
                    .status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .build()
            );
        }
        if (!FileUtils.isImageFile(file)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(ResponseObject.builder()
                    .message("Uploaded file must be an image.")
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .build()
            );
        }
        // Store file and get filename
        String oldFileName = loginUser.getProfileImage();
        String imageName = FileUtils.storeFile(file);

        userService.changeProfileImage(loginUser.getId(), imageName);
        if (!StringUtils.isEmpty(oldFileName)) {
            FileUtils.deleteFile(oldFileName);
        }

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Profile image uploaded successfully.")
                .status(HttpStatus.CREATED)
                .data(imageName)
                .build()
        );
    }

    @GetMapping("/profile-images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable("imageName") String imageName) {
        try {
            java.nio.file.Path imagePath = Paths.get("uploads/" + imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
            } else {
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(
                        new UrlResource(Paths.get("uploads/default-profile-image.jpeg").toUri())
                );
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/reset-password/{userId}")
    public ResponseEntity<ResponseObject> resetPassword(@PathVariable("userId") Long userId) {
        try {
            String newPassword = UUID.randomUUID().toString().substring(0, 5);
            userService.resetPassword(userId, newPassword);
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Password reset successfully. userId = " + userId)
                    .data(newPassword)
                    .status(HttpStatus.OK)
                    .build()
            );
        } catch (InvalidPasswordException | DataNotFoundException e) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message(e.getMessage())
                    .data("")
                    .status(HttpStatus.BAD_REQUEST)
                    .build()
            );
        }
    }

    @PutMapping("/block/{userId}/{active}")
    public ResponseEntity<ResponseObject> blockOrEnable(
            @PathVariable("userId") Long userId,
            @PathVariable("active") Integer active
    ) throws Exception {
        userService.blockOrEnable(userId, active > 0);
        String message = active > 0 ? "Successfully enabled the user." : "Successfully blocked the user.";
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(message)
                .status(HttpStatus.OK)
                .data(null)
                .build()
        );
    }

    private boolean isMobileDevice(String userAgent) {
        return userAgent != null && userAgent.toLowerCase().contains("mobile");
    }

}
