package com.smarthospital.prateek.repository;

import com.smarthospital.prateek.entity.QueueEntry;
import com.smarthospital.prateek.entity.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QueueRepository extends JpaRepository<QueueEntry, Long> {

    List<QueueEntry> findByDoctorIdAndStatusOrderByCheckedInAtAsc(
            Long doctorId,
            QueueStatus status
    );

    List<QueueEntry> findByPatientIdOrderByCheckedInAtDesc(
            Long patientId
    );

    Optional<QueueEntry> findByAppointmentId(Long appointmentId);
}
