package br.com.chamados.dto;

import java.time.LocalDateTime;

import br.com.chamados.model.Comentario;

public record ComentarioResponse(Long id, String autor, String mensagem, LocalDateTime dataComentario) {

	public static ComentarioResponse from(Comentario comentario) {
		return new ComentarioResponse(
				comentario.getId(),
				comentario.getAutor(),
				comentario.getMensagem(),
				comentario.getDataComentario());
	}
}
