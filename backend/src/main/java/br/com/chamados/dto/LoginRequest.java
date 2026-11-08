package br.com.chamados.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
		@NotBlank(message = "O e-mail e obrigatorio")
		String email) {
}
