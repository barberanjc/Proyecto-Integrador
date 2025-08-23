package com.digitalmoneyhouse.user_service.service;

import com.digitalmoneyhouse.user_service.dto.UsuarioRegistroDTO;
import com.digitalmoneyhouse.user_service.dto.UsuarioRespuestaDTO;

public interface UsuarioService {
    UsuarioRespuestaDTO registrarUsuario(UsuarioRegistroDTO dto);
    UsuarioRespuestaDTO obtenerUsuarioPorId(Long id);
}
