package br.com.acessoja.controller;

import br.com.acessoja.dto.request.CadastroRequest;
import br.com.acessoja.dto.request.LoginRequest;
import br.com.acessoja.dto.response.LoginResponse;
import br.com.acessoja.dto.response.UsuarioResponse;
import br.com.acessoja.entity.Usuario;
import br.com.acessoja.mapper.UsuarioMapper;
import br.com.acessoja.services.AutenticacaoService;
import br.com.acessoja.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Conta e acesso (E1): cadastro, login e logout.
 * Controllers ficam "finos": só recebem a requisição, chamam o service e devolvem o status HTTP certo.
 * As regras mesmo ficam nos services.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final AutenticacaoService autenticacaoService;

    public AuthController(UsuarioService usuarioService, AutenticacaoService autenticacaoService) {
        this.usuarioService = usuarioService;
        this.autenticacaoService = autenticacaoService;
    }

    /** POST /auth/cadastro (público). */
    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrar(@RequestBody CadastroRequest dto) {
        Usuario criado = usuarioService.cadastrar(UsuarioMapper.toEntity(dto), dto.senha(), dto.confirmarSenha());
        return UsuarioMapper.toResponse(criado, null);
    }

    /** POST /auth/login (público). */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest dto) {
        return autenticacaoService.login(dto);
    }

    /** POST /auth/logout: o servidor não guarda sessão, então não há o que encerrar. O front só esquece o usuário. */
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout() {
    }
}
