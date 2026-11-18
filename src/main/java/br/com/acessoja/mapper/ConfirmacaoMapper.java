package br.com.acessoja.mapper;

import br.com.acessoja.dto.response.ConfirmacaoResumoResponse;
import br.com.acessoja.entity.Confirmacao;

import java.time.LocalDateTime;

//Responsável pelas conversões objeto -> DTO e DTO -> objeto
public class ConfirmacaoMapper {

    /** Nova confirmação do "usuário" no "aviso", com a data de agora. */
    public static Confirmacao toEntity(int idAviso, int idUsuario) {
        Confirmacao confirmacao = new Confirmacao();
        confirmacao.setIdAviso(idAviso);
        confirmacao.setIdUsuario(idUsuario);
        confirmacao.setDataConfirmacao(LocalDateTime.now());
        return confirmacao;
    }

    /** Resumo para o front: total, mínimo, se já está confirmado (RN03) e se o usuário já confirmou. */
    public static ConfirmacaoResumoResponse toResponse(long total, int minimo, boolean euConfirmei) {
        return new ConfirmacaoResumoResponse(total, minimo, total >= minimo, euConfirmei);
    }
}
