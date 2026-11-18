package br.com.acessoja.dto.response;

/**
 * Resposta de GET /categorias.
 */
public record CategoriaResponse(
        int id,
        String nome,
        /** Dias até o aviso expirar sem nova confirmação (RN12). Nulo = não expira. */
        Integer diasExpiracao,
        /** true = o app deve perguntar "Está acontecendo agora?" antes de registrar (RN10, RF18). */
        boolean perguntaEmAndamento,
        /** Órgão responsável pela categoria. */
        OrgaoResumoResponse orgaoResponsavel
) {
}
