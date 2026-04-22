package com.cronowise.domain;

import java.util.Objects;

public class Solicitacao {
    private final int id;
    private final TipoSolicitacao tipo;
    private final long timestamp;

    public Solicitacao(int id, TipoSolicitacao tipo, long timestamp) {
        this.id = id;
        this.tipo = tipo;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public TipoSolicitacao getTipo() {
        return tipo;
    }

    public long getTimestamp() {
        return timestamp;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Solicitacao that = (Solicitacao) o;
        return id == that.id && timestamp == that.timestamp && tipo == that.tipo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tipo, timestamp);
    }

    @Override
    public String toString() {
        return "Solicitacao{" +
                "id=" + id +
                ", tipo=" + tipo +
                ", timestamp=" + timestamp +
                '}';
    }
}
