package br.com.acessoja.entity;

import br.com.acessoja.enums.EnumStatusAviso;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Aviso de problema urbano registrado por um cidadão.
 * Espelha a tabela aviso do banco. Os nomes seguem as colunas em camelCase.
 * Chaves estrangeiras ficam como ids (int); quem precisa do objeto completo busca pelo DAO.
 * Lombok gera os getters e setters (mesmo padrão do projeto Soulpass).
 */
@Getter
@Setter
public class Aviso {
    /** id_aviso (PK). */
    private int idAviso;
    /** descricao (até 300 caracteres). */
    private String descricao;
    /** endereco (até 120). */
    private String endereco;
    /** bairro (até 50). Usado no filtro do board. */
    private String bairro;
    /** foto_url: caminho da foto, ex.: /fotos/aviso_001.jpg. O banco guarda uma foto por aviso. */
    private String fotoUrl;
    /** status_atual: NAO_RECEBIDA, RECEBIDA, RESOLVENDO ou RESOLVIDA. */
    private EnumStatusAviso statusAtual;
    /** data_registro. */
    private LocalDateTime dataRegistro;
    /** removido: true quando o moderador tirou o aviso do board (coluna CHAR 'S'/'N'). */
    private boolean removido;
    /** id_usuario (FK): quem registrou. Nunca vai para o público (RN08). */
    private int idUsuario;
    /** id_categoria (FK). */
    private int idCategoria;
}
