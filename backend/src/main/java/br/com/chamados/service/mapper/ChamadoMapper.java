package br.com.chamados.service.mapper;

import java.time.LocalDateTime;
import java.util.List;

import br.com.chamados.dto.ChamadoResponse;
import br.com.chamados.dto.ComentarioResponse;
import br.com.chamados.model.Chamado;
import br.com.chamados.model.Usuario;
import br.com.chamados.model.enums.StatusChamado;

public final class ChamadoMapper {

	private ChamadoMapper() {
	}

	public static ChamadoResponse toResponse(Chamado chamado) {
		Usuario solicitante = chamado.getSolicitante();
		Usuario tecnico = chamado.getTecnico();

		List<ComentarioResponse> comentarios = chamado.getComentarios()
				.stream()
				.map(ComentarioResponse::from)
				.toList();

		boolean slaViolado = chamado.getPrazoSla() != null
				&& chamado.getPrazoSla().isBefore(LocalDateTime.now())
				&& chamado.getStatus() != StatusChamado.RESOLVIDO
				&& chamado.getStatus() != StatusChamado.FECHADO;

		return new ChamadoResponse(
				chamado.getId(),
				chamado.getProtocolo(),
				chamado.getTitulo(),
				chamado.getDescricao(),
				chamado.getCategoria().name(),
				chamado.getPrioridade().name(),
				chamado.getStatus().name(),
				solicitante.getId(),
				solicitante.getNome(),
				solicitante.getSetor(),
				tecnico == null ? null : tecnico.getId(),
				tecnico == null ? null : tecnico.getNome(),
				chamado.getLocal(),
				chamado.getDataAbertura(),
				chamado.getDataAtualizacao(),
				chamado.getDataFechamento(),
				chamado.getPrazoSla(),
				slaViolado,
				chamado.getSolucao(),
				comentarios);
	}
}
