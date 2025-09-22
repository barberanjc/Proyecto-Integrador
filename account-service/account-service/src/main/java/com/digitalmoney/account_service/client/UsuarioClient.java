package com.digitalmoney.account_service.client;

import com.digitalmoney.account_service.dto.UsuarioRespuestaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UsuarioClient {
    @GetMapping("/api/usuarios/internal/userinfo")
    UsuarioRespuestaDTO getUsuarioByEmail(@RequestParam("email") String email);
}