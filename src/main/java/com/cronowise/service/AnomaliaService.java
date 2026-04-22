package com.cronowise.service;

import java.util.ArrayList;
import java.util.List;

public class AnomaliaService {

    private static final int TEMPO_LIMITE_MS = 2000;
    private static final int LIMITE_CONSECUTIVO = 3;

    public List<Integer> verificarAlertas(List<Integer> tempos) {
        List<Integer> alertas = new ArrayList<>();

        if (tempos == null || tempos.size() < LIMITE_CONSECUTIVO) {
            return alertas;
        }

        int contagemConsecutiva = 0;

        for (int i = 0; i < tempos.size(); i++) {
            if (tempos.get(i) > TEMPO_LIMITE_MS) {
                contagemConsecutiva++;

                if (contagemConsecutiva == LIMITE_CONSECUTIVO) {
                    alertas.add(i);
                    contagemConsecutiva = 0;
                }
            } else {
                contagemConsecutiva = 0;
            }
        }

        return alertas;
    }
}