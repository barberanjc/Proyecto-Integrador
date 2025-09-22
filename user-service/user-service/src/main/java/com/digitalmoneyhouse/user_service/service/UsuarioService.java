package com.digitalmoneyhouse.user_service.service;

import com.digitalmoneyhouse.user_service.dto.UsuarioRegistroDTO;
import com.digitalmoneyhouse.user_service.dto.UsuarioRespuestaDTO;
import com.digitalmoneyhouse.user_service.model.Usuario;

public interface UsuarioService {
    UsuarioRespuestaDTO registrarUsuario(UsuarioRegistroDTO dto);
    UsuarioRespuestaDTO obtenerUsuarioPorId(Long id);
    UsuarioRespuestaDTO obtenerUsuarioPorEmail(String email);
    Usuario getUsuarioEntityByEmail(String email);
}
