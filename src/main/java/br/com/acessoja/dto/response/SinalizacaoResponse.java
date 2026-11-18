package br.com.acessoja.dto.response;

import java.time.LocalDateTime;

/**
 * Resposta de GET /moderacao/sinalizacoes e POST /avisos/{id}/sinalizacoes.
 */
public record SinalizacaoResponse(
        int id,
        String motivo,
        LocalDateTime dataSinalizacao,
        int avisoId,
        /** Descrição do aviso sinalizado, para o moderador analisar sem abrir outra tela. */
        String descricaoAviso,
        /** Quem sinalizou. Só o moderador vê. */
        UsuarioResumoResponse sinalizadoPor
) {
}
