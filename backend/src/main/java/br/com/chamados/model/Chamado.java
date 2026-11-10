package br.com.chamados.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.chamados.model.enums.CategoriaChamado;
import br.com.chamados.model.enums.PrioridadeChamado;
import br.com.chamados.model.enums.StatusChamado;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "chamados")
public class Chamado {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, length = 20)
	private String protocolo;

	@Column(nullable = false, length = 160)
	private String titulo;

	@Column(nullable = false, length = 2000)
	private String descricao;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private CategoriaChamado categoria;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PrioridadeChamado prioridade;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 25)
	private StatusChamado status = StatusChamado.ABERTO;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "solicitante_id", nullable = false)
	private Usuario solicitante;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tecnico_id")
	private Usuario tecnico;

	@Column(length = 120)
	private String local;

	@Column(nullable = false)
	private LocalDateTime dataAbertura;

	@Column(nullable = false)
	private LocalDateTime dataAtualizacao;

	private LocalDateTime dataFechamento;

	@Column(nullable = false)
	private LocalDateTime prazoSla;

	@Column(length = 2000)
	private String solucao;

	@OneToMany(mappedBy = "chamado", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("dataComentario ASC")
	private List<Comentario> comentarios = new ArrayList<>();

	public Chamado() {
	}

	public void adicionarComentario(Comentario comentario) {
		comentario.setChamado(this);
		this.comentarios.add(comentario);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public CategoriaChamado getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaChamado categoria) {
		this.categoria = categoria;
	}

	public PrioridadeChamado getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(PrioridadeChamado prioridade) {
		this.prioridade = prioridade;
	}

	public StatusChamado getStatus() {
		return status;
	}

	public void setStatus(StatusChamado status) {
		this.status = status;
	}

	public Usuario getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(Usuario solicitante) {
		this.solicitante = solicitante;
	}

	public Usuario getTecnico() {
		return tecnico;
	}

	public void setTecnico(Usuario tecnico) {
		this.tecnico = tecnico;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public LocalDateTime getDataAbertura() {
		return dataAbertura;
	}

	public void setDataAbertura(LocalDateTime dataAbertura) {
		this.dataAbertura = dataAbertura;
	}

	public LocalDateTime getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public LocalDateTime getDataFechamento() {
		return dataFechamento;
	}

	public void setDataFechamento(LocalDateTime dataFechamento) {
		this.dataFechamento = dataFechamento;
	}

	public LocalDateTime getPrazoSla() {
		return prazoSla;
	}

	public void setPrazoSla(LocalDateTime prazoSla) {
		this.prazoSla = prazoSla;
	}

	public String getSolucao() {
		return solucao;
	}

	public void setSolucao(String solucao) {
		this.solucao = solucao;
	}

	public List<Comentario> getComentarios() {
		return comentarios;
	}

	public void setComentarios(List<Comentario> comentarios) {
		this.comentarios = comentarios;
	}
}
