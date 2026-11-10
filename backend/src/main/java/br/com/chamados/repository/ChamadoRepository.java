package br.com.chamados.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.chamados.model.Chamado;
import br.com.chamados.model.enums.CategoriaChamado;
import br.com.chamados.model.enums.PrioridadeChamado;
import br.com.chamados.model.enums.StatusChamado;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

	@Query("""
			SELECT c FROM Chamado c
			JOIN FETCH c.solicitante
			LEFT JOIN FETCH c.tecnico
			WHERE (:status IS NULL OR c.status = :status)
			  AND (:prioridade IS NULL OR c.prioridade = :prioridade)
			  AND (:categoria IS NULL OR c.categoria = :categoria)
			  AND (:tecnicoId IS NULL OR c.tecnico.id = :tecnicoId)
			  AND (:busca IS NULL
			       OR LOWER(c.titulo) LIKE LOWER(CONCAT('%', :busca, '%'))
			       OR LOWER(c.protocolo) LIKE LOWER(CONCAT('%', :busca, '%'))
			       OR LOWER(c.descricao) LIKE LOWER(CONCAT('%', :busca, '%')))
			ORDER BY c.dataAbertura DESC
			""")
	List<Chamado> buscar(@Param("status") StatusChamado status,
			@Param("prioridade") PrioridadeChamado prioridade,
			@Param("categoria") CategoriaChamado categoria,
			@Param("tecnicoId") Long tecnicoId,
			@Param("busca") String busca);

	List<Chamado> findTop6ByOrderByDataAberturaDesc();

	long countByStatus(StatusChamado status);

	long countByPrioridade(PrioridadeChamado prioridade);

	long countByCategoria(CategoriaChamado categoria);

	long countByPrazoSlaBeforeAndStatusNotIn(LocalDateTime agora, List<StatusChamado> status);
}
