package com.smarthospital.prateek.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;

    @NotBlank(message = "Medicine name is required")
    @Column(nullable = false)
    private String medicineName;

    @NotBlank(message = "Dosage is required")
    @Column(nullable = false)
    private String dosage;

    @NotBlank(message = "Frequency is required")
    @Column(nullable = false)
    private String frequency;

    @NotBlank(message = "Duration is required")
    @Column(nullable = false)
    private String duration;

    @Column(length = 500)
    private String instructions;
}