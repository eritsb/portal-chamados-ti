package br.com.chamados.dto;

import br.com.chamados.model.enums.CategoriaChamado;
import br.com.chamados.model.enums.PrioridadeChamado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChamadoRequest(
		@NotBlank(message = "O titulo e obrigatorio")
		@Size(max = 160, message = "O titulo deve ter no maximo 160 caracteres")
		String titulo,

		@NotBlank(message = "A descricao e obrigatoria")
		@Size(max = 2000, message = "A descricao deve ter no maximo 2000 caracteres")
		String descricao,

		@NotNull(message = "A categoria e obrigatoria")
		CategoriaChamado categoria,

		@NotNull(message = "A prioridade e obrigatoria")
		PrioridadeChamado prioridade,

		@NotNull(message = "O solicitante e obrigatorio")
		Long solicitanteId,

		Long tecnicoId,

		@Size(max = 120, message = "O local deve ter no maximo 120 caracteres")
		String local) {
}
