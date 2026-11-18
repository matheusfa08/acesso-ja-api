package br.com.acessoja.dto.request;

import org.springframework.web.multipart.MultipartFile;

/**
 * Campos de POST /avisos (HU04, RF03, RF18). Chega como multipart/form-data por causa da foto.
 * O Spring preenche o record a partir dos campos do formulário.
 */
public record AvisoCriarRequest(
        /** Id da categoria (veja GET /categorias). */
        Integer categoriaId,
        /** "Detalhes" da tela Novo aviso (até 300). */
        String descricao,
        /** "Local" da tela (até 120). */
        String endereco,
        /** Bairro (até 50). Usado no filtro do board. */
        String bairro,
        /**
         * Resposta de "Está acontecendo agora?" (RN10, RF18).
         * Obrigatória só nas categorias que fazem a pergunta (delito). Nas outras pode ficar vazia.
         */
        Boolean acontecendoAgora,
        /** Foto de prova, até 5 MB (RN02, RNF08). */
        MultipartFile foto
) {
}
