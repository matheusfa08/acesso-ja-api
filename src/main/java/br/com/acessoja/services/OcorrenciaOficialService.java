package br.com.acessoja.services;

import br.com.acessoja.dao.OcorrenciaOficialDAO;
import br.com.acessoja.dao.OrgaoDAO;
import br.com.acessoja.dto.request.OcorrenciaOficialCriarRequest;
import br.com.acessoja.dto.response.OcorrenciaOficialResponse;
import br.com.acessoja.entity.OcorrenciaOficial;
import br.com.acessoja.entity.Orgao;
import br.com.acessoja.mapper.OcorrenciaOficialMapper;
import br.com.acessoja.contexto.ContextoUsuario;
import br.com.acessoja.contexto.SessaoUsuario;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Comunicados oficiais dos órgãos. */
@Service
public class OcorrenciaOficialService {

    private static final int MAX_TITULO = 100;
    private static final int MAX_DESCRICAO = 300;

    /** POST /ocorrencias-oficiais. O comunicado sai em nome do órgão do servidor logado. */
    public OcorrenciaOficialResponse publicar(OcorrenciaOficialCriarRequest dto) {
        SessaoUsuario eu = ContextoUsuario.exigir();
        if (eu.idOrgao() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seu usuário não está ligado a nenhum órgão");
        }
        Validacao.texto(dto.titulo(), "titulo", MAX_TITULO);
        Validacao.texto(dto.descricao(), "descricao", MAX_DESCRICAO);

        OcorrenciaOficial nova = OcorrenciaOficialMapper.toEntity(dto, eu.idOrgao());
        nova.setIdOcorrencia(new OcorrenciaOficialDAO().publicarOcorrencia(nova));
        Orgao orgao = new OrgaoDAO().buscarOrgaoPorId(eu.idOrgao());
        return OcorrenciaOficialMapper.toResponse(nova, orgao);
    }

    /** GET /ocorrencias-oficiais. orgaoId nulo = de todos os órgãos. */
    public List<OcorrenciaOficialResponse> listar(Integer orgaoId) {
        OrgaoDAO orgaoDAO = new OrgaoDAO();
        Map<Integer, Orgao> cacheOrgaos = new HashMap<>(); // evita buscar o mesmo órgão várias vezes
        return new OcorrenciaOficialDAO().listarOcorrencias(orgaoId).stream()
                .map(o -> OcorrenciaOficialMapper.toResponse(o,
                        cacheOrgaos.computeIfAbsent(o.getIdOrgao(), orgaoDAO::buscarOrgaoPorId)))
                .toList();
    }
}
