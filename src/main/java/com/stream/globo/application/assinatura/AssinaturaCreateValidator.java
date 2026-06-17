package com.stream.globo.application.assinatura;

import com.stream.globo.domain.plano.entity.Plano;
import com.stream.globo.domain.plano.PlanoId;
import com.stream.globo.domain.plano.PlanoRepository;
import com.stream.globo.domain.usuario.entity.Usuario;
import com.stream.globo.domain.usuario.UsuarioId;
import com.stream.globo.domain.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AssinaturaCreateValidator {

    private final PlanoRepository planoRepository;
    private final UsuarioRepository usuarioRepository;

    public AssinaturaCreateValidator(PlanoRepository planoRepository, UsuarioRepository usuarioRepository) {
        this.planoRepository = planoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Plano validarPlano(PlanoId planoId){
        return planoRepository.findByAggregateId(planoId.getValue()).orElseThrow(() -> new EntityNotFoundException("Plano informado não encontrado: " + planoId.getValue()));
    }

    public Usuario validarUsuario(final UsuarioId usuarioId){
        return usuarioRepository.findByAggregateId(usuarioId.getValue()).orElseThrow(() -> new EntityNotFoundException("Usuario informado não encontrado: " + usuarioId.getValue()));
    }
}
