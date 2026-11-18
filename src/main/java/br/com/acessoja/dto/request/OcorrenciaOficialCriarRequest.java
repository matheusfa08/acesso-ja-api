package br.com.acessoja.dto.request;

/**
 * Corpo de POST /ocorrencias-oficiais (HU14, RF12). O órgão vem do servidor logado.
 */
public record OcorrenciaOficialCriarRequest(
        /** Título (até 100). */
        String titulo,
        /** Descrição (até 300). */
        String descricao
) {
}
