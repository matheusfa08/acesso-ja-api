package br.com.acessoja.mapper;

import br.com.acessoja.dto.request.AvisoCriarRequest;
import br.com.acessoja.dto.response.AvisoDetalheResponse;
import br.com.acessoja.dto.response.AvisoResponse;
import br.com.acessoja.dto.response.HistoricoStatusResponse;
import br.com.acessoja.entity.Aviso;
import br.com.acessoja.entity.Categoria;
import br.com.acessoja.entity.Usuario;
import br.com.acessoja.enums.EnumStatusAviso;

import java.time.LocalDateTime;
import java.util.List;

//Responsável pelas conversões objeto -> DTO e DTO -> objeto
public class AvisoMapper {

    /**
     * Formulário de novo aviso (POST /avisos) vira a entidade pronta para gravar.
     * O status inicial é sempre NAO_RECEBIDA (RN06) e o aviso nasce sem remoção.
     * O service já validou os campos e salvou a foto (fotoUrl).
     */
    public static Aviso toEntity(AvisoCriarRequest dto, int idUsuario, String fotoUrl) {
        Aviso aviso = new Aviso();
        aviso.setDescricao(dto.descricao().trim());
        aviso.setEndereco(dto.endereco().trim());
        aviso.setBairro(dto.bairro().trim());
        aviso.setFotoUrl(fotoUrl);
        aviso.setStatusAtual(EnumStatusAviso.NAO_RECEBIDA);
        aviso.setDataRegistro(LocalDateTime.now());
        aviso.setRemovido(false);
        aviso.setIdUsuario(idUsuario);
        aviso.setIdCategoria(dto.categoriaId());
        return aviso;
    }

    /**
     * Aviso para a API.
     *
     * @param qtdConfirmacoes quantas confirmações o aviso tem
     * @param minimo          mínimo para ficar "confirmado" (RN03)
     * @param validoAte       validade (RN12); null se a categoria não expira
     * @param diasEmAberto    dias desde o registro (HU11)
     * @param autor           quem registrou. Passe NULL nas respostas públicas (RN08)
     */
    public static AvisoResponse toResponse(Aviso aviso, Categoria categoria, long qtdConfirmacoes, int minimo,
                                           LocalDateTime validoAte, long diasEmAberto, Usuario autor) {
        return new AvisoResponse(
                aviso.getIdAviso(),
                aviso.getDescricao(),
                aviso.getEndereco(),
                aviso.getBairro(),
                aviso.getStatusAtual(),
                aviso.getDataRegistro(),
                diasEmAberto,
                CategoriaMapper.toResumo(categoria),
                qtdConfirmacoes,
                qtdConfirmacoes >= minimo,
                validoAte,
                caminhoDaFoto(aviso.getIdAviso()),
                UsuarioMapper.toResumo(autor) // null quando o autor não deve aparecer
        );
    }

    /** Aviso + linha do tempo (GET /avisos/{id}). */
    public static AvisoDetalheResponse toDetalhe(AvisoResponse aviso, List<HistoricoStatusResponse> linhaDoTempo,
                                                 boolean euConfirmei, int minimo) {
        return new AvisoDetalheResponse(aviso, linhaDoTempo, euConfirmei, minimo);
    }

    /**
     * Caminho (relativo à base /api/v1) para baixar a foto.
     * Não expomos o caminho do arquivo no disco: a foto sempre passa por GET /avisos/{id}/foto,
     * que é onde a regra RN09 (placa só para o órgão) pode ser aplicada.
     */
    private static String caminhoDaFoto(int idAviso) {
        return "/avisos/" + idAviso + "/foto";
    }
}
