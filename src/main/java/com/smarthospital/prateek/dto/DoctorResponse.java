package com.smarthospital.prateek.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String specialization;
    private String qualification;
    private Integer experience;
    private String department;
    private Boolean available;
}