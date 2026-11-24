package seneca.parte1;

import java.io.Serializable;
import java.time.LocalDate;
public final class InscripcionTorneo implements Serializable {
    private static final long serialVersionUID=1L;
    private final ParticipanteTorneo participante;
    private final LocalDate fechaInscripcion=LocalDate.now();
    private final boolean preferencial;
    private final double valorInscripcion;
    private boolean activa=true;
    private Integer posicionFinal;
    InscripcionTorneo(ParticipanteTorneo p,boolean preferencial,double valor){participante=p;this.preferencial=preferencial;valorInscripcion=valor;}
    public ParticipanteTorneo getParticipante(){return participante;}
    public LocalDate getFechaInscripcion(){return fechaInscripcion;}
    public boolean isPreferencial(){return preferencial;}
    public double getValorInscripcion(){return valorInscripcion;}
    public boolean isActiva(){return activa;}
    public Integer getPosicionFinal(){return posicionFinal;}
    void cancelar(){activa=false;}
    void asignarPosicion(int posicion){posicionFinal=posicion;}
}
