package br.com.acessoja.controller;

import br.com.acessoja.dto.request.AtualizarStatusRequest;
import br.com.acessoja.dto.request.AvisoCriarRequest;
import br.com.acessoja.dto.request.ListagemAvisosRequest;
import br.com.acessoja.dto.request.RedirecionarAvisoRequest;
import br.com.acessoja.dto.response.AvisoDetalheResponse;
import br.com.acessoja.dto.response.AvisoResponse;
import br.com.acessoja.dto.response.EstatisticasResponse;
import br.com.acessoja.dto.response.PontoMapaResponse;
import br.com.acessoja.services.AvisoService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Tudo sobre avisos: registro (E2), board (E4), resposta do órgão (E5) e remoção pelo moderador (E6).
 * Não há controle de permissão por perfil.
 */
@RestController
public class AvisoController {

    private final AvisoService avisoService;

    public AvisoController(AvisoService avisoService) {
        this.avisoService = avisoService;
    }

    // ----------------------------- E2: registro -----------------------------

    /**
     * POST /avisos (multipart/form-data).
     * O front envia um formulário com categoriaId, descricao, endereco, bairro, foto
     * e, se a categoria for delito, acontecendoAgora.
     */
    @PostMapping(value = "/avisos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AvisoResponse registrar(@ModelAttribute AvisoCriarRequest dto) {
        return avisoService.registrar(dto);
    }

    // ----------------------------- E4: board público -----------------------------

    /** GET /avisos?categoriaId=&status=&bairro=&ordem= (público). */
    @GetMapping("/avisos")
    public List<AvisoResponse> listar(@ModelAttribute ListagemAvisosRequest dto) {
        return avisoService.listarBoard(dto);
    }

    /**
     * GET /avisos/mapa.
     * PENDENTE: a tabela aviso só tem endereço e bairro, sem latitude e longitude.
     * Para ligar este endpoint: (1) adicione as colunas no banco e na entidade Aviso,
     * (2) peça as coordenadas no AvisoCriarRequest, (3) crie o método no AvisoService
     * e devolva a lista de PontoMapaResponse aqui.
     */
    @GetMapping("/avisos/mapa")
    public List<PontoMapaResponse> mapa() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED,
                "O mapa depende de latitude e longitude nos avisos, que o banco ainda não tem");
    }

    /** GET /avisos/estatisticas (público). O Spring prefere o caminho fixo ao /avisos/{id}. */
    @GetMapping("/avisos/estatisticas")
    public EstatisticasResponse estatisticas() {
        return avisoService.estatisticas();
    }

    /** GET /avisos/{id}: aviso com linha do tempo (público; o login é opcional). */
    @GetMapping("/avisos/{id}")
    public AvisoDetalheResponse detalhar(@PathVariable int id) {
        return avisoService.detalhar(id);
    }

    /** GET /avisos/{id}/foto: devolve a imagem. */
    @GetMapping("/avisos/{id}/foto")
    public ResponseEntity<Resource> foto(@PathVariable int id) {
        AvisoService.FotoArquivo foto = avisoService.buscarFoto(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(foto.tipoConteudo()))
                .body(foto.recurso());
    }

    // ----------------------------- E5: resposta do órgão -----------------------------

    /** GET /orgao/avisos: avisos das categorias do órgão do servidor logado. */
    @GetMapping("/orgao/avisos")
    public List<AvisoResponse> listarDoOrgao(@ModelAttribute ListagemAvisosRequest dto) {
        return avisoService.listarDoOrgao(dto);
    }

    /** PATCH /avisos/{id}/status: servidor avança o status (RN06, RN07). */
    @PatchMapping("/avisos/{id}/status")
    public AvisoResponse atualizarStatus(@PathVariable int id, @RequestBody AtualizarStatusRequest dto) {
        return avisoService.atualizarStatus(id, dto);
    }

    /** PATCH /avisos/{id}/categoria: servidor redireciona o aviso para outra categoria/órgão (RN07). */
    @PatchMapping("/avisos/{id}/categoria")
    public AvisoResponse redirecionar(@PathVariable int id, @RequestBody RedirecionarAvisoRequest dto) {
        return avisoService.redirecionar(id, dto);
    }

    // ----------------------------- E6: moderação -----------------------------

    /** POST /moderacao/avisos/{id}/remocao: moderador tira o aviso do board. */
    @PostMapping("/moderacao/avisos/{id}/remocao")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable int id) {
        avisoService.remover(id);
    }
}
