package br.com.chamados.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ResponseEntity<Map<String, Object>> tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
		return montarResposta(HttpStatus.NOT_FOUND, ex.getMessage(), null);
	}

	@ExceptionHandler(RegraDeNegocioException.class)
	public ResponseEntity<Map<String, Object>> tratarRegraDeNegocio(RegraDeNegocioException ex) {
		return montarResposta(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> tratarValidacao(MethodArgumentNotValidException ex) {
		Map<String, String> campos = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(erro -> campos.put(erro.getField(), erro.getDefaultMessage()));
		return montarResposta(HttpStatus.BAD_REQUEST, "Dados invalidos", campos);
	}

	private ResponseEntity<Map<String, Object>> montarResposta(HttpStatus status, String mensagem,
			Map<String, String> campos) {
		Map<String, Object> corpo = new LinkedHashMap<>();
		corpo.put("timestamp", LocalDateTime.now());
		corpo.put("status", status.value());
		corpo.put("mensagem", mensagem);
		if (campos != null && !campos.isEmpty()) {
			corpo.put("campos", campos);
		}
		return ResponseEntity.status(status).body(corpo);
	}
}
