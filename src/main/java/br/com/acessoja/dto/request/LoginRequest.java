package br.com.acessoja.dto.request;

/**
 * Corpo de POST /auth/login (HU02, HU03).
 */
public record LoginRequest(String email, String senha) {
}
