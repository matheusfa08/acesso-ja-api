package br.com.acessoja.dto.response;

/**
 * Resposta de GET e POST /avisos/{id}/confirmacoes (HU07, HU08).
 */
public record ConfirmacaoResumoResponse(
        long total,
        int minimo,
        boolean confirmado,
        boolean euConfirmei
) {
}
