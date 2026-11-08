package br.com.chamados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ComentarioRequest(
		@NotBlank(message = "O autor e obrigatorio")
		@Size(max = 120, message = "O autor deve ter no maximo 120 caracteres")
		String autor,

		@NotBlank(message = "A mensagem e obrigatoria")
		@Size(max = 2000, message = "A mensagem deve ter no maximo 2000 caracteres")
		String mensagem) {
}
