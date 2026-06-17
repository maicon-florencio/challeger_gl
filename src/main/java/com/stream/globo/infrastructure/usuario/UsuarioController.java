package com.stream.globo.infrastructure.usuario;

import com.stream.globo.application.usuario.UsuarioCriarRequest;
import com.stream.globo.application.usuario.UsuarioCriarUseCase;
import com.stream.globo.application.usuario.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/usuario")
public class UsuarioController {

    private final UsuarioCriarUseCase usuarioCriarUseCase;

    public UsuarioController(UsuarioCriarUseCase usuarioCriarUseCase) {
        this.usuarioCriarUseCase = usuarioCriarUseCase;
    }

    @PostMapping
    @Operation(
            summary = "Criar um novo usuário",
            description = "Endpoint para criar um novo usuário. O corpo da requisição deve conter os dados necessários para a criação do usuário, como nome, email e CPF."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida ou corpo vazio")
    })
    public ResponseEntity<UsuarioResponse> criarUsuario(@RequestBody @Valid UsuarioCriarRequest criarRequest) {
        return new ResponseEntity<UsuarioResponse>(usuarioCriarUseCase.create(criarRequest), HttpStatus.CREATED);
    }
}
