package br.com.acessoja.dto.response;

/**
 * Resposta de GET /avisos/estatisticas (números das telas Início e Como funciona).
 */
public record EstatisticasResponse(long total, long emAberto, long resolvidos) {
}
