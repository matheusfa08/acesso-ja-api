package br.com.acessoja.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Validações simples de entrada, feitas "na mão" porque o pom não tem spring-boot-starter-validation.
 */
public final class Validacao {

    private Validacao() {
    }

    /** Responde 400 com a mensagem se a condição for falsa. */
    public static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
    }

    /** Texto obrigatório, com tamanho máximo. Devolve o texto sem espaços nas pontas. */
    public static String texto(String valor, String campo, int maximo) {
        exigir(valor != null && !valor.isBlank(), "O campo " + campo + " é obrigatório");
        String aparado = valor.trim();
        exigir(aparado.length() <= maximo, "O campo " + campo + " aceita até " + maximo + " caracteres");
        return aparado;
    }

    /** Valor obrigatório (não pode ser nulo). */
    public static <T> T obrigatorio(T valor, String campo) {
        exigir(valor != null, "O campo " + campo + " é obrigatório");
        return valor;
    }
}
