package br.com.acessoja.controller;

import br.com.acessoja.dto.request.ExcluirContaRequest;
import br.com.acessoja.dto.request.ListagemAvisosRequest;
import br.com.acessoja.dto.request.VincularServidorRequest;
import br.com.acessoja.dto.response.AvisoResponse;
import br.com.acessoja.dto.response.UsuarioResponse;
import br.com.acessoja.entity.Usuario;
import br.com.acessoja.mapper.UsuarioMapper;
import br.com.acessoja.services.AvisoService;
import br.com.acessoja.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Dados do próprio usuário (E1/E2) e a promoção a servidor feita pelo moderador (E6). */
@RestController
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AvisoService avisoService;

    public UsuarioController(UsuarioService usuarioService, AvisoService avisoService) {
        this.usuarioService = usuarioService;
        this.avisoService = avisoService;
    }

    /** GET /usuarios/me. */
    @GetMapping("/usuarios/me")
    public UsuarioResponse meuPerfil() {
        Usuario usuario = usuarioService.buscarLogado();
        return UsuarioMapper.toResponse(usuario, usuarioService.buscarOrgaoDoUsuario(usuario));
    }

    /** DELETE /usuarios/me: exclui a própria conta (corpo com a senha). */
    @DeleteMapping("/usuarios/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirMinhaConta(@RequestBody ExcluirContaRequest dto) {
        usuarioService.excluirMinhaConta(dto.senha());
    }

    /** GET /usuarios/me/avisos: avisos que o cidadão registrou, com o status atual. */
    @GetMapping("/usuarios/me/avisos")
    public List<AvisoResponse> meusAvisos(@ModelAttribute ListagemAvisosRequest dto) {
        return avisoService.listarMeus(dto);
    }

    /** PUT /moderacao/usuarios/{id}/servidor: o moderador liga um usuário a um órgão. */
    @PutMapping("/moderacao/usuarios/{id}/servidor")
    public UsuarioResponse promoverServidor(@PathVariable int id, @RequestBody VincularServidorRequest dto) {
        Usuario usuario = usuarioService.promoverServidor(id, dto.orgaoId());
        return UsuarioMapper.toResponse(usuario, usuarioService.buscarOrgaoDoUsuario(usuario));
    }
}
