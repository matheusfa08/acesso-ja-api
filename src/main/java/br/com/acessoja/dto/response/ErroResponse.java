package br.com.acessoja.dto.response;

/**
 * Corpo padrão de TODO erro da API (montado pelo GlobalExceptionHandler).
 * Mantém os mesmos campos que o Spring já devolvia por padrão (timestamp, status, error, message, path),
 * então o front continua lendo "message" como antes.
 */
public record ErroResponse(
        /** Data e hora do erro (ISO-8601). */
        String timestamp,
        /** Status HTTP (404, 409, 500...). */
        int status,
        /** Nome do status ("Not Found", "Conflict"...). */
        String error,
        /** Mensagem pronta para mostrar ao usuário. Nunca traz texto do banco nem de exceção interna. */
        String message,
        /** Caminho chamado. */
        String path
) {
}
