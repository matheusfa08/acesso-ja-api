package br.com.acessoja.dto.response;

import br.com.acessoja.enums.EnumEsfera;
import java.util.List;

/**
 * Resposta de GET /orgaos.
 */
public record OrgaoResponse(
        int id,
        String nome,
        String sigla,
        EnumEsfera esfera,
        String emailContato,
        /** Categorias que este órgão atende. */
        List<CategoriaResumoResponse> categorias
) {
}
