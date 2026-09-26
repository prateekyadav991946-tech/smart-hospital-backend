package com.smarthospital.prateek.config;

import com.smarthospital.prateek.entity.*;
import com.smarthospital.prateek.repository.AppointmentRepository;
import com.smarthospital.prateek.repository.DoctorRepository;
import com.smarthospital.prateek.repository.PatientRepository;
import com.smarthospital.prateek.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner createDemoData(
            UserRepository userRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // =========================================================
            // ADMIN
            // =========================================================

            createUserIfMissing(
                    userRepository,
                    passwordEncoder,
                    "admin",
                    "Admin@123",
                    Role.ADMIN,
                    null
            );

            // =========================================================
            // DEMO PATIENTS + PATIENT ACCOUNTS
            // =========================================================

            Patient amit = createPatientIfMissing(
                    patientRepository,
                    "Amit Kumar",
                    "amit2@gmail.com",
                    "9876543213",
                    "Male",
                    22,
                    "O+"
            );

            Patient rahul = createPatientIfMissing(
                    patientRepository,
                    "Rahul Verma",
                    "rahul@gmail.com",
                    "9876543214",
                    "Male",
                    25,
                    "A+"
            );

            Patient pooja = createPatientIfMissing(
                    patientRepository,
                    "Pooja Yadav",
                    "pooja@gmail.com",
                    "9876543215",
                    "Female",
                    23,
                    "B+"
            );

            createUserIfMissing(
                    userRepository,
                    passwordEncoder,
                    "amit2@gmail.com",
                    "patient123",
                    Role.PATIENT,
                    amit
            );

            createUserIfMissing(
                    userRepository,
                    passwordEncoder,
                    "rahul@gmail.com",
                    "patient123",
                    Role.PATIENT,
                    rahul
            );

            createUserIfMissing(
                    userRepository,
                    passwordEncoder,
                    "pooja@gmail.com",
                    "patient123",
                    Role.PATIENT,
                    pooja
            );

            // =========================================================
            // DEMO DOCTORS
            // =========================================================

            Doctor rajesh = createDoctorIfMissing(
                    doctorRepository,
                    "Dr. Rajesh Sharma",
                    "rajesh.sharma@hospital.com",
                    "9876543212",
                    "Cardiology",
                    "MBBS, MD",
                    12,
                    "Cardiology"
            );

            Doctor priya = createDoctorIfMissing(
                    doctorRepository,
                    "Dr. Priya Verma",
                    "priya.verma@hospital.com",
                    "9876543216",
                    "Neurology",
                    "MBBS, MD",
                    8,
                    "Neurology"
            );

            // =========================================================
            // DEMO STAFF LOGINS
            // =========================================================

            createUserIfMissing(
                    userRepository,
                    passwordEncoder,
                    "doctor1",
                    "doctor123",
                    Role.DOCTOR,
                    null
            );

            createUserIfMissing(
                    userRepository,
                    passwordEncoder,
                    "reception1",
                    "reception123",
                    Role.RECEPTIONIST,
                    null
            );

            createUserIfMissing(
                    userRepository,
                    passwordEncoder,
                    "lab2",
                    "lab12345",
                    Role.LAB_TECHNICIAN,
                    null
            );

            // =========================================================
            // DEMO APPOINTMENTS - ONLY IF DATABASE HAS NONE
            // =========================================================

            if (appointmentRepository.count() == 0) {

                Appointment a1 = new Appointment();
                a1.setPatient(amit);
                a1.setDoctor(rajesh);
                a1.setAppointmentDateTime(
                        LocalDateTime.now().plusDays(1)
                                .withHour(10)
                                .withMinute(30)
                                .withSecond(0)
                                .withNano(0)
                );
                a1.setReason("Regular heart checkup");
                a1.setStatus(AppointmentStatus.SCHEDULED);
                appointmentRepository.save(a1);

                Appointment a2 = new Appointment();
                a2.setPatient(rahul);
                a2.setDoctor(priya);
                a2.setAppointmentDateTime(
                        LocalDateTime.now().plusDays(1)
                                .withHour(11)
                                .withMinute(0)
                                .withSecond(0)
                                .withNano(0)
                );
                a2.setReason("Neurology consultation");
                a2.setStatus(AppointmentStatus.SCHEDULED);
                appointmentRepository.save(a2);
            }
        };
    }

    private Patient createPatientIfMissing(
            PatientRepository repository,
            String name,
            String email,
            String phone,
            String gender,
            Integer age,
            String bloodGroup) {

        return repository.findByEmail(email)
                .orElseGet(() -> {
                    Patient patient = new Patient();
                    patient.setName(name);
                    patient.setEmail(email);
                    patient.setPhone(phone);
                    patient.setGender(gender);
                    patient.setAge(age);
                    patient.setBloodGroup(bloodGroup);
                    return repository.save(patient);
                });
    }

    private Doctor createDoctorIfMissing(
            DoctorRepository repository,
            String name,
            String email,
            String phone,
            String specialization,
            String qualification,
            Integer experience,
            String department) {

        return repository.findAll()
                .stream()
                .filter(d -> d.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseGet(() -> {
                    Doctor doctor = new Doctor();
                    doctor.setName(name);
                    doctor.setEmail(email);
                    doctor.setPhone(phone);
                    doctor.setSpecialization(specialization);
                    doctor.setQualification(qualification);
                    doctor.setExperience(experience);
                    doctor.setDepartment(department);
                    doctor.setAvailable(true);
                    return repository.save(doctor);
                });
    }

    private User createUserIfMissing(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            String username,
            String password,
            Role role,
            Patient patient) {

        User existing = repository.findByUsername(username).orElse(null);

        if (existing != null) {
            if (role == Role.PATIENT && existing.getPatient() == null && patient != null) {
                existing.setPatient(patient);
                return repository.save(existing);
            }
            return existing;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);
        user.setPatient(patient);

        return repository.save(user);
    }
}
