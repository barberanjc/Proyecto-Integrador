package com.digitalmoneyhouse.user_service.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor

public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @Column(unique = true, nullable = false)
    private String dni;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    private String telefono;

    @NotBlank
    private String password;

    @Column(unique = true)
    private String cvu;

    @Column(unique = true)
    private String alias;
}
