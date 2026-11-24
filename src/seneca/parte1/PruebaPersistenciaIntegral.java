package seneca.parte1;

import java.nio.file.*;
import java.time.*;
import java.util.*;

/** Debe invocarse primero con 'escribir ruta' y despues con 'leer ruta' desde otra JVM. */
public final class PruebaPersistenciaIntegral {
    private static int checks=0;
    private static void check(boolean ok,String detalle){checks++;if(!ok)throw new AssertionError(detalle);}
    private static Socio socio(String login){Socio s=new Socio();s.setLogin(login);s.setPassword("clave_"+login);s.setEdad(20);return s;}
    private static Entrenador entrenador(String login){Entrenador e=new Entrenador();e.setLogin(login);e.setPassword("clave_"+login);e.setCodigoDescuento("CODE_"+login);return e;}
    private static void escribir(Path ruta)throws Exception{
        ClubSeneca club=new ClubSeneca();Administrador admin=club.crearAdministradorInicial("root","test-root");
        Socio ana=socio("ana"),be=socio("be"),ca=socio("ca"),di=socio("di");
        for(Socio s:List.of(ana,be,ca,di))club.registrarUsuario(admin,s);
        Entrenador entrenador=entrenador("coach");club.registrarUsuario(admin,entrenador);
        Deporte futbol=new Deporte("Futbol");Equipo equipo=new Equipo();equipo.setNombre("Universitarios");equipo.setDeporte(futbol);
        equipo.setCategoria(new Categoria("Mayores"));equipo.setCupoMaximo(25);
        club.crearEquipo(admin,equipo);club.inscribirEquipo(ana,equipo,"Delantera",9);
        List<Socio> nomina=new ArrayList<>(List.of(ana));
        for(int j=0;j<6;j++){
            Socio socioNuevo=socio("convocado"+j);
            club.registrarUsuario(admin,socioNuevo);
            club.inscribirEquipo(socioNuevo,equipo,"Defensa",j+10);
            nomina.add(socioNuevo);
        }
        Competencia ascun=new Competencia("ASCUN","ASCUN",2030,"GRUPOS_ELIMINACION");
        club.getDeportivo().configurarCompetencia(ascun,futbol,new Categoria("Mayores"));
        club.getDeportivo().inscribirCompetencia(equipo,ascun,nomina);
        PartidoCompetencia oficial=club.getDeportivo().programarCompetencia(ascun,equipo,new InstitucionRival("Uni Rival"),
              new Date(),new Escenario("Estadio central"),nomina);
        club.getDeportivo().configurarFase(oficial,"GRUPOS","A");
        club.getDeportivo().registrarResultado(oficial,2,0);
        EstadisticaFutbol marcador=new EstadisticaFutbol(1,1,0,0);
        marcador.setSocio(ana);marcador.setPartido(oficial);
        club.getDeportivo().registrarEstadistica(marcador);
        Instalacion cancha=new Instalacion("Tenis central","Cancha",4,new Deporte("Tenis"),false,true);
        club.registrarInstalacion(admin,cancha);
        for(Socio s:List.of(ana,be,ca,di))club.inscribirAcademia(s,new Deporte("Tenis"),"Intermedio");
        Reserva reserva=club.reservar(ana,cancha,"2030-11-04T09:00","sencillos",List.of(ana,be));
        PartidoPractica partido=club.getAcademias().programarPractica(reserva);
        club.getAcademias().finalizarPartido(partido,List.of(ana),Map.of(ana,new int[]{2,10,1,2},be,new int[]{0,3,0,5}));
        SesionEntrenamiento sesion=club.getAcademias().programarSesion(entrenador,cancha,"2030-11-05T09:00",60,List.of(ana,ca));
        club.getAcademias().evaluar(sesion,ana,"ASISTIO",4.7,"Mejora notable");
        ArticuloTienda articulo=new ArticuloTienda();articulo.setNombre("Balon oficial");articulo.setPrecio(30000);articulo.setCantidad(10);articulo.setCategoria("Balones");
        club.registrarProducto(admin,articulo);
        club.comprar(ana,"TIENDA",Map.of(articulo,2),null,0,0,0);
        club.reabastecer(admin,articulo,5,"TIENDA");
        club.pagarMensualidad(ana,20000);
        Snack sn=new Snack();sn.setNombre("Barra protein");sn.setPrecio(4000);sn.setAlergenos("mani");
        club.registrarProducto(admin,sn);
        club.comprarConfirmado(ana,"CAFETERIA",Map.of(sn,1),null,0,0.10,0,LocalDateTime.of(2030,11,6,11,0),true);
        club.getAdministrativo().crearTurno(admin,entrenador,"LUNES","08:00","18:00");
        Sugerencia sugerencia=club.sugerir(entrenador,"Toalla","ARTICULO","Uso entrenamiento");
        ArticuloTienda toalla=new ArticuloTienda();toalla.setNombre("Toalla");toalla.setPrecio(12000);toalla.setCantidad(6);toalla.setCategoria("Accesorios");
        club.decidirSugerencia(admin,sugerencia,true,toalla);
        club.guardar(ruta);
        check(Files.size(ruta)>0,"Archivo binario vacio");
        System.out.println("PERSISTENCIA: ESCRITURA OK (datos de las 3 partes y cuentas)");
    }
    private static void leer(Path ruta)throws Exception{
        ClubSeneca club=ClubSeneca.cargar(ruta);
        Administrador admin=(Administrador)club.autenticar("root","test-root");
        Socio ana=(Socio)club.autenticar("ana","clave_ana");
        Socio be=(Socio)club.autenticar("be","clave_be");
        Entrenador coach=(Entrenador)club.autenticar("coach","clave_coach");
        check(admin!=null,"Administrador no recuperado");
        check(ana!=null&&be!=null&&coach!=null,"Usuarios de 3 roles perdidos");
        check(club.autenticar("ana","incorrecta")==null,"Contrasena rota");
        check(club.getUsuarios().size()==12,"Numero de usuarios recuperados");
        check(club.getDeportivo().getEquipos().size()==1,"Parte 1 equipos");
        Equipo e=club.getDeportivo().getEquipos().get(0);
        check(e.getInscripciones().size()==7,"Parte 1 inscripciones");
        check(e.getInscripciones().get(0).getSocio()==ana,"Identidad socio P1");
        check(e.getInscripciones().get(0).getEquipo()==e,"Referencia equipo P1");
        check(club.getDeportivo().getPartidos().size()==1,"Partido oficial persistido");
        PartidoCompetencia pc=(PartidoCompetencia)club.getDeportivo().getPartidos().get(0);
        check(pc.getMarcador().equals("2-0"),"Marcador oficial persistido");
        check(pc.getEquipo()==e,"El torneo conserva el equipo original");
        check(club.getDeportivo().getEstadisticas().size()==1,"Estadistica futbol persistida");
        check(club.getDeportivo().getEstadisticas().get(0).getSocio()==ana,"Estadistica oficial conserva la identidad del socio");
        check(club.getDeportivo().getCompetencias().size()==1,"Competencia configurada persistida");
        Competencia ascun=club.getDeportivo().getCompetencias().get(0);
        check(club.getDeportivo().puntos(ascun,e)==3,"Puntuacion grupal preservada");
        check(club.getAcademias().getInscripciones().size()==4,"Parte 2 inscripciones individuales");
        check(club.getAcademias().buscar(ana,new Deporte("Tenis")).getRankingELO()>1000,"Persistencia ELO ganador");
        check(club.getAcademias().buscar(be,new Deporte("Tenis")).getRankingELO()<1000,"Persistencia ELO perdedor");
        check(club.getAcademias().evolucionRanking(ana,new Deporte("Tenis")).size()==2,"Historial ranking persistido");
        check(club.getAcademias().getReservas().size()==1,"Reservas recuperadas");
        Reserva r=club.getAcademias().getReservas().get(0);
        check(r.getResponsable()==ana,"Identidad socio reserva");
        check(club.getAcademias().getPartidos().size()==1&&club.getAcademias().getPartidos().get(0).isFinalizado(),"Partido practica persistido");
        check(club.getAcademias().getEstadisticas().size()==2,"Estadisticas raqueta persistidas");
        check(club.getAcademias().getSesiones().size()==1,"Sesiones persistidas");
        check(club.getAcademias().getSesiones().get(0).getEntrenador()==coach,"Referencia entrenador sesion");
        check(club.getAcademias().getSesiones().get(0).getEvaluaciones().size()==1,"Asistencia/evaluacion persistida");
        check(club.getAcademias().getSesiones().get(0).getEvaluaciones().get(0).getSocio()==ana,"Referencia socio asistencia");
        check(club.getComercial().getCatalogo().size()==3,"Catalogo persistido");
        check(club.getComercial().getVentas().size()==3,"Ventas tienda/cafeteria/mensualidad persistidas");
        check(club.getComercial().getVentas().get(0).getComprador()==ana,"Identidad compartida socio venta");
        check(ana.getPuntos()>0,"Puntos fidelidad persistidos");
        ArticuloTienda balon=(ArticuloTienda)club.getComercial().getCatalogo().stream().filter(x->x.getNombre().equals("Balon oficial")).findFirst().orElseThrow();
        check(balon.getCantidad()==13,"Stock persistido de articulo");
        check(club.getComercial().stock(balon,"TIENDA")==13,"Mapa de existencias mantiene identidad");
        check(club.getComercial().getReabastecimientos().size()==1,"Reabastecimiento persistido");
        check(club.getAdministrativo().getTurnos().size()==1,"Turnos persistidos");
        check(club.getAdministrativo().getSugerencias().size()==1,"Sugerencias persistidas");
        check(club.getAdministrativo().getSugerencias().get(0).getProponente()==coach,"Proponente conserva identidad");
        check(club.getAdministrativo().getSugerencias().get(0).getEstado().equals("APROBADA"),"Aprobacion persistida");
        check(club.reporteDeportivo(admin).get("partidosRaqueta")==1,"Reporte integrado recuperado");
        Date desde=new Date(System.currentTimeMillis()-86400000L),hasta=new Date(System.currentTimeMillis()+86400000L);
        check(club.ingresosPorRubro(admin,desde,hasta).size()==3,"Reportes ventas tras cargar");
        // Un archivo cargado debe seguir siendo editable y guardable.
        club.pagarMensualidad(ana,3000);
        club.guardar(ruta);
        ClubSeneca recarga=ClubSeneca.cargar(ruta);
        check(recarga.getComercial().getVentas().size()==4,"Nueva venta sobrevivo a una segunda escritura");
        check(((Socio)recarga.autenticar("ana","clave_ana")).getPuntos()>ana.getPuntos()-1,"Puntos sobreviven segunda escritura");
        System.out.println("PERSISTENCIA: LECTURA/RE-ESCRITURA OK ("+checks+" verificaciones)");
    }
    public static void main(String[]args)throws Exception{
        if(args.length!=2||!(args[0].equals("escribir")||args[0].equals("leer")))throw new IllegalArgumentException("Uso: escribir|leer /ruta/fuera/src/club.dat");
        Path ruta=Path.of(args[1]);if(args[0].equals("escribir"))escribir(ruta);else leer(ruta);
    }
}
