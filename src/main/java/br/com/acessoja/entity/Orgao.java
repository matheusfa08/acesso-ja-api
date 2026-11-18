package br.com.acessoja.entity;

import br.com.acessoja.enums.EnumEsfera;
import lombok.Getter;
import lombok.Setter;

/**
 * Órgão público que atende avisos (Metrô, Defesa Civil, SSP, CET, subprefeituras).
 * Espelha a tabela orgao do banco. Os nomes seguem as colunas em camelCase.
 * Chaves estrangeiras ficam como ids (int); quem precisa do objeto completo busca pelo DAO.
 * Lombok gera os getters e setters (mesmo padrão do projeto Soulpass).
 */
@Getter
@Setter
public class Orgao {
    /** id_orgao (PK). */
    private int idOrgao;
    /** nome_orgao (único). */
    private String nomeOrgao;
    /** sigla_orgao. */
    private String siglaOrgao;
    /** esfera: MUNICIPAL ou ESTADUAL. */
    private EnumEsfera esfera;
    /** email_contato (único). */
    private String emailContato;
}
