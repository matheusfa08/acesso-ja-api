package br.com.acessoja.services;

import br.com.acessoja.dao.CategoriaDAO;
import br.com.acessoja.dao.OrgaoDAO;
import br.com.acessoja.dto.response.CategoriaResponse;
import br.com.acessoja.entity.Categoria;
import br.com.acessoja.entity.Orgao;
import br.com.acessoja.mapper.CategoriaMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Regras de categorias (GET /categorias). */
@Service
public class CategoriaService {

    /** Nome da categoria que pergunta "Está acontecendo agora?". Mude aqui se o nome mudar no banco. */
    public static final String NOME_CATEGORIA_DELITO = "delito";

    /** Todas as categorias com o órgão responsável e a indicação da pergunta "Está acontecendo agora?". */
    public List<CategoriaResponse> listar() {
        // Carrega os órgãos uma vez só, em vez de uma consulta por categoria.
        Map<Integer, Orgao> orgaos = new OrgaoDAO().listarOrgaos().stream()
                .collect(Collectors.toMap(Orgao::getIdOrgao, Function.identity()));
        return new CategoriaDAO().listarCategorias().stream()
                .map(c -> CategoriaMapper.toResponse(c, orgaos.get(c.getIdOrgao()), perguntaEmAndamento(c)))
                .toList();
    }

    /** Devolve a categoria ou responde 404. */
    public Categoria buscarPorId(int idCategoria) {
        Categoria categoria = new CategoriaDAO().buscarCategoriaPorId(idCategoria);
        if (categoria == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada");
        }
        return categoria;
    }

    /** Mapa idCategoria -> Categoria, útil para montar listas de avisos sem uma consulta por aviso. */
    public Map<Integer, Categoria> mapaPorId() {
        return new CategoriaDAO().listarCategorias().stream()
                .collect(Collectors.toMap(Categoria::getIdCategoria, Function.identity()));
    }

    /** Esta categoria precisa da pergunta "Está acontecendo agora?". */
    public static boolean perguntaEmAndamento(Categoria categoria) {
        return categoria != null && NOME_CATEGORIA_DELITO.equalsIgnoreCase(categoria.getNomeCategoria());
    }
}
