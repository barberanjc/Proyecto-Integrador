package com.digitalmoneyhouse.user_service.controller;
import com.digitalmoneyhouse.user_service.dto.UsuarioAuthDTO;
import com.digitalmoneyhouse.user_service.dto.UsuarioRegistroDTO;
import com.digitalmoneyhouse.user_service.dto.UsuarioRespuestaDTO;
import com.digitalmoneyhouse.user_service.model.Usuario;
import com.digitalmoneyhouse.user_service.repository.UsuarioRepository;
import com.digitalmoneyhouse.user_service.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioRespuestaDTO> registrarUsuario(@Valid @RequestBody UsuarioRegistroDTO dto) {
        UsuarioRespuestaDTO respuesta = usuarioService.registrarUsuario(dto);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioRespuestaDTO> getByEmail(@PathVariable String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Usuario usuario = usuarioOpt.get();

        UsuarioRespuestaDTO dto = new UsuarioRespuestaDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getDni(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getCvu(),
                usuario.getAlias()
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/internal/email")
    public ResponseEntity<UsuarioAuthDTO> getUsuarioAuthByEmail(@RequestParam String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOpt.get();

        UsuarioAuthDTO dto = new UsuarioAuthDTO();
        dto.setEmail(usuario.getEmail());
        dto.setPassword(usuario.getPassword());

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRespuestaDTO> obtenerUsuario(@PathVariable Long id) {
        try {
            UsuarioRespuestaDTO usuario = usuarioService.obtenerUsuarioPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

