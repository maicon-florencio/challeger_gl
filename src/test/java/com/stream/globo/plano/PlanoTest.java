package com.stream.globo.plano;

import com.stream.globo.domain.plano.entity.Plano;
import com.stream.globo.domain.plano.events.PlanoCriadoEvent;
import com.stream.globo.shared.StatusEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class PlanoTest {

    @Test
    @DisplayName("Deve criar plano com dados válidos")
    void testCriarPlanoComSucesso() {
        // Arrange
        String planoType = "PREMIUM";
        BigDecimal valor = new BigDecimal("99.99");
        Integer acessos = 5;

        // Act
        Plano plano = Plano.criar(planoType, valor, acessos);

        // Assert
        assertThat(plano).isNotNull();
        assertThat(plano.getPlanoId()).isNotNull();
        assertThat(plano.getPlanoId().getValue()).isNotBlank();
        assertThat(plano.getPlanoType()).isEqualTo(planoType);
        assertThat(plano.getValor()).isEqualTo(valor);
        assertThat(plano.getAcessosSimultaneos()).isEqualTo(acessos);
        assertThat(plano.getStatus()).isEqualTo(StatusEnum.ATIVO);
        assertThat(plano.getCriacaoDt()).isNotNull();
    }

    @Test
    @DisplayName("Deve publicar evento PlanoCriadoEvent no factory")
    void testEventoPlanoCriadoPublicado() {
        // Arrange
        String planoType = "BASIC";
        BigDecimal valor = new BigDecimal("29.99");
        Integer acessos = 2;

        // Act
        Plano plano = Plano.criar(planoType, valor, acessos);

        // Assert
        assertThat(plano.getDomainEvents()).hasSize(1);
        assertThat(plano.getDomainEvents().getFirst())
                .isInstanceOf(PlanoCriadoEvent.class);

        PlanoCriadoEvent evento = (PlanoCriadoEvent) plano.getDomainEvents().getFirst();
        assertThat(evento.getAggregateId()).isEqualTo(plano.getPlanoId().getValue());
        assertThat(evento.getPlanoType()).isEqualTo(planoType);
        assertThat(evento.getValor()).isEqualTo(valor);
    }

    @Test
    @DisplayName("Deve falhar ao criar plano com tipo vazio")
    void testCriarComTipoVazioFalha() {
        // Act & Assert
        assertThatThrownBy(() -> Plano.criar("", new BigDecimal("99.99"), 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo de plano não pode estar vazio");
    }

    @Test
    @DisplayName("Deve falhar ao criar plano com tipo muito curto")
    void testCriarComTipoCurtoFalha() {
        // Act & Assert
        assertThatThrownBy(() -> Plano.criar("A", new BigDecimal("99.99"), 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo de plano deve ter no mínimo 2 caracteres");
    }

    @Test
    @DisplayName("Deve falhar ao criar plano com valor zero")
    void testCriarComValorZeroFalha() {
        // Act & Assert
        assertThatThrownBy(() -> Plano.criar("PREMIUM", BigDecimal.ZERO, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valor do plano deve ser maior que zero");
    }

    @Test
    @DisplayName("Deve falhar ao criar plano com valor negativo")
    void testCriarComValorNegativoFalha() {
        // Act & Assert
        assertThatThrownBy(() -> Plano.criar("PREMIUM", new BigDecimal("-10.00"), 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valor do plano deve ser maior que zero");
    }

    @Test
    @DisplayName("Deve falhar ao criar plano com acessos zero")
    void testCriarComAcessosZeroFalha() {
        // Act & Assert
        assertThatThrownBy(() -> Plano.criar("PREMIUM", new BigDecimal("99.99"), 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Número de acessos simultâneos deve ser maior que zero");
    }

    @Test
    @DisplayName("Deve falhar ao criar plano com acessos negativo")
    void testCriarComAcessosNegativoFalha() {
        // Act & Assert
        assertThatThrownBy(() -> Plano.criar("PREMIUM", new BigDecimal("99.99"), -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Número de acessos simultâneos deve ser maior que zero");
    }

    @Test
    @DisplayName("Deve falhar ao criar plano com acessos acima do limite (100)")
    void testCriarComAcessosAcimaLimiteFalha() {
        // Act & Assert
        assertThatThrownBy(() -> Plano.criar("PREMIUM", new BigDecimal("99.99"), 101))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Número de acessos simultâneos não pode exceder 100");
    }

    @Test
    @DisplayName("Deve falhar ao criar plano com tipo null")
    void testCriarComTipoNullFalha() {
        // Act & Assert
        assertThatThrownBy(() -> Plano.criar(null, new BigDecimal("99.99"), 5))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve falhar ao criar plano com valor null")
    void testCriarComValorNullFalha() {
        // Act & Assert
        assertThatThrownBy(() -> Plano.criar("PREMIUM", null, 5))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve falhar ao criar plano com acessos null")
    void testCriarComAcessosNullFalha() {
        // Act & Assert
        assertThatThrownBy(() -> Plano.criar("PREMIUM", new BigDecimal("99.99"), null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve traçar identidade por aggregateId nos equals")
    void testEqualsBasadoEmAggregateId() {
        // Arrange
        Plano plano1 = Plano.criar("PREMIUM", new BigDecimal("99.99"), 5);
        Plano plano2 = Plano.criar("PREMIUM", new BigDecimal("99.99"), 5);

        // Act & Assert - Mesmo com dados iguais, são objetos diferentes
        assertThat(plano1).isNotEqualTo(plano2);
        assertThat(plano1).isEqualTo(plano1);
    }

    @Test
    @DisplayName("Deve limpar eventos após publicação")
    void testLimparDomainEvents() {
        // Arrange
        Plano plano = Plano.criar("BASIC", new BigDecimal("29.99"), 2);
        assertThat(plano.getDomainEvents()).hasSize(1);

        // Act
        plano.limparDomainEvents();

        // Assert
        assertThat(plano.getDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Deve ter uma lista imutável de eventos")
    void testDomainEventsImutavel() {
        // Arrange
        Plano plano = Plano.criar("PREMIUM", new BigDecimal("99.99"), 5);
        var eventos = plano.getDomainEvents();

        // Act & Assert - Tentar modificar a lista retornada não deve alterar a lista interna
        assertThatThrownBy(() -> eventos.clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Deve estar sempre ATIVO após criação")
    void testStatusPadraoAtivo() {
        // Arrange & Act
        Plano plano = Plano.criar("ENTERPRISE", new BigDecimal("299.99"), 50);

        // Assert
        assertThat(plano.getStatus()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve criar múltiplos planos com diferentes IDs")
    void testCriarMultiplosPlanosComIdsUnicos() {
        // Arrange & Act
        Plano plano1 = Plano.criar("BASIC", new BigDecimal("29.99"), 2);
        Plano plano2 = Plano.criar("PREMIUM", new BigDecimal("99.99"), 5);
        Plano plano3 = Plano.criar("ENTERPRISE", new BigDecimal("299.99"), 50);

        // Assert
        assertThat(plano1.getPlanoId()).isNotEqualTo(plano2.getPlanoId());
        assertThat(plano2.getPlanoId()).isNotEqualTo(plano3.getPlanoId());
        assertThat(plano1.getPlanoId()).isNotEqualTo(plano3.getPlanoId());
    }

    @Test
    @DisplayName("Deve retornar toString com dados completos")
    void testToStringComDados() {
        // Arrange
        Plano plano = Plano.criar("PREMIUM", new BigDecimal("99.99"), 5);

        // Act
        String toString = plano.toString();

        // Assert
        assertThat(toString).contains("Plano{");
        assertThat(toString).contains("PREMIUM");
        assertThat(toString).contains("99.99");
        assertThat(toString).contains("ATIVO");
    }

}

