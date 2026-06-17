package com.stream.globo.application.assinatura;

import com.stream.globo.domain.assinatura.entity.Assinatura;
import com.stream.globo.domain.assinatura.AssinaturaRepository;
import com.stream.globo.domain.plano.entity.Plano;
import com.stream.globo.domain.plano.PlanoId;
import com.stream.globo.domain.usuario.entity.Usuario;
import com.stream.globo.domain.usuario.UsuarioId;
import org.springframework.stereotype.Component;

/**
 * Caso de uso para criar uma nova assinatura.
 * Implementa a lógica de orchestração entre agregados e a publicação de eventos.
 */
@Component
public class AssinaturaCriarUseCase {

    private final AssinaturaCreateValidator validator;
    private final AssinaturaRepository repository;

    public AssinaturaCriarUseCase(AssinaturaCreateValidator validator, AssinaturaRepository repository) {
        this.validator = validator;
        this.repository = repository;
    }

    public AssinaturaResponse criar(AssinaturaCriarRequest criarRequest) {

        Plano plano = validator.validarPlano(PlanoId.from(criarRequest.planoAggregateId()));
        Usuario usuario = validator.validarUsuario(UsuarioId.from(criarRequest.usuarioAggregateId()));

        Assinatura novaAssinatura = Assinatura.criar(
                PlanoId.from(criarRequest.planoAggregateId()),
                UsuarioId.from(criarRequest.usuarioAggregateId())
        );


        Assinatura assinaturaSalva = repository.save(novaAssinatura);

        return mapearParaResponse(assinaturaSalva, usuario, plano);
    }

    private AssinaturaResponse mapearParaResponse(Assinatura assinatura, Usuario usuario, Plano plano) {
        return new AssinaturaResponse(
                assinatura.getAssinaturaId().getValue(),
                usuario.getNome(),
                plano.getPlanoType(),
                assinatura.getDataInicio(),
                assinatura.getDataFim(),
                assinatura.getStatus().name()
        );
    }

}
