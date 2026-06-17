package com.stream.globo.infrastructure.assinatura;

import com.stream.globo.application.assinatura.AssinaturaCriarRequest;
import com.stream.globo.application.assinatura.AssinaturaCriarUseCase;
import com.stream.globo.application.assinatura.AssinaturaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assinatura")
public class AssinaturaController {

    private final AssinaturaCriarUseCase criarUseCase;

    public AssinaturaController(AssinaturaCriarUseCase criarUseCase) {
        this.criarUseCase = criarUseCase;
    }

    @PostMapping
    @Operation(
            summary = "Criar um nova assinatura",
            description = "Endpoint para criar uma nova assinatura. O corpo da requisição deve conter os dados necessários para a criação da assinatura, como o ID do plano e o ID do usuário."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida ou corpo vazio")
    })
    public ResponseEntity<AssinaturaResponse> criarAssinatura(@Validated @RequestBody AssinaturaCriarRequest criarRequest) {
        return new ResponseEntity<>(criarUseCase.criar(criarRequest), HttpStatus.CREATED);
    }

}
