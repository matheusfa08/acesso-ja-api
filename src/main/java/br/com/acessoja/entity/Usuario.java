package br.com.acessoja.entity;

import br.com.acessoja.enums.EnumPerfil;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Pessoa que usa o sistema: cidadão, servidor de um órgão ou moderador.
 * Espelha a tabela usuario do banco. Os nomes seguem as colunas em camelCase.
 * Chaves estrangeiras ficam como ids (int); quem precisa do objeto completo busca pelo DAO.
 * Lombok gera os getters e setters (mesmo padrão do projeto Soulpass).
 */
@Getter
@Setter
public class Usuario {
    /** id_usuario (PK). */
    private int idUsuario;
    /** nome_usuario. */
    private String nomeUsuario;
    /** email_usuario (único). Usado no login. */
    private String emailUsuario;
    /** senha (texto simples). ATENÇÃO: essa coluna não existe no script original; veja resources/sql/ajustes_sugeridos.sql. */
    private String senha;
    /** perfil: CIDADAO, SERVIDOR ou MODERADOR. */
    private EnumPerfil perfil;
    /** data_cadastro. */
    private LocalDateTime dataCadastro;
    /** id_orgao (FK, opcional). Obrigatório para SERVIDOR e nulo para os demais perfis (regra do banco). */
    private Integer idOrgao;
}
