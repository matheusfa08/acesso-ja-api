package br.com.acessoja.mapper;

import br.com.acessoja.dto.response.CategoriaResponse;
import br.com.acessoja.dto.response.CategoriaResumoResponse;
import br.com.acessoja.entity.Categoria;
import br.com.acessoja.entity.Orgao;

//Responsável pelas conversões objeto -> DTO e DTO -> objeto
public class CategoriaMapper {

    public static CategoriaResumoResponse toResumo(Categoria categoria) {
        if (categoria == null) return null;
        return new CategoriaResumoResponse(categoria.getIdCategoria(), categoria.getNomeCategoria());
    }

    /**
     * Versão completa (GET /categorias).
     *
     * @param perguntaEmAndamento resultado de CategoriaService.perguntaEmAndamento(categoria)
     */
    public static CategoriaResponse toResponse(Categoria categoria, Orgao orgao, boolean perguntaEmAndamento) {
        return new CategoriaResponse(categoria.getIdCategoria(), categoria.getNomeCategoria(),
                categoria.getDiasExpiracao(), perguntaEmAndamento, OrgaoMapper.toResumo(orgao));
    }
}
