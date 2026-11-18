package br.com.acessoja.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Sinalização de aviso falso ou inadequado, feita por um cidadão para a moderação (RF15).
 * Espelha a tabela sinalizacao do banco. Os nomes seguem as colunas em camelCase.
 * Chaves estrangeiras ficam como ids (int); quem precisa do objeto completo busca pelo DAO.
 * Lombok gera os getters e setters (mesmo padrão do projeto Soulpass).
 */
@Getter
@Setter
public class Sinalizacao {
    /** id_sinalizacao (PK). */
    private int idSinalizacao;
    /** motivo: texto livre de até 200 caracteres. */
    private String motivo;
    /** data_sinalizacao. */
    private LocalDateTime dataSinalizacao;
    /** id_aviso (FK). */
    private int idAviso;
    /** id_usuario (FK): quem sinalizou. Cada usuário sinaliza um aviso uma vez só. */
    private int idUsuario;
}
