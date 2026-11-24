package seneca.parte1;
import java.nio.file.*;
import java.util.*;

public class PruebasTorneos {
    static int total=0;
    static void check(boolean b,String msg){total++;if(!b)throw new AssertionError(msg);}
    static void fail(Runnable r,String msg){total++;try{r.run();throw new AssertionError("Debio fallar: "+msg);}catch(IllegalArgumentException expected){}}
    static ServicioDeportivo.FilaTabla buscar(List<ServicioDeportivo.FilaTabla> tabla,String n){return tabla.stream().filter(f->f.equipo.equals(n)).findFirst().orElseThrow();}
    public static void main(String[] args) throws Exception {
        ServicioDeportivo s=new ServicioDeportivo();
        Deporte fut=new Deporte("Futbol");Categoria mayores=new Categoria("Mayores");
        Competencia ascun=new Competencia("ASCUN","ASCUN",2026,"GRUPOS_ELIMINACION");
        s.configurarCompetencia(ascun,fut,mayores);
        fail(()->s.registrarEncuentroExterno(ascun,"A","A",1,0,"GRUPOS","1"),"mismo equipo");
        fail(()->s.registrarEncuentroExterno(ascun,"A","B",-1,0,"GRUPOS","1"),"negativo");
        fail(()->s.registrarEncuentroExterno(ascun,"A","B",1,0,"DESCONOCIDA","1"),"fase");
        fail(()->s.registrarEncuentroExterno(ascun,"A","B",1,0,"GRUPOS",null),"grupo obligatorio");
        s.registrarEncuentroExterno(ascun,"A","B",2,0,"GRUPOS","G1");
        s.registrarEncuentroExterno(ascun,"C","D",1,0,"GRUPOS","G1");
        s.registrarEncuentroExterno(ascun,"A","C",1,0,"GRUPOS","G1");
        s.registrarEncuentroExterno(ascun,"B","D",2,0,"GRUPOS","G1");
        s.registrarEncuentroExterno(ascun,"E","F",2,0,"GRUPOS","G2");
        s.registrarEncuentroExterno(ascun,"G","H",1,0,"GRUPOS","G2");
        s.registrarEncuentroExterno(ascun,"E","G",1,0,"GRUPOS","G2");
        s.registrarEncuentroExterno(ascun,"F","H",2,0,"GRUPOS","G2");
        check(buscar(s.tablaCompleta(ascun,"GRUPOS","G1"),"A").puntos==6,"A 6 puntos");
        check(buscar(s.tablaCompleta(ascun,"GRUPOS","G1"),"A").diferencia()==3,"diferencia");
        check(buscar(s.tablaCompleta(ascun,"GRUPOS","G2"),"E").puntos==6,"E 6 puntos");
        check(s.tablaConDesempateDirecto(ascun,"GRUPOS","G1").size()==4,"tabla grupo");
        check(s.tablaCompleta(ascun,null,null).size()==8,"tabla completa");
        List<String> clasificados=s.clasificarPorGrupos(ascun,2);
        check(clasificados.size()==4,"4 clasificados");
        check(clasificados.get(0).equals("A"),"A clasificado primero");
        check(clasificados.contains("E"),"E clasificado");
        Map<String,String> cruces=s.generarCruces(ascun);
        check(cruces.size()==2,"dos cruces");
        fail(()->s.avanzarRonda(ascun),"no avanzar sin resultados");
        for(var e:cruces.entrySet())s.registrarEncuentroExterno(ascun,e.getKey(),e.getValue(),2,0,"ELIMINACION",null);
        List<String> semifinal=s.avanzarRonda(ascun);
        check(semifinal.size()==2,"dos ganadores");
        check(semifinal.containsAll(cruces.keySet()),"ganadores correctos");
        String eliminado=cruces.values().iterator().next();
        fail(()->s.registrarEncuentroExterno(ascun,eliminado,"Z",1,0,"ELIMINACION",null),"eliminado no puede jugar");
        Map<String,String> finalCruce=s.generarCruces(ascun);
        check(finalCruce.size()==1,"final creada");
        var finalista=finalCruce.entrySet().iterator().next();
        s.registrarEncuentroExterno(ascun,finalista.getKey(),finalista.getValue(),1,0,"ELIMINACION",null);
        check(s.avanzarRonda(ascun).equals(List.of(finalista.getKey())),"campeon");
        Path archivo=Files.createTempFile("seneca-torneos-",".dat");
        try{
            s.guardar(archivo);
            ServicioDeportivo restaurado=ServicioDeportivo.cargar(archivo);
            check(restaurado.getCompetencias().size()==1,"competencia restaurada");
            Competencia recuperada=restaurado.getCompetencias().get(0);
            check(restaurado.getEncuentrosExternos(recuperada).size()==11,"resultados restaurados");
            check(restaurado.tablaCompleta(recuperada,"GRUPOS","G1").size()==4,"tabla restaurada");
        }finally{Files.deleteIfExists(archivo);}
        System.out.println("OK: "+total+" verificaciones de torneos superadas.");
    }
}
