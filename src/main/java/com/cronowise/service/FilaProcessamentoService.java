package com.cronowise.service;

import com.cronowise.domain.Solicitacao;

import java.util.Comparator;
import java.util.List;

public class FilaProcessamentoService {

    public List<Integer> ordenarProcessamento(List<Solicitacao> solicitacoes) {
        if (solicitacoes == null || solicitacoes.isEmpty()) {
            return List.of();
        }

        return solicitacoes.stream()
                // A ordenação do Enum natural garante que URGENTE (declarado primeiro)
                // venha antes de NORMAL. Em seguida, desempata pelo timestamp.
                .sorted(Comparator.comparing(Solicitacao::getTipo)
                        .thenComparingLong(Solicitacao::getTimestamp))
                .map(Solicitacao::getId)
                .toList();
    }
}