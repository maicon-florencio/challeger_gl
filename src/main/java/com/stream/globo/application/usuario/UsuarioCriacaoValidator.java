package com.stream.globo.application.usuario;

import com.stream.globo.shared.exception.BussinessException;
import com.stream.globo.domain.usuario.UsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class UsuarioCriacaoValidator {

    private final UsuarioRepository repository;

    public UsuarioCriacaoValidator(UsuarioRepository repository) {
        this.repository = repository;
    }

    public void validarEmail(String email) {
        if (repository.existsByEmail(email)) {
            throw new BussinessException("Email já cadastrado: " + email);
        }
    }

    public void validarCpf(String cpf) {
        final String cpfLimpo = cpf.replaceAll("\\D", "");

        if (repository.existsByCpf(cpfLimpo)) {
            throw new BussinessException("CPF já cadastrado: " + cpf);
        }
    }


}
