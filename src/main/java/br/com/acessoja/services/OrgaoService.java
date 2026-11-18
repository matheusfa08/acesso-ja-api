package br.com.acessoja.services;

import br.com.acessoja.dao.CategoriaDAO;
import br.com.acessoja.dao.OrgaoDAO;
import br.com.acessoja.dto.response.OrgaoResponse;
import br.com.acessoja.entity.Orgao;
import br.com.acessoja.mapper.OrgaoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** Regras de órgãos (GET /orgaos). */
@Service
public class OrgaoService {

    /** Todos os órgãos, cada um com as categorias que atende. */
    public List<OrgaoResponse> listar() {
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        return new OrgaoDAO().listarOrgaos().stream()
                .map(o -> OrgaoMapper.toResponse(o, categoriaDAO.listarCategoriasPorOrgao(o.getIdOrgao())))
                .toList();
    }

    /** Usado por outros services: devolve o órgão ou responde 404. */
    public Orgao buscarPorId(int idOrgao) {
        Orgao orgao = new OrgaoDAO().buscarOrgaoPorId(idOrgao);
        if (orgao == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Órgão não encontrado");
        }
        return orgao;
    }
}
