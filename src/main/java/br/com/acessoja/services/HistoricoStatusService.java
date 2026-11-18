package br.com.acessoja.services;

import br.com.acessoja.dao.AvisoDAO;
import br.com.acessoja.dao.HistoricoStatusDAO;
import br.com.acessoja.dao.OrgaoDAO;
import br.com.acessoja.dao.UsuarioDAO;
import br.com.acessoja.dto.response.HistoricoStatusResponse;
import br.com.acessoja.entity.HistoricoStatus;
import br.com.acessoja.entity.Orgao;
import br.com.acessoja.entity.Usuario;
import br.com.acessoja.enums.EnumStatusAviso;
import br.com.acessoja.mapper.HistoricoStatusMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Linha do tempo dos avisos.
 * A gravação de cada mudança acontece em AvisoDAO.atualizarStatusComHistorico (junto com o novo status).
 */
@Service
public class HistoricoStatusService {

    /** GET /avisos/{id}/historico: confere que o aviso existe e devolve a linha do tempo. */
    public List<HistoricoStatusResponse> listarDoAviso(int idAviso) {
        if (new AvisoDAO().buscarAvisoNaoRemovido(idAviso) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aviso não encontrado");
        }
        return listarPorAviso(idAviso);
    }

    /** Linha do tempo sem checar o aviso (uso interno, quando o aviso já foi carregado). */
    public List<HistoricoStatusResponse> listarPorAviso(int idAviso) {
        // Guarda as siglas já descobertas para não repetir a consulta do mesmo servidor.
        Map<Integer, String> siglaPorUsuario = new HashMap<>();
        return new HistoricoStatusDAO().listarHistoricoPorAviso(idAviso).stream()
                .map(h -> HistoricoStatusMapper.toResponse(h,
                        siglaPorUsuario.computeIfAbsent(h.getIdUsuario(), this::siglaDoOrgaoDoUsuario)))
                .toList();
    }

    /**
     * Quando o aviso foi resolvido (para parar a contagem de dias em aberto).
     * @return a data, ou {@code null} se não houver registro de resolução.
     */
    public LocalDateTime resolvidoEm(int idAviso) {
        return new HistoricoStatusDAO().listarHistoricoPorAviso(idAviso).stream()
                .filter(h -> h.getStatusHistorico() == EnumStatusAviso.RESOLVIDA)
                .map(HistoricoStatus::getDataHora)
                .findFirst()
                .orElse(null);
    }

    /** Sigla do órgão do servidor que mexeu no aviso, ou "—" se o usuário não existe mais. */
    private String siglaDoOrgaoDoUsuario(int idUsuario) {
        Usuario usuario = new UsuarioDAO().buscarUsuarioPorId(idUsuario);
        if (usuario == null || usuario.getIdOrgao() == null) {
            return "—";
        }
        Orgao orgao = new OrgaoDAO().buscarOrgaoPorId(usuario.getIdOrgao());
        return orgao == null ? "—" : orgao.getSiglaOrgao();
    }
}
