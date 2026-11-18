package br.com.acessoja.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Confirmação ("Eu também vi") de um usuário em um aviso. Tabela associativa N:N.
 * Espelha a tabela confirmacao do banco. Os nomes seguem as colunas em camelCase.
 * Chaves estrangeiras ficam como ids (int); quem precisa do objeto completo busca pelo DAO.
 * Lombok gera os getters e setters (mesmo padrão do projeto Soulpass).
 */
@Getter
@Setter
public class Confirmacao {
    /** id_aviso (parte 1 da PK). */
    private int idAviso;
    /** id_usuario (parte 2 da PK). A mesma pessoa não confirma o mesmo aviso duas vezes (RN04). */
    private int idUsuario;
    /** data_confirmacao. A última data também decide a validade do aviso (RN12). */
    private LocalDateTime dataConfirmacao;
}
