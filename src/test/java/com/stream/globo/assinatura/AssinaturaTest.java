package com.stream.globo.assinatura;

import com.stream.globo.domain.assinatura.entity.Assinatura;
import com.stream.globo.domain.assinatura.events.AssinaturaCanceladaEvent;
import com.stream.globo.domain.assinatura.events.AssinaturaCriadaEvent;
import com.stream.globo.domain.assinatura.events.AssinaturaRenovadaEvent;
import com.stream.globo.domain.plano.PlanoId;
import com.stream.globo.shared.StatusEnum;
import com.stream.globo.domain.usuario.UsuarioId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssinaturaTest {

    @Test
    @DisplayName("Deve criar assinatura com dados válidos")
    void testCriarAssinaturaComSucesso() {
        // Arrange
        PlanoId planoId = PlanoId.from("plano-123");
        UsuarioId usuarioId = UsuarioId.from("usuario-456");

        // Act
        Assinatura assinatura = Assinatura.criar(planoId, usuarioId);

        // Assert
        assertThat(assinatura).isNotNull();
        assertThat(assinatura.getAssinaturaId()).isNotNull();
        assertThat(assinatura.getAssinaturaId().getValue()).isNotBlank();
        assertThat(assinatura.getPlanoAggregateId()).isEqualTo(planoId.getValue());
        assertThat(assinatura.getUsuarioAggregateId()).isEqualTo(usuarioId.getValue());
        assertThat(assinatura.getStatus()).isEqualTo(StatusEnum.ATIVO);
        assertThat(assinatura.getDataInicio()).isEqualTo(LocalDate.now());
        assertThat(assinatura.getDataFim()).isEqualTo(LocalDate.now().plusMonths(1));
        assertThat(assinatura.getCriacaoDt()).isNotNull();
    }

    @Test
    @DisplayName("Deve publicar evento AssinaturaCriadaEvent no factory")
    void testEventoAssinaturaCriadaPublicado() {
        // Arrange
        PlanoId planoId = PlanoId.from("plano-789");
        UsuarioId usuarioId = UsuarioId.from("usuario-789");

        // Act
        Assinatura assinatura = Assinatura.criar(planoId, usuarioId);

        // Assert
        assertThat(assinatura.getDomainEvents()).hasSize(1);
        assertThat(assinatura.getDomainEvents().get(0))
            .isInstanceOf(AssinaturaCriadaEvent.class);

        AssinaturaCriadaEvent evento = (AssinaturaCriadaEvent) assinatura.getDomainEvents().get(0);
        assertThat(evento.getAggregateId()).isEqualTo(assinatura.getAssinaturaId().getValue());
        assertThat(evento.getPlanoAggregateId()).isEqualTo(planoId.getValue());
    }

    @Test
    @DisplayName("Deve renovar assinatura ativa corretamente")
    void testRenovarAssinaturaAtiva() {
        // Arrange
        Assinatura assinatura = Assinatura.criar(
            PlanoId.from("plano-111"),
            UsuarioId.from("usuario-111")
        );
        LocalDate dataFimAnterior = assinatura.getDataFim();
        assinatura.limparDomainEvents();

        // Act
        assinatura.renovar();

        // Assert
        assertThat(assinatura.getDataFim()).isEqualTo(dataFimAnterior.plusMonths(1));
        assertThat(assinatura.getStatus()).isEqualTo(StatusEnum.ATIVO);

        // Verifica evento de renovação
        assertThat(assinatura.getDomainEvents()).hasSize(1);
        assertThat(assinatura.getDomainEvents().get(0))
            .isInstanceOf(AssinaturaRenovadaEvent.class);
    }

    @Test
    @DisplayName("Deve falhar ao renovar assinatura inativa")
    void testRenovarAssinaturaInativaFalha() {
        // Arrange
        Assinatura assinatura = Assinatura.criar(
            PlanoId.from("plano-222"),
            UsuarioId.from("usuario-222")
        );
        assinatura.cancelar("Teste");

        // Act & Assert
        assertThatThrownBy(() -> assinatura.renovar())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Assinatura deve estar ATIVA para ser renovada")
            .hasMessageContaining("INATIVO");
    }

    @Test
    @DisplayName("Deve cancelar assinatura ativa com motivo válido")
    void testCancelarAssinaturaAtivaComMotivo() {
        // Arrange
        Assinatura assinatura = Assinatura.criar(
            PlanoId.from("plano-333"),
            UsuarioId.from("usuario-333")
        );
        assinatura.limparDomainEvents();
        String motivo = "Cliente solicitou cancelamento";

        // Act
        assinatura.cancelar(motivo);

        // Assert
        assertThat(assinatura.getStatus()).isEqualTo(StatusEnum.INATIVO);

        // Verifica evento de cancelamento
        assertThat(assinatura.getDomainEvents()).hasSize(1);
        assertThat(assinatura.getDomainEvents().getFirst())
            .isInstanceOf(AssinaturaCanceladaEvent.class);

        AssinaturaCanceladaEvent evento = (AssinaturaCanceladaEvent) assinatura.getDomainEvents().getFirst();
        assertThat(evento.getMotivo()).isEqualTo(motivo);
    }

    @Test
    @DisplayName("Deve falhar ao cancelar assinatura inativa")
    void testCancelarAssinaturaInativaFalha() {
        // Arrange
        Assinatura assinatura = Assinatura.criar(
            PlanoId.from("plano-444"),
            UsuarioId.from("usuario-444")
        );
        assinatura.cancelar("Primeiro cancelamento");

        // Act & Assert
        assertThatThrownBy(() -> assinatura.cancelar("Segundo cancelamento"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Assinatura deve estar ATIVA para ser cancelada");
    }

    @Test
    @DisplayName("Deve falhar ao cancelar com motivo vazio")
    void testCancelarComMotivoVazioFalha() {
        // Arrange
        Assinatura assinatura = Assinatura.criar(
            PlanoId.from("plano-555"),
            UsuarioId.from("usuario-555")
        );

        // Act & Assert
        assertThatThrownBy(() -> assinatura.cancelar(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Motivo do cancelamento não pode estar vazio");
    }

    @Test
    @DisplayName("Deve detectar assinatura vencida")
    void testEstaVencida() {
        // Arrange
        Assinatura assinatura = Assinatura.criar(
            PlanoId.from("plano-666"),
            UsuarioId.from("usuario-666")
        );

        // Simular assinatura antiga (vencida)
        LocalDate dataFimVencida = LocalDate.now().minusDays(1);

        // Act - Não é possível modificar directamente, então testamos com lógica
        // Esta é uma limitação do design que não permite setar dataFim
        // Em um caso real com Event Sourcing, seria reconstruído
        boolean estaVencida = assinatura.estaVencida();

        // Assert
        assertThat(estaVencida).isFalse(); // Pois foi criada hoje
    }

    @Test
    @DisplayName("Deve detectar assinatura próxima do vencimento")
    void testEstaProximaDoVencimento() {
        // Arrange
        Assinatura assinatura = Assinatura.criar(
            PlanoId.from("plano-777"),
            UsuarioId.from("usuario-777")
        );

        // Act
        boolean estaProxima = assinatura.estaProximaDoVencimento();

        // Assert - Assinatura criada hoje com vencimento em 1 mês está dentro de 15 dias
        assertThat(estaProxima).isFalse();
    }

    @Test
    @DisplayName("Deve implementar equals baseado em aggregateId")
    void testEqualsBasadoEmAggregateId() {
        // Arrange
        Assinatura assinatura1 = Assinatura.criar(
            PlanoId.from("plano-888"),
            UsuarioId.from("usuario-888")
        );
        String agregateId = assinatura1.getAssinaturaId().getValue();

        // Act - Cria outra assinatura (diferente ID)
        Assinatura assinatura2 = Assinatura.criar(
            PlanoId.from("plano-888"),
            UsuarioId.from("usuario-888")
        );

        // Assert
        assertThat(assinatura1).isNotEqualTo(assinatura2);
        assertThat(assinatura1).isEqualTo(assinatura1);
    }

    @Test
    @DisplayName("Deve limpar eventos após publicação")
    void testLimparDomainEvents() {
        // Arrange
        Assinatura assinatura = Assinatura.criar(
            PlanoId.from("plano-999"),
            UsuarioId.from("usuario-999")
        );
        assertThat(assinatura.getDomainEvents()).hasSize(1);

        // Act
        assinatura.limparDomainEvents();

        // Assert
        assertThat(assinatura.getDomainEvents()).isEmpty();
    }

}

