package br.com.acessoja.services;

import br.com.acessoja.dao.AvisoDAO;
import br.com.acessoja.dao.ConfirmacaoDAO;
import br.com.acessoja.dto.response.ConfirmacaoResumoResponse;
import br.com.acessoja.entity.Aviso;
import br.com.acessoja.mapper.ConfirmacaoMapper;
import br.com.acessoja.contexto.ContextoUsuario;
import br.com.acessoja.contexto.SessaoUsuario;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Confirmação coletiva ("Eu também vi"): HU07, HU08, RF06, RF07, RN03 e RN04. */
@Service
public class ConfirmacaoService {

    /** Confirmações de pessoas diferentes para o aviso ganhar destaque (meta, a ajustar). */
    public static final int MINIMO_CONFIRMACOES = 3;

    /** POST /avisos/{id}/confirmacoes. */
    public ConfirmacaoResumoResponse confirmar(int idAviso) {
        SessaoUsuario eu = ContextoUsuario.exigir();
        Aviso aviso = buscarAvisoVisivel(idAviso);
        ConfirmacaoDAO dao = new ConfirmacaoDAO();

        // RN04: não confirma o próprio aviso (o banco não impede, então a regra mora aqui).
        if (aviso.getIdUsuario() == eu.idUsuario()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode confirmar o seu próprio aviso");
        }
        // RN04: nem o mesmo aviso duas vezes (o banco também impede, pela chave composta).
        if (dao.existeConfirmacao(idAviso, eu.idUsuario())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já confirmou este aviso");
        }

        dao.confirmarAviso(ConfirmacaoMapper.toEntity(idAviso, eu.idUsuario()));
        return ConfirmacaoMapper.toResponse(dao.contarConfirmacoes(idAviso), MINIMO_CONFIRMACOES, true);
    }

    /** GET /avisos/{id}/confirmacoes. O login é opcional: sem login, euConfirmei = false. */
    public ConfirmacaoResumoResponse resumo(int idAviso) {
        buscarAvisoVisivel(idAviso);
        ConfirmacaoDAO dao = new ConfirmacaoDAO();
        boolean euConfirmei = ContextoUsuario.atual()
                .map(eu -> dao.existeConfirmacao(idAviso, eu.idUsuario()))
                .orElse(false);
        return ConfirmacaoMapper.toResponse(dao.contarConfirmacoes(idAviso), MINIMO_CONFIRMACOES, euConfirmei);
    }

    /** Aviso que o público pode ver (não removido) ou 404. */
    private Aviso buscarAvisoVisivel(int idAviso) {
        Aviso aviso = new AvisoDAO().buscarAvisoNaoRemovido(idAviso);
        if (aviso == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aviso não encontrado");
        }
        return aviso;
    }
}
