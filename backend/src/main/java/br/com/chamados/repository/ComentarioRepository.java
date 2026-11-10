package br.com.chamados.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.chamados.model.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

}
