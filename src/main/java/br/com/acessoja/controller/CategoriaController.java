package br.com.acessoja.controller;

import br.com.acessoja.dto.response.CategoriaResponse;
import br.com.acessoja.services.CategoriaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Categorias de aviso (para o filtro do board e o campo Tipo do formulário). */
@RestController
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    /** GET /categorias (público). */
    @GetMapping("/categorias")
    public List<CategoriaResponse> listar() {
        return categoriaService.listar();
    }
}
