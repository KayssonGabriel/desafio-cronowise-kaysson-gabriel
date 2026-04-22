package com.cronowise;

import com.cronowise.domain.Solicitacao;
import com.cronowise.domain.TipoSolicitacao;
import com.cronowise.service.AnomaliaService;
import com.cronowise.service.FilaProcessamentoService;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // =========================================================
        // TESTE 1.1: Fila de Processamento
        // =========================================================
        System.out.println("=== Teste 1.1: Fila de Processamento ===");
        FilaProcessamentoService filaService = new FilaProcessamentoService();

        List<Solicitacao> solicitacoes = Arrays.asList(
                new Solicitacao(1, TipoSolicitacao.NORMAL, 10),
                new Solicitacao(2, TipoSolicitacao.URGENTE, 20),
                new Solicitacao(3, TipoSolicitacao.NORMAL, 15),
                new Solicitacao(4, TipoSolicitacao.URGENTE, 5)
        );

        List<Integer> resultado = filaService.ordenarProcessamento(solicitacoes);

        System.out.println("Saída esperada : [4, 2, 1, 3]");
        System.out.println("Saída do sistema : " + resultado);
        System.out.println("========================================\n");


        // =========================================================
        // TESTE 1.2: Detecção de Anomalia
        // =========================================================
        System.out.println("=== Teste 1.2: Detecção de Anomalia ===");

        AnomaliaService anomaliaService = new AnomaliaService();

        List<Integer> tempos = Arrays.asList(300, 2100, 2300, 2500, 400, 2100, 2800, 2200);

        List<Integer> resultadoAlertas = anomaliaService.verificarAlertas(tempos);

        System.out.println("Saída esperada : [3, 7]");
        System.out.println("Saída do sistema : " + resultadoAlertas);
        System.out.println("========================================");
    }
}
