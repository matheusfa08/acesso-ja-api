package br.com.acessoja.controller;

import br.com.acessoja.dto.response.HistoricoStatusResponse;
import br.com.acessoja.services.HistoricoStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Linha do tempo de um aviso (HU11). O detalhe do aviso já traz a mesma lista; este endpoint é só a linha do tempo. */
@RestController
public class HistoricoStatusController {

    private final HistoricoStatusService historicoService;

    public HistoricoStatusController(HistoricoStatusService historicoService) {
        this.historicoService = historicoService;
    }

    /** GET /avisos/{id}/historico (público). */
    @GetMapping("/avisos/{id}/historico")
    public List<HistoricoStatusResponse> listar(@PathVariable int id) {
        return historicoService.listarDoAviso(id);
    }
}
