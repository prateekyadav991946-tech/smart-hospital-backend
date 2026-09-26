package com.smarthospital.prateek.repository;

import com.smarthospital.prateek.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}