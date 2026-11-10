package br.com.chamados.dto;

import br.com.chamados.model.enums.StatusChamado;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizarStatusRequest(
		@NotNull(message = "O status e obrigatorio")
		StatusChamado status,

		Long tecnicoId,

		@Size(max = 2000, message = "A solucao deve ter no maximo 2000 caracteres")
		String solucao) {
}
