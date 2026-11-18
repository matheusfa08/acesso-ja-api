package br.com.acessoja.dto.response;

import java.util.List;

/**
 * Resposta de GET /avisos/{id}: o aviso com a linha do tempo.
 */
public record AvisoDetalheResponse(
        AvisoResponse aviso,
        /** Da mudança mais antiga para a mais nova. */
        List<HistoricoStatusResponse> linhaDoTempo,
        /** O usuário logado já confirmou? Falso se não houver login. */
        boolean euConfirmei,
        /** Mínimo de confirmações para o destaque (RN03). */
        int minimoParaConfirmar
) {
}
