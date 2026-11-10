package br.com.chamados.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.chamados.dto.AtualizarStatusRequest;
import br.com.chamados.dto.ChamadoRequest;
import br.com.chamados.dto.ChamadoResponse;
import br.com.chamados.dto.ComentarioRequest;
import br.com.chamados.dto.ComentarioResponse;
import br.com.chamados.dto.ContagemResponse;
import br.com.chamados.dto.DashboardResponse;
import br.com.chamados.exception.RecursoNaoEncontradoException;
import br.com.chamados.exception.RegraDeNegocioException;
import br.com.chamados.model.Chamado;
import br.com.chamados.model.Comentario;
import br.com.chamados.model.Usuario;
import br.com.chamados.model.enums.CategoriaChamado;
import br.com.chamados.model.enums.PrioridadeChamado;
import br.com.chamados.model.enums.StatusChamado;
import br.com.chamados.repository.ChamadoRepository;
import br.com.chamados.repository.UsuarioRepository;
import br.com.chamados.service.mapper.ChamadoMapper;

@Service
@Transactional
public class ChamadoService {

	private static final List<StatusChamado> STATUS_ENCERRADOS = List.of(StatusChamado.RESOLVIDO,
			StatusChamado.FECHADO);

	private final ChamadoRepository chamadoRepository;
	private final UsuarioRepository usuarioRepository;

	public ChamadoService(ChamadoRepository chamadoRepository, UsuarioRepository usuarioRepository) {
		this.chamadoRepository = chamadoRepository;
		this.usuarioRepository = usuarioRepository;
	}

	@Transactional(readOnly = true)
	public List<ChamadoResponse> listar(StatusChamado status, PrioridadeChamado prioridade,
			CategoriaChamado categoria, Long tecnicoId, String busca) {
		String filtro = (busca == null || busca.isBlank()) ? null : busca.trim();
		return chamadoRepository.buscar(status, prioridade, categoria, tecnicoId, filtro)
				.stream()
				.map(ChamadoMapper::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public ChamadoResponse buscarPorId(Long id) {
		return ChamadoMapper.toResponse(buscarEntidade(id));
	}

	public ChamadoResponse criar(ChamadoRequest request) {
		Chamado chamado = new Chamado();
		preencherDadosBasicos(chamado, request);

		LocalDateTime agora = LocalDateTime.now();
		chamado.setStatus(StatusChamado.ABERTO);
		chamado.setDataAbertura(agora);
		chamado.setDataAtualizacao(agora);
		chamado.setPrazoSla(calcularPrazo(request.prioridade(), agora));

		chamado = chamadoRepository.save(chamado);
		chamado.setProtocolo(gerarProtocolo(chamado.getId()));
		chamado = chamadoRepository.save(chamado);

		return ChamadoMapper.toResponse(chamado);
	}

	public ChamadoResponse atualizar(Long id, ChamadoRequest request) {
		Chamado chamado = buscarEntidade(id);
		preencherDadosBasicos(chamado, request);
		chamado.setDataAtualizacao(LocalDateTime.now());
		chamado.setPrazoSla(calcularPrazo(request.prioridade(), chamado.getDataAbertura()));
		return ChamadoMapper.toResponse(chamadoRepository.save(chamado));
	}

	public ChamadoResponse atualizarStatus(Long id, AtualizarStatusRequest request) {
		Chamado chamado = buscarEntidade(id);

		if (chamado.getStatus() == request.status() && request.tecnicoId() == null
				&& (request.solucao() == null || request.solucao().isBlank())) {
			throw new RegraDeNegocioException("Nenhuma alteracao informada para o chamado");
		}

		if (request.tecnicoId() != null) {
			chamado.setTecnico(buscarUsuario(request.tecnicoId()));
		}

		if (request.solucao() != null && !request.solucao().isBlank()) {
			chamado.setSolucao(request.solucao().trim());
		}

		chamado.setStatus(request.status());
		chamado.setDataAtualizacao(LocalDateTime.now());

		if (STATUS_ENCERRADOS.contains(request.status())) {
			if (chamado.getDataFechamento() == null) {
				chamado.setDataFechamento(LocalDateTime.now());
			}
		} else {
			chamado.setDataFechamento(null);
		}

		return ChamadoMapper.toResponse(chamadoRepository.save(chamado));
	}

	public ChamadoResponse reabrir(Long id) {
		Chamado chamado = buscarEntidade(id);

		if (!STATUS_ENCERRADOS.contains(chamado.getStatus())) {
			throw new RegraDeNegocioException(
					"Apenas chamados resolvidos ou fechados podem ser reabertos");
		}

		chamado.setStatus(StatusChamado.EM_ANDAMENTO);
		chamado.setDataFechamento(null);
		chamado.setDataAtualizacao(LocalDateTime.now());
		return ChamadoMapper.toResponse(chamadoRepository.save(chamado));
	}

	public ComentarioResponse adicionarComentario(Long id, ComentarioRequest request) {
		Chamado chamado = buscarEntidade(id);
		Comentario comentario = new Comentario(request.autor().trim(), request.mensagem().trim(),
				LocalDateTime.now());
		chamado.adicionarComentario(comentario);
		chamado.setDataAtualizacao(LocalDateTime.now());
		chamadoRepository.save(chamado);

		Comentario salvo = chamado.getComentarios().get(chamado.getComentarios().size() - 1);
		return ComentarioResponse.from(salvo);
	}

	public void deletar(Long id) {
		Chamado chamado = buscarEntidade(id);
		chamadoRepository.delete(chamado);
	}

	@Transactional(readOnly = true)
	public DashboardResponse dashboard() {
		long total = chamadoRepository.count();
		long abertos = chamadoRepository.countByStatus(StatusChamado.ABERTO);
		long emAndamento = chamadoRepository.countByStatus(StatusChamado.EM_ANDAMENTO);
		long aguardando = chamadoRepository.countByStatus(StatusChamado.AGUARDANDO_USUARIO);
		long resolvidos = chamadoRepository.countByStatus(StatusChamado.RESOLVIDO);
		long fechados = chamadoRepository.countByStatus(StatusChamado.FECHADO);
		long criticos = chamadoRepository.countByPrioridade(PrioridadeChamado.CRITICA);
		long slaViolados = chamadoRepository.countByPrazoSlaBeforeAndStatusNotIn(LocalDateTime.now(),
				STATUS_ENCERRADOS);

		double percentualResolvidos = total == 0 ? 0
				: Math.round(((resolvidos + fechados) * 1000.0 / total)) / 10.0;

		List<ContagemResponse> porCategoria = Arrays.stream(CategoriaChamado.values())
				.map(categoria -> new ContagemResponse(categoria.name(),
						chamadoRepository.countByCategoria(categoria)))
				.toList();

		List<ContagemResponse> porPrioridade = Arrays.stream(PrioridadeChamado.values())
				.map(prioridade -> new ContagemResponse(prioridade.name(),
						chamadoRepository.countByPrioridade(prioridade)))
				.toList();

		List<ChamadoResponse> recentes = chamadoRepository.findTop6ByOrderByDataAberturaDesc()
				.stream()
				.map(ChamadoMapper::toResponse)
				.toList();

		return new DashboardResponse(total, abertos, emAndamento, aguardando, resolvidos, fechados, criticos,
				slaViolados, percentualResolvidos, porCategoria, porPrioridade, recentes);
	}

	private Chamado buscarEntidade(Long id) {
		return chamadoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Chamado nao encontrado: " + id));
	}

	private void preencherDadosBasicos(Chamado chamado, ChamadoRequest request) {
		chamado.setTitulo(request.titulo().trim());
		chamado.setDescricao(request.descricao().trim());
		chamado.setCategoria(request.categoria());
		chamado.setPrioridade(request.prioridade());
		chamado.setLocal(request.local() == null || request.local().isBlank() ? null : request.local().trim());
		chamado.setSolicitante(buscarUsuario(request.solicitanteId()));
		chamado.setTecnico(request.tecnicoId() == null ? null : buscarUsuario(request.tecnicoId()));
	}

	private Usuario buscarUsuario(Long id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado: " + id));
	}

	private String gerarProtocolo(Long id) {
		return "TI-" + LocalDateTime.now().getYear() + "-" + String.format("%04d", id);
	}

	private LocalDateTime calcularPrazo(PrioridadeChamado prioridade, LocalDateTime referencia) {
		return referencia.plusHours(switch (prioridade) {
			case CRITICA -> 4;
			case ALTA -> 8;
			case MEDIA -> 24;
			case BAIXA -> 72;
		});
	}
}
