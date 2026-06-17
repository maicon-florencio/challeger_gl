package com.stream.globo.application.usuario;

import com.stream.globo.domain.usuario.entity.Usuario;
import com.stream.globo.domain.usuario.UsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class UsuarioCriarUseCase {

    private final UsuarioRepository repository;
    private final UsuarioCriacaoValidator validator;

    public UsuarioCriarUseCase(UsuarioRepository repository, UsuarioCriacaoValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public UsuarioResponse create(UsuarioCriarRequest criarRequest) {

        validator.validarEmail(criarRequest.email());
        validator.validarCpf(criarRequest.cpf());

        Usuario novoUsuario = Usuario.criar(
            criarRequest.nome(),
            criarRequest.email(),
            criarRequest.cpf()
        );

        Usuario usuarioSalvo = repository.save(novoUsuario);

        return mapearParaResponse(usuarioSalvo);
    }

    private UsuarioResponse mapearParaResponse(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getUsuarioId().getValue(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getCpf(),
            usuario.getCriacaoDt(),
            usuario.getStatus()
        );
    }
}
