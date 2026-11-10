package br.com.chamados.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.com.chamados.model.Chamado;
import br.com.chamados.model.Comentario;
import br.com.chamados.model.Usuario;
import br.com.chamados.model.enums.CategoriaChamado;
import br.com.chamados.model.enums.PerfilUsuario;
import br.com.chamados.model.enums.PrioridadeChamado;
import br.com.chamados.model.enums.StatusChamado;
import br.com.chamados.repository.ChamadoRepository;
import br.com.chamados.repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

	private final UsuarioRepository usuarioRepository;
	private final ChamadoRepository chamadoRepository;

	public DataInitializer(UsuarioRepository usuarioRepository, ChamadoRepository chamadoRepository) {
		this.usuarioRepository = usuarioRepository;
		this.chamadoRepository = chamadoRepository;
	}

	@Override
	public void run(String... args) {
		if (usuarioRepository.count() > 0) {
			return;
		}

		Usuario ericha = usuarioRepository.save(new Usuario(null, "Ericha Taina", "ericha@senac.pe.gov.br",
				"Diretoria de TI", PerfilUsuario.ADMINISTRADOR));
		Usuario kennedy = usuarioRepository.save(new Usuario(null, "Kennedy Veras", "kennedy@senac.pe.gov.br",
				"Suporte Tecnico", PerfilUsuario.TECNICO));
		Usuario diego = usuarioRepository.save(new Usuario(null, "Diego Alves", "diego@senac.pe.gov.br",
				"Suporte Tecnico", PerfilUsuario.TECNICO));
		Usuario ana = usuarioRepository.save(new Usuario(null, "Ana Souza", "ana@senac.pe.gov.br",
				"Secretaria Academica", PerfilUsuario.SOLICITANTE));
		Usuario bruno = usuarioRepository.save(new Usuario(null, "Bruno Lima", "bruno@senac.pe.gov.br",
				"Recursos Humanos", PerfilUsuario.SOLICITANTE));
		Usuario carla = usuarioRepository.save(new Usuario(null, "Carla Mendes", "carla@senac.pe.gov.br",
				"Biblioteca", PerfilUsuario.SOLICITANTE));

		LocalDateTime agora = LocalDateTime.now();

		Chamado c1 = novo("Computador nao liga apos queda de energia", kennedy, diego,
				CategoriaChamado.HARDWARE, PrioridadeChamado.ALTA, StatusChamado.EM_ANDAMENTO,
				"Secretaria Academica - Sala 12", agora.minusHours(3),
				"Ao chegar no setor pela manha, o computador da recepcao nao liga. O estabilizador acende a luz, mas o gabinete nao da nenhum sinal.");
		c1.adicionarComentario(new Comentario("Diego Alves",
				"Teste de fonte realizado. Vou substituir a fonte por uma reserva do almoxarifado.", agora.minusHours(2)));

		Chamado c2 = novo("Setor sem acesso a internet", bruno, null,
				CategoriaChamado.REDE, PrioridadeChamado.CRITICA, StatusChamado.ABERTO,
				"Recursos Humanos - 2 andar", agora.minusHours(1),
				"Todo o setor de RH esta sem acesso a internet e ao sistema de ponto desde as 8h.");

		Chamado c3 = novo("Impressora nao imprime documentos", carla, diego,
				CategoriaChamado.IMPRESSORA, PrioridadeChamado.MEDIA, StatusChamado.AGUARDANDO_USUARIO,
				"Biblioteca - Balcao", agora.minusDays(1),
				"A impressora aparece como disponivel, mas os trabalhos ficam na fila e nao sao impressos.");
		c3.adicionarComentario(new Comentario("Diego Alves",
				"Fila limpa e driver reinstalado. Solicito que a usuaria teste uma nova impressao.", agora.minusHours(20)));

		Chamado c4 = novo("Redefinicao de senha do sistema academico", ana, kennedy,
				CategoriaChamado.ACESSO_SENHA, PrioridadeChamado.BAIXA, StatusChamado.RESOLVIDO,
				"Secretaria Academica", agora.minusDays(3),
				"Usuario bloqueado apos varias tentativas de acesso ao sistema academico.");
		c4.setSolucao("Senha redefinida e usuario orientado a cadastrar uma nova senha no primeiro acesso.");
		c4.setDataFechamento(agora.minusDays(2));
		c4.adicionarComentario(new Comentario("Kennedy Veras",
				"Atendimento concluido e validado com a solicitante.", agora.minusDays(2)));

		Chamado c5 = novo("Instalacao do pacote Office", carla, diego,
				CategoriaChamado.SOFTWARE, PrioridadeChamado.MEDIA, StatusChamado.EM_ANDAMENTO,
				"Biblioteca", agora.minusHours(5),
				"Necessario instalar o pacote Office no computador novo da biblioteca para elaboracao de relatorios.");

		Chamado c6 = novo("Ramal telefonico sem sinal", bruno, null,
				CategoriaChamado.TELEFONIA, PrioridadeChamado.ALTA, StatusChamado.ABERTO,
				"Recursos Humanos - Sala 4", agora.minusHours(10),
				"O ramal 4025 esta sem tom de discagem desde ontem. O setor depende do telefone para contato com candidatos.");

		Chamado c7 = novo("Erro ao emitir relatorio de frequencia", ana, kennedy,
				CategoriaChamado.SOFTWARE, PrioridadeChamado.ALTA, StatusChamado.RESOLVIDO,
				"Secretaria Academica", agora.minusDays(4),
				"Ao gerar o relatorio de frequencia o sistema exibe erro 500 e encerra a sessao.");
		c7.setSolucao("Cache do servidor de relatorios limpo e versao do componente atualizada.");
		c7.setDataFechamento(agora.minusDays(3));
		c7.adicionarComentario(new Comentario("Kennedy Veras",
				"Relatorio testado com sucesso apos a correcao.", agora.minusDays(3)));

		Chamado c8 = novo("Troca de teclado com teclas danificadas", carla, diego,
				CategoriaChamado.HARDWARE, PrioridadeChamado.BAIXA, StatusChamado.FECHADO,
				"Biblioteca", agora.minusDays(10),
				"Algumas teclas do teclado do balcao de atendimento nao funcionam.");
		c8.setSolucao("Teclado substituido por equipamento do estoque.");
		c8.setDataFechamento(agora.minusDays(9));

		Chamado c9 = novo("Wi-Fi lento na biblioteca", carla, null,
				CategoriaChamado.REDE, PrioridadeChamado.MEDIA, StatusChamado.ABERTO,
				"Biblioteca - Sala de estudos", agora.minusMinutes(40),
				"Os alunos relatam lentidao e quedas constantes na rede Wi-Fi da sala de estudos.");

		Chamado c10 = novo("Atualizacao do sistema de ponto", bruno, kennedy,
				CategoriaChamado.SOFTWARE, PrioridadeChamado.MEDIA, StatusChamado.FECHADO,
				"Recursos Humanos", agora.minusDays(15),
				"Necessaria atualizacao do sistema de ponto para o novo calendario de feriados.");
		c10.setSolucao("Atualizacao aplicada e validada com o setor de RH.");
		c10.setDataFechamento(agora.minusDays(13));

		List<Chamado> chamados = List.of(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10);
		chamadoRepository.saveAll(chamados);
		chamados.forEach(chamado -> chamado.setProtocolo(
				"TI-" + chamado.getDataAbertura().getYear() + "-" + String.format("%04d", chamado.getId())));
		chamadoRepository.saveAll(chamados);
	}

	private Chamado novo(String titulo, Usuario solicitante, Usuario tecnico, CategoriaChamado categoria,
			PrioridadeChamado prioridade, StatusChamado status, String local, LocalDateTime abertura,
			String descricao) {
		Chamado chamado = new Chamado();
		chamado.setTitulo(titulo);
		chamado.setDescricao(descricao);
		chamado.setSolicitante(solicitante);
		chamado.setTecnico(tecnico);
		chamado.setCategoria(categoria);
		chamado.setPrioridade(prioridade);
		chamado.setStatus(status);
		chamado.setLocal(local);
		chamado.setDataAbertura(abertura);
		chamado.setDataAtualizacao(abertura);
		chamado.setPrazoSla(calcularPrazo(prioridade, abertura));
		return chamado;
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
