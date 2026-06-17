package com.stream.globo.application.assinatura;

import com.stream.globo.domain.assinatura.AssinaturaRepository;
import com.stream.globo.domain.plano.entity.Plano;
import com.stream.globo.domain.plano.PlanoId;
import com.stream.globo.domain.plano.PlanoRepository;
import com.stream.globo.domain.usuario.entity.Usuario;
import com.stream.globo.domain.usuario.UsuarioId;
import com.stream.globo.domain.usuario.UsuarioRepository;
import com.stream.globo.shared.StatusEnum;
import com.stream.globo.shared.exception.BussinessException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AssinaturaCreateValidator {

    private final PlanoRepository planoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AssinaturaRepository assinaturaRepository;

    public AssinaturaCreateValidator(PlanoRepository planoRepository, UsuarioRepository usuarioRepository, AssinaturaRepository assinaturaRepository) {
        this.planoRepository = planoRepository;
        this.usuarioRepository = usuarioRepository;
        this.assinaturaRepository = assinaturaRepository;
    }

    public Plano validarPlano(PlanoId planoId){
        return planoRepository.findByAggregateId(planoId.getValue()).orElseThrow(() -> new EntityNotFoundException("Plano informado não encontrado: " + planoId.getValue()));
    }

    public Usuario validarUsuario(final UsuarioId usuarioId){
        return usuarioRepository.findByAggregateId(usuarioId.getValue()).orElseThrow(() -> new EntityNotFoundException("Usuario informado não encontrado: " + usuarioId.getValue()));
    }

    public void validarAssinaturaAtiva(final Usuario usuario){

        assinaturaRepository.findByUsuarioAggregateIdAndStatus(usuario.getUsuarioId().getValue(), StatusEnum.ATIVO)
                .ifPresent(assinatura -> {
                    throw new BussinessException("Usuario já possui uma assinatura ativa.");
                });

    }
}
