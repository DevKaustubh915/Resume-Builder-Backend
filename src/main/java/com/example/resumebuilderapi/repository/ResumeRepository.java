package com.example.resumebuilderapi.repository;

import com.example.resumebuilderapi.document.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends MongoRepository<Resume, String> {

    // this method id not like the tutorial because tutorial method is not working for me
    List<Resume> findResumesByUserIdOrderByUpdatedAtDesc(String userId);


    // this method id not like the tutorial because tutorial method is not working for me
    Optional<Resume> findResumeByUserIdAndId(String userId, String id);
}
