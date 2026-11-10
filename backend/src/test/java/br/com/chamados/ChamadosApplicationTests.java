package br.com.chamados;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.chamados.service.ChamadoService;

@SpringBootTest
class ChamadosApplicationTests {

	@Autowired
	private ChamadoService chamadoService;

	@Test
	void contextLoads() {
		assertThat(chamadoService).isNotNull();
	}

	@Test
	void deveCarregarDadosIniciais() {
		assertThat(chamadoService.listar(null, null, null, null, null)).isNotEmpty();
		assertThat(chamadoService.dashboard().total()).isGreaterThan(0);
	}

}
