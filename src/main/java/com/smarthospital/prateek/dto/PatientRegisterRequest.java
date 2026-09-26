package com.smarthospital.prateek.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientRegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[6-9][0-9]{9}$",
            message = "Enter a valid 10-digit mobile number"
    )
    private String phone;

    @NotBlank(message = "Gender is required")
    @Pattern(
            regexp = "Male|Female|Other",
            message = "Gender must be Male, Female or Other"
    )
    private String gender;

    @NotNull(message = "Age is required")
    @Min(0)
    @Max(120)
    private Integer age;

    @NotBlank(message = "Blood group is required")
    @Pattern(
            regexp = "^(A|B|AB|O)[+-]$",
            message = "Enter a valid blood group"
    )
    private String bloodGroup;

    @NotBlank(message = "Password is required")
    @Size(min = 6)
    private String password;
}