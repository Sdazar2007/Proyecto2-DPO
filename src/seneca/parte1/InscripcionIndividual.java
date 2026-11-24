package seneca.parte1;

import java.io.Serializable;
import java.util.*;

/** Modelo del UML integrado del Club Deportivo Seneca. */
public class InscripcionIndividual implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nivel;
    private int rankingELO;
    private Socio socio;
    private Deporte deporte;

    public InscripcionIndividual() {}

    public InscripcionIndividual(String nivel, int rankingELO, Socio socio, Deporte deporte) {
        this.nivel = nivel;
        this.rankingELO = rankingELO;
        this.socio = socio;
        this.deporte = deporte;
    }

    public String getNivel() { return nivel; }
    public void setNivel(String value) { this.nivel = value; }

    public int getRankingELO() { return rankingELO; }
    public void setRankingELO(int value) { this.rankingELO = value; }

    public Socio getSocio() { return socio; }
    public void setSocio(Socio value) { this.socio = value; }

    public Deporte getDeporte() { return deporte; }
    public void setDeporte(Deporte value) { this.deporte = value; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final InscripcionIndividual x = new InscripcionIndividual();
        public Builder nivel(String value) { x.setNivel(value); return this; }
        public Builder rankingELO(int value) { x.setRankingELO(value); return this; }
        public Builder socio(Socio value) { x.setSocio(value); return this; }
        public Builder deporte(Deporte value) { x.setDeporte(value); return this; }
        public InscripcionIndividual build() { return x; }
    }
}
