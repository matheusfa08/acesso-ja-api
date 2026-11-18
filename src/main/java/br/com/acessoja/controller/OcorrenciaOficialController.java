package br.com.acessoja.controller;

import br.com.acessoja.dto.request.OcorrenciaOficialCriarRequest;
import br.com.acessoja.dto.response.OcorrenciaOficialResponse;
import br.com.acessoja.services.OcorrenciaOficialService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Comunicados oficiais dos órgãos (HU14). */
@RestController
@RequestMapping("/ocorrencias-oficiais")
public class OcorrenciaOficialController {

    private final OcorrenciaOficialService ocorrenciaService;

    public OcorrenciaOficialController(OcorrenciaOficialService ocorrenciaService) {
        this.ocorrenciaService = ocorrenciaService;
    }

    /** GET /ocorrencias-oficiais?orgaoId= (público). */
    @GetMapping
    public List<OcorrenciaOficialResponse> listar(@RequestParam(required = false) Integer orgaoId) {
        return ocorrenciaService.listar(orgaoId);
    }

    /** POST /ocorrencias-oficiais: servidor publica em nome do seu órgão. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OcorrenciaOficialResponse publicar(@RequestBody OcorrenciaOficialCriarRequest dto) {
        return ocorrenciaService.publicar(dto);
    }
}
