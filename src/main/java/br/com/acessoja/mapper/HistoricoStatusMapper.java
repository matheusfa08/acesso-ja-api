package br.com.acessoja.mapper;

import br.com.acessoja.dto.response.HistoricoStatusResponse;
import br.com.acessoja.entity.HistoricoStatus;

//Responsável pelas conversões objeto -> DTO e DTO -> objeto
public class HistoricoStatusMapper {

    /**
     * @param atualizadoPor sigla do órgão que fez a mudança (o service descobre a partir do servidor)
     */
    public static HistoricoStatusResponse toResponse(HistoricoStatus historico, String atualizadoPor) {
        return new HistoricoStatusResponse(historico.getIdHistorico(), historico.getStatusHistorico(),
                historico.getDataHora(), atualizadoPor);
    }
}
