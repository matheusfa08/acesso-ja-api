package br.com.acessoja.dto.request;

import br.com.acessoja.enums.EnumStatusAviso;

/**
 * Corpo de PATCH /avisos/{id}/status (HU13, RF11).
 * A ordem é fixa (RN06): NAO_RECEBIDA > RECEBIDA > RESOLVENDO > RESOLVIDA, sem pular etapas.
 */
public record AtualizarStatusRequest(EnumStatusAviso status) {
}
