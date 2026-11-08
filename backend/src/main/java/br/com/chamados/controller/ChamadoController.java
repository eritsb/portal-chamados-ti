package br.com.chamados.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.chamados.dto.AtualizarStatusRequest;
import br.com.chamados.dto.ChamadoRequest;
import br.com.chamados.dto.ChamadoResponse;
import br.com.chamados.dto.ComentarioRequest;
import br.com.chamados.dto.ComentarioResponse;
import br.com.chamados.dto.DashboardResponse;
import br.com.chamados.model.enums.CategoriaChamado;
import br.com.chamados.model.enums.PrioridadeChamado;
import br.com.chamados.model.enums.StatusChamado;
import br.com.chamados.service.ChamadoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {

	private final ChamadoService chamadoService;

	public ChamadoController(ChamadoService chamadoService) {
		this.chamadoService = chamadoService;
	}

	@GetMapping
	public List<ChamadoResponse> listar(
			@RequestParam(required = false) StatusChamado status,
			@RequestParam(required = false) PrioridadeChamado prioridade,
			@RequestParam(required = false) CategoriaChamado categoria,
			@RequestParam(required = false) Long tecnicoId,
			@RequestParam(required = false) String busca) {
		return chamadoService.listar(status, prioridade, categoria, tecnicoId, busca);
	}

	@GetMapping("/{id}")
	public ChamadoResponse buscarPorId(@PathVariable Long id) {
		return chamadoService.buscarPorId(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ChamadoResponse criar(@Valid @RequestBody ChamadoRequest request) {
		return chamadoService.criar(request);
	}

	@PutMapping("/{id}")
	public ChamadoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ChamadoRequest request) {
		return chamadoService.atualizar(id, request);
	}

	@PatchMapping("/{id}/status")
	public ChamadoResponse atualizarStatus(@PathVariable Long id,
			@Valid @RequestBody AtualizarStatusRequest request) {
		return chamadoService.atualizarStatus(id, request);
	}

	@PatchMapping("/{id}/reabrir")
	public ChamadoResponse reabrir(@PathVariable Long id) {
		return chamadoService.reabrir(id);
	}

	@PostMapping("/{id}/comentarios")
	@ResponseStatus(HttpStatus.CREATED)
	public ComentarioResponse adicionarComentario(@PathVariable Long id,
			@Valid @RequestBody ComentarioRequest request) {
		return chamadoService.adicionarComentario(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletar(@PathVariable Long id) {
		chamadoService.deletar(id);
	}

	@GetMapping("/dashboard")
	public DashboardResponse dashboard() {
		return chamadoService.dashboard();
	}
}
