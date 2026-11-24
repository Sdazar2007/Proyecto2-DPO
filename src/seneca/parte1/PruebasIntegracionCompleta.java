package seneca.parte1;

import java.nio.file.*;
import java.time.*;
import java.util.*;

/** Pruebas de contrato de las partes 2 y 3 y cruces con la parte 1. Sin dependencias externas. */
public final class PruebasIntegracionCompleta {
    private static int verificaciones;
    private static void ok(boolean cond,String m){verificaciones++;if(!cond)throw new AssertionError("FALLO #"+verificaciones+": "+m);}
    private static void no(Runnable r,String m){
        verificaciones++;try{r.run();}catch(IllegalArgumentException esperado){return;}
        throw new AssertionError("SE ACEPTO UNA OPERACION ILEGAL #"+verificaciones+": "+m);
    }
    private static void igual(double actual,double esperado,String m){ok(Math.abs(actual-esperado)<0.011,m+" obtenido="+actual+" esperado="+esperado);}
    private static Socio socio(String nombre,int edad){Socio s=new Socio();s.setLogin(nombre);s.setPassword("clave_"+nombre);s.setEdad(edad);return s;}
    private static Entrenador entrenador(String nombre){Entrenador x=new Entrenador();x.setLogin(nombre);x.setPassword("clave_"+nombre);x.setCodigoDescuento("COD_"+nombre);return x;}
    private static Fisioterapeuta fisio(String nombre){Fisioterapeuta x=new Fisioterapeuta();x.setLogin(nombre);x.setPassword("clave_"+nombre);x.setCodigoDescuento("COD_"+nombre);return x;}
    private static Deporte d(String nombre){return new Deporte(nombre);}
    private static Instalacion cancha(String nombre,String deporte,int capacidad,boolean techada){
        return new Instalacion(nombre,"Cancha",capacidad,d(deporte),techada,true);
    }
    private static class Entorno {
        final ClubSeneca club=new ClubSeneca();
        final Administrador admin=club.crearAdministradorInicial("admin","claveSegura");
        Entorno(){}
        Socio altaSocio(String n,int edad){Socio x=socio(n,edad);club.registrarUsuario(admin,x);return x;}
        Entrenador altaEntrenador(String n){Entrenador e=entrenador(n);club.registrarUsuario(admin,e);return e;}
        Fisioterapeuta altaFisio(String n){Fisioterapeuta e=fisio(n);club.registrarUsuario(admin,e);return e;}
        Instalacion altaCancha(String n,String deporte,int capacidad,boolean techada){
            Instalacion c=cancha(n,deporte,capacidad,techada);club.registrarInstalacion(admin,c);return c;
        }
    }
    private static void cuentas(){
        Entorno e=new Entorno();Socio s=e.altaSocio("Sofia",19);
        ok(e.club.autenticar("SOFIA","clave_Sofia")==s,"Autenticacion insensible a mayusculas");
        ok(e.club.autenticar("Sofia","mala")==null,"Clave incorrecta");
        ok(e.club.autenticar("noexiste","clave")==null,"Usuario no existe");
        ok(!s.getPassword().equals("clave_Sofia"),"Password no se almacena en claro");
        no(()->e.club.registrarUsuario(e.admin,socio("sOfIa",18)),"Login duplicado");
        no(()->e.club.crearAdministradorInicial("otra","clave"),"Solo admin inicial");
        Entrenador profe=e.altaEntrenador("tomas");
        Entrenador duplicado=entrenador("otro");duplicado.setCodigoDescuento("COD_tomas");
        no(()->e.club.registrarUsuario(e.admin,duplicado),"Codigo duplicado");
        no(()->e.club.registrarUsuario(e.admin,new Entrenador()),"Empleado sin datos");
        no(()->e.club.registrarUsuario(new Administrador(),socio("intruso",22)),"Admin ajeno");

        e.club.darBajaUsuario(e.admin,s);
        ok(e.club.autenticar("sofia","clave_Sofia")==null,"Baja deshabilita login");
        no(()->e.club.validarSocio(s),"Socio dado de baja");
        no(()->e.club.darBajaUsuario(e.admin,e.admin),"Admin no puede darse de baja");
        ok(e.club.getHistorial().size()>=4,"Audit log usuarios");
    }
    private static void academias(){
        Entorno e=new Entorno();
        Socio a=e.altaSocio("ana",21),b=e.altaSocio("beto",22),c=e.altaSocio("carla",25),f=e.altaSocio("fede",22);
        Instalacion tenis=e.altaCancha("Tenis 1","Tenis",4,false);
        Instalacion tenis2=e.altaCancha("Tenis 2","Tenis",4,false);
        Instalacion padel=e.altaCancha("Padel 1","Padel",4,false);
        Instalacion mesa=e.altaCancha("Mesa techada","Tenis de mesa",4,true);
        Instalacion mesaExterior=e.altaCancha("Mesa abierta","Tenis de mesa",4,false);
        no(()->e.club.registrarInstalacion(e.admin,cancha("Tenis 1","Tenis",4,false)),"Nombre repetido");
        no(()->e.club.getAcademias().inscribir(a,d("futbol"),"intermedio"),"Solo disciplinas de raqueta");
        for(Socio s:List.of(a,b,c,f)){
            e.club.inscribirAcademia(s,d("tenis"),s==a?"Principiante":s==b?"Intermedio":"Avanzado");
            e.club.inscribirAcademia(s,d("padel"),"Intermedio");
            e.club.inscribirAcademia(s,d("tenis de mesa"),"Intermedio");
        }
        ok(e.club.getAcademias().getInscripciones().size()==12,"Academias simultaneas por socio");
        no(()->e.club.inscribirAcademia(a,d("TENIS"),"Avanzado"),"Duplicado por disciplina");
        no(()->e.club.inscribirAcademia(a,d("Tenis de mesa"),"experto"),"Nivel invalido");
        igual(e.club.getAcademias().ranking(a,d("Tenis")),1000,"ELO inicial");
        String bloque="2030-05-14T10:00";
        no(()->e.club.reservar(a,padel,bloque,"Sencillos",List.of(a,b)),"Padel solo dobles");
        no(()->e.club.reservar(a,tenis,bloque,"Sencillos",List.of(a)),"Jugadores insuficientes");
        no(()->e.club.reservar(a,tenis,bloque,"Dobles",List.of(a,b)),"Dobles exige 4");
        no(()->e.club.reservar(a,tenis,bloque,"Sencillos",List.of(a,a)),"Jugador duplicado");
        no(()->e.club.reservar(a,tenis,bloque,"sencillos",List.of(b,c)),"Responsable participa");
        Reserva r=e.club.reservar(a,tenis,bloque,"Sencillos",List.of(a,b));
        ok(e.club.getAcademias().getReservas().size()==1,"Reserva valida");
        no(()->e.club.reservar(c,tenis,bloque,"Sencillos",List.of(c,f)),"Cancha ocupada");
        no(()->e.club.reservar(a,tenis2,bloque,"Sencillos",List.of(a,c)),"Socio doble reservado");
        ok(e.club.reservar(c,tenis2,bloque,"Sencillos",List.of(c,f))!=null,"Segunda cancha en paralelo");
        no(()->e.club.reservar(c,tenis2,bloque,"Sencillos",List.of(c,f)),"Instalaciones sin cupos");
        PartidoPractica p=e.club.getAcademias().programarPractica(r);
        ok(!p.isFinalizado(),"Partido programado");
        no(()->e.club.getAcademias().programarPractica(r),"Partido duplicado");
        no(()->e.club.getAcademias().finalizarPartido(p,List.of(a),Map.of(a,new int[]{2,12,3,1})),"Estadisticas faltantes");
        no(()->e.club.getAcademias().finalizarPartido(p,List.of(a),Map.of(a,new int[]{0,2,2,2},b,new int[]{2,12,2,2})),"Ganador con menos sets");
        e.club.getAcademias().finalizarPartido(p,List.of(a),Map.of(a,new int[]{2,12,3,1},b,new int[]{0,5,0,3}));
        ok(p.isFinalizado()&&p.getGanadores().contains(a),"Ganador del partido");
        ok(e.club.getAcademias().ranking(a,d("Tenis"))>1000,"Elo ganador sube");
        ok(e.club.getAcademias().ranking(b,d("Tenis"))<1000,"Elo perdedor baja");
        ok(e.club.getAcademias().evolucionRanking(a,d("Tenis")).size()==2,"Historial ELO");
        ok(e.club.getAcademias().getEstadisticas().size()==2,"Registro estadisticas tenis");
        no(()->e.club.getAcademias().finalizarPartido(p,List.of(a),Map.of()),"Resultado duplicado");
        no(()->e.club.getAcademias().cancelarReserva(r,a),"No cancelar reserva con partido");
        ok(e.club.getAcademias().programarPractica(e.club.reservar(b,tenis,"2030-05-14T15:00","sencillos",List.of(b,c)))!=null,"Intermedio vs avanzado permitido");
    }
    private static void academiasNivelesYDeportes(){
        Entorno e=new Entorno();
        Socio p=e.altaSocio("principiante",17),v=e.altaSocio("avanzado",19),i=e.altaSocio("intermedio",20),d=e.altaSocio("dobles",20);
        for(Socio s:List.of(p,v,i,d))e.club.inscribirAcademia(s,d("tenis"),s==p?"Principiante":s==v?"Avanzado":"Intermedio");
        Instalacion cancha=e.altaCancha("T1","tenis",4,false);
        Reserva r=e.club.reservar(p,cancha,"2030-06-10T09:00","sencillos",List.of(p,v));
        no(()->e.club.getAcademias().programarPractica(r),"Principiante vs avanzado no se permite");
        e.club.declararSocioEntrenador(e.admin,p,true);
        PartidoPractica partido=e.club.getAcademias().programarPractica(r);
        ok(partido!=null,"Excepcion para socio entrenador");
        Reserva dobles=e.club.reservar(i,cancha,"2030-06-10T12:00","dobles",List.of(p,v,i,d));
        PartidoPractica pd=e.club.getAcademias().programarPractica(dobles);
        Map<Socio,int[]> stats=Map.of(p,new int[]{2,14,2,1},v,new int[]{0,8,0,3},i,new int[]{2,14,1,2},d,new int[]{0,7,0,5});
        e.club.getAcademias().finalizarPartido(pd,List.of(p,i),stats);
        ok(pd.isFinalizado(),"Dobles finalizados");
        Instalacion padel=e.altaCancha("P1","padel",4,false);
        for(Socio s:List.of(p,v,i,d))e.club.inscribirAcademia(s,d("padel"),"Intermedio");
        Reserva rp=e.club.reservar(p,padel,"2030-06-11T08:00","dobles",List.of(p,v,i,d));
        PartidoPractica pp=e.club.getAcademias().programarPractica(rp);
        e.club.getAcademias().finalizarPartido(pp,List.of(p,i),Map.of(p,new int[]{2,11,3,2},v,new int[]{0,8,0,1},i,new int[]{2,12,4,2},d,new int[]{0,6,0,3}));
        ok(e.club.getAcademias().getEstadisticas().stream().filter(x->x instanceof EstadisticaTenisPadel).count()==8,"Stats tenis y padel");
        Instalacion mesa=e.altaCancha("M1","Tenis de mesa",2,true);
        for(Socio s:List.of(p,v))e.club.inscribirAcademia(s,d("Tenis de mesa"),"Intermedio");
        Reserva rm=e.club.reservar(p,mesa,"2030-06-12T08:00","sencillos",List.of(p,v));
        PartidoPractica pm=e.club.getAcademias().programarPractica(rm);
        no(()->e.club.getAcademias().finalizarPartido(pm,List.of(p),Map.of(p,new int[]{-1,21},v,new int[]{0,10})),"Stats negativas");
        e.club.getAcademias().finalizarPartido(pm,List.of(p),Map.of(p,new int[]{3,33},v,new int[]{1,24}));
        ok(e.club.getAcademias().getEstadisticas().stream().filter(x->x instanceof EstadisticaTenisMesa).count()==2,"Stats mesa");
        ok(e.club.getAcademias().ranking(p,d("Tenis de mesa"))>1000,"Elo tenis mesa independiente");
        ok(e.club.getAcademias().ranking(p,d("Padel"))>1000,"Elo padel independiente");
    }
    private static void entrenamientos(){
        Entorno e=new Entorno();Socio a=e.altaSocio("leon",19),b=e.altaSocio("lina",21);
        Entrenador t=e.altaEntrenador("mister");
        Instalacion c=e.altaCancha("C1","Tenis",4,false);
        e.club.inscribirAcademia(a,d("tenis"),"Principiante");e.club.inscribirAcademia(b,d("tenis"),"Principiante");
        SesionEntrenamiento s=e.club.getAcademias().programarSesion(t,c,"2030-07-05T08:00",90,List.of(a,b));
        ok(s.getDuracion()==90&&s.getConvocados().size()==2,"Sesion registrada");
        no(()->e.club.reservar(a,c,"2030-07-05T08:00","sencillos",List.of(a,b)),"Reserva solapa entrenamiento");
        no(()->e.club.getAcademias().programarSesion(t,c,"2030-07-05T09:00",60,List.of(a)),"Choque instalacion/entrenador");
        no(()->e.club.getAcademias().evaluar(s,a,"Ausente",3.0,""),"Asistencia no permitida");
        no(()->e.club.getAcademias().evaluar(s,a,"ASISTIO",5.5,""),"Nota >5");
        no(()->e.club.getAcademias().evaluar(s,a,"ASISTIO",0.9,""),"Nota <1");
        Evaluacion x=e.club.getAcademias().evaluar(s,a,"ASISTIO",4.5,"Mejoro saque");
        Evaluacion y=e.club.getAcademias().evaluar(s,b,"TARDE",null,"Sin nota");
        ok(x.getNotaDesempeno()==4.5 && y.getNotaDesempeno()==0,"Evaluaciones completas y opcionales");
        no(()->e.club.getAcademias().evaluar(s,a,"FALTO",null,""),"No doble asistencia");
        ok(e.club.getAcademias().asistencia(a).size()==1,"Reporte asistencia socio");
        Equipo futbol=new Equipo();futbol.setNombre("Juveniles");futbol.setCupoMaximo(20);futbol.setDeporte(d("Futbol"));futbol.setCategoria(new Categoria("Mayores"));
        e.club.crearEquipo(e.admin,futbol);
        e.club.inscribirEquipo(a,futbol,"Delantero",8);
        Instalacion campo=e.altaCancha("Campo 1","Futbol",25,false);
        SesionEntrenamiento st=e.club.programarEntrenamientoEquipo(t,futbol,campo,"2030-07-06T08:00",75,List.of(a));
        ok(st.getEntrenador()==t,"Entrenamiento de equipo colectivo");
        no(()->e.club.programarEntrenamientoEquipo(t,futbol,campo,"2030-07-06T09:30",60,List.of(b)),"Convocado no pertenece al equipo");
        e.club.darBajaEquipo(e.admin,futbol);
        no(()->e.club.inscribirEquipo(b,futbol,"Defensa",10),"Equipo dado de baja");
    }
    private static void comercial(){
        Entorno e=new Entorno();Socio s=e.altaSocio("compradora",19);
        Entrenador emp=e.altaEntrenador("vendedor");
        ArticuloTienda balon=new ArticuloTienda();balon.setNombre("Balon");balon.setPrecio(100000);balon.setCantidad(6);balon.setCategoria("Balones");
        e.club.registrarProducto(e.admin,balon);
        ok(e.club.getComercial().stock(balon,"TIENDA")==6,"Stock inicial");
        no(()->e.club.registrarProducto(e.admin,balon),"Producto duplicado");
        no(()->e.club.comprar(s,"TIENDA",Map.of(balon,7),null,0,0,0),"Sin stock");
        ok(e.club.getComercial().stock(balon,"TIENDA")==6 && e.club.getComercial().getVentas().isEmpty(),"Fallo no muta inventario ni ventas");
        Venta v=e.club.comprar(s,"TIENDA",Map.of(balon,2),null,0,0,0);
        igual(v.getSubtotal(),200000,"Subtotal tienda");igual(v.getImpuesto(),38000,"IVA 19%");igual(v.getTotal(),238000,"Total tienda");
        ok(s.getPuntos()==4760,"2% fidelidad");
        ok(balon.getCantidad()==4&&e.club.getComercial().stock(balon,"TIENDA")==4,"Inventario descontado");
        Venta v2=e.club.comprar(s,"TIENDA",Map.of(balon,1),emp,0,0,1000);
        igual(v2.getDescuento(),9000,"8% codigo socio + 1000 puntos");igual(v2.getImpuesto(),17290,"IVA despues del descuento y redencion");
        ok(s.getPuntos()>0,"Puntos se consumen y acumulan");
        no(()->e.club.comprar(s,"TIENDA",Map.of(balon,1),emp,0.10,0,0),"Codigo + promocion no acumulables");
        Venta v3=e.club.comprar(emp,"TIENDA",Map.of(balon,1),emp,0,0,0);
        igual(v3.getDescuento(),15000,"Empleado 15%");
        no(()->e.club.comprar(s,"TIENDA",Map.of(balon,1),null,0,0,999999),"No redimir mas puntos de los disponibles");
        e.club.moverArticulo(e.admin,balon,"TIENDA","BODEGA",1);
        ok(e.club.getComercial().stock(balon,"BODEGA")==1,"Inventario por ubicacion");
        no(()->e.club.moverArticulo(e.admin,balon,"TIENDA","BODEGA",100),"No trasladar mas que stock");
        e.club.reabastecer(e.admin,balon,9,"BODEGA");
        ok(e.club.getComercial().stock(balon,"BODEGA")==10,"Reabastecimiento");
        e.club.moverArticulo(e.admin,balon,"BODEGA","TIENDA",5);
        ok(e.club.getComercial().stock(balon,"TIENDA")==6,"Traslado a tienda");
        Bebida cafe=new Bebida();cafe.setNombre("Cafe caliente");cafe.setPrecio(5000);cafe.setEsCaliente(true);
        Snack barra=new Snack();barra.setNombre("Barra de mani");barra.setPrecio(3000);barra.setAlergenos("mani, gluten");
        e.club.registrarProducto(e.admin,cafe);e.club.registrarProducto(e.admin,barra);
        no(()->e.club.comprar(s,"CAFETERIA",Map.of(barra,1),null,0,0.10,0),"Sin aviso previo de alergenos");
        ok(e.club.consultarAlergenos(Map.of(barra,1)).get(0).contains("mani"),"Aviso de alergenos");
        Venta vf=e.club.comprarConfirmado(s,"CAFETERIA",Map.of(barra,1),null,0,0.10,0,LocalDateTime.now(),true);
        igual(vf.getImpuesto(),240,"Impuesto consumo 8%");igual(vf.getPropina(),300,"Propina 10%");igual(vf.getTotal(),3540,"Total cafeteria");
        Venta vf2=e.club.comprar(s,"CAFETERIA",Map.of(cafe,1),null,0,0.0,0);
        igual(vf2.getImpuesto(),400,"Impuesto bebida");igual(vf2.getPropina(),0,"Propina opcional");
        ok(e.club.getComercial().tieneBebidaCaliente(s),"Bebida caliente en mano");
        Instalacion padel=e.altaCancha("Padel","Padel",4,false);
        Socio a=e.altaSocio("pareja1",19),b=e.altaSocio("pareja2",19),c=e.altaSocio("pareja3",19);
        for(Socio x:List.of(s,a,b,c))e.club.inscribirAcademia(x,d("Padel"),"Intermedio");
        no(()->e.club.reservar(s,padel,"2030-08-10T15:00","Dobles",List.of(s,a,b,c)),"No ingresar con bebida caliente a padel");
        e.club.getComercial().bebidaTerminada(s);
        Reserva r=e.club.reservar(s,padel,"2030-08-10T15:00","Dobles",List.of(s,a,b,c));
        no(()->e.club.comprarConfirmado(s,"CAFETERIA",Map.of(cafe,1),null,0,0.10,0,LocalDateTime.of(2030,8,10,15,30),true),"No vender caliente dentro de padel");
        ok(e.club.getComercial().getVentas().size()==5,"Intento fallido no agrega venta");
        e.club.comprarConfirmado(s,"CAFETERIA",Map.of(cafe,1),null,0,0.10,0,LocalDateTime.of(2030,8,10,17,30),true);
        ok(e.club.getComercial().tieneBebidaCaliente(s),"Fuera de franja se permite caliente");
        e.club.getComercial().bebidaTerminada(s);
        Venta mensual=e.club.pagarMensualidad(s,25000);
        ok(mensual.getRubro().equals("MENSUALIDAD"),"Reportes mensualidad");
        Date inicio=new Date(System.currentTimeMillis()-24L*3600000),fin=new Date(System.currentTimeMillis()+24L*3600000);
        ok(e.club.ingresosPorRubro(e.admin,inicio,fin).containsKey("TIENDA"),"Reporte rubro tienda");
        ok(e.club.ingresosPorRubro(e.admin,inicio,fin).containsKey("CAFETERIA"),"Reporte rubro cafetería");
        ok(e.club.ingresosPorRubro(e.admin,inicio,fin).containsKey("MENSUALIDAD"),"Reporte rubro mensualidad");
        ok(e.club.reporteVentas(e.admin,"DIARIO",LocalDate.now(),"TIENDA").getTotal()>0,"Reporte diario");
        ok(e.club.reporteVentas(e.admin,"SEMANAL",LocalDate.now(),null).getTotal()>0,"Reporte semanal");
        ok(e.club.reporteVentas(e.admin,"MENSUAL",LocalDate.now(),null).getTotal()>0,"Reporte mensual");
        no(()->e.club.reporteVentas(e.admin,"SEMESTRAL",LocalDate.now(),null),"Periodo invalido");
        Sugerencia sug=e.club.sugerir(emp,"Nueva barra","SNACK","Bajo azucar");
        ok("PENDIENTE".equals(sug.getEstado()),"Sugerencia pendiente");
        no(()->e.club.decidirSugerencia(e.admin,sug,true,balon),"No aprobar con producto incorrecto");
        Snack nueva=new Snack();nueva.setNombre("Nueva barra");nueva.setPrecio(8000);nueva.setAlergenos("lacteos");
        e.club.decidirSugerencia(e.admin,sug,true,nueva);
        ok("APROBADA".equals(sug.getEstado()) && e.club.getComercial().getCatalogo().contains(nueva),"Aprobacion crea producto");
        no(()->e.club.decidirSugerencia(e.admin,sug,false,null),"No resolver dos veces");
        ok(e.club.inventarioProductos(e.admin).size()==4,"Reporte inventario");
        ok(!e.club.historialVentas(e.admin).isEmpty(),"Historial completo ventas");
    }
    private static void turnos(){
        Entorno e=new Entorno();Entrenador a=e.altaEntrenador("coach1"),b=e.altaEntrenador("coach2"),c=e.altaEntrenador("coach3");
        Fisioterapeuta f=e.altaFisio("physio1");
        ok(!e.club.getAdministrativo().coberturaCompleta(),"Sin turnos no existe cobertura");
        String[] dias={"LUNES","MARTES","MIERCOLES","JUEVES","VIERNES","SABADO","DOMINGO"};
        Turno lunes=null;
        for(String dia:dias){
            Turno t=e.club.getAdministrativo().crearTurno(e.admin,a,dia,"08:00","18:00");
            if(dia.equals("LUNES"))lunes=t;
            e.club.getAdministrativo().crearTurno(e.admin,b,dia,"08:00","18:00");
            e.club.getAdministrativo().crearTurno(e.admin,f,dia,"08:00","18:00");
        }
        ok(e.club.getAdministrativo().coberturaCompleta(),"Cobertura semanal completa");
        Turno original=lunes;
        no(()->e.club.getAdministrativo().cambiarTurno(e.admin,original,"LUNES","09:00","18:00"),"No dejar club sin segundo entrenador");
        ok(original.getHoraInicio().equals("08:00"),"Turno invalidado revierte el cambio");
        no(()->e.club.getAdministrativo().darBajaTurno(e.admin,original),"Dar de baja rompe minimo");
        ok(original.isActivo(),"Dar de baja no deja estado intermedio");
        no(()->e.club.getAdministrativo().crearTurno(e.admin,a,"LUNES","10:00","12:00"),"Mismo entrenador en dos turnos superpuestos");
        e.club.getAdministrativo().crearTurno(e.admin,c,"LUNES","08:00","18:00");
        e.club.getAdministrativo().cambiarTurno(e.admin,original,"LUNES","09:00","18:00");
        ok(e.club.getAdministrativo().coberturaCompleta(),"Cambio permitido con relevo");
        no(()->e.club.getAdministrativo().darBajaTurno(e.admin,e.club.getAdministrativo().getTurnos().stream().filter(t->t.getEmpleado()==f&&t.getDia().equals("LUNES")).findFirst().get()),"No eliminar unico fisioterapeuta");
    }
    private static void limitesYSeguridad(){
        Entorno e=new Entorno();
        List<Socio> grupo1=new ArrayList<>(),grupo2=new ArrayList<>();
        for(int k=0;k<8;k++){
            Socio socio=e.altaSocio("limites"+k,19);e.club.inscribirAcademia(socio,d("Padel"),"Intermedio");
            (k<4?grupo1:grupo2).add(socio);
        }
        Instalacion p1=e.altaCancha("P1","Padel",4,false),p2=e.altaCancha("P2","Padel",4,false);
        Instalacion futbol=e.altaCancha("Cancha futbol","Futbol",25,false);
        ok(e.club.getAcademias().limiteSimultaneo(d("Padel"))==2,"Cupo inicial instalaciones reales");
        no(()->e.club.configurarLimiteAcademias(e.admin,d("Padel"),0),"Limite simultaneo cero");
        e.club.configurarLimiteAcademias(e.admin,d("Padel"),1);
        ok(e.club.getAcademias().limiteSimultaneo(d("Padel"))==1,"Limite configurable");
        Reserva a=e.club.reservar(grupo1.get(0),p1,"2031-03-01T08:00","dobles",grupo1);
        no(()->e.club.reservar(grupo2.get(0),p2,"2031-03-01T08:00","dobles",grupo2),"Limite aunque segunda instalacion libre");
        ok(e.club.reservar(grupo2.get(0),p2,"2031-03-01T09:00","dobles",grupo2)!=null,"Bloque siguiente disponible");
        e.club.getAcademias().cancelarReserva(a,grupo1.get(0));
        ok(e.club.reservar(grupo1.get(0),p1,"2031-03-01T08:00","dobles",grupo1)!=null,"Cancelacion libera cupo");
        ok(e.club.inventarioInstalaciones(e.admin).size()==3,"Inventario contiene deporte colectivo e individual");
        Socio a1=grupo1.get(0),a2=grupo1.get(1);
        for(Socio x:List.of(a1,a2))e.club.inscribirAcademia(x,d("Tenis de mesa"),"Intermedio");
        Instalacion mesaTecho=e.altaCancha("Techada","Tenis de mesa",2,true);
        Instalacion mesaAbierta=e.altaCancha("Abierta","Tenis de mesa",2,false);
        Bebida bebida=new Bebida();bebida.setNombre("Chocolate caliente");bebida.setPrecio(8000);bebida.setEsCaliente(true);
        e.club.registrarProducto(e.admin,bebida);
        e.club.comprar(a1,"CAFETERIA",Map.of(bebida,1),null,0,0.10,0);
        no(()->e.club.reservar(a1,mesaTecho,"2031-03-02T08:00","sencillos",List.of(a1,a2)),"Mesa techada prohibe bebida caliente");
        ok(e.club.reservar(a1,mesaAbierta,"2031-03-02T08:00","sencillos",List.of(a1,a2))!=null,"Mesa sin techo permite caliente");
        e.club.getComercial().bebidaTerminada(a1);
        ok(e.club.reservar(a1,mesaTecho,"2031-03-02T11:00","sencillos",List.of(a1,a2))!=null,"Mesa techada permitida sin bebida caliente");
        no(()->e.club.reservar(a1,futbol,"2031-03-03T08:00","sencillos",List.of(a1,a2)),"No reservas individuales en futbol");
    }
    private static void reglasAleatorias(){
        Random rnd=new Random(340_112);
        Entorno e=new Entorno();Socio s=e.altaSocio("invariante",19);
        ArticuloTienda p=new ArticuloTienda();p.setNombre("TestStock");p.setPrecio(12500.25);p.setCantidad(5000);p.setCategoria("Prueba");e.club.registrarProducto(e.admin,p);
        int inicial=e.club.getComercial().stock(p,"TIENDA");
        for(int k=0;k<500;k++){
            int invalido=inicial+1+rnd.nextInt(100000);
            int antes=e.club.getComercial().stock(p,"TIENDA");int ventas=e.club.getComercial().getVentas().size();
            no(()->e.club.comprar(s,"TIENDA",Map.of(p,invalido),null,0,0,0),"Stock invalido aleatorio");
            ok(e.club.getComercial().stock(p,"TIENDA")==antes,"Atomicidad inventario fallo");
            ok(e.club.getComercial().getVentas().size()==ventas,"Atomicidad ventas fallo");
        }
        for(int k=0;k<200;k++){
            int q=1+rnd.nextInt(3);
            Venta v=e.club.comprar(s,"TIENDA",Map.of(p,q),null,0,0,0);
            igual(v.getImpuesto(),Math.round((12500.25*q*0.19)*100)/100.0,"IVA formula aleatoria");
            ok(v.getItems().get(0).getCantidad()==q,"Linea mantiene cantidades");
        }
        int neto=e.club.getComercial().stock(p,"TIENDA");
        int vendido=e.club.getComercial().getVentas().stream().mapToInt(v->v.getItems().get(0).getCantidad()).sum();
        ok(neto+vendido==inicial,"Conservacion inventario");
        ok(s.getPuntos()>0,"Puntos acumulados en 200 ventas");
    }
    public static int ejecutar(){
        verificaciones=0;
        cuentas();
        academias();
        academiasNivelesYDeportes();
        entrenamientos();
        comercial();
        turnos();
        limitesYSeguridad();
        reglasAleatorias();
        System.out.println("INTEGRACION OK: "+verificaciones+" verificaciones superadas.");
        return verificaciones;
    }
    public static void main(String[] args){ejecutar();}
}
