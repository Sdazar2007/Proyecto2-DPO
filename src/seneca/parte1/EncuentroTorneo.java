package seneca.parte1;

import java.io.Serializable;
import java.time.LocalDate;
public final class EncuentroTorneo implements Serializable {
    private static final long serialVersionUID=1L;
    private final String id;
    private final LocalDate fecha;
    private final ParticipanteTorneo local,visitante;
    private Integer puntosLocal,puntosVisitante;
    EncuentroTorneo(String id,LocalDate fecha,ParticipanteTorneo local,ParticipanteTorneo visitante){this.id=id;this.fecha=fecha;this.local=local;this.visitante=visitante;}
    public String getId(){return id;}
    public LocalDate getFecha(){return fecha;}
    public ParticipanteTorneo getLocal(){return local;}
    public ParticipanteTorneo getVisitante(){return visitante;}
    public boolean isFinalizado(){return puntosLocal!=null;}
    public String getMarcador(){return isFinalizado()?puntosLocal+"-"+puntosVisitante:"Pendiente";}
    void registrarResultado(int a,int b){if(isFinalizado()||a<0||b<0||a==b)throw new IllegalArgumentException("Resultado invalido, repetido o sin ganador");puntosLocal=a;puntosVisitante=b;}
    public ParticipanteTorneo consultarGanador(){return !isFinalizado()?null:puntosLocal>puntosVisitante?local:visitante;}
    public int diferencia(ParticipanteTorneo p){return !isFinalizado()?0:p==local?puntosLocal-puntosVisitante:p==visitante?puntosVisitante-puntosLocal:0;}
    public String toString(){return id+" "+local+" vs "+visitante+" "+getMarcador();}
}
