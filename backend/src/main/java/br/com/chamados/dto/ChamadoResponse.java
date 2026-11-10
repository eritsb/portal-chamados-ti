package br.com.chamados.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ChamadoResponse(
		Long id,
		String protocolo,
		String titulo,
		String descricao,
		String categoria,
		String prioridade,
		String status,
		Long solicitanteId,
		String solicitanteNome,
		String solicitanteSetor,
		Long tecnicoId,
		String tecnicoNome,
		String local,
		LocalDateTime dataAbertura,
		LocalDateTime dataAtualizacao,
		LocalDateTime dataFechamento,
		LocalDateTime prazoSla,
		boolean slaViolado,
		String solucao,
		List<ComentarioResponse> comentarios) {
}
