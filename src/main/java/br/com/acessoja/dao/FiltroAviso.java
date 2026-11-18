package br.com.acessoja.dao;

import br.com.acessoja.enums.EnumStatusAviso;

/**
 * Filtros usados pelo {@link AvisoDAO#listarAvisos}. Campo nulo = "não filtrar por isso".
 *
 * @param categoriaId      só avisos desta categoria
 * @param status           só avisos neste status
 * @param bairro           só avisos deste bairro (sem diferenciar maiúsculas)
 * @param idUsuario        só avisos deste autor (tela "meus avisos")
 * @param idOrgao          só avisos das categorias deste órgão (lista do servidor)
 * @param incluirExpirados false no board (RF19/RN12 escondem o delito expirado); true em "meus avisos"
 */
public record FiltroAviso(
        Integer categoriaId,
        EnumStatusAviso status,
        String bairro,
        Integer idUsuario,
        Integer idOrgao,
        boolean incluirExpirados
) { }
