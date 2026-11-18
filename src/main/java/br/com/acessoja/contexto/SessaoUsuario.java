package br.com.acessoja.contexto;

import br.com.acessoja.enums.EnumPerfil;

/**
 * Quem está fazendo a requisição. Os dados vêm do banco (não do cliente).
 * idOrgao só tem valor para SERVIDOR.
 */
public record SessaoUsuario(int idUsuario, EnumPerfil perfil, Integer idOrgao) {
}
