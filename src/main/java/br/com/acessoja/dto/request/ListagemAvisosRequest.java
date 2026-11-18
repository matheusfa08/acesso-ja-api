package br.com.acessoja.dto.request;

import br.com.acessoja.enums.EnumStatusAviso;

/**
 * Parâmetros de busca (query string) de GET /avisos, GET /orgao/avisos e GET /usuarios/me/avisos.
 * Tudo é opcional. Exemplo: /avisos?categoriaId=2&status=RECEBIDA&bairro=Sé&ordem=confirmacoes
 */
public record ListagemAvisosRequest(
        /** Filtra por categoria. */
        Integer categoriaId,
        /** Filtra por status (NAO_RECEBIDA, RECEBIDA, RESOLVENDO, RESOLVIDA). */
        EnumStatusAviso status,
        /** Filtra por bairro. */
        String bairro,
        /** "recentes" (padrão) ou "confirmacoes". */
        String ordem
) {
}
