package br.com.chamados.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.chamados.dto.LoginRequest;
import br.com.chamados.dto.UsuarioResponse;
import br.com.chamados.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final UsuarioService usuarioService;

	public AuthController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@PostMapping("/login")
	public UsuarioResponse login(@Valid @RequestBody LoginRequest request) {
		return usuarioService.autenticar(request.email().trim());
	}
}
