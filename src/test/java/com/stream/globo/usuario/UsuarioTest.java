package com.stream.globo.usuario;

import com.stream.globo.domain.usuario.entity.Usuario;
import com.stream.globo.shared.StatusEnum;
import com.stream.globo.shared.exception.BussinessException;
import com.stream.globo.domain.usuario.events.UsuarioCriadoEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class UsuarioTest {

    @Test
    @DisplayName("Deve criar usuário com dados válidos")
    void testCriarUsuarioComSucesso() {
        String nome = "João Silva";
        String email = "joao@example.com";
        String cpf = "12345678901";

        Usuario usuario = Usuario.criar(nome, email, cpf);

        assertThat(usuario).isNotNull();
        assertThat(usuario.getUsuarioId()).isNotNull();
        assertThat(usuario.getUsuarioId().getValue()).isNotBlank();
        assertThat(usuario.getNome()).isEqualTo(nome);
        assertThat(usuario.getEmail()).isEqualTo(email);
        assertThat(usuario.getCpf()).isEqualTo(cpf);
        assertThat(usuario.getStatus()).isEqualTo(StatusEnum.ATIVO);
        assertThat(usuario.getCriacaoDt()).isNotNull();
    }

    @Test
    @DisplayName("Deve publicar evento UsuarioCriadoEvent no factory")
    void testEventoUsuarioCriadoPublicado() {
        String nome = "Maria Santos";
        String email = "maria@example.com";
        String cpf = "98765432101";

        Usuario usuario = Usuario.criar(nome, email, cpf);

        assertThat(usuario.getDomainEvents()).hasSize(1);
        assertThat(usuario.getDomainEvents().get(0))
            .isInstanceOf(UsuarioCriadoEvent.class);

        UsuarioCriadoEvent evento = (UsuarioCriadoEvent) usuario.getDomainEvents().getFirst();
        assertThat(evento.getAggregateId()).isEqualTo(usuario.getUsuarioId().getValue());
        assertThat(evento.getNome()).isEqualTo(nome);
        assertThat(evento.getEmail()).isEqualTo(email);
        assertThat(evento.getCpf()).isEqualTo(cpf);
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com nome em branco")
    void testCriarComNomeEmBrancoFalha() {
        assertThatThrownBy(() -> Usuario.criar("", "email@test.com", "12345678901"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Nome do usuário não pode estar vazio");
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com nome muito curto")
    void testCriarComNomeCurtoFalha() {
        assertThatThrownBy(() -> Usuario.criar("Jo", "email@test.com", "12345678901"))
            .isInstanceOf(BussinessException.class)
            .hasMessageContaining("Nome do usuário deve ter no mínimo 3 caracteres");
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com nome null")
    void testCriarComNomeNullFalha() {
        assertThatThrownBy(() -> Usuario.criar(null, "email@test.com", "12345678901"))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com email em branco")
    void testCriarComEmailEmBrancoFalha() {
        assertThatThrownBy(() -> Usuario.criar("João Silva", "", "12345678901"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email não pode estar vazio");
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com email inválido (sem @)")
    void testCriarComEmailInvalidoFalha() {
        assertThatThrownBy(() -> Usuario.criar("João Silva", "email-sem-arroba.com", "12345678901"))
            .isInstanceOf(BussinessException.class)
            .hasMessageContaining("Email inválido");
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com email null")
    void testCriarComEmailNullFalha() {
        assertThatThrownBy(() -> Usuario.criar("João Silva", null, "12345678901"))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com CPF em branco")
    void testCriarComCpfEmBrancoFalha() {
        assertThatThrownBy(() -> Usuario.criar("João Silva", "joao@example.com", ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("CPF não pode estar vazio");
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com CPF com comprimento inválido")
    void testCriarComCpfComprimentoInvalidoFalha() {
        assertThatThrownBy(() -> Usuario.criar("João Silva", "joao@example.com", "123456789"))
            .isInstanceOf(BussinessException.class)
            .hasMessageContaining("CPF deve conter 11 dígitos");

        assertThatThrownBy(() -> Usuario.criar("João Silva", "joao@example.com", "123456789012"))
            .isInstanceOf(BussinessException.class)
            .hasMessageContaining("CPF deve conter 11 dígitos");
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com CPF contendo letras")
    void testCriarComCpfComLetrasFalha() {
        assertThatThrownBy(() -> Usuario.criar("João Silva", "joao@example.com", "1234567890a"))
            .isInstanceOf(BussinessException.class)
            .hasMessageContaining("CPF deve conter apenas dígitos");
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com CPF contendo caracteres especiais")
    void testCriarComCpfCaracteresEspeciaisFalha() {
        assertThatThrownBy(() -> Usuario.criar("João Silva", "joao@example.com", "123.456.789-01"))
            .isInstanceOf(BussinessException.class)
            .hasMessageContaining("CPF deve conter apenas dígitos");
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com CPF null")
    void testCriarComCpfNullFalha() {
        assertThatThrownBy(() -> Usuario.criar("João Silva", "joao@example.com", null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Deve aceitar CPF com apenas zeros")
    void testCriarComCpfApenasZeros() {
        Usuario usuario = Usuario.criar("João Silva", "joao@example.com", "00000000000");
        assertThat(usuario.getCpf()).isEqualTo("00000000000");
    }

    @Test
    @DisplayName("Deve traçar identidade por aggregateId nos equals")
    void testEqualsBasadoEmAggregateId() {
        Usuario usuario1 = Usuario.criar("João Silva", "joao@example.com", "12345678901");
        Usuario usuario2 = Usuario.criar("João Silva", "joao@example.com", "12345678901");

        assertThat(usuario1).isNotEqualTo(usuario2);
        assertThat(usuario1).isEqualTo(usuario1);
    }

    @Test
    @DisplayName("Deve limpar eventos após publicação")
    void testLimparDomainEvents() {
        Usuario usuario = Usuario.criar("João Silva", "joao@example.com", "12345678901");
        assertThat(usuario.getDomainEvents()).hasSize(1);

        usuario.limparDomainEvents();

        assertThat(usuario.getDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("Deve ter uma lista imutável de eventos")
    void testDomainEventsImutavel() {
        Usuario usuario = Usuario.criar("João Silva", "joao@example.com", "12345678901");
        var eventos = usuario.getDomainEvents();

        assertThatThrownBy(() -> eventos.clear())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Deve ter nome, email e status como obrigatórios")
    void testCamposObrigatorios() {
        Usuario usuario = Usuario.criar("Ana Silva", "ana@example.com", "11111111111");

        assertThat(usuario.getNome()).isNotBlank();
        assertThat(usuario.getEmail()).isNotBlank();
        assertThat(usuario.getCpf()).isNotBlank();
        assertThat(usuario.getStatus()).isNotNull();
    }

    @Test
    @DisplayName("Deve criar múltiplos usuários com diferentes IDs")
    void testCriarMultiplosUsuariosComIdsUnicos() {
        // Arrange & Act
        Usuario usuario1 = Usuario.criar("User 1", "user1@example.com", "11111111111");
        Usuario usuario2 = Usuario.criar("User 2", "user2@example.com", "22222222222");
        Usuario usuario3 = Usuario.criar("User 3", "user3@example.com", "33333333333");

        // Assert
        assertThat(usuario1.getUsuarioId()).isNotEqualTo(usuario2.getUsuarioId());
        assertThat(usuario2.getUsuarioId()).isNotEqualTo(usuario3.getUsuarioId());
        assertThat(usuario1.getUsuarioId()).isNotEqualTo(usuario3.getUsuarioId());
    }

    @Test
    @DisplayName("Deve permitir emails diferentes para usuários diferentes")
    void testEmailsUnicos() {
        Usuario usuario1 = Usuario.criar("João", "joao@example.com", "11111111111");
        Usuario usuario2 = Usuario.criar("Maria", "maria@example.com", "22222222222");

        assertThat(usuario1.getEmail()).isNotEqualTo(usuario2.getEmail());
    }

    @Test
    @DisplayName("Deve estar sempre ATIVO após criação")
    void testStatusPadraoAtivo() {
        // Arrange & Act
        Usuario usuario = Usuario.criar("João Silva", "joao@example.com", "12345678901");

        // Assert
        assertThat(usuario.getStatus()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve retornar toString com dados completos")
    void testToStringComDados() {
        // Arrange
        Usuario usuario = Usuario.criar("João Silva", "joao@example.com", "12345678901");

        // Act
        String toString = usuario.toString();

        // Assert
        assertThat(toString).contains("Usuario{");
        assertThat(toString).contains("João Silva");
        assertThat(toString).contains("joao@example.com");
        assertThat(toString).contains("ATIVO");
    }

}

