package br.com.acessoja.exception;

import br.com.acessoja.dto.response.ErroResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

/**
 * Ponto único onde as exceções viram resposta HTTP.
 *
 * Regra principal: o erro 500 NUNCA mostra detalhes. Texto de SQL (ex.: "ORA-02289"), nome de classe,
 * stack trace e mensagem de exceção ficam só no log do servidor; o cliente recebe uma mensagem genérica.
 *
 * Como funciona:
 *  - ResponseStatusException (a que os services lançam) e os erros do próprio Spring MVC (JSON inválido,
 *    parâmetro com tipo errado, foto grande demais, rota inexistente...) passam por
 *    {@link #handleExceptionInternal} e saem com o status e a mensagem de sempre.
 *  - Qualquer outra exceção (SQLException embrulhada em RuntimeException nos DAOs, NullPointerException etc.)
 *    cai em {@link #tratarErroInesperado} e vira 500 genérico.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Única mensagem que o cliente vê num erro 500. Mude aqui se quiser outro texto. */
    public static final String MENSAGEM_ERRO_INTERNO = "Erro interno. Tente novamente mais tarde.";

    /** Rede de segurança: tudo o que não foi tratado em outro lugar vira 500 genérico. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> tratarErroInesperado(Exception ex, WebRequest request) {
        log.error("Erro inesperado em {}", caminho(request), ex); // a causa real fica só no log
        return montar(HttpStatus.INTERNAL_SERVER_ERROR.value(), MENSAGEM_ERRO_INTERNO, request, null);
    }

    /**
     * Todas as exceções conhecidas (ResponseStatusException e as do Spring MVC) passam por aqui.
     * Mantém o status e a mensagem delas, exceto no 500, que sempre sai genérico.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        if (statusCode.value() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            log.error("Erro interno em {}", caminho(request), ex);
            return montar(statusCode.value(), MENSAGEM_ERRO_INTERNO, request, headers);
        }
        String mensagem = null;
        if (body instanceof ProblemDetail problema) {
            mensagem = problema.getDetail(); // é o "reason" da ResponseStatusException
        }
        return montar(statusCode.value(), mensagem, request, headers);
    }

    /** Monta o corpo padrão ({@link ErroResponse}). Sem mensagem, usa o nome do status. */
    private ResponseEntity<Object> montar(int status, String mensagem, WebRequest request, HttpHeaders headers) {
        HttpStatus http = HttpStatus.resolve(status);
        String nome = http != null ? http.getReasonPhrase() : "Erro";
        String texto = (mensagem == null || mensagem.isBlank()) ? nome : mensagem;

        ErroResponse corpo = new ErroResponse(Instant.now().toString(), status, nome, texto, caminho(request));
        ResponseEntity.BodyBuilder resposta = ResponseEntity.status(status);
        if (headers != null) {
            resposta.headers(headers);
        }
        return resposta.body(corpo);
    }

    /** Caminho chamado (ex.: /api/v1/avisos), só para o log e para o corpo do erro. */
    private String caminho(WebRequest request) {
        if (request instanceof ServletWebRequest web) {
            return web.getRequest().getRequestURI();
        }
        return "";
    }
}
