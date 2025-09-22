package com.digitalmoneyhouse.user_service.config;

import com.digitalmoneyhouse.user_service.model.Usuario;
import com.digitalmoneyhouse.user_service.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encoder;

    public DataSeeder(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) return;

        Usuario juan = new Usuario();
        juan.setId(1L);
        juan.setNombre("Juan");
        juan.setApellido("Rodriguez");
        juan.setDni("12345622733228");
        juan.setEmail("juan@33ejemplo123.com");
        juan.setTelefono("1133445561");
        juan.setPassword(encoder.encode("secreta1023"));
        juan.setAlias("espíritu.vapor.ecosistema");
        juan.setCvu("0002232619012993814142");
        usuarioRepository.save(juan);

        Usuario carlos = new Usuario();
        carlos.setId(2L);
        carlos.setNombre("Carlos");
        carlos.setApellido("Perez");
        carlos.setDni("1234343427228");
        carlos.setEmail("carlos@ejempl23o123.com");
        carlos.setTelefono("1145583445561");
        carlos.setPassword(encoder.encode("claveCarlos"));
        carlos.setAlias("pozo.volcán.cascada");
        carlos.setCvu("0000763866839600730337");
        usuarioRepository.save(carlos);

        System.out.println("✅ Usuarios precargados en user-service");
    }
}