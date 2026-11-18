package br.com.acessoja.controller;

import br.com.acessoja.dto.response.OrgaoResponse;
import br.com.acessoja.services.OrgaoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Órgãos públicos e as categorias que atendem. */
@RestController
public class OrgaoController {

    private final OrgaoService orgaoService;

    public OrgaoController(OrgaoService orgaoService) {
        this.orgaoService = orgaoService;
    }

    /** GET /orgaos (público). */
    @GetMapping("/orgaos")
    public List<OrgaoResponse> listar() {
        return orgaoService.listar();
    }
}
