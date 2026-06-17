package com.stream.globo.assinatura;

import com.stream.globo.application.assinatura.AssinaturaCreateValidator;
import com.stream.globo.application.assinatura.AssinaturaCriarRequest;
import com.stream.globo.application.assinatura.AssinaturaCriarUseCase;
import com.stream.globo.application.assinatura.AssinaturaResponse;
import com.stream.globo.domain.assinatura.entity.Assinatura;
import com.stream.globo.domain.assinatura.events.AssinaturaCriadaEvent;
import com.stream.globo.domain.assinatura.AssinaturaRepository;
import com.stream.globo.domain.plano.entity.Plano;
import com.stream.globo.domain.plano.PlanoId;
import com.stream.globo.shared.StatusEnum;
import com.stream.globo.domain.usuario.entity.Usuario;
import com.stream.globo.domain.usuario.UsuarioId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssinaturaCriarUseCaseTest {

    @Mock
    private AssinaturaCreateValidator validator;

    @Mock
    private AssinaturaRepository repository;

    @InjectMocks
    private AssinaturaCriarUseCase useCase;

    private PlanoId planoId;
    private UsuarioId usuarioId;
    private Plano plano;
    private Usuario usuario;

    @BeforeEach
    void setup() {
        planoId = PlanoId.from("plano-premium");
        usuarioId = UsuarioId.from("usuario-123");

        // Setup Plano mock
        plano = Plano.criar("PREMIUM", new BigDecimal("99.99"), 5);

        // Setup Usuario mock
        usuario = Usuario.criar("João Silva", "joao@example.com", "12345678901");
    }

    @Test
    @DisplayName("Deve criar assinatura com dados válidos do request")
    void testCriarAssinaturaComSucesso() {
        // Arrange
        AssinaturaCriarRequest request = new AssinaturaCriarRequest(
            "usuario-123",
            "plano-premium"
        );

        when(validator.validarPlano(any(PlanoId.class))).thenReturn(plano);
        when(validator.validarUsuario(any(UsuarioId.class))).thenReturn(usuario);
        when(repository.save(any(Assinatura.class))).thenAnswer(invocation -> {
            Assinatura assinatura = invocation.getArgument(0);
            // Simular ID gerado pelo banco
            return assinatura;
        });

        // Act
        AssinaturaResponse response = useCase.criar(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.aggregateId()).isNotBlank();
        assertThat(response.usuarioNome()).isEqualTo("João Silva");
        assertThat(response.tipoPlano()).isEqualTo("PREMIUM");
        assertThat(response.dataInicio()).isEqualTo(LocalDate.now());
        assertThat(response.dataExpiracao()).isEqualTo(LocalDate.now().plusMonths(1));
        assertThat(response.status()).isEqualTo("ATIVO");

        // Verify
        verify(validator, times(1)).validarPlano(any(PlanoId.class));
        verify(validator, times(1)).validarUsuario(any(UsuarioId.class));
        verify(repository, times(1)).save(any(Assinatura.class));
    }

    @Test
    @DisplayName("Deve chamar validator para plano")
    void testValidarPlano() {
        // Arrange
        AssinaturaCriarRequest request = new AssinaturaCriarRequest(
            "usuario-123",
            "plano-premium"
        );

        when(validator.validarPlano(any(PlanoId.class))).thenReturn(plano);
        when(validator.validarUsuario(any(UsuarioId.class))).thenReturn(usuario);
        when(repository.save(any(Assinatura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        useCase.criar(request);

        // Assert
        verify(validator).validarPlano(argThat(id -> id.getValue().equals("plano-premium")));
    }

    @Test
    @DisplayName("Deve chamar validator para usuário")
    void testValidarUsuario() {
        // Arrange
        AssinaturaCriarRequest request = new AssinaturaCriarRequest(
            "usuario-123",
            "plano-premium"
        );

        when(validator.validarPlano(any(PlanoId.class))).thenReturn(plano);
        when(validator.validarUsuario(any(UsuarioId.class))).thenReturn(usuario);
        when(repository.save(any(Assinatura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        useCase.criar(request);

        // Assert
        verify(validator).validarUsuario(argThat(id -> id.getValue().equals("usuario-123")));
    }

    @Test
    @DisplayName("Deve persistir assinatura no repositório")
    void testPersistirAssinatura() {
        // Arrange
        AssinaturaCriarRequest request = new AssinaturaCriarRequest(
            "usuario-123",
            "plano-premium"
        );

        when(validator.validarPlano(any(PlanoId.class))).thenReturn(plano);
        when(validator.validarUsuario(any(UsuarioId.class))).thenReturn(usuario);
        when(repository.save(any(Assinatura.class))).thenAnswer(invocation -> {
            Assinatura assinatura = invocation.getArgument(0);
            assertThat(assinatura.getPlanoAggregateId()).isEqualTo("plano-premium");
            assertThat(assinatura.getUsuarioAggregateId()).isEqualTo("usuario-123");
            assertThat(assinatura.getStatus()).isEqualTo(StatusEnum.ATIVO);
            return assinatura;
        });

        // Act
        useCase.criar(request);

        // Assert
        verify(repository, times(1)).save(any(Assinatura.class));
    }

    @Test
    @DisplayName("Deve mapear corretamente plano type para response")
    void testMapearPlanoTypeCorretamente() {
        // Arrange
        Plano planoBasico = Plano.criar("BASIC", new BigDecimal("29.99"), 2);
        AssinaturaCriarRequest request = new AssinaturaCriarRequest(
            "usuario-456",
            "plano-basic"
        );

        when(validator.validarPlano(any(PlanoId.class))).thenReturn(planoBasico);
        when(validator.validarUsuario(any(UsuarioId.class))).thenReturn(usuario);
        when(repository.save(any(Assinatura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AssinaturaResponse response = useCase.criar(request);

        // Assert
        assertThat(response.tipoPlano()).isEqualTo("BASIC");
    }

    @Test
    @DisplayName("Deve mapear corretamente nome do usuário para response")
    void testMapearNomeUsuarioCorretamente() {
        // Arrange
        usuario = Usuario.criar("Ana Silva", "ana@example.com", "98765432101");
        AssinaturaCriarRequest request = new AssinaturaCriarRequest(
            "usuario-789",
            "plano-premium"
        );

        when(validator.validarPlano(any(PlanoId.class))).thenReturn(plano);
        when(validator.validarUsuario(any(UsuarioId.class))).thenReturn(usuario);
        when(repository.save(any(Assinatura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AssinaturaResponse response = useCase.criar(request);

        // Assert
        assertThat(response.usuarioNome()).isEqualTo("Ana Silva");
    }

    @Test
    @DisplayName("Deve criar assinatura com datas corretas")
    void testDatasAssinaturaCorretas() {
        // Arrange
        AssinaturaCriarRequest request = new AssinaturaCriarRequest(
            "usuario-123",
            "plano-premium"
        );

        when(validator.validarPlano(any(PlanoId.class))).thenReturn(plano);
        when(validator.validarUsuario(any(UsuarioId.class))).thenReturn(usuario);
        when(repository.save(any(Assinatura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AssinaturaResponse response = useCase.criar(request);
        LocalDate hoje = LocalDate.now();

        // Assert
        assertThat(response.dataInicio()).isEqualTo(hoje);
        assertThat(response.dataExpiracao()).isEqualTo(hoje.plusMonths(1));
    }

    @Test
    @DisplayName("Deve incluir ID da assinatura na response")
    void testIncluirIdAssinaturaResponse() {
        // Arrange
        AssinaturaCriarRequest request = new AssinaturaCriarRequest(
            "usuario-123",
            "plano-premium"
        );

        when(validator.validarPlano(any(PlanoId.class))).thenReturn(plano);
        when(validator.validarUsuario(any(UsuarioId.class))).thenReturn(usuario);
        when(repository.save(any(Assinatura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AssinaturaResponse response = useCase.criar(request);

        // Assert
        assertThat(response.aggregateId()).isNotBlank();
        assertThat(response.aggregateId()).isNotNull();
    }

    @Test
    @DisplayName("Deve manter ordem de chamadas: validação → persistência → mapeamento")
    void testOrdemChamadas() {
        // Arrange
        AssinaturaCriarRequest request = new AssinaturaCriarRequest(
            "usuario-123",
            "plano-premium"
        );

        // Criar um mock para verificar ordem
        InOrder inOrder = inOrder(validator, validator, repository);

        when(validator.validarPlano(any(PlanoId.class))).thenReturn(plano);
        when(validator.validarUsuario(any(UsuarioId.class))).thenReturn(usuario);
        when(repository.save(any(Assinatura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        useCase.criar(request);

        // Assert - Validar que plano foi validado antes de usuário
        inOrder.verify(validator).validarPlano(any());
        inOrder.verify(validator).validarUsuario(any());
        inOrder.verify(repository).save(any());
    }

    @Test
    @DisplayName("Deve retornar response com todos os campos obrigatórios")
    void testResponseComCamposObrigatorios() {
        // Arrange
        AssinaturaCriarRequest request = new AssinaturaCriarRequest(
            "usuario-123",
            "plano-premium"
        );

        when(validator.validarPlano(any(PlanoId.class))).thenReturn(plano);
        when(validator.validarUsuario(any(UsuarioId.class))).thenReturn(usuario);
        when(repository.save(any(Assinatura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AssinaturaResponse response = useCase.criar(request);

        // Assert
        assertThat(response.aggregateId()).isNotBlank();
        assertThat(response.usuarioNome()).isNotBlank();
        assertThat(response.tipoPlano()).isNotBlank();
        assertThat(response.dataInicio()).isNotNull();
        assertThat(response.dataExpiracao()).isNotNull();
        assertThat(response.status()).isNotBlank();
    }

    @Test
    @DisplayName("Deve criar assinatura com evento de domínio")
    void testAssinaturaCriadaComEvento() {
        // Arrange
        AssinaturaCriarRequest request = new AssinaturaCriarRequest(
            "usuario-123",
            "plano-premium"
        );

        Assinatura assinaturaCriada = null;

        when(validator.validarPlano(any(PlanoId.class))).thenReturn(plano);
        when(validator.validarUsuario(any(UsuarioId.class))).thenReturn(usuario);
        when(repository.save(any(Assinatura.class))).thenAnswer(invocation -> {
            Assinatura assinatura = invocation.getArgument(0);
            // Verificar que tem eventos antes de salvar
            assertThat(assinatura.getDomainEvents()).hasSize(1);
            assertThat(assinatura.getDomainEvents().get(0))
                .isInstanceOf(AssinaturaCriadaEvent.class);
            return assinatura;
        });

        // Act
        useCase.criar(request);

        // Assert
        verify(repository).save(argThat(assinatura ->
            assinatura.getDomainEvents().size() == 1 &&
            assinatura.getDomainEvents().get(0) instanceof AssinaturaCriadaEvent
        ));
    }

}

