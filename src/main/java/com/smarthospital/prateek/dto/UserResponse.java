package com.smarthospital.prateek.dto;

import com.smarthospital.prateek.entity.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private Role role;
    private Long patientId;
    private Boolean enabled;
}