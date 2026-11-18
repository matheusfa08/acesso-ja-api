package br.com.acessoja.mapper;

import br.com.acessoja.dto.request.SinalizacaoCriarRequest;
import br.com.acessoja.dto.response.SinalizacaoResponse;
import br.com.acessoja.entity.Aviso;
import br.com.acessoja.entity.Sinalizacao;
import br.com.acessoja.entity.Usuario;

import java.time.LocalDateTime;

//Responsável pelas conversões objeto -> DTO e DTO -> objeto
public class SinalizacaoMapper {

    public static Sinalizacao toEntity(SinalizacaoCriarRequest dto, int idAviso, int idUsuario) {
        Sinalizacao sinalizacao = new Sinalizacao();
        sinalizacao.setMotivo(dto.motivo().trim());
        sinalizacao.setDataSinalizacao(LocalDateTime.now());
        sinalizacao.setIdAviso(idAviso);
        sinalizacao.setIdUsuario(idUsuario);
        return sinalizacao;
    }

    /**
     * @param aviso       aviso sinalizado (para mostrar a descrição)
     * @param sinalizador quem sinalizou; passe null para esconder (ex.: resposta ao próprio cidadão)
     */
    public static SinalizacaoResponse toResponse(Sinalizacao sinalizacao, Aviso aviso, Usuario sinalizador) {
        return new SinalizacaoResponse(
                sinalizacao.getIdSinalizacao(),
                sinalizacao.getMotivo(),
                sinalizacao.getDataSinalizacao(),
                sinalizacao.getIdAviso(),
                aviso == null ? null : aviso.getDescricao(),
                UsuarioMapper.toResumo(sinalizador));
    }
}
