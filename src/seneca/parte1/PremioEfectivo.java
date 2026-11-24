package seneca.parte1;

import java.io.Serializable;
public final class PremioEfectivo implements Serializable {
    private static final long serialVersionUID=1L;
    private final ParticipanteTorneo participante;
    private final double monto;
    private boolean entregado;
    PremioEfectivo(ParticipanteTorneo participante,double monto){this.participante=participante;this.monto=monto;}
    public ParticipanteTorneo getParticipante(){return participante;}
    public double getMonto(){return monto;}
    public boolean isEntregado(){return entregado;}
    void marcarEntregado(){if(entregado)throw new IllegalArgumentException("Premio ya entregado");entregado=true;}
}
