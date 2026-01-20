package com.example.resumebuilderapi.controller;


import com.example.resumebuilderapi.dto.AuthResponse;
import com.example.resumebuilderapi.dto.LoginRequest;
import com.example.resumebuilderapi.dto.RegisterRequest;
import com.example.resumebuilderapi.service.AuthService;
import com.example.resumebuilderapi.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static com.example.resumebuilderapi.util.AppConstants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(AUTH_CONTROLLER)
public class AuthController {

    private final AuthService authService;
    private final FileUploadService fileUploadService;


    //Handeler methods
    @PostMapping(REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){

        log.info("Inside Authcontroller: register(): {}",request);

           AuthResponse response = authService.register(request);
           return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping(VERIFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@RequestParam String token){

        log.info("Inside Authcontroller: verifyEmail(): {}",token);

        authService.verifyEmail(token);
        return  ResponseEntity.status(HttpStatus.OK).body(Map.of("message" , "Email verified successfully!!"));

    }

    @PostMapping(UPLOAD_PROFILE)
    public ResponseEntity<?> uploadImage(@RequestPart("image")MultipartFile file) throws IOException {

        log.info("Inside Authcontroller: uploadImage()");

        Map<String, String> response = fileUploadService.uploadSingleImage(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping(LOGIN)

    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        log.info("Inside AuthController: login()");

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public String testValidationToken(){
        log.info("Inside AuthController: testValidationToken()");
        return "Token validation is working";
    }

    @PostMapping(RESEND_VERIFICATION)
    public ResponseEntity<?> resendVerification(@RequestBody Map<String,String> body){
        //step 1: get the mail from request
        String email = body.get("email");

        //step 2: Add the validations
        if (Objects.isNull(email)){
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }

        //step 3: call the service method to resend verification link
        authService.resendVerification(email);

        //step  4: return response
        return ResponseEntity.ok(Map.of("success", true, "message", "Verification email sent"));
    }

    @GetMapping(PROFILE)
    public ResponseEntity<?> getProfile(Authentication authentication ){
        //step 01: get the principle object
        Object principleObject = authentication.getPrincipal();

        //step 2: call the service method
        AuthResponse currentProfile = authService.getProfile(principleObject);

        //step 3: return the response
        return  ResponseEntity.ok(currentProfile);
    }


}
