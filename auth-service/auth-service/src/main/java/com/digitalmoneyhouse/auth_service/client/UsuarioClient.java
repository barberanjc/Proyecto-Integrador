package com.digitalmoneyhouse.auth_service.client;

import com.digitalmoneyhouse.auth_service.dto.UsuarioAuthDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UsuarioClient {

    @GetMapping("/api/usuarios/internal/email")
    UsuarioAuthDTO getUsuarioByEmail(@RequestParam("email") String email);
}