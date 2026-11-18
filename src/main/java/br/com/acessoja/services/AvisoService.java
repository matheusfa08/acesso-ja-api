package br.com.acessoja.services;

import br.com.acessoja.dao.AvisoDAO;
import br.com.acessoja.dao.CategoriaDAO;
import br.com.acessoja.dao.ConfirmacaoDAO;
import br.com.acessoja.dao.FiltroAviso;
import br.com.acessoja.dao.UsuarioDAO;
import br.com.acessoja.dto.request.AtualizarStatusRequest;
import br.com.acessoja.dto.request.AvisoCriarRequest;
import br.com.acessoja.dto.request.ListagemAvisosRequest;
import br.com.acessoja.dto.request.RedirecionarAvisoRequest;
import br.com.acessoja.dto.response.AvisoDetalheResponse;
import br.com.acessoja.dto.response.AvisoResponse;
import br.com.acessoja.dto.response.EstatisticasResponse;
import br.com.acessoja.entity.Aviso;
import br.com.acessoja.entity.Categoria;
import br.com.acessoja.entity.Usuario;
import br.com.acessoja.enums.EnumPerfil;
import br.com.acessoja.enums.EnumStatusAviso;
import br.com.acessoja.mapper.AvisoMapper;
import br.com.acessoja.contexto.ContextoUsuario;
import br.com.acessoja.contexto.SessaoUsuario;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * Regras dos avisos: registrar, listar, detalhar, mudar status, redirecionar e remover.
 * É o service mais importante. As regras do backlog (RN01 a RN12) estão marcadas nos comentários.
 */
@Service
public class AvisoService {

    // ---- Regras numéricas (marcadas como "meta, a ajustar" no backlog). Mude aqui. ----
    /** RN05: máximo de avisos por usuário por dia. */
    public static final int LIMITE_AVISOS_DIA = 5;
    /** RN11: janela, em minutos, para bloquear aviso duplicado. */
    public static final int DUPLICADO_MINUTOS = 10;

    // Tamanhos das colunas do banco (ajuste se o script mudar).
    private static final int MAX_DESCRICAO = 300;
    private static final int MAX_ENDERECO = 120;
    private static final int MAX_BAIRRO = 50;

    private final CategoriaService categoriaService;
    private final HistoricoStatusService historicoService;
    private final FotoService fotoService;

    public AvisoService(CategoriaService categoriaService, HistoricoStatusService historicoService,
                        FotoService fotoService) {
        this.categoriaService = categoriaService;
        this.historicoService = historicoService;
        this.fotoService = fotoService;
    }

    /** Foto pronta para ser devolvida pelo controller. */
    public record FotoArquivo(Resource recurso, String tipoConteudo) {
    }

    /** POST /avisos. A ordem das verificações importa: primeiro o barato, por último o que grava. */
    public AvisoResponse registrar(AvisoCriarRequest dto) {
        SessaoUsuario eu = ContextoUsuario.exigir();
        AvisoDAO avisoDAO = new AvisoDAO();

        Validacao.texto(dto.descricao(), "descricao", MAX_DESCRICAO);
        String endereco = Validacao.texto(dto.endereco(), "endereco", MAX_ENDERECO);
        Validacao.texto(dto.bairro(), "bairro", MAX_BAIRRO);

        Integer categoriaId = Validacao.obrigatorio(dto.categoriaId(), "categoriaId");
        Categoria categoria = new CategoriaDAO().buscarCategoriaPorId(categoriaId);
        if (categoria == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Categoria não encontrada");
        }

        if (CategoriaService.perguntaEmAndamento(categoria)) {
            Boolean agora = Validacao.obrigatorio(dto.acontecendoAgora(), "acontecendoAgora");
            if (agora) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Se está acontecendo agora, ligue para o 190 ou 193. Este aviso não foi registrado.");
            }
        }

        LocalDateTime inicioDoDia = LocalDate.now().atStartOfDay();
        if (avisoDAO.contarAvisosDoUsuarioDesde(eu.idUsuario(), inicioDoDia) >= LIMITE_AVISOS_DIA) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Você já registrou o máximo de " + LIMITE_AVISOS_DIA + " avisos hoje");
        }

        LocalDateTime janela = LocalDateTime.now().minusMinutes(DUPLICADO_MINUTOS);
        Aviso duplicado = avisoDAO.buscarAvisoDuplicado(categoriaId, endereco, janela);
        if (duplicado != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe um aviso igual a este (id " + duplicado.getIdAviso() + "). Que tal confirmá-lo?");
        }

        String fotoUrl = fotoService.salvar(dto.foto());

        Aviso novo = AvisoMapper.toEntity(dto, eu.idUsuario(), fotoUrl);
        novo.setIdAviso(avisoDAO.registrarAviso(novo));
        return montarResposta(novo, categoria, true); // o próprio autor pode ver o seu nome
    }

    /** GET /avisos: board público. Esconde removidos e expirados. Sem identidade do autor (RN08). */
    public List<AvisoResponse> listarBoard(ListagemAvisosRequest dto) {
        ListagemAvisosRequest r = valorOuVazio(dto);
        FiltroAviso filtro = new FiltroAviso(r.categoriaId(), r.status(), r.bairro(), null, null, false);
        return listar(filtro, r, false);
    }

    /** GET /usuarios/me/avisos: avisos do próprio cidadão (inclui expirados, para ele acompanhar). */
    public List<AvisoResponse> listarMeus(ListagemAvisosRequest dto) {
        SessaoUsuario eu = ContextoUsuario.exigir();
        ListagemAvisosRequest r = valorOuVazio(dto);
        FiltroAviso filtro = new FiltroAviso(r.categoriaId(), r.status(), r.bairro(), eu.idUsuario(), null, true);
        return listar(filtro, r, false);
    }

    /** GET /orgao/avisos: avisos das categorias do órgão do servidor. Aqui o autor aparece (RN08). */
    public List<AvisoResponse> listarDoOrgao(ListagemAvisosRequest dto) {
        SessaoUsuario eu = ContextoUsuario.exigir();
        if (eu.idOrgao() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seu usuário não está ligado a nenhum órgão");
        }
        ListagemAvisosRequest r = valorOuVazio(dto);
        FiltroAviso filtro = new FiltroAviso(r.categoriaId(), r.status(), r.bairro(), null, eu.idOrgao(), false);
        return listar(filtro, r, true);
    }

    /** Parte comum das três listagens: consulta e converte cada aviso. */
    private List<AvisoResponse> listar(FiltroAviso filtro, ListagemAvisosRequest r, boolean incluirAutor) {
        // Carrega as categorias uma vez só (são poucas) em vez de uma consulta por aviso.
        Map<Integer, Categoria> categorias = categoriaService.mapaPorId();
        return new AvisoDAO().listarAvisos(filtro, r.ordem()).stream()
                .map(a -> montarResposta(a, categorias.get(a.getIdCategoria()), incluirAutor))
                .toList();
    }

    /** GET /avisos/{id}: aviso + linha do tempo. */
    public AvisoDetalheResponse detalhar(int idAviso) {
        Aviso aviso = buscarVisivel(idAviso);
        Categoria categoria = categoriaService.buscarPorId(aviso.getIdCategoria());

        AvisoResponse resposta = montarResposta(aviso, categoria, podeVerAutor(categoria));
        boolean euConfirmei = ContextoUsuario.atual()
                .map(eu -> new ConfirmacaoDAO().existeConfirmacao(idAviso, eu.idUsuario()))
                .orElse(false);
        return AvisoMapper.toDetalhe(resposta, historicoService.listarPorAviso(idAviso), euConfirmei,
                ConfirmacaoService.MINIMO_CONFIRMACOES);
    }

    /**
     * GET /avisos/{id}/foto.
     * Quando existir uma versão com a placa borrada, devolva-a aqui para quem não for servidor do órgão
     */
    public FotoArquivo buscarFoto(int idAviso) {
        Aviso aviso = buscarVisivel(idAviso);
        return new FotoArquivo(fotoService.carregar(aviso.getFotoUrl()), fotoService.tipoConteudo(aviso.getFotoUrl()));
    }

    /** GET /avisos/estatisticas. */
    public EstatisticasResponse estatisticas() {
        long[] n = new AvisoDAO().estatisticas();
        return new EstatisticasResponse(n[0], n[1], n[2]);
    }

    /** PATCH /avisos/{id}/status. Só o próximo passo da fila é aceito. */
    public AvisoResponse atualizarStatus(int idAviso, AtualizarStatusRequest dto) {
        SessaoUsuario eu = ContextoUsuario.exigir();
        EnumStatusAviso novo = Validacao.obrigatorio(dto.status(), "status");

        Aviso aviso = buscarVisivel(idAviso);
        Categoria categoria = categoriaService.buscarPorId(aviso.getIdCategoria());
        exigirServidorDaCategoria(eu, categoria); // RN07

        // RN06: ordem fixa, sem pular etapas. Usa a ordem em que o enum foi declarado.
        EnumStatusAviso[] ordem = EnumStatusAviso.values();
        int posicaoAtual = aviso.getStatusAtual().ordinal();
        if (posicaoAtual == ordem.length - 1) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Este aviso já está resolvido");
        }
        EnumStatusAviso proximo = ordem[posicaoAtual + 1];
        if (novo != proximo) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "O próximo status deste aviso é " + proximo.name());
        }

        // Atualiza o aviso e grava a linha do tempo juntos (transação dentro do DAO).
        new AvisoDAO().atualizarStatusComHistorico(idAviso, novo, eu.idUsuario());

        aviso.setStatusAtual(novo);
        return montarResposta(aviso, categoria, true);
    }

    /**
     * PATCH /avisos/{id}/categoria (RN07): o servidor redireciona um aviso que chegou ao órgão errado.
     * Ex.: a subprefeitura manda um aviso de transporte para a categoria transporte (e para o Metrô).
     * Não grava histórico (o banco não tem onde guardar o redirecionamento).
     */
    public AvisoResponse redirecionar(int idAviso, RedirecionarAvisoRequest dto) {
        SessaoUsuario eu = ContextoUsuario.exigir();
        Integer novaCategoriaId = Validacao.obrigatorio(dto.categoriaId(), "categoriaId");

        Aviso aviso = buscarVisivel(idAviso);
        Categoria atual = categoriaService.buscarPorId(aviso.getIdCategoria());
        exigirServidorDaCategoria(eu, atual); // só o órgão que está com o aviso pode redirecioná-lo

        Categoria nova = new CategoriaDAO().buscarCategoriaPorId(novaCategoriaId);
        if (nova == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Categoria não encontrada");
        }

        if (nova.getIdCategoria() != atual.getIdCategoria()) {
            new AvisoDAO().atualizarCategoria(idAviso, nova.getIdCategoria());
            aviso.setIdCategoria(nova.getIdCategoria());
        }
        // Depois do redirecionamento o aviso já é de outro órgão, então o autor não é exposto a quem enviou.
        return montarResposta(aviso, nova, false);
    }

    /** POST /moderacao/avisos/{id}/remocao: tira o aviso do board (remoção lógica). */
    public void remover(int idAviso) {
        AvisoDAO dao = new AvisoDAO();
        Aviso aviso = dao.buscarAvisoPorId(idAviso);
        if (aviso == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aviso não encontrado");
        }
        if (!aviso.isRemovido()) { // repetir a remoção não dá erro
            dao.marcarRemovido(idAviso);
        }
    }

    /** Aviso que o público pode ver (não removido) ou 404. */
    private Aviso buscarVisivel(int idAviso) {
        Aviso aviso = new AvisoDAO().buscarAvisoNaoRemovido(idAviso);
        if (aviso == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aviso não encontrado");
        }
        return aviso;
    }

    /** Só o servidor do órgão dono da categoria mexe no aviso. */
    private void exigirServidorDaCategoria(SessaoUsuario eu, Categoria categoria) {
        boolean ehDoOrgao = eu.perfil() == EnumPerfil.SERVIDOR
                && eu.idOrgao() != null
                && eu.idOrgao() == categoria.getIdOrgao();
        if (!ehDoOrgao) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Este aviso é de outro órgão");
        }
    }

    /** Só o moderador e o servidor do órgão responsável enxergam quem registrou o aviso. */
    private boolean podeVerAutor(Categoria categoria) {
        return ContextoUsuario.atual().map(eu ->
                eu.perfil() == EnumPerfil.MODERADOR
                        || (eu.perfil() == EnumPerfil.SERVIDOR && eu.idOrgao() != null
                        && eu.idOrgao() == categoria.getIdOrgao())
        ).orElse(false);
    }

    /** Junta os dados extras (confirmações, validade, dias em aberto, autor) e converte para o DTO. */
    private AvisoResponse montarResposta(Aviso aviso, Categoria categoria, boolean incluirAutor) {
        long qtdConfirmacoes = new ConfirmacaoDAO().contarConfirmacoes(aviso.getIdAviso());
        Usuario autor = incluirAutor ? new UsuarioDAO().buscarUsuarioPorId(aviso.getIdUsuario()) : null;
        return AvisoMapper.toResponse(aviso, categoria, qtdConfirmacoes, ConfirmacaoService.MINIMO_CONFIRMACOES,
                calcularValidade(aviso, categoria), calcularDiasEmAberto(aviso), autor);
    }

    /**
     * RN12: validade = (última confirmação, ou o registro se ninguém confirmou) + dias_expiracao da categoria.
     * Devolve null se a categoria não expira. É a mesma conta que o AvisoDAO faz no SQL para esconder o expirado.
     */
    private LocalDateTime calcularValidade(Aviso aviso, Categoria categoria) {
        if (categoria == null || categoria.getDiasExpiracao() == null) {
            return null;
        }
        LocalDateTime base = new ConfirmacaoDAO().buscarUltimaConfirmacao(aviso.getIdAviso());
        if (base == null) {
            base = aviso.getDataRegistro();
        }
        return base.plusDays(categoria.getDiasExpiracao());
    }

    /** Dias desde o registro. Se já foi resolvido, a contagem para na data da resolução. */
    private long calcularDiasEmAberto(Aviso aviso) {
        LocalDateTime fim = LocalDateTime.now();
        if (aviso.getStatusAtual() == EnumStatusAviso.RESOLVIDA) {
            LocalDateTime resolvidoEm = historicoService.resolvidoEm(aviso.getIdAviso());
            if (resolvidoEm != null) {
                fim = resolvidoEm;
            }
        }
        return ChronoUnit.DAYS.between(aviso.getDataRegistro().toLocalDate(), fim.toLocalDate());
    }

    /** Evita NullPointerException quando a requisição vem sem nenhum parâmetro de busca. */
    private ListagemAvisosRequest valorOuVazio(ListagemAvisosRequest dto) {
        return dto != null ? dto : new ListagemAvisosRequest(null, null, null, null);
    }
}
