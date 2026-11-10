package br.com.chamados.dto;

import br.com.chamados.model.Usuario;

public record UsuarioResponse(Long id, String nome, String email, String setor, String perfil, boolean ativo) {

	public static UsuarioResponse from(Usuario usuario) {
		return new UsuarioResponse(
				usuario.getId(),
				usuario.getNome(),
				usuario.getEmail(),
				usuario.getSetor(),
				usuario.getPerfil().name(),
				usuario.isAtivo());
	}
}
