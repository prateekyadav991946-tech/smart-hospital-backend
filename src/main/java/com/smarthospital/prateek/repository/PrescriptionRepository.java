package com.smarthospital.prateek.repository;

import com.smarthospital.prateek.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository
        extends JpaRepository<Prescription, Long> {

    List<Prescription> findByConsultationId(Long consultationId);
}