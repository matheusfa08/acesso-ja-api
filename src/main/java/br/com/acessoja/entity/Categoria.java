package br.com.acessoja.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Categoria do aviso e o órgão responsável por ela (RN01 e RN07).
 * Espelha a tabela categoria do banco. Os nomes seguem as colunas em camelCase.
 * Chaves estrangeiras ficam como ids (int); quem precisa do objeto completo busca pelo DAO.
 * Lombok gera os getters e setters (mesmo padrão do projeto Soulpass).
 */
@Getter
@Setter
public class Categoria {
    /** id_categoria (PK). */
    private int idCategoria;
    /** nome_categoria (único): transporte, alagamento, delito, zeladoria, outro. */
    private String nomeCategoria;
    /** dias_expiracao: avisos da categoria expiram após N dias sem confirmação (RN12). Nulo = nunca expira. */
    private Integer diasExpiracao;
    /** id_orgao (FK): órgão que responde por esta categoria. */
    private int idOrgao;
}
