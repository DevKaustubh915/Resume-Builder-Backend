package com.example.resumebuilderapi.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.resumebuilderapi.document.Resume;
import com.example.resumebuilderapi.dto.AuthResponse;
import com.example.resumebuilderapi.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {
    private final Cloudinary cloudinary;
    private final AuthService authService;
    private final ResumeRepository resumeRepository;

    public Map<String , String> uploadSingleImage(MultipartFile file) throws IOException {

        Map<String , Object> imageUploadResult =  cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type" , "image"));

        log.info("Inside FileUploadService: uploadSingleImage() {}", imageUploadResult.get("secure_url").toString());

        return Map.of("imageUrl", imageUploadResult.get("secure_url").toString());


    }

    public Map<String, String> uploadResumeImages(String resumeId,
                                                  Object principal,
                                                  MultipartFile thumbnail,
                                                  MultipartFile profileImage) throws IOException {

        //step 1: get the current profile
        AuthResponse response = authService.getProfile(principal);

        //step 2: get the existing resume
        Resume existingResume = resumeRepository.findResumeByUserIdAndId(response.getId(), resumeId)
                .orElseThrow(()-> new RuntimeException("Resume not found"));

        //upload the resume images and set the resume
        Map<String, String> returnvalue = new HashMap<>();
        Map<String, String> uploadResult ;
        if (Objects.nonNull(thumbnail)){
            uploadResult = uploadSingleImage(thumbnail);
            existingResume.setThumbnailLink(uploadResult.get("imageUrl"));
            returnvalue.put("thumbnailLink", uploadResult.get("imageUrl"));
        }

        if (Objects.nonNull(profileImage)){
            uploadResult = uploadSingleImage(profileImage);
            if (Objects.isNull(existingResume.getProfileInfo())){
                existingResume.setProfileInfo(new Resume.ProfileInfo());
            }
            existingResume.getProfileInfo().setProfilePreviewUrl(uploadResult.get("imageUrl"));
            returnvalue.put("profilePreviewUrl", uploadResult.get("imageUrl"));
        }

        //step 4: update the details into database
        resumeRepository.save(existingResume);
        returnvalue.put("message", "Images Uploaded Successfully");


        //return the result
        return returnvalue;
    }
}
