package br.com.chamados.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.chamados.dto.UsuarioResponse;
import br.com.chamados.exception.RecursoNaoEncontradoException;
import br.com.chamados.model.Usuario;
import br.com.chamados.repository.UsuarioRepository;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;

	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public List<UsuarioResponse> listar() {
		return usuarioRepository.findByAtivoTrueOrderByNomeAsc()
				.stream()
				.map(UsuarioResponse::from)
				.toList();
	}

	public Usuario buscarEntidade(Long id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado: " + id));
	}

	public UsuarioResponse autenticar(String email) {
		return usuarioRepository.findByEmailIgnoreCase(email)
				.filter(Usuario::isAtivo)
				.map(UsuarioResponse::from)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						"Usuario nao encontrado ou inativo para o e-mail informado"));
	}
}
