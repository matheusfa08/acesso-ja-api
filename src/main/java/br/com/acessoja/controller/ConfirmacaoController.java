package br.com.acessoja.controller;

import br.com.acessoja.dto.response.ConfirmacaoResumoResponse;
import br.com.acessoja.services.ConfirmacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Confirmação coletiva (E3): botão "Eu também vi". */
@RestController
@RequestMapping("/avisos/{id}/confirmacoes")
public class ConfirmacaoController {

    private final ConfirmacaoService confirmacaoService;

    public ConfirmacaoController(ConfirmacaoService confirmacaoService) {
        this.confirmacaoService = confirmacaoService;
    }

    /** POST /avisos/{id}/confirmacoes: o cidadão logado confirma o aviso. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConfirmacaoResumoResponse confirmar(@PathVariable int id) {
        return confirmacaoService.confirmar(id);
    }

    /** GET /avisos/{id}/confirmacoes: total, mínimo e se o usuário já confirmou (público, login opcional). */
    @GetMapping
    public ConfirmacaoResumoResponse resumo(@PathVariable int id) {
        return confirmacaoService.resumo(id);
    }
}
