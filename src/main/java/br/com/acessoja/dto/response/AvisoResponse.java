package br.com.acessoja.dto.response;

import br.com.acessoja.enums.EnumStatusAviso;
import java.time.LocalDateTime;

/**
 * Resposta de GET /avisos, POST /avisos, PATCH /avisos/{id}/status e das listas do servidor e do cidadão.
 */
public record AvisoResponse(
        int id,
        String descricao,
        String endereco,
        String bairro,
        EnumStatusAviso status,
        LocalDateTime dataRegistro,
        /** HU11/RF08: dias desde o registro (ou até a resolução, se já resolvido). */
        long diasEmAberto,
        CategoriaResumoResponse categoria,
        /** Quantas pessoas confirmaram ("Eu também vi"). */
        long qtdConfirmacoes,
        /** RN03: true a partir do mínimo de confirmações (padrão 3). */
        boolean confirmado,
        /** RN12: até quando o aviso vale sem nova confirmação. Nulo se a categoria não expira. */
        LocalDateTime validoAte,
        /** Caminho para buscar a foto (GET /avisos/{id}/foto). */
        String fotoUrl,
        /**
         * Quem registrou. RN08: fica NULO nas respostas públicas;
         * só o órgão responsável e o moderador recebem este campo.
         */
        UsuarioResumoResponse autor
) {
}
