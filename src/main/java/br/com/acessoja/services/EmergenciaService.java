package br.com.acessoja.services;

import br.com.acessoja.dto.response.ContatoEmergenciaResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/** Aviso fixo de emergência. */
@Service
public class EmergenciaService {

    /** Números que aparecem em todas as telas. Mude a lista aqui se precisar. */
    public List<ContatoEmergenciaResponse> listarContatos() {
        return List.of(
                new ContatoEmergenciaResponse("190", "Polícia Militar"),
                new ContatoEmergenciaResponse("193", "Corpo de Bombeiros"),
                new ContatoEmergenciaResponse("192", "SAMU")
        );
    }
}
