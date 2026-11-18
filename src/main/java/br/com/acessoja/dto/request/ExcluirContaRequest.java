package br.com.acessoja.dto.request;

/**
 * Corpo de DELETE /usuarios/me (HU17, RF17). A senha confirma que é a própria pessoa.
 */
public record ExcluirContaRequest(String senha) {
}
