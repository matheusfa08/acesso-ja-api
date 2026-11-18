package br.com.acessoja.controller;

import br.com.acessoja.dto.response.ContatoEmergenciaResponse;
import br.com.acessoja.services.EmergenciaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Aviso fixo de emergência (HU05, RF13): 190, 193 e 192. */
@RestController
public class EmergenciaController {

    private final EmergenciaService emergenciaService;

    public EmergenciaController(EmergenciaService emergenciaService) {
        this.emergenciaService = emergenciaService;
    }

    /** GET /emergencia/contatos (público). A lista fica no EmergenciaService. */
    @GetMapping("/emergencia/contatos")
    public List<ContatoEmergenciaResponse> contatos() {
        return emergenciaService.listarContatos();
    }
}
