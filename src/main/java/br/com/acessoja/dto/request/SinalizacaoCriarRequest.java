package br.com.acessoja.dto.request;

/**
 * Corpo de POST /avisos/{id}/sinalizacoes (HU15, RF15).
 */
public record SinalizacaoCriarRequest(
        /** Motivo em texto livre, até 200 caracteres (o banco não tem lista fixa de motivos). */
        String motivo
) {
}
