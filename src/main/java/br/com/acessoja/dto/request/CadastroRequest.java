package br.com.acessoja.dto.request;

/**
 * Corpo de POST /auth/cadastro (HU01, RF01).
 * O perfil não vem do cliente: o cadastro sempre cria um CIDADAO.
 * Quem vira servidor é promovido pelo moderador (PUT /moderacao/usuarios/{id}/servidor).
 */
public record CadastroRequest(
        /** Nome completo (até 100). */
        String nome,
        /** E-mail (até 100). Precisa ser único. */
        String email,
        /** Senha em texto simples. */
        String senha,
        /** Repetição da senha, como na tela "Criar conta". */
        String confirmarSenha
) {
}
