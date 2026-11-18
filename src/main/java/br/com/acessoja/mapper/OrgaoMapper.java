package br.com.acessoja.mapper;

import br.com.acessoja.dto.response.CategoriaResumoResponse;
import br.com.acessoja.dto.response.OrgaoResponse;
import br.com.acessoja.dto.response.OrgaoResumoResponse;
import br.com.acessoja.entity.Categoria;
import br.com.acessoja.entity.Orgao;

import java.util.List;

//Responsável pelas conversões objeto -> DTO e DTO -> objeto (mesmo padrão do projeto Soulpass: métodos estáticos)
public class OrgaoMapper {

    /** Versão curta (id, nome, sigla). Devolve null se o órgão for nulo (ex.: cidadão não tem órgão). */
    public static OrgaoResumoResponse toResumo(Orgao orgao) {
        if (orgao == null) return null;
        return new OrgaoResumoResponse(orgao.getIdOrgao(), orgao.getNomeOrgao(), orgao.getSiglaOrgao());
    }

    /** Versão completa, com as categorias que o órgão atende (GET /orgaos). */
    public static OrgaoResponse toResponse(Orgao orgao, List<Categoria> categorias) {
        List<CategoriaResumoResponse> resumos = categorias.stream().map(CategoriaMapper::toResumo).toList();
        return new OrgaoResponse(orgao.getIdOrgao(), orgao.getNomeOrgao(), orgao.getSiglaOrgao(),
                orgao.getEsfera(), orgao.getEmailContato(), resumos);
    }
}
