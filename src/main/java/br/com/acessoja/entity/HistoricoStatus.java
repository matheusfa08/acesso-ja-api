package br.com.acessoja.entity;

import br.com.acessoja.enums.EnumStatusAviso;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Uma linha da linha do tempo de um aviso: cada mudança de status feita por um servidor.
 * Espelha a tabela historico_status do banco. Os nomes seguem as colunas em camelCase.
 * Chaves estrangeiras ficam como ids (int); quem precisa do objeto completo busca pelo DAO.
 * Lombok gera os getters e setters (mesmo padrão do projeto Soulpass).
 */
@Getter
@Setter
public class HistoricoStatus {
    /** id_historico (PK). */
    private int idHistorico;
    /** status_historico: o status para o qual o aviso passou. */
    private EnumStatusAviso statusHistorico;
    /** data_hora da mudança. */
    private LocalDateTime dataHora;
    /** id_aviso (FK). */
    private int idAviso;
    /** id_usuario (FK): servidor que fez a mudança. */
    private int idUsuario;
}
