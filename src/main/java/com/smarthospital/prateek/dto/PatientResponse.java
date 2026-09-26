package com.smarthospital.prateek.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String gender;
    private Integer age;
    private String bloodGroup;
}