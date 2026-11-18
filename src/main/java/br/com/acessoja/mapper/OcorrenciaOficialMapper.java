package br.com.acessoja.mapper;

import br.com.acessoja.dto.request.OcorrenciaOficialCriarRequest;
import br.com.acessoja.dto.response.OcorrenciaOficialResponse;
import br.com.acessoja.entity.OcorrenciaOficial;
import br.com.acessoja.entity.Orgao;

import java.time.LocalDateTime;

//Responsável pelas conversões objeto -> DTO e DTO -> objeto
public class OcorrenciaOficialMapper {

    public static OcorrenciaOficial toEntity(OcorrenciaOficialCriarRequest dto, int idOrgao) {
        OcorrenciaOficial ocorrencia = new OcorrenciaOficial();
        ocorrencia.setTitulo(dto.titulo().trim());
        ocorrencia.setDescricao(dto.descricao().trim());
        ocorrencia.setDataPublicacao(LocalDateTime.now());
        ocorrencia.setIdOrgao(idOrgao);
        return ocorrencia;
    }

    public static OcorrenciaOficialResponse toResponse(OcorrenciaOficial ocorrencia, Orgao orgao) {
        return new OcorrenciaOficialResponse(ocorrencia.getIdOcorrencia(), ocorrencia.getTitulo(),
                ocorrencia.getDescricao(), ocorrencia.getDataPublicacao(), OrgaoMapper.toResumo(orgao));
    }
}
