package br.com.acessoja.services;

import br.com.acessoja.dao.AvisoDAO;
import br.com.acessoja.dao.SinalizacaoDAO;
import br.com.acessoja.dao.UsuarioDAO;
import br.com.acessoja.dto.request.SinalizacaoCriarRequest;
import br.com.acessoja.dto.response.SinalizacaoResponse;
import br.com.acessoja.entity.Aviso;
import br.com.acessoja.entity.Sinalizacao;
import br.com.acessoja.mapper.SinalizacaoMapper;
import br.com.acessoja.contexto.ContextoUsuario;
import br.com.acessoja.contexto.SessaoUsuario;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** Sinalização de aviso falso ou inadequado e fila da moderação. */
@Service
public class SinalizacaoService {

    /** Tamanho da coluna motivo no banco. */
    private static final int MAX_MOTIVO = 200;

    /** POST /avisos/{id}/sinalizacoes. */
    public SinalizacaoResponse sinalizar(int idAviso, SinalizacaoCriarRequest dto) {
        SessaoUsuario eu = ContextoUsuario.exigir();
        Validacao.texto(dto.motivo(), "motivo", MAX_MOTIVO);
        Aviso aviso = new AvisoDAO().buscarAvisoNaoRemovido(idAviso);
        if (aviso == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aviso não encontrado");
        }

        SinalizacaoDAO dao = new SinalizacaoDAO();
        // O banco tem UNIQUE (id_aviso, id_usuario); conferimos antes para dar uma mensagem amigável.
        if (dao.existeSinalizacao(idAviso, eu.idUsuario())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já sinalizou este aviso");
        }

        Sinalizacao nova = SinalizacaoMapper.toEntity(dto, idAviso, eu.idUsuario());
        nova.setIdSinalizacao(dao.registrarSinalizacao(nova));
        // Ao próprio cidadão não devolvemos "sinalizadoPor" (ele já sabe quem é).
        return SinalizacaoMapper.toResponse(nova, aviso, null);
    }

    /** GET /moderacao/sinalizacoes: fila de sinalizações, da mais recente para a mais antiga. */
    public List<SinalizacaoResponse> listar() {
        AvisoDAO avisoDAO = new AvisoDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        return new SinalizacaoDAO().listarSinalizacoes().stream()
                .map(s -> SinalizacaoMapper.toResponse(
                        s,
                        avisoDAO.buscarAvisoPorId(s.getIdAviso()),      // inclui avisos já removidos
                        usuarioDAO.buscarUsuarioPorId(s.getIdUsuario()))) // o moderador vê quem sinalizou
                .toList();
    }

    /** DELETE /moderacao/sinalizacoes/{id}: descarta uma sinalização sem fundamento. */
    public void descartar(int idSinalizacao) {
        SinalizacaoDAO dao = new SinalizacaoDAO();
        if (dao.buscarSinalizacaoPorId(idSinalizacao) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sinalização não encontrada");
        }
        dao.excluirSinalizacao(idSinalizacao);
    }
}
