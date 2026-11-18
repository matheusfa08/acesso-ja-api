package br.com.acessoja.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Comunicado oficial publicado por um órgão (RF12). É separado do aviso: não tem categoria, local nem status.
 * Espelha a tabela ocorrencia_oficial do banco. Os nomes seguem as colunas em camelCase.
 * Chaves estrangeiras ficam como ids (int); quem precisa do objeto completo busca pelo DAO.
 * Lombok gera os getters e setters (mesmo padrão do projeto Soulpass).
 */
@Getter
@Setter
public class OcorrenciaOficial {
    /** id_ocorrencia (PK). */
    private int idOcorrencia;
    /** titulo (até 100). */
    private String titulo;
    /** descricao (até 300). */
    private String descricao;
    /** data_publicacao. */
    private LocalDateTime dataPublicacao;
    /** id_orgao (FK): órgão que publicou. */
    private int idOrgao;
}
