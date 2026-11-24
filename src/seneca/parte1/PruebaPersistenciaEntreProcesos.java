package seneca.parte1;

import java.nio.file.*;
import java.util.*;
/** Dos JVM diferentes: escribir <ruta> / leer <ruta>. */
public final class PruebaPersistenciaEntreProcesos {
    public static void main(String[] args) throws Exception {
        if(args.length!=2)throw new IllegalArgumentException("Uso: escribir|leer archivo.dat");
        Path archivo=Path.of(args[1]);
        if(args[0].equals("escribir")) {
            ServicioDeportivo ss=new ServicioDeportivo();
            Equipo a=new Equipo("Club Azul",11,new Deporte("Futbol"),new Categoria("Mayores"),new ArrayList<>());
            ss.registrarEquipo(a);
            List<Socio> alineados=new ArrayList<>();
            for(int i=0;i<8;i++) {
                Socio s=new Socio();s.setEdad(20+i);s.setLogin("jugador"+i);
                ss.inscribir(s,a,"Defensa",i+1);alineados.add(s);
            }
            Competencia c=new Competencia("ASCUN 2026","ASCUN",2026,"GRUPOS+ELIMINACION");
            ss.configurarCompetencia(c,a.getDeporte(),a.getCategoria());ss.inscribirCompetencia(a,c,alineados);
            PartidoCompetencia p=ss.programarCompetencia(c,a,new InstitucionRival("UniAndes Rival"),new Date(1800000000000L),new Escenario("Estadio"),alineados);
            ss.configurarFase(p,"GRUPOS","G1");ss.registrarResultado(p,2,0);
            EstadisticaFutbol f=new EstadisticaFutbol();f.setPartido(p);f.setSocio(alineados.get(0));f.setGoles(2);f.setAmarillas(2);
            ss.registrarEstadistica(f);
            ss.registrarEncuentroExterno(c,"Rival A","Rival B",1,0,"GRUPOS","G1");
            ss.guardar(archivo);
            if(!Files.isRegularFile(archivo)||Files.size(archivo)<100)throw new AssertionError("Archivo no creado");
            System.out.println("ESCRITURA OK: equipos=1, jugadores=8, partidos=1, estadisticas=1, torneos=1, suspensiones=1, encuentrosExternos=1");
        } else if(args[0].equals("leer")) {
            if(!Files.isRegularFile(archivo))throw new AssertionError("Archivo inexistente");
            ServicioDeportivo ss=ServicioDeportivo.cargar(archivo);
            if(ss.getEquipos().size()!=1||ss.getPartidos().size()!=1||ss.getEstadisticas().size()!=1)throw new AssertionError("Cantidades erradas");
            Equipo eq=ss.getEquipos().get(0);
            if(!eq.getNombre().equals("Club Azul")||eq.getInscripciones().size()!=8)throw new AssertionError("Equipo o inscripciones perdidos");
            Socio jugador=eq.getInscripciones().get(0).getSocio();
            if(jugador.getInscripciones().get(0)!=eq.getInscripciones().get(0))throw new AssertionError("Referencias inversas perdidas");
            if(eq.getInscripciones().get(0).getEquipo()!=eq)throw new AssertionError("Equipo no consistente");
            PartidoCompetencia p=(PartidoCompetencia)ss.getPartidos().get(0);
            Competencia c=p.getCompetencia();
            if(p.getEquipo()!=eq||ss.puntos(c,eq)!=3||ss.goles(jugador)!=2)throw new AssertionError("Puntos, goles o referencias errados");
            if(ss.partidosSuspendidoEnTorneo(jugador,c)!=1||ss.amarillasEnTorneo(c,jugador)!=2)throw new AssertionError("Sanciones no persistidas");
            if(ss.getEncuentrosExternos(c).size()!=1||ss.tablaCompleta(c,"GRUPOS","G1").size()!=4)throw new AssertionError("Encuentro/tabla no persistieron");
            // Utilizable despues de reiniciar: registrar nuevo partido respetando suspension.
            List<Socio> sinSancionado=new ArrayList<>();
            for(InscripcionEquipo i:eq.getInscripciones())if(i.getSocio()!=jugador)sinSancionado.add(i.getSocio());
            PartidoCompetencia siguiente=ss.programarCompetencia(c,eq,new InstitucionRival("Rival B"),new Date(1800000100000L),new Escenario("Estadio"),sinSancionado);
            ss.configurarFase(siguiente,"GRUPOS","G1");ss.registrarResultado(siguiente,1,1);
            if(!ss.habilitadoEnCompetencia(jugador,c))throw new AssertionError("Sancion no cumplida tras restaurar");
            System.out.println("LECTURA OK: datos, relaciones, estadisticas, tabla, suspensiones y operacion posterior correctos");
        } else throw new IllegalArgumentException("Modo invalido");
    }
}
