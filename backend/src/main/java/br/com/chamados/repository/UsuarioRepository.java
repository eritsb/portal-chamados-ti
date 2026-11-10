package br.com.chamados.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.chamados.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	List<Usuario> findByAtivoTrueOrderByNomeAsc();

	Optional<Usuario> findByEmailIgnoreCase(String email);
}
