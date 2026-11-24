package seneca.parte1;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
public abstract class Torneo implements Serializable {
    private static final long serialVersionUID=1L;
    private final String id,nombre;
    private final LocalDate fecha;
    private final int cupoMaximo;
    private final Deporte deporte;
    private final Categoria categoria;
    private final ModalidadTorneo modalidad;
    private EstadoTorneo estado=EstadoTorneo.ABIERTO;
    private boolean cuposPreferencialesLiberados;
    private final List<InscripcionTorneo> inscripciones=new ArrayList<>();
    private final List<EncuentroTorneo> encuentros=new ArrayList<>();
    private final List<String> ranking=new ArrayList<>();
    protected Torneo(String id,String nombre,LocalDate fecha,int cupo,Deporte deporte,Categoria categoria,ModalidadTorneo modalidad){
        exigir(id!=null&&!id.isBlank()&&nombre!=null&&!nombre.isBlank()&&fecha!=null&&cupo>=2&&cupo<=256&&deporte!=null&&deporte.getNombre()!=null&&categoria!=null&&categoria.getNombre()!=null&&modalidad!=null,"Datos de torneo invalidos");
        this.id=id.trim();this.nombre=nombre.trim();this.fecha=fecha;cupoMaximo=cupo;this.deporte=deporte;this.categoria=categoria;this.modalidad=modalidad;
        if(modalidad!=ModalidadTorneo.EQUIPOS){String d=ServicioAcademias.disciplina(deporte);ServicioAcademias.nivel(categoria.getNombre());exigir(!d.equals("PADEL")||modalidad==ModalidadTorneo.DOBLES,"Padel se juega en dobles");}
        else {String d=deporte.getNombre().toLowerCase(Locale.ROOT);exigir(d.contains("fut")||d.contains("balon")||d.contains("basket")||d.contains("vole"),"Deporte de conjunto invalido");}
    }
    static void exigir(boolean ok,String m){if(!ok)throw new IllegalArgumentException(m);}
    public String getId(){return id;} public String getNombre(){return nombre;} public LocalDate getFecha(){return fecha;}
    public int getCupoMaximo(){return cupoMaximo;} public Deporte getDeporte(){return deporte;} public Categoria getCategoria(){return categoria;}
    public ModalidadTorneo getModalidad(){return modalidad;} public EstadoTorneo getEstado(){return estado;}
    public boolean isCuposPreferencialesLiberados(){return cuposPreferencialesLiberados;}
    public List<InscripcionTorneo> getInscripciones(){return List.copyOf(inscripciones);}
    public List<EncuentroTorneo> getEncuentros(){return List.copyOf(encuentros);}
    public List<String> getRanking(){return List.copyOf(ranking);}
    public int calcularCuposPreferenciales(){return (cupoMaximo+4)/5;}
    public int calcularCuposDisponibles(){return cupoMaximo-activas().size();}
    public int cuposRegularesDisponibles(){long usados=activas().stream().filter(InscripcionTorneo::isPreferencial).count();return Math.max(0,calcularCuposDisponibles()-(cuposPreferencialesLiberados?0:Math.max(0,calcularCuposPreferenciales()-(int)usados)));}
    List<InscripcionTorneo> activas(){return inscripciones.stream().filter(InscripcionTorneo::isActiva).toList();}
    void configurarRanking(List<String> ids){exigir(estado==EstadoTorneo.ABIERTO&&inscripciones.isEmpty()&&!cuposPreferencialesLiberados,"Ranking se fija antes de inscribir");exigir(ids!=null&&ids.stream().noneMatch(x->x==null||x.isBlank())&&new HashSet<>(ids).size()==ids.size(),"Ranking invalido");ranking.clear();ranking.addAll(ids);}
    InscripcionTorneo inscribir(ParticipanteTorneo p){
        exigir(estado==EstadoTorneo.ABIERTO,"Inscripciones cerradas");exigir(calcularCuposDisponibles()>0,"Torneo lleno");
        exigir(activas().stream().noneMatch(i->i.getParticipante().getId().equals(p.getId())||i.getParticipante().getUsuarios().stream().anyMatch(p::contiene)),"Participante ya inscrito");
        int lugar=ranking.indexOf(p.getId());boolean pref=!cuposPreferencialesLiberados&&lugar>=0&&lugar<calcularCuposPreferenciales();
        if(!pref)exigir(cuposRegularesDisponibles()>0,"Solo quedan cupos preferenciales");
        InscripcionTorneo i=new InscripcionTorneo(p,pref,tarifa(p));inscripciones.add(i);return i;
    }
    void desinscribir(String participante){exigir(estado==EstadoTorneo.ABIERTO,"Torneo iniciado");InscripcionTorneo i=activas().stream().filter(x->x.getParticipante().getId().equals(participante)).findFirst().orElseThrow(()->new IllegalArgumentException("Inscripcion no encontrada"));i.cancelar();}
    void liberarCuposPreferenciales(){exigir(estado==EstadoTorneo.ABIERTO,"Torneo iniciado");cuposPreferencialesLiberados=true;}
    void iniciarTorneo(){exigir(estado==EstadoTorneo.ABIERTO&&activas().size()>=2,"Se requieren dos participantes y torneo abierto");liberarCuposPreferenciales();List<InscripcionTorneo> a=activas();for(int i=0;i<a.size();i++)for(int j=i+1;j<a.size();j++)encuentros.add(new EncuentroTorneo(id+"-"+(encuentros.size()+1),fecha,a.get(i).getParticipante(),a.get(j).getParticipante()));estado=EstadoTorneo.EN_CURSO;}
    void registrarResultado(String encuentro,int a,int b){exigir(estado==EstadoTorneo.EN_CURSO,"Torneo no esta en curso");encuentros.stream().filter(e->e.getId().equals(encuentro)).findFirst().orElseThrow(()->new IllegalArgumentException("Encuentro inexistente")).registrarResultado(a,b);}
    public int victorias(ParticipanteTorneo p){return (int)encuentros.stream().filter(e->e.consultarGanador()==p).count();}
    public int diferencia(ParticipanteTorneo p){return encuentros.stream().mapToInt(e->e.diferencia(p)).sum();}
    public List<InscripcionTorneo> consultarPosiciones(){return activas().stream().sorted(Comparator.<InscripcionTorneo>comparingInt(i->victorias(i.getParticipante())).reversed().thenComparing(Comparator.<InscripcionTorneo>comparingInt(i->diferencia(i.getParticipante())).reversed()).thenComparing(i->i.getParticipante().getId())).toList();}
    void finalizarTorneo(){exigir(estado==EstadoTorneo.EN_CURSO&&encuentros.stream().allMatch(EncuentroTorneo::isFinalizado),"Hay encuentros pendientes o estado invalido");List<InscripcionTorneo> posiciones=consultarPosiciones();for(int i=0;i<posiciones.size();i++)posiciones.get(i).asignarPosicion(i+1);otorgar(posiciones.getFirst().getParticipante());estado=EstadoTorneo.FINALIZADO;}
    abstract double tarifa(ParticipanteTorneo p);
    abstract void otorgar(ParticipanteTorneo ganador);
    public String toString(){return id+" "+nombre+" "+estado+" cupos libres="+calcularCuposDisponibles();}
}
