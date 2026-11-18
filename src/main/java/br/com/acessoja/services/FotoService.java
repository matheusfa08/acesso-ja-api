package br.com.acessoja.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

/**
 * Guarda e entrega as fotos dos avisos em uma pasta do servidor (acessoja.upload.pasta).
 *
 * COMO TROCAR DEPOIS: para usar S3, Cloudinary etc., reescreva só {@link #salvar} e {@link #carregar}.
 * O banco guarda apenas o texto devolvido por salvar() na coluna foto_url.
 *
 * ATENÇÃO (Render/Docker): o disco do contêiner é apagado a cada deploy. Para produção, use um serviço de arquivos.
 */
@Service
public class FotoService {

    /** RNF08: tamanho máximo da foto, em bytes (5 MB). */
    public static final long MAX_BYTES = 5L * 1024 * 1024;

    /** Tipos aceitos e a extensão usada ao gravar. */
    private static final Map<String, String> EXTENSOES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private final Path pasta;

    public FotoService(@Value("${acessoja.upload.pasta:uploads}") String pasta) {
        this.pasta = Path.of(pasta).toAbsolutePath().normalize();
    }

    /** Valida e grava a foto. Devolve o caminho lógico (ex.: /fotos/3f2a...jpg) para a coluna foto_url. */
    public String salvar(MultipartFile foto) {
        validar(foto);
        String extensao = EXTENSOES.get(foto.getContentType());
        String nome = UUID.randomUUID() + "." + extensao; // nome aleatório: nunca usamos o nome enviado pelo cliente
        try {
            Files.createDirectories(pasta);
            try (InputStream entrada = foto.getInputStream()) {
                Files.copy(entrada, pasta.resolve(nome), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível salvar a foto", e);
        }
        return "/fotos/" + nome;
    }

    /** Abre o arquivo da foto a partir do foto_url. */
    public Resource carregar(String fotoUrl) {
        if (fotoUrl == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto não encontrada");
        }
        String nome = fotoUrl.substring(fotoUrl.lastIndexOf('/') + 1);
        Path arquivo = pasta.resolve(nome).normalize();
        // Proteção: o caminho final precisa continuar dentro da pasta de fotos (evita ../).
        if (!arquivo.startsWith(pasta) || !Files.isRegularFile(arquivo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto não encontrada");
        }
        return new FileSystemResource(arquivo);
    }

    /** Tipo de conteúdo (Content-Type) pela extensão do arquivo. */
    public String tipoConteudo(String fotoUrl) {
        String minusculo = fotoUrl == null ? "" : fotoUrl.toLowerCase();
        if (minusculo.endsWith(".png")) {
            return "image/png";
        }
        if (minusculo.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/jpeg";
    }

    /** Foto obrigatória, até 5 MB, e tipo de imagem permitido. */
    private void validar(MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anexe uma foto do local");
        }
        if (foto.getSize() > MAX_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "A foto pode ter no máximo 5 MB");
        }
        if (foto.getContentType() == null || !EXTENSOES.containsKey(foto.getContentType())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Envie uma foto em JPG, PNG ou WEBP");
        }
    }
}
