package br.com.chamados.dto;

import java.util.List;

public record DashboardResponse(
		long total,
		long abertos,
		long emAndamento,
		long aguardandoUsuario,
		long resolvidos,
		long fechados,
		long criticos,
		long slaViolados,
		double percentualResolvidos,
		List<ContagemResponse> porCategoria,
		List<ContagemResponse> porPrioridade,
		List<ChamadoResponse> recentes) {
}
