package br.com.acessoja.controller;

import br.com.acessoja.dto.request.SinalizacaoCriarRequest;
import br.com.acessoja.dto.response.SinalizacaoResponse;
import br.com.acessoja.services.SinalizacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Sinalização de avisos falsos ou inadequados (HU15) e a fila da moderação (HU16). */
@RestController
public class SinalizacaoController {

    private final SinalizacaoService sinalizacaoService;

    public SinalizacaoController(SinalizacaoService sinalizacaoService) {
        this.sinalizacaoService = sinalizacaoService;
    }

    /** POST /avisos/{id}/sinalizacoes: o cidadão sinaliza um aviso. */
    @PostMapping("/avisos/{id}/sinalizacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public SinalizacaoResponse sinalizar(@PathVariable int id, @RequestBody SinalizacaoCriarRequest dto) {
        return sinalizacaoService.sinalizar(id, dto);
    }

    /** GET /moderacao/sinalizacoes: fila do moderador. */
    @GetMapping("/moderacao/sinalizacoes")
    public List<SinalizacaoResponse> listar() {
        return sinalizacaoService.listar();
    }

    /** DELETE /moderacao/sinalizacoes/{id}: moderador descarta a sinalização. */
    @DeleteMapping("/moderacao/sinalizacoes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void descartar(@PathVariable int id) {
        sinalizacaoService.descartar(id);
    }
}
