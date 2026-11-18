package br.com.acessoja.dto.response;

import br.com.acessoja.enums.EnumStatusAviso;

/**
 * Resposta de GET /avisos/mapa.
 * PENDENTE: o banco ainda não tem latitude/longitude em aviso; veja o comentário no AvisoController.
 */
public record PontoMapaResponse(
        int id,
        CategoriaResumoResponse categoria,
        EnumStatusAviso status,
        Double latitude,
        Double longitude
) {
}
