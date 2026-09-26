package com.smarthospital.prateek.repository;

import com.smarthospital.prateek.entity.LabTest;
import com.smarthospital.prateek.entity.LabTestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabTestRepository
        extends JpaRepository<LabTest, Long> {

    List<LabTest> findByStatus(LabTestStatus status);

    List<LabTest> findByPatientId(Long patientId);

    List<LabTest> findByDoctorId(Long doctorId);
}