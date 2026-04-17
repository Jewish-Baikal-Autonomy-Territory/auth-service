package org.example.ai_auth_service.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "verification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String verificationCode;
}
