package br.com.chamados;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Year;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.com.chamados.dto.AtualizarStatusRequest;
import br.com.chamados.dto.ChamadoRequest;
import br.com.chamados.dto.ChamadoResponse;
import br.com.chamados.dto.ComentarioRequest;
import br.com.chamados.dto.ComentarioResponse;
import br.com.chamados.exception.RegraDeNegocioException;
import br.com.chamados.model.Usuario;
import br.com.chamados.model.enums.CategoriaChamado;
import br.com.chamados.model.enums.PrioridadeChamado;
import br.com.chamados.model.enums.StatusChamado;
import br.com.chamados.repository.UsuarioRepository;
import br.com.chamados.service.ChamadoService;

@SpringBootTest
@Transactional
class ChamadoServiceTests {

	@Autowired
	private ChamadoService chamadoService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Test
	void deveCriarChamadoComProtocoloESlaCorretos() {
		Usuario solicitante = usuario("ana@senac.pe.gov.br");

		ChamadoResponse alta = chamadoService.criar(request(PrioridadeChamado.ALTA, solicitante.getId()));
		ChamadoResponse critica = chamadoService.criar(request(PrioridadeChamado.CRITICA, solicitante.getId()));

		assertThat(alta.status()).isEqualTo(StatusChamado.ABERTO.name());
		assertThat(alta.protocolo()).isEqualTo("TI-" + Year.now() + "-" + String.format("%04d", alta.id()));
		assertThat(alta.prazoSla()).isEqualTo(alta.dataAbertura().plusHours(8));
		assertThat(critica.prazoSla()).isEqualTo(critica.dataAbertura().plusHours(4));
	}

	@Test
	void devePreencherDataFechamentoAoResolverChamado() {
		Usuario solicitante = usuario("ana@senac.pe.gov.br");
		ChamadoResponse criado = chamadoService.criar(request(PrioridadeChamado.MEDIA, solicitante.getId()));

		ChamadoResponse resolvido = chamadoService.atualizarStatus(criado.id(),
				new AtualizarStatusRequest(StatusChamado.RESOLVIDO, null, "Equipamento testado e liberado"));

		assertThat(resolvido.status()).isEqualTo(StatusChamado.RESOLVIDO.name());
		assertThat(resolvido.dataFechamento()).isNotNull();
		assertThat(resolvido.solucao()).isEqualTo("Equipamento testado e liberado");
	}

	@Test
	void deveReabrirChamadoResolvidoLimpandoDataFechamento() {
		Usuario solicitante = usuario("ana@senac.pe.gov.br");
		ChamadoResponse criado = chamadoService.criar(request(PrioridadeChamado.BAIXA, solicitante.getId()));
		ChamadoResponse resolvido = chamadoService.atualizarStatus(criado.id(),
				new AtualizarStatusRequest(StatusChamado.RESOLVIDO, null, "Ajuste aplicado"));

		ChamadoResponse reaberto = chamadoService.reabrir(resolvido.id());

		assertThat(reaberto.status()).isEqualTo(StatusChamado.EM_ANDAMENTO.name());
		assertThat(reaberto.dataFechamento()).isNull();
	}

	@Test
	void naoDeveReabrirChamadoQueNaoEstaEncerrado() {
		Usuario solicitante = usuario("ana@senac.pe.gov.br");
		ChamadoResponse criado = chamadoService.criar(request(PrioridadeChamado.MEDIA, solicitante.getId()));

		assertThatThrownBy(() -> chamadoService.reabrir(criado.id()))
				.isInstanceOf(RegraDeNegocioException.class)
				.hasMessageContaining("resolvidos ou fechados");
	}

	@Test
	void deveAdicionarComentarioAoChamado() {
		Usuario solicitante = usuario("ana@senac.pe.gov.br");
		ChamadoResponse criado = chamadoService.criar(request(PrioridadeChamado.MEDIA, solicitante.getId()));

		ComentarioResponse comentario = chamadoService.adicionarComentario(criado.id(),
				new ComentarioRequest("Kennedy Veras", "Equipamento em analise no laboratorio"));

		assertThat(comentario.id()).isNotNull();
		assertThat(comentario.autor()).isEqualTo("Kennedy Veras");
		assertThat(comentario.dataComentario()).isNotNull();

		ChamadoResponse detalhe = chamadoService.buscarPorId(criado.id());
		assertThat(detalhe.comentarios())
				.extracting(ComentarioResponse::mensagem)
				.contains("Equipamento em analise no laboratorio");
	}

	@Test
	void deveFiltrarChamadosPorTecnico() {
		Usuario kennedy = usuario("kennedy@senac.pe.gov.br");

		List<ChamadoResponse> doTecnico = chamadoService.listar(null, null, null, kennedy.getId(), null);

		assertThat(doTecnico).isNotEmpty();
		assertThat(doTecnico).allMatch(chamado -> kennedy.getId().equals(chamado.tecnicoId()));
	}

	@Test
	void deveCombinarFiltroPorTecnicoComStatus() {
		Usuario kennedy = usuario("kennedy@senac.pe.gov.br");

		List<ChamadoResponse> resolvidosDoTecnico = chamadoService.listar(StatusChamado.RESOLVIDO,
				null, null, kennedy.getId(), null);

		assertThat(resolvidosDoTecnico).isNotEmpty();
		assertThat(resolvidosDoTecnico).allMatch(chamado -> kennedy.getId().equals(chamado.tecnicoId())
				&& StatusChamado.RESOLVIDO.name().equals(chamado.status()));
	}

	private Usuario usuario(String email) {
		return usuarioRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new IllegalStateException("Usuario de teste nao encontrado: " + email));
	}

	private ChamadoRequest request(PrioridadeChamado prioridade, Long solicitanteId) {
		return new ChamadoRequest(
				"Chamado de teste automatizado",
				"Descricao criada pelo teste de servico do backend",
				CategoriaChamado.HARDWARE,
				prioridade,
				solicitanteId,
				null,
				"Laboratorio de TI");
	}
}
