package br.com.acessoja.dto.response;

import br.com.acessoja.enums.EnumStatusAviso;
import java.time.LocalDateTime;

/**
 * Um ponto da linha do tempo (HU11, RF05).
 */
public record HistoricoStatusResponse(
        int id,
        EnumStatusAviso status,
        LocalDateTime dataHora,
        /** Sigla do órgão que fez a mudança (não mostra o nome do servidor). */
        String atualizadoPor
) {
}
