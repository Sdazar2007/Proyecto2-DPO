package seneca.parte1;

import java.io.Serializable;
import java.util.*;
public final class ParticipanteTorneo implements Serializable {
    private static final long serialVersionUID=1L;
    private final String id, nombre;
    private final List<Usuario> usuarios;
    private final Equipo equipo;
    private ParticipanteTorneo(String id,String nombre,List<Usuario> usuarios,Equipo equipo){this.id=id;this.nombre=nombre;this.usuarios=List.copyOf(usuarios);this.equipo=equipo;}
    public static ParticipanteTorneo jugadores(List<Usuario> usuarios){
        if(usuarios==null||usuarios.size()<1||usuarios.size()>2||usuarios.stream().anyMatch(Objects::isNull)||new HashSet<>(usuarios).size()!=usuarios.size())throw new IllegalArgumentException("Jugadores invalidos");
        String id=usuarios.stream().map(Usuario::getLogin).sorted().reduce((a,b)->a+"|"+b).orElseThrow();
        return new ParticipanteTorneo("J:"+id,id,usuarios,null);
    }
    public static ParticipanteTorneo equipo(Equipo e){
        Objects.requireNonNull(e,"Equipo obligatorio");
        return new ParticipanteTorneo("E:"+e.getNombre().toLowerCase(Locale.ROOT),e.getNombre(),e.getInscripciones().stream().map(i->(Usuario)i.getSocio()).toList(),e);
    }
    public String getId(){return id;}
    public String getNombre(){return nombre;}
    public List<Usuario> getUsuarios(){return usuarios;}
    public Equipo getEquipo(){return equipo;}
    public boolean contiene(Usuario u){return usuarios.contains(u);}
    public boolean tieneEmpleado(){return usuarios.stream().anyMatch(u->u instanceof Empleado);}
    public String toString(){return id;}
}
