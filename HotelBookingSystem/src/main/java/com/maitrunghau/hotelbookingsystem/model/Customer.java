package com.maitrunghau.hotelbookingsystem.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String full_name;

    @Column(name = "email",nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.Customer;

    @Column(name = "phone_number", length = 15)
    private String phone_number;

    @Column(name = "address",length = 200)
    private String address;

    @Column(name = "date_of_birth")
    private LocalDate date_of_birth;

    @Column(name = "avatar", length = 500)
    private String avatar;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}
