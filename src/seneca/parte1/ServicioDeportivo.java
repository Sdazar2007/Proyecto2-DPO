package seneca.parte1;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/** Logica de negocio de la Parte 1. No modifica las 18 clases del UML aprobado. */
public class ServicioDeportivo implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Equipo> equipos = new ArrayList<>();
    private final List<Partido> partidos = new ArrayList<>();
    private final List<InscripcionCompetencia> inscripcionesCompetencia = new ArrayList<>();
    private final List<NominaCompetencia> nominas = new ArrayList<>();
    private final List<EstadisticaJugador> estadisticas = new ArrayList<>();
    private final Map<Socio,Integer> suspensiones = new IdentityHashMap<>();
    private final Map<Socio,Integer> amarillasAcumuladas = new IdentityHashMap<>();
    private final Map<Partido, List<Socio>> alineaciones = new IdentityHashMap<>();
    private final Map<Partido, int[]> resultados = new IdentityHashMap<>();
    private final Map<Competencia,Deporte> deportesCompetencia = new IdentityHashMap<>();
    private final Map<Competencia,Categoria> categoriasCompetencia = new IdentityHashMap<>();
    private final Map<Competencia,Map<Equipo,Integer>> puntosCompetencia = new IdentityHashMap<>();
    private final Map<Partido, Set<Socio>> participantesPartido = new IdentityHashMap<>();
    private final Map<Competencia, Map<Equipo, int[]>> estadisticasTabla = new IdentityHashMap<>();
    private final Map<PartidoCompetencia, String> fases = new IdentityHashMap<>();
    private final Map<PartidoCompetencia, String> grupos = new IdentityHashMap<>();
    private final Map<Competencia, Set<Equipo>> eliminados = new IdentityHashMap<>();
    private final Map<Competencia, Map<Socio,Integer>> amarillasTorneo = new IdentityHashMap<>();
    // Las sanciones oficiales no pueden cumplirse en partidos internos de practica.
    private final Map<Competencia, Map<Socio,Integer>> suspensionesTorneo = new IdentityHashMap<>();

    // Resultados de instituciones externas: necesarios para tablas y cruces de ASCUN/Cerros.
    private final Map<Competencia,List<EncuentroExterno>> encuentrosExternos = new IdentityHashMap<>();
    private final Map<Competencia,Map<String,String>> gruposInstitucion = new IdentityHashMap<>();
    private final Map<Competencia,Map<String,Integer>> umbralesAmarillas = new IdentityHashMap<>();
    private final Map<Competencia,Set<String>> eliminadosExternos = new IdentityHashMap<>();
    private final Map<Competencia,List<String>> clasificados = new IdentityHashMap<>();
    private final Map<Competencia,Map<String,String>> cruces = new IdentityHashMap<>();

    public static final class EncuentroExterno implements Serializable {
        private static final long serialVersionUID = 1L;
        public final String local, visitante, fase, grupo;
        public final int golesLocal, golesVisitante;
        public EncuentroExterno(String local,String visitante,int golesLocal,int golesVisitante,String fase,String grupo){
            this.local=local;this.visitante=visitante;this.golesLocal=golesLocal;this.golesVisitante=golesVisitante;this.fase=fase;this.grupo=grupo;
        }
    }
    public static final class FilaTabla implements Serializable {
        private static final long serialVersionUID = 1L;
        public final String equipo;
        public int jugados,ganados,empatados,perdidos,favor,contra,puntos;
        private FilaTabla(String equipo){this.equipo=equipo;}
        public int diferencia(){return favor-contra;}
        public String toString(){return equipo+" J="+jugados+" P="+puntos+" DG="+diferencia();}
    }
    private static String nombreValido(String nombre){
        exigir(nombre!=null&&!nombre.isBlank(),"Nombre obligatorio");return nombre.trim();
    }
    private static int puntosVictoria(Deporte d){return minimo(d)==7?3:2;}
    private static void sumar(FilaTabla a,FilaTabla b,int x,int y,Deporte d){
        a.jugados++;b.jugados++;a.favor+=x;a.contra+=y;b.favor+=y;b.contra+=x;
        if(x>y){a.ganados++;b.perdidos++;a.puntos+=puntosVictoria(d);}
        else if(x<y){b.ganados++;a.perdidos++;b.puntos+=puntosVictoria(d);}
        else{a.empatados++;b.empatados++;a.puntos++;b.puntos++;}
    }
    /** Registra resultados entre instituciones externas, necesarios para una tabla completa. */
    public EncuentroExterno registrarEncuentroExterno(Competencia c,String local,String visitante,int a,int b,String fase,String grupo){
        exigir(deportesCompetencia.containsKey(c),"Competencia no configurada");
        local=nombreValido(local);visitante=nombreValido(visitante);
        exigir(!igual(local,visitante),"Equipos rivales diferentes");
        exigir(a>=0&&b>=0,"Marcador negativo");
        Deporte d=deportesCompetencia.get(c);
        if(minimo(d)!=7)exigir(a!=b,"No se permiten empates");
        if(minimo(d)==6)exigir((a==3&&b<=2)||(b==3&&a<=2),"Marcador de voleibol invalido");
        String f=nombreValido(fase).toUpperCase(Locale.ROOT);
        exigir(Set.of("LIGA","GRUPOS","ELIMINACION").contains(f),"Fase invalida");
        if(f.equals("GRUPOS")){
            grupo=nombreValido(grupo);
            Map<String,String> asignados=gruposInstitucion.getOrDefault(c,Collections.emptyMap());
            for(String equipo:List.of(local,visitante)){
                String registrado=asignados.get(equipo.toLowerCase(Locale.ROOT));
                exigir(registrado==null||registrado.equalsIgnoreCase(grupo),"Equipo asignado a otro grupo");
            }
        }
        if(f.equals("ELIMINACION")){
            Map<String,String> llave=cruces.get(c);
            if(llave!=null) {
                boolean permitido=false;
                for(Map.Entry<String,String> cruce:llave.entrySet()){
                    if((igual(local,cruce.getKey())&&igual(visitante,cruce.getValue()))||
                       (igual(visitante,cruce.getKey())&&igual(local,cruce.getValue()))) permitido=true;
                }
                exigir(permitido,"Encuentro ajeno a la llave de eliminacion");
            }
            Set<String> eliminados=eliminadosExternos.computeIfAbsent(c,k->new HashSet<>());
            exigir(!eliminados.contains(local.toLowerCase(Locale.ROOT))&&!eliminados.contains(visitante.toLowerCase(Locale.ROOT)),"Equipo eliminado");
            exigir(a!=b,"Eliminatoria exige ganador");
            eliminados.add((a>b?visitante:local).toLowerCase(Locale.ROOT));
        }
        EncuentroExterno e=new EncuentroExterno(local,visitante,a,b,f,grupo);
        encuentrosExternos.computeIfAbsent(c,k->new ArrayList<>()).add(e);
        if(f.equals("GRUPOS")){
            Map<String,String> asignados=gruposInstitucion.computeIfAbsent(c,k->new HashMap<>());
            asignados.put(local.toLowerCase(Locale.ROOT),grupo);
            asignados.put(visitante.toLowerCase(Locale.ROOT),grupo);
        }
        return e;
    }
    /** Tabla de toda la competencia, incluidos rivales, filtrable por fase y grupo. */
    public List<FilaTabla> tablaCompleta(Competencia c,String fase,String grupo){
        exigir(deportesCompetencia.containsKey(c),"Competencia no configurada");
        Map<String,FilaTabla> filas=new LinkedHashMap<>();
        Deporte d=deportesCompetencia.get(c);
        if(grupo==null){for(InscripcionCompetencia ins:inscripcionesCompetencia){
            if(ins.getCompetencia()==c){String nombre=ins.getEquipo().getNombre();filas.putIfAbsent(nombre.toLowerCase(Locale.ROOT),new FilaTabla(nombre));}
        }}
        for(EncuentroExterno e:encuentrosExternos.getOrDefault(c,List.of())){
            if(fase!=null&&!fase.equalsIgnoreCase(e.fase))continue;
            if(grupo!=null&&!grupo.equalsIgnoreCase(e.grupo))continue;
            FilaTabla a=filas.computeIfAbsent(e.local.toLowerCase(Locale.ROOT),k->new FilaTabla(e.local));
            FilaTabla b=filas.computeIfAbsent(e.visitante.toLowerCase(Locale.ROOT),k->new FilaTabla(e.visitante));
            sumar(a,b,e.golesLocal,e.golesVisitante,d);
        }
        for(Partido p:partidos)if(p instanceof PartidoCompetencia){
            PartidoCompetencia pc=(PartidoCompetencia)p;
            if(pc.getCompetencia()!=c||!resultados.containsKey(p))continue;
            String f=fases.getOrDefault(pc,"LIGA");String g=grupos.get(pc);
            if(fase!=null&&!fase.equalsIgnoreCase(f))continue;
            if(grupo!=null&&!grupo.equalsIgnoreCase(g))continue;
            String n=pc.getEquipo().getNombre(),r=pc.getRival().getNombre();
            FilaTabla a=filas.computeIfAbsent(n.toLowerCase(Locale.ROOT),k->new FilaTabla(n));
            FilaTabla b=filas.computeIfAbsent(r.toLowerCase(Locale.ROOT),k->new FilaTabla(r));
            int[] res=resultados.get(p);sumar(a,b,res[0],res[1],d);
        }
        List<FilaTabla> tabla=new ArrayList<>(filas.values());
        tabla.sort((a,b)->{
            int x=Integer.compare(b.puntos,a.puntos);if(x!=0)return x;
            x=Integer.compare(b.diferencia(),a.diferencia());if(x!=0)return x;
            x=Integer.compare(b.favor,a.favor);return x!=0?x:a.equipo.compareToIgnoreCase(b.equipo);
        });
        return Collections.unmodifiableList(tabla);
    }
    /** Desempate por enfrentamiento directo cuando hay igualdad de puntos, diferencia y goles. */
    public List<FilaTabla> tablaConDesempateDirecto(Competencia c,String fase,String grupo){
        List<FilaTabla> orden=new ArrayList<>(tablaCompleta(c,fase,grupo));
        orden.sort((a,b)->{
            int x=Integer.compare(b.puntos,a.puntos);if(x!=0)return x;
            x=Integer.compare(b.diferencia(),a.diferencia());if(x!=0)return x;
            x=Integer.compare(b.favor,a.favor);if(x!=0)return x;
            int directos=resultadoDirecto(c,a.equipo,b.equipo,fase,grupo);
            return directos!=0?-Integer.signum(directos):a.equipo.compareToIgnoreCase(b.equipo);
        });return Collections.unmodifiableList(orden);
    }
    private int resultadoDirecto(Competencia c,String a,String b,String fase,String grupo){
        int saldo=0;
        for(EncuentroExterno e:encuentrosExternos.getOrDefault(c,List.of())){
            if(fase!=null&&!fase.equalsIgnoreCase(e.fase))continue;
            if(grupo!=null&&!grupo.equalsIgnoreCase(e.grupo))continue;
            if(igual(e.local,a)&&igual(e.visitante,b))saldo+=e.golesLocal-e.golesVisitante;
            if(igual(e.local,b)&&igual(e.visitante,a))saldo+=e.golesVisitante-e.golesLocal;
        }
        for(Partido p:partidos)if(p instanceof PartidoCompetencia){
            PartidoCompetencia pc=(PartidoCompetencia)p;
            if(pc.getCompetencia()!=c||!resultados.containsKey(p))continue;
            if(fase!=null&&!fase.equalsIgnoreCase(fases.getOrDefault(pc,"LIGA")))continue;
            if(grupo!=null&&!grupo.equalsIgnoreCase(grupos.get(pc)))continue;
            int[] r=resultados.get(p);
            if(igual(pc.getEquipo().getNombre(),a)&&igual(pc.getRival().getNombre(),b))saldo+=r[0]-r[1];
            if(igual(pc.getEquipo().getNombre(),b)&&igual(pc.getRival().getNombre(),a))saldo+=r[1]-r[0];
        }
        return saldo;
    }
    /** Clasifica los mejores N de cada grupo. El grupo se identifica en cada partido. */
    public List<String> clasificarPorGrupos(Competencia c,int porGrupo){
        exigir(porGrupo>0,"Numero de clasificados invalido");
        Set<String> nombres=new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for(EncuentroExterno e:encuentrosExternos.getOrDefault(c,List.of()))if("GRUPOS".equals(e.fase))nombres.add(e.grupo);
        for(Map.Entry<PartidoCompetencia,String> e:grupos.entrySet())if(e.getKey().getCompetencia()==c&&"GRUPOS".equals(fases.get(e.getKey())))nombres.add(e.getValue());
        exigir(!nombres.isEmpty(),"No hay grupos registrados");
        List<String> salida=new ArrayList<>();
        for(String g:nombres){
            List<FilaTabla> tabla=tablaConDesempateDirecto(c,"GRUPOS",g);
            exigir(tabla.size()>=porGrupo,"Grupo con pocos participantes");
            for(int i=0;i<porGrupo;i++)salida.add(tabla.get(i).equipo);
        }
        clasificados.put(c,new ArrayList<>(salida));return Collections.unmodifiableList(salida);
    }
    /** Genera cruces de eliminacion (primero contra ultimo). */
    public Map<String,String> generarCruces(Competencia c){
        List<String> participantes=clasificados.get(c);
        exigir(participantes!=null&&participantes.size()>=2&&Integer.bitCount(participantes.size())==1,"Se requieren 2,4,8... clasificados");
        Map<String,String> llave=new LinkedHashMap<>();
        for(int i=0;i<participantes.size()/2;i++)llave.put(participantes.get(i),participantes.get(participantes.size()-1-i));
        cruces.put(c,llave);return Collections.unmodifiableMap(llave);
    }
    /** Avanza ganadores de una ronda, siempre que todos los cruces tengan resultado. */
    public List<String> avanzarRonda(Competencia c){
        Map<String,String> llave=cruces.get(c);exigir(llave!=null&&!llave.isEmpty(),"No hay cruces");
        List<String> ganadores=new ArrayList<>();
        for(Map.Entry<String,String> cruce:llave.entrySet()){
            String a=cruce.getKey(),b=cruce.getValue();String ganador=null;
            for(EncuentroExterno e:encuentrosExternos.getOrDefault(c,List.of()))if("ELIMINACION".equals(e.fase)){
                if(igual(e.local,a)&&igual(e.visitante,b))ganador=e.golesLocal>e.golesVisitante?a:b;
                if(igual(e.local,b)&&igual(e.visitante,a))ganador=e.golesLocal>e.golesVisitante?b:a;
            }
            for(Partido p:partidos)if(p instanceof PartidoCompetencia){
                PartidoCompetencia pc=(PartidoCompetencia)p;
                if(pc.getCompetencia()==c&&"ELIMINACION".equals(fases.get(pc))&&resultados.containsKey(p)){
                    if(igual(pc.getEquipo().getNombre(),a)&&igual(pc.getRival().getNombre(),b))ganador=resultados.get(p)[0]>resultados.get(p)[1]?a:b;
                    if(igual(pc.getEquipo().getNombre(),b)&&igual(pc.getRival().getNombre(),a))ganador=resultados.get(p)[0]>resultados.get(p)[1]?b:a;
                }
            }
            exigir(ganador!=null,"Cruce sin resultado: "+a+" vs "+b);ganadores.add(ganador);
        }
        clasificados.put(c,new ArrayList<>(ganadores));cruces.remove(c);
        return Collections.unmodifiableList(ganadores);
    }
    public void configurarUmbralAmarillas(Competencia c,int amarillas){
        exigir(c!=null&&amarillas>=1,"Umbral invalido");
        umbralesAmarillas.computeIfAbsent(c,k->new HashMap<>()).put("umbral",amarillas);
    }
    public List<Competencia> getCompetencias(){return Collections.unmodifiableList(new ArrayList<>(deportesCompetencia.keySet()));}
    public List<EncuentroExterno> getEncuentrosExternos(Competencia c){return Collections.unmodifiableList(encuentrosExternos.getOrDefault(c,List.of()));}

    private static void exigir(boolean ok,String mensaje) {if(!ok)throw new IllegalArgumentException(mensaje);}
    private static boolean igual(String a,String b) {return a!=null&&b!=null&&a.trim().equalsIgnoreCase(b.trim());}
    private static boolean deporteIgual(Deporte a,Deporte b){return a!=null&&b!=null&&igual(a.getNombre(),b.getNombre());}
    private static boolean categoriaIgual(Categoria a,Categoria b){return a!=null&&b!=null&&igual(a.getNombre(),b.getNombre());}
    private static int minimo(Deporte d) {
        String n=d.getNombre().toLowerCase(Locale.ROOT);
        if(n.contains("fut"))return 7;
        if(n.contains("balon")||n.contains("basket"))return 5;
        if(n.contains("vole")||n.contains("volley"))return 6;
        throw new IllegalArgumentException("Deporte no soportado: "+d.getNombre());
    }
    private static boolean edadValida(int edad,Categoria c){
        exigir(c!=null&&c.getNombre()!=null,"Categoria obligatoria");
        String n=c.getNombre().toLowerCase(Locale.ROOT).replace(" ","");
        if(n.contains("13"))return edad>=0&&edad<13;
        if(n.contains("15"))return edad>=13&&edad<15;
        if(n.contains("17"))return edad>=15&&edad<17;
        if(n.contains("mayor"))return edad>=17;
        throw new IllegalArgumentException("Categoria desconocida: "+c.getNombre());
    }
    public void registrarEquipo(Equipo e){
        exigir(e!=null&&e.getNombre()!=null&&e.getDeporte()!=null&&e.getCategoria()!=null&&e.getCupoMaximo()>0,"Equipo invalido");
        exigir(!equipos.contains(e),"Equipo ya registrado");
        for(Equipo otro:equipos)exigir(!igual(otro.getNombre(),e.getNombre()) || !deporteIgual(otro.getDeporte(),e.getDeporte()) || !categoriaIgual(otro.getCategoria(),e.getCategoria()),"Equipo duplicado por nombre, deporte y categoria");
        equipos.add(e);
    }
    public InscripcionEquipo inscribir(Socio socio,Equipo equipo,String posicion,int camiseta){
        exigir(socio!=null&&socio.isActivo()&&equipo!=null&&equipo.isActivo()&&equipos.contains(equipo),"Socio o equipo invalido");
        exigir(posicion!=null&&!posicion.isBlank()&&camiseta>0,"Posicion o camiseta invalida");
        exigir(edadValida(socio.getEdad(),equipo.getCategoria()),"Edad fuera de categoria");
        exigir(equipo.getInscripciones().size()<equipo.getCupoMaximo(),"Equipo sin cupo");
        for(InscripcionEquipo i:equipo.getInscripciones()){
            exigir(i.getSocio()!=socio,"Socio ya inscrito");
            exigir(i.getNumeroCamiseta()!=camiseta,"Camiseta repetida");
        }
        for(InscripcionEquipo i:socio.getInscripciones())
            exigir(!deporteIgual(i.getEquipo().getDeporte(),equipo.getDeporte()),"Un equipo por deporte");
        InscripcionEquipo i=new InscripcionEquipo(posicion,camiseta,socio,equipo);
        equipo.getInscripciones().add(i); socio.getInscripciones().add(i);
        return i;
    }
    public void retirar(Socio s,Equipo e){
        exigir(s!=null&&e!=null,"Datos invalidos");
        InscripcionEquipo encontrado=null;
        for(InscripcionEquipo i:e.getInscripciones())if(i.getSocio()==s){encontrado=i;break;}
        exigir(encontrado!=null,"No inscrito");
        e.getInscripciones().remove(encontrado);s.getInscripciones().remove(encontrado);
    }
    public boolean habilitado(Socio s){return s!=null&&s.isActivo()&&suspensiones.getOrDefault(s,0)==0;}
    /** Solo un partido oficial de esta competencia permite cumplir la sancion del torneo. */
    public boolean habilitadoEnCompetencia(Socio s,Competencia c){
        return habilitado(s)&&suspensionesTorneo.getOrDefault(c,Collections.emptyMap()).getOrDefault(s,0)==0;
    }
    public int partidosSuspendidoEnTorneo(Socio s,Competencia c){
        return suspensionesTorneo.getOrDefault(c,Collections.emptyMap()).getOrDefault(s,0);
    }
    private void suspenderEnTorneo(Competencia c,Socio s){
        Map<Socio,Integer> m=suspensionesTorneo.computeIfAbsent(c,k->new IdentityHashMap<>());
        m.put(s,Math.max(1,m.getOrDefault(s,0)));
    }
    public List<Socio> jugadoresHabilitados(Equipo e){
        exigir(e!=null,"Equipo requerido");
        return e.getInscripciones().stream().map(InscripcionEquipo::getSocio).filter(this::habilitado).collect(Collectors.toList());
    }
    /** Version simple: se supone que participan todos los jugadores habilitados. */
    public PartidoInterno programarInterno(Equipo local,Equipo visitante,Date fecha,Escenario escenario){
        exigir(local!=null&&visitante!=null,"Equipos obligatorios");
        return programarInterno(local,visitante,fecha,escenario,
                jugadoresHabilitados(local),jugadoresHabilitados(visitante));
    }
    /** Permite registrar exactamente quienes participaron, sin incluir a suplentes. */
    public PartidoInterno programarInterno(Equipo local,Equipo visitante,Date fecha,Escenario escenario,
            List<Socio> alineadosLocal,List<Socio> alineadosVisitante){
        exigir(local!=null&&visitante!=null&&local.isActivo()&&visitante.isActivo()&&local!=visitante,"Equipos diferentes obligatorios");
        exigir(equipos.contains(local)&&equipos.contains(visitante),"Equipos no registrados");
        exigir(deporteIgual(local.getDeporte(),visitante.getDeporte())&&categoriaIgual(local.getCategoria(),visitante.getCategoria()),"Deporte y categoria deben coincidir");
        exigir(fecha!=null&&escenario!=null,"Fecha y escenario requeridos");
        exigir(alineadosLocal!=null&&alineadosVisitante!=null,"Alineaciones obligatorias");
        exigir(alineadosLocal.size()>=minimo(local.getDeporte())&&alineadosVisitante.size()>=minimo(visitante.getDeporte()),"Jugadores alineados insuficientes");
        Set<Socio> convocados=Collections.newSetFromMap(new IdentityHashMap<>());
        for(Socio s:alineadosLocal){
            exigir(inscrito(s,local)&&habilitado(s),"Jugador local no habilitado o no inscrito");
            exigir(convocados.add(s),"Jugador repetido en alineaciones");
        }
        for(Socio s:alineadosVisitante){
            exigir(inscrito(s,visitante)&&habilitado(s),"Jugador visitante no habilitado o no inscrito");
            exigir(convocados.add(s),"Jugador repetido en alineaciones");
        }
        PartidoInterno partido=new PartidoInterno(local,visitante);
        partido.setFecha(fecha);partido.setEscenario(escenario);
        partidos.add(partido);
        participantesPartido.put(partido,convocados);
        return partido;
    }
    public void configurarCompetencia(Competencia c,Deporte d,Categoria categoria){
        exigir(c!=null&&d!=null&&categoria!=null,"Competencia invalida");
        minimo(d); // valida que el deporte pertenezca a las tres disciplinas de conjunto
        if(deportesCompetencia.containsKey(c)){
            exigir(deporteIgual(deportesCompetencia.get(c),d)&&categoriaIgual(categoriasCompetencia.get(c),categoria),
                   "No se puede cambiar el deporte ni categoria de una competencia configurada");
        }
        deportesCompetencia.put(c,d);categoriasCompetencia.put(c,categoria);
    }
    public InscripcionCompetencia inscribirCompetencia(Equipo equipo,Competencia competencia,List<Socio> jugadores){
        exigir(equipo!=null&&equipo.isActivo()&&competencia!=null&&jugadores!=null&&equipos.contains(equipo),"Datos invalidos");
        exigir(deporteIgual(equipo.getDeporte(),deportesCompetencia.get(competencia))&&categoriaIgual(equipo.getCategoria(),categoriasCompetencia.get(competencia)),"Equipo no cumple deporte/categoria");
        exigir(jugadores.size()>=minimo(equipo.getDeporte()),"Nomina insuficiente");
        exigir(jugadores.size()<=equipo.getCupoMaximo(),"Nomina excede cupo");
        exigir(new HashSet<>(jugadores).size()==jugadores.size(),"Jugadores repetidos");
        for(Socio s:jugadores)exigir(jugadoresHabilitados(equipo).contains(s),"Jugador no inscrito o suspendido");
        for(InscripcionCompetencia i:inscripcionesCompetencia)exigir(i.getEquipo()!=equipo||i.getCompetencia()!=competencia,"Inscripcion duplicada");
        InscripcionCompetencia ins=new InscripcionCompetencia(equipo,competencia);
        inscripcionesCompetencia.add(ins);nominas.add(new NominaCompetencia(ins,new ArrayList<>(jugadores)));
        puntosCompetencia.computeIfAbsent(competencia,k->new IdentityHashMap<>()).put(equipo,0);
        return ins;
    }
    private NominaCompetencia nomina(Equipo equipo,Competencia c){
        for(NominaCompetencia n:nominas)if(n.getInscripcion().getEquipo()==equipo&&n.getInscripcion().getCompetencia()==c)return n;
        throw new IllegalArgumentException("Equipo no inscrito en competencia");
    }
    public PartidoCompetencia programarCompetencia(Competencia c,Equipo e,InstitucionRival rival,Date fecha,Escenario escenario,List<Socio> alineados){
        exigir(c!=null&&e!=null&&e.isActivo()&&rival!=null&&fecha!=null&&escenario!=null&&alineados!=null,"Datos incompletos");
        NominaCompetencia n=nomina(e,c);
        exigir(new HashSet<>(alineados).size()==alineados.size(),"Alineacion repetida");
        exigir(alineados.size()>=minimo(e.getDeporte()),"Alineacion insuficiente");
        for(Socio s:alineados)exigir(n.getJugadores().contains(s)&&inscrito(s,e)&&habilitadoEnCompetencia(s,c),"Jugador fuera de nomina o suspendido");
        PartidoCompetencia p=new PartidoCompetencia(c,e,rival);
        p.setFecha(fecha);p.setEscenario(escenario);
        partidos.add(p);alineaciones.put(p,new ArrayList<>(alineados));
        participantesPartido.put(p,new HashSet<>(alineados));return p;
    }
    public void registrarResultado(Partido partido,int local,int visitante){
        exigir(partidos.contains(partido),"Partido no registrado");
        exigir(!resultados.containsKey(partido),"Resultado ya registrado");
        exigir(local>=0&&visitante>=0,"Marcador negativo");
        Deporte d=partido instanceof PartidoInterno ? ((PartidoInterno)partido).getEquipoLocal().getDeporte():((PartidoCompetencia)partido).getEquipo().getDeporte();
        if(minimo(d)!=7)exigir(local!=visitante,"Baloncesto y voleibol no admiten empate");
        if(partido instanceof PartidoCompetencia && "ELIMINACION".equals(fases.get((PartidoCompetencia)partido))) exigir(local!=visitante,"Eliminatoria requiere ganador");
        if(minimo(d)==6)exigir((local==3&&visitante>=0&&visitante<=2)||(visitante==3&&local>=0&&local<=2),"Voleibol se registra por sets: 3-0, 3-1 o 3-2");
        partido.setMarcador(local+"-"+visitante);resultados.put(partido,new int[]{local,visitante});
        if(partido instanceof PartidoCompetencia){
            PartidoCompetencia p=(PartidoCompetencia)partido;
            String fase=fases.getOrDefault(p,"LIGA");
            if(!"ELIMINACION".equals(fase)){
                int pts=local>visitante?(minimo(d)==7?3:2):local==visitante?1:0;
                Map<Equipo,Integer> tabla=puntosCompetencia.computeIfAbsent(p.getCompetencia(),k->new IdentityHashMap<>());
                tabla.put(p.getEquipo(),tabla.getOrDefault(p.getEquipo(),0)+pts);
                int[] t=estadisticasTabla.computeIfAbsent(p.getCompetencia(),k->new IdentityHashMap<>()).computeIfAbsent(p.getEquipo(),k->new int[5]);
                t[0]++;if(local>visitante)t[1]++;else if(local==visitante)t[2]++;else t[3]++;t[4]+=local-visitante;
            }
            if("ELIMINACION".equals(fase) && local<visitante)
                eliminados.computeIfAbsent(p.getCompetencia(),k->Collections.newSetFromMap(new IdentityHashMap<>())).add(p.getEquipo());
            // Solo un encuentro oficial cumple una suspension oficial.
            Map<Socio,Integer> pendientes=suspensionesTorneo.get(p.getCompetencia());
            if(pendientes!=null){
                for(InscripcionEquipo ins:p.getEquipo().getInscripciones()){
                    Socio socio=ins.getSocio();int saldo=pendientes.getOrDefault(socio,0);
                    if(saldo>0&&!alineaciones.getOrDefault(p,Collections.emptyList()).contains(socio))pendientes.put(socio,saldo-1);
                }
            }
        }
        // Una suspension se cumple al terminar un partido al que el jugador NO fue alineado.
        // Las sanciones generadas por este partido se registran despues con registrarEstadistica.
        Set<Equipo> equiposDelPartido=Collections.newSetFromMap(new IdentityHashMap<>());
        if(partido instanceof PartidoInterno){equiposDelPartido.add(((PartidoInterno)partido).getEquipoLocal());equiposDelPartido.add(((PartidoInterno)partido).getEquipoVisitante());}
        else equiposDelPartido.add(((PartidoCompetencia)partido).getEquipo());
        for(Equipo eq:equiposDelPartido)for(InscripcionEquipo ins:eq.getInscripciones()){
            Socio jugador=ins.getSocio();int pendientes=suspensiones.getOrDefault(jugador,0);
            if(pendientes>0&&!participantesPartido.getOrDefault(partido,Collections.emptySet()).contains(jugador))suspensiones.put(jugador,pendientes-1);
        }
    }
    private List<Socio> participantesInterno(PartidoInterno p){
        List<Socio> todos=new ArrayList<>(jugadoresHabilitados(p.getEquipoLocal()));
        todos.addAll(jugadoresHabilitados(p.getEquipoVisitante()));return todos;
    }
    public void registrarEstadistica(EstadisticaJugador estadistica){
        exigir(estadistica!=null&&estadistica.getPartido()!=null&&estadistica.getSocio()!=null,"Estadistica incompleta");
        Partido p=estadistica.getPartido();Socio s=estadistica.getSocio();
        exigir(partidos.contains(p),"Partido desconocido");
        exigir(resultados.containsKey(p),"Debe registrarse primero el resultado");
        boolean pertenece;
        if(p instanceof PartidoInterno){PartidoInterno pi=(PartidoInterno)p;
            pertenece=participantesPartido.getOrDefault(p,Collections.emptySet()).contains(s);
        }else pertenece=alineaciones.getOrDefault(p,Collections.emptyList()).contains(s);
        exigir(pertenece,"Jugador no participante");
        Deporte deporte=p instanceof PartidoInterno?((PartidoInterno)p).getEquipoLocal().getDeporte():((PartidoCompetencia)p).getEquipo().getDeporte();
        exigir((minimo(deporte)==7&&estadistica instanceof EstadisticaFutbol)||(minimo(deporte)==5&&estadistica instanceof EstadisticaBaloncesto)||(minimo(deporte)==6&&estadistica instanceof EstadisticaVoleibol),"Estadistica incompatible con deporte");
        for(EstadisticaJugador e:estadisticas)exigir(e.getPartido()!=p||e.getSocio()!=s,"Estadistica duplicada");
        if(estadistica instanceof EstadisticaFutbol){
            EstadisticaFutbol f=(EstadisticaFutbol)estadistica;
            exigir(f.getGoles()>=0&&f.getAsistencias()>=0&&f.getAmarillas()>=0&&f.getRojas()>=0,"Valores negativos");
            int a=amarillasAcumuladas.getOrDefault(s,0)+f.getAmarillas();
            if(p instanceof PartidoCompetencia){
                Competencia torneo=((PartidoCompetencia)p).getCompetencia();
                int umbral=umbralesAmarillas.getOrDefault(torneo,Collections.emptyMap()).getOrDefault("umbral",2);
                int totalTorneo=amarillasTorneo.getOrDefault(torneo,Collections.emptyMap()).getOrDefault(s,0)+f.getAmarillas();
                if((totalTorneo/umbral)>((totalTorneo-f.getAmarillas())/umbral)) {
                    suspensiones.put(s,Math.max(1,suspensiones.getOrDefault(s,0)));
                    suspenderEnTorneo(torneo,s);
                }
            }
            if(a>=2||f.getRojas()>0){
                suspensiones.put(s,Math.max(1,suspensiones.getOrDefault(s,0)));
                if(p instanceof PartidoCompetencia)suspenderEnTorneo(((PartidoCompetencia)p).getCompetencia(),s);
                a=a%2;
            }
            amarillasAcumuladas.put(s,a);
            if(p instanceof PartidoCompetencia){Competencia c=((PartidoCompetencia)p).getCompetencia();Map<Socio,Integer> m=amarillasTorneo.computeIfAbsent(c,k->new IdentityHashMap<>());m.put(s,m.getOrDefault(s,0)+f.getAmarillas());}
        }
        if(estadistica instanceof EstadisticaBaloncesto){EstadisticaBaloncesto b=(EstadisticaBaloncesto)estadistica;exigir(b.getPuntos()>=0&&b.getRebotes()>=0,"Valores negativos");}
        if(estadistica instanceof EstadisticaVoleibol){EstadisticaVoleibol v=(EstadisticaVoleibol)estadistica;exigir(v.getAces()>=0&&v.getBloqueos()>=0,"Valores negativos");}
        estadisticas.add(estadistica);
    }
    private boolean inscrito(Socio s,Equipo e){return e.getInscripciones().stream().anyMatch(i->i.getSocio()==s);}
    /** Formato: LIGA (Cerros), GRUPOS y ELIMINACION (ASCUN). */
    public void configurarFase(PartidoCompetencia partido,String fase,String grupo){
        exigir(partido!=null&&partidos.contains(partido)&&!resultados.containsKey(partido),"Partido invalido o finalizado");
        exigir(fase!=null&&(fase.equalsIgnoreCase("LIGA")||fase.equalsIgnoreCase("GRUPOS")||fase.equalsIgnoreCase("ELIMINACION")),"Fase desconocida");
        String f=fase.toUpperCase(Locale.ROOT);
        if(f.equals("GRUPOS")){
            grupo=nombreValido(grupo);
            Map<String,String> asignados=gruposInstitucion.getOrDefault(partido.getCompetencia(),Collections.emptyMap());
            for(String nombre:List.of(partido.getEquipo().getNombre(),partido.getRival().getNombre())){
                String registrado=asignados.get(nombre.toLowerCase(Locale.ROOT));
                exigir(registrado==null||registrado.equalsIgnoreCase(grupo),"Equipo o rival pertenece a otro grupo");
            }
        }
        if(f.equals("ELIMINACION")){
            exigir(!eliminados.getOrDefault(partido.getCompetencia(),Collections.emptySet()).contains(partido.getEquipo()),"Equipo eliminado");
            Map<String,String> llave=cruces.get(partido.getCompetencia());
            if(llave!=null){
                boolean permitido=false;
                for(Map.Entry<String,String> par:llave.entrySet()){
                    if((igual(partido.getEquipo().getNombre(),par.getKey())&&igual(partido.getRival().getNombre(),par.getValue()))||
                       (igual(partido.getEquipo().getNombre(),par.getValue())&&igual(partido.getRival().getNombre(),par.getKey())))permitido=true;
                }
                exigir(permitido,"Partido ajeno a los cruces asignados");
            }
        }
        fases.put(partido,f);
        if(grupo!=null)grupos.put(partido,grupo);
        if(f.equals("GRUPOS")){
            Map<String,String> asignados=gruposInstitucion.computeIfAbsent(partido.getCompetencia(),k->new HashMap<>());
            asignados.put(partido.getEquipo().getNombre().toLowerCase(Locale.ROOT),grupo);
            asignados.put(partido.getRival().getNombre().toLowerCase(Locale.ROOT),grupo);
        }
    }
    public List<Equipo> tabla(Competencia c){
        List<Equipo> lista=new ArrayList<>(puntosCompetencia.getOrDefault(c,Collections.emptyMap()).keySet());
        lista.sort((a,b)->{
            int puntos=Integer.compare(puntos(c,b),puntos(c,a));if(puntos!=0)return puntos;
            Map<Equipo,int[]> datos=estadisticasTabla.getOrDefault(c,Collections.emptyMap());
            int dif=Integer.compare(datos.getOrDefault(b,new int[5])[4],datos.getOrDefault(a,new int[5])[4]);
            return dif!=0?dif:a.getNombre().compareToIgnoreCase(b.getNombre());
        });return Collections.unmodifiableList(lista);
    }
    public int[] resumenEquipo(Competencia c,Equipo e){return estadisticasTabla.getOrDefault(c,Collections.emptyMap()).getOrDefault(e,new int[5]).clone();}
    public boolean eliminado(Competencia c,Equipo e){return eliminados.getOrDefault(c,Collections.emptySet()).contains(e);}
    public int amarillasEnTorneo(Competencia c,Socio s){return amarillasTorneo.getOrDefault(c,Collections.emptyMap()).getOrDefault(s,0);}
    public int estadisticaAcumulada(Socio s,Competencia c,String campo){
        int total=0;
        for(EstadisticaJugador e:estadisticas){if(e.getSocio()!=s)continue;
            if(c!=null&&(!(e.getPartido() instanceof PartidoCompetencia)||((PartidoCompetencia)e.getPartido()).getCompetencia()!=c))continue;
            switch(campo.toLowerCase(Locale.ROOT)){
                case "goles":if(e instanceof EstadisticaFutbol)total+=((EstadisticaFutbol)e).getGoles();break;
                case "asistencias":if(e instanceof EstadisticaFutbol)total+=((EstadisticaFutbol)e).getAsistencias();break;
                case "puntos":if(e instanceof EstadisticaBaloncesto)total+=((EstadisticaBaloncesto)e).getPuntos();break;
                case "rebotes":if(e instanceof EstadisticaBaloncesto)total+=((EstadisticaBaloncesto)e).getRebotes();break;
                case "aces":if(e instanceof EstadisticaVoleibol)total+=((EstadisticaVoleibol)e).getAces();break;
                case "bloqueos":if(e instanceof EstadisticaVoleibol)total+=((EstadisticaVoleibol)e).getBloqueos();break;
                default:throw new IllegalArgumentException("Campo no soportado: "+campo);
            }
        }return total;
    }
    /** Numero de estadisticas faltantes: cada participante debe tener un registro. */
    public List<Socio> participantesSinEstadistica(Partido p){
        exigir(p!=null&&partidos.contains(p)&&resultados.containsKey(p),"Partido sin resultado registrado");
        List<Socio> pendientes=new ArrayList<>();
        for(Socio jugador:participantesPartido.getOrDefault(p,Collections.emptySet())){
            boolean completo=false;
            for(EstadisticaJugador est:estadisticas){
                if(est.getPartido()==p&&est.getSocio()==jugador){completo=true;break;}
            }
            if(!completo)pendientes.add(jugador);
        }
        return Collections.unmodifiableList(pendientes);
    }
    public void validarEstadisticasCompletas(Partido p){
        exigir(participantesSinEstadistica(p).isEmpty(),"Faltan estadisticas de participantes");
    }
    /** Clasificacion acumulada del torneo (goles, asistencias, puntos, rebotes, aces, bloqueos). */
    public static final class PuntuacionJugador implements Serializable{
        private static final long serialVersionUID=1L;
        public final Socio socio;
        public final int total;
        private PuntuacionJugador(Socio socio,int total){this.socio=socio;this.total=total;}
    }
    public List<PuntuacionJugador> clasificacionJugadores(Competencia c,String campo){
        exigir(c!=null&&deportesCompetencia.containsKey(c),"Competencia desconocida");
        exigir(campo!=null&&Set.of("goles","asistencias","puntos","rebotes","aces","bloqueos")
             .contains(campo.toLowerCase(Locale.ROOT)),"Estadistica no soportada");
        Set<Socio> jugadores=Collections.newSetFromMap(new IdentityHashMap<>());
        for(EstadisticaJugador est:estadisticas){
            if(est.getPartido() instanceof PartidoCompetencia &&
                    ((PartidoCompetencia)est.getPartido()).getCompetencia()==c)
                jugadores.add(est.getSocio());
        }
        List<PuntuacionJugador> salida=new ArrayList<>();
        for(Socio socio:jugadores)salida.add(new PuntuacionJugador(socio,estadisticaAcumulada(socio,c,campo)));
        salida.sort(Comparator.comparingInt((PuntuacionJugador pj)->pj.total).reversed()
            .thenComparing(pj->pj.socio.getLogin()==null?"":pj.socio.getLogin(),String.CASE_INSENSITIVE_ORDER));
        return Collections.unmodifiableList(salida);
    }

    public int puntos(Competencia c,Equipo e){return puntosCompetencia.getOrDefault(c,Collections.emptyMap()).getOrDefault(e,0);}
    public int goles(Socio s){return estadisticas.stream().filter(e->e.getSocio()==s&&e instanceof EstadisticaFutbol).mapToInt(e->((EstadisticaFutbol)e).getGoles()).sum();}
    public List<Equipo> getEquipos(){return Collections.unmodifiableList(equipos);}
    public List<Partido> getPartidos(){return Collections.unmodifiableList(partidos);}
    public List<EstadisticaJugador> getEstadisticas(){return Collections.unmodifiableList(estadisticas);}
    public void guardar(Path ruta) throws IOException{
        exigir(ruta!=null,"Ruta obligatoria");
        Path destino=ruta.toAbsolutePath();Path parent=destino.getParent();
        if(parent!=null)Files.createDirectories(parent);
        // Escritura temporal: ante error, conservar el ultimo archivo valido.
        Path temporal=Files.createTempFile(parent,"seneca-",".tmp");
        try{
            try(ObjectOutputStream o=new ObjectOutputStream(Files.newOutputStream(temporal))){o.writeObject(this);}
            try{Files.move(temporal,destino,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}
            catch(AtomicMoveNotSupportedException ex){Files.move(temporal,destino,StandardCopyOption.REPLACE_EXISTING);}
        }finally{Files.deleteIfExists(temporal);}
    }
    public static ServicioDeportivo cargar(Path ruta) throws IOException,ClassNotFoundException{
        try(ObjectInputStream o=new ObjectInputStream(Files.newInputStream(ruta))){return (ServicioDeportivo)o.readObject();}
    }
}
