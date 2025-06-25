package com.digitalmoneyhouse.user_service.service;

import com.digitalmoneyhouse.user_service.dto.UsuarioRegistroDTO;
import com.digitalmoneyhouse.user_service.dto.UsuarioRespuestaDTO;
import com.digitalmoneyhouse.user_service.model.Usuario;
import com.digitalmoneyhouse.user_service.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    private List<String> palabras;

    @PostConstruct
    private void cargarPalabrasDesdeArchivo() {
        try {
            ClassPathResource resource = new ClassPathResource("palabras.txt");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                palabras = reader.lines().collect(Collectors.toList());
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudieron cargar las palabras.txt para alias", e);
        }
    }

    public UsuarioRespuestaDTO registrarUsuario(UsuarioRegistroDTO dto) {


        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }
        if (usuarioRepository.findByDni(dto.getDni()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con ese DNI");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setDni(dto.getDni());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setPassword(dto.getPassword());

        usuario.setCvu(generarCVUUnico());
        usuario.setAlias(generarAliasUnico());

        Usuario guardado = usuarioRepository.save(usuario);

        return new UsuarioRespuestaDTO(
                guardado.getId(),
                guardado.getNombre(),
                guardado.getApellido(),
                guardado.getDni(),
                guardado.getEmail(),
                guardado.getTelefono(),
                guardado.getCvu(),
                guardado.getAlias()
        );
    }

    private String generarCVUUnico() {
        String cvu;
        do {
            cvu = String.format("%022d", new Random().nextLong() & Long.MAX_VALUE);
        } while (usuarioRepository.existsByCvu(cvu));
        return cvu;
    }

    private String generarAliasUnico() {
        String alias;
        Random random = new Random();
        do {
            String palabra1 = palabras.get(random.nextInt(palabras.size()));
            String palabra2 = palabras.get(random.nextInt(palabras.size()));
            String palabra3 = palabras.get(random.nextInt(palabras.size()));
            alias = palabra1 + "." + palabra2 + "." + palabra3;
        } while (usuarioRepository.existsByAlias(alias));
        return alias;
    }

}

