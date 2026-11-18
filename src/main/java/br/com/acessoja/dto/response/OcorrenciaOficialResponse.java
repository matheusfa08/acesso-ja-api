package br.com.acessoja.dto.response;

import java.time.LocalDateTime;

/**
 * Resposta de GET e POST /ocorrencias-oficiais.
 */
public record OcorrenciaOficialResponse(
        int id,
        String titulo,
        String descricao,
        LocalDateTime dataPublicacao,
        OrgaoResumoResponse orgao
) {
}
