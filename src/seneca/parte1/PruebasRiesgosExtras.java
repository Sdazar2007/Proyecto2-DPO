package seneca.parte1;

import java.nio.file.*;
import java.util.*;

/** Regresiones de incidentes detectados al auditar el codigo original. */
public final class PruebasRiesgosExtras {
    private static int checks;
    private static void ok(boolean v,String detalle){checks++;if(!v)throw new AssertionError(detalle);}
    private static void error(Runnable r,String detalle){checks++;try{r.run();}catch(IllegalArgumentException x){return;}throw new AssertionError("No rechazo: "+detalle);}
    private static Equipo eq(String n){return new Equipo(n,12,new Deporte("Futbol"),new Categoria("Mayores"),new ArrayList<>());}
    private static List<Socio> poblar(ServicioDeportivo servicio,Equipo eq){
        List<Socio> arr=new ArrayList<>();
        for(int i=0;i<9;i++){Socio socio=new Socio();socio.setEdad(20);servicio.inscribir(socio,eq,"P",i+1);arr.add(socio);}return arr;
    }
    private static void gruposLlaves(){
        ServicioDeportivo s=new ServicioDeportivo();Competencia c=new Competencia("ASCUN","ASCUN",2026,"GRUPOS+ELIMINACION");
        s.configurarCompetencia(c,new Deporte("Futbol"),new Categoria("Mayores"));
        s.registrarEncuentroExterno(c,"A","B",2,0,"GRUPOS","G1");
        error(()->s.registrarEncuentroExterno(c,"A","C",1,0,"GRUPOS","G2"),"A en dos grupos");
        error(()->s.registrarEncuentroExterno(c,"C","B",1,0,"GRUPOS","G2"),"B en dos grupos");
        s.registrarEncuentroExterno(c,"C","D",1,0,"GRUPOS","G1");
        s.registrarEncuentroExterno(c,"E","F",2,0,"GRUPOS","G2");
        s.registrarEncuentroExterno(c,"G","H",1,0,"GRUPOS","G2");
        s.clasificarPorGrupos(c,2);
        Map<String,String> llave=s.generarCruces(c);
        error(()->s.registrarEncuentroExterno(c,"Q","R",1,0,"ELIMINACION",null),"encuentro ajeno a cruces");
        ok(s.getEncuentrosExternos(c).size()==4,"encuentros invalidos no agregados");
        var first=llave.entrySet().iterator().next();
        s.registrarEncuentroExterno(c,first.getKey(),first.getValue(),3,0,"ELIMINACION",null);
        error(()->s.registrarEncuentroExterno(c,first.getValue(),first.getKey(),1,0,"ELIMINACION",null),"revivir eliminado");
        error(()->s.avanzarRonda(c),"aun falta el segundo cruce");
        ok(s.generarCruces(c).size()==2,"llave sin modificar por avance incompleto");
    }
    private static void reincidencia(){
        ServicioDeportivo s=new ServicioDeportivo();Equipo e=eq("Club"),otro=eq("Rival Interno");s.registrarEquipo(e);s.registrarEquipo(otro);
        List<Socio> jugadores=poblar(s,e);poblar(s,otro);
        Competencia c=new Competencia("ASCUN","ASCUN",2026,"GRUPOS+ELIMINACION");
        s.configurarCompetencia(c,e.getDeporte(),e.getCategoria());s.inscribirCompetencia(e,c,jugadores);
        Escenario lugar=new Escenario("Cancha");Date f=new Date();InstitucionRival rival=new InstitucionRival("Otro");
        PartidoCompetencia primero=s.programarCompetencia(c,e,rival,f,lugar,jugadores);s.configurarFase(primero,"GRUPOS","A");s.registrarResultado(primero,1,0);
        EstadisticaFutbol am1=new EstadisticaFutbol();am1.setPartido(primero);am1.setSocio(jugadores.get(0));am1.setAmarillas(2);s.registrarEstadistica(am1);
        ok(s.partidosSuspendidoEnTorneo(jugadores.get(0),c)==1,"primera suspension");
        List<Socio> restantes=new ArrayList<>(jugadores);restantes.remove(0);
        PartidoCompetencia segundo=s.programarCompetencia(c,e,rival,f,lugar,restantes);s.configurarFase(segundo,"GRUPOS","A");s.registrarResultado(segundo,0,1);
        ok(s.habilitadoEnCompetencia(jugadores.get(0),c),"primer partido de sancion servido");
        PartidoCompetencia tercero=s.programarCompetencia(c,e,rival,f,lugar,jugadores);s.configurarFase(tercero,"GRUPOS","A");s.registrarResultado(tercero,0,1);
        EstadisticaFutbol am2=new EstadisticaFutbol();am2.setPartido(tercero);am2.setSocio(jugadores.get(0));am2.setAmarillas(2);s.registrarEstadistica(am2);
        ok(s.amarillasEnTorneo(c,jugadores.get(0))==4,"acumulacion cuatro amarillas");
        ok(s.partidosSuspendidoEnTorneo(jugadores.get(0),c)==1,"segunda sancion tras cuatro amarillas");
        // El amistoso sin el jugador libera la suspension general, NO la oficial.
        PartidoInterno interno=s.programarInterno(e,otro,f,lugar);s.registrarResultado(interno,0,0);
        ok(s.habilitado(jugadores.get(0)),"suspension general levantada");
        ok(!s.habilitadoEnCompetencia(jugadores.get(0),c),"continua sancion oficial");
        error(()->s.programarCompetencia(c,e,rival,f,lugar,jugadores),"sancionado no juega oficial");
    }
    private static void seguridadGuardado() throws Exception {
        ServicioDeportivo s=new ServicioDeportivo();s.registrarEquipo(eq("Primero"));
        Path carpeta=Files.createTempDirectory("seneca-guardado-"),destino=carpeta.resolve("datos").resolve("club.dat");
        try{
            s.guardar(destino);ok(Files.size(destino)>100,"creacion archivo");
            s.registrarEquipo(eq("Segundo"));s.guardar(destino);
            ServicioDeportivo cargado=ServicioDeportivo.cargar(destino);
            ok(cargado.getEquipos().size()==2,"reemplazo datos en archivo");
            ok(Files.list(destino.getParent()).count()==1,"sin temporales sobrantes");
        }finally{Files.deleteIfExists(destino);Files.deleteIfExists(destino.getParent());Files.deleteIfExists(carpeta);}
    }
    private static void estadisticasCompletasYRanking(){
        ServicioDeportivo s=new ServicioDeportivo();Equipo e=eq("E"),visitante=eq("V");
        s.registrarEquipo(e);s.registrarEquipo(visitante);
        List<Socio> a=poblar(s,e),b=poblar(s,visitante);
        Date f=new Date();Escenario es=new Escenario("Estadio");
        PartidoInterno interno=s.programarInterno(e,visitante,f,es,a.subList(0,7),b.subList(0,7));
        error(()->s.validarEstadisticasCompletas(interno),"partido sin resultados");
        s.registrarResultado(interno,1,0);
        ok(s.participantesSinEstadistica(interno).size()==14,"catorce participantes sin registro");
        error(()->s.validarEstadisticasCompletas(interno),"estadisticas incompletas");
        for(Socio jugador:a.subList(0,7)){
            EstadisticaFutbol est=new EstadisticaFutbol();est.setSocio(jugador);est.setPartido(interno);s.registrarEstadistica(est);
        }
        ok(s.participantesSinEstadistica(interno).size()==7,"faltan siete visitantes");
        for(Socio jugador:b.subList(0,7)){
            EstadisticaFutbol est=new EstadisticaFutbol();est.setSocio(jugador);est.setPartido(interno);s.registrarEstadistica(est);
        }
        s.validarEstadisticasCompletas(interno);
        ok(s.participantesSinEstadistica(interno).isEmpty(),"estadisticas completas");
        Competencia c=new Competencia("Torneo","ASCUN",2026,"Liga");
        s.configurarCompetencia(c,e.getDeporte(),e.getCategoria());s.inscribirCompetencia(e,c,a);
        PartidoCompetencia p=s.programarCompetencia(c,e,new InstitucionRival("X"),f,es,a);
        s.registrarResultado(p,2,0);
        for(int i=0;i<a.size();i++){
            EstadisticaFutbol estad=new EstadisticaFutbol();estad.setSocio(a.get(i));estad.setPartido(p);estad.setGoles(i==2?2:i==5?1:0);s.registrarEstadistica(estad);
        }
        s.validarEstadisticasCompletas(p);
        var lideres=s.clasificacionJugadores(c,"goles");
        ok(lideres.size()==9,"nueve jugadores rankeados");
        ok(lideres.get(0).socio==a.get(2)&&lideres.get(0).total==2,"lider dos goles");
        ok(lideres.get(1).socio==a.get(5)&&lideres.get(1).total==1,"segundo un gol");
        error(()->s.clasificacionJugadores(c,"tarjetas"),"ranking no soportado");
    }
    public static void main(String[] args)throws Exception{
        gruposLlaves();reincidencia();seguridadGuardado();estadisticasCompletasYRanking();
        System.out.println("OK: "+checks+" verificaciones adicionales de regresion superadas.");
    }
}
