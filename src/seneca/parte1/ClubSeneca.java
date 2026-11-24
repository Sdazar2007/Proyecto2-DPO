package seneca.parte1;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.time.*;
import java.util.*;

/** Fachada integrada: una unica identidad compartida y un unico archivo persistente. */
public final class ClubSeneca implements Serializable {
    private static final long serialVersionUID = 1L;
    private final ServicioDeportivo deportivo=new ServicioDeportivo();
    private final ServicioAcademias academias=new ServicioAcademias();
    private final ServicioComercial comercial=new ServicioComercial();
    private final ServicioAdministrativo administrativo=new ServicioAdministrativo();
    private ServicioTorneos torneos=new ServicioTorneos();
    private final Map<String,Usuario> usuarios=new HashMap<>();
    private final Map<String,String> salts=new HashMap<>();
    private final List<String> historial=new ArrayList<>();

    private static void exigir(boolean ok,String mensaje){if(!ok)throw new IllegalArgumentException(mensaje);}
    private static String login(String login){exigir(login!=null&&!login.isBlank(),"Login obligatorio");return login.trim().toLowerCase(Locale.ROOT);}
    private static String hash(String clave,String sal){
        try{
            MessageDigest md=MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest((sal+":"+clave).getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        }catch(NoSuchAlgorithmException ex){throw new IllegalStateException(ex);}
    }
    private void registrar(Usuario usuario){
        exigir(usuario!=null&&usuario.getPassword()!=null&&!usuario.getPassword().isBlank(),"Usuario o clave invalida");
        String id=login(usuario.getLogin());exigir(!usuarios.containsKey(id),"Login duplicado");
        if(usuario instanceof Empleado e){
            exigir(e.getCodigoDescuento()!=null&&!e.getCodigoDescuento().isBlank(),"Empleado requiere codigo de descuento");
            exigir(usuarios.values().stream().filter(u->u instanceof Empleado).map(u->(Empleado)u).noneMatch(u->u.getCodigoDescuento().equalsIgnoreCase(e.getCodigoDescuento())),"Codigo de empleado duplicado");
        }
        String sal=UUID.randomUUID().toString();
        usuario.setLogin(id);usuario.setPassword(hash(usuario.getPassword(),sal));usuario.setActivo(true);
        usuarios.put(id,usuario);salts.put(id,sal);historial.add("ALTA USUARIO "+id);
    }
    public Administrador crearAdministradorInicial(String login,String clave){
        exigir(usuarios.values().stream().noneMatch(x->x instanceof Administrador),"Ya existe un administrador");
        Administrador a=new Administrador();a.setLogin(login);a.setPassword(clave);registrar(a);return a;
    }
    private void autorizar(Administrador a){exigir(a!=null&&a.isActivo()&&usuarios.get(login(a.getLogin()))==a&&a instanceof Administrador,"Accion reservada al administrador");}
    public void registrarUsuario(Administrador admin,Usuario usuario){autorizar(admin);registrar(usuario);}
    public Usuario autenticar(String login,String password){
        String id=login(login);Usuario u=usuarios.get(id);
        if(u==null||!u.isActivo()||password==null)return null;
        String clave=hash(password,salts.get(id));
        return MessageDigest.isEqual(u.getPassword().getBytes(java.nio.charset.StandardCharsets.UTF_8),
            clave.getBytes(java.nio.charset.StandardCharsets.UTF_8))?u:null;
    }
    public void darBajaUsuario(Administrador admin,Usuario u){autorizar(admin);
        exigir(u!=null&&usuarios.get(login(u.getLogin()))==u&&!(u instanceof Administrador),"No se puede dar de baja");
        u.setActivo(false);historial.add("BAJA USUARIO "+u.getLogin());
    }
    public void darBajaEquipo(Administrador admin,Equipo e){
        autorizar(admin);exigir(e!=null&&deportivo.getEquipos().contains(e),"Equipo inexistente");
        e.setActivo(false);historial.add("BAJA EQUIPO "+e.getNombre());
    }
    public void crearEquipo(Administrador admin,Equipo equipo){autorizar(admin);deportivo.registrarEquipo(equipo);historial.add("ALTA EQUIPO "+equipo.getNombre());}
    public InscripcionEquipo inscribirEquipo(Socio s,Equipo e,String pos,int numero){
        validarSocio(s);exigir(e!=null&&e.isActivo(),"Equipo dado de baja");return deportivo.inscribir(s,e,pos,numero);
    }
    public void validarSocio(Socio s){exigir(s!=null&&s.isActivo()&&usuarios.get(login(s.getLogin()))==s,"Socio no habilitado o sin registrar");}
    public void registrarInstalacion(Administrador a,Instalacion i){autorizar(a);academias.registrarInstalacion(i);historial.add("ALTA INSTALACION "+i.getNombre());}
    public void configurarLimiteAcademias(Administrador a,Deporte d,int limite){autorizar(a);academias.configurarLimiteSimultaneo(d,limite);}
    public InscripcionIndividual inscribirAcademia(Socio s,Deporte d,String nivel){validarSocio(s);return academias.inscribir(s,d,nivel);}
    public Reserva reservar(Socio responsable,Instalacion i,String bloque,String modalidad,List<Socio> jugadores){
        validarSocio(responsable);
        exigir(jugadores!=null,"Participantes obligatorios");
        for(Socio s:jugadores){validarSocio(s);
            exigir(!(comercial.tieneBebidaCaliente(s) && academias.restringida(i)),"Bebida caliente incompatible con la instalacion");
        }
        academias.exigirSinConflictoEntrenamientos(i,bloque);
        return academias.reservar(responsable,i,bloque,modalidad,jugadores);
    }
    public Venta comprar(Usuario comprador,String rubro,Map<Producto,Integer> productos,Empleado codigoEmpleado,
          double promocion,double propina,int puntosRedimir,LocalDateTime momento){
        return comprarConfirmado(comprador,rubro,productos,codigoEmpleado,promocion,propina,puntosRedimir,momento,false);
    }
    public Venta comprarConfirmado(Usuario comprador,String rubro,Map<Producto,Integer> productos,Empleado codigoEmpleado,
          double promocion,double propina,int puntosRedimir,LocalDateTime momento,boolean confirmaAlergenos){
        exigir(comprador!=null&&usuarios.get(login(comprador.getLogin()))==comprador&&comprador.isActivo(),"Comprador no registrado");
        if(codigoEmpleado!=null)exigir(usuarios.get(login(codigoEmpleado.getLogin()))==codigoEmpleado&&codigoEmpleado.isActivo(),"Codigo de empleado no valido");
        exigir(productos!=null,"Carrito obligatorio");
        if(!comercial.advertencias(productos).isEmpty())exigir(confirmaAlergenos,"Debe confirmar que le informaron los alergenos");
        boolean restringida=comprador instanceof Socio && academias.dentroInstalacionRestringida((Socio)comprador,momento);
        return comercial.comprar(comprador,rubro,productos,codigoEmpleado,promocion,propina,puntosRedimir,restringida);
    }
    public Venta comprar(Usuario comprador,String rubro,Map<Producto,Integer> productos,Empleado codigoEmpleado,
          double promocion,double propina,int puntosRedimir){
        return comprar(comprador,rubro,productos,codigoEmpleado,promocion,propina,puntosRedimir,LocalDateTime.now());
    }
    public Venta comprarCafeteria(Usuario comprador,Map<Producto,Integer> productos,String codigo,boolean confirmaAlergenos){
        return comprarConCodigo(comprador,"CAFETERIA",productos,codigo,0,0.10,0,LocalDateTime.now(),confirmaAlergenos);
    }
    public List<String> consultarAlergenos(Map<Producto,Integer> carrito){return comercial.advertencias(carrito);}
    public Venta comprarConCodigo(Usuario comprador,String rubro,Map<Producto,Integer> productos,String codigo,
          double promocion,double propina,int puntosRedimir,LocalDateTime momento,boolean confirmaAlergenos){
        Empleado empleado=null;
        if(codigo!=null&&!codigo.isBlank()){
            for(Usuario u:usuarios.values())if(u instanceof Empleado e && e.isActivo() && codigo.equalsIgnoreCase(e.getCodigoDescuento())){empleado=e;break;}
            exigir(empleado!=null,"Codigo de empleado inexistente");
        }
        return comprarConfirmado(comprador,rubro,productos,empleado,promocion,propina,puntosRedimir,momento,confirmaAlergenos);
    }
    public SesionEntrenamiento programarEntrenamientoEquipo(Entrenador entrenador,Equipo equipo,Instalacion instalacion,
          String bloque,int duracion,List<Socio> convocados){
        exigir(entrenador!=null&&usuarios.get(login(entrenador.getLogin()))==entrenador&&entrenador.isActivo(),"Entrenador no autorizado");
        exigir(equipo!=null&&equipo.isActivo()&&deportivo.getEquipos().contains(equipo),"Equipo no habilitado");
        exigir(instalacion!=null&&instalacion.getDeporte()!=null&&instalacion.getDeporte().getNombre().equalsIgnoreCase(equipo.getDeporte().getNombre()),"Instalacion de deporte diferente");
        exigir(convocados!=null&&convocados.stream().allMatch(s->s!=null&&s.isActivo()&&equipo.getInscripciones().stream().anyMatch(i->i.getSocio()==s)),"Convocado no pertenece a equipo");
        return academias.programarSesion(entrenador,instalacion,bloque,duracion,convocados);
    }
    public void declararSocioEntrenador(Administrador a,Socio s,boolean esEntrenador){autorizar(a);validarSocio(s);academias.declararSocioEntrenador(s,esEntrenador);}
    public void registrarProducto(Administrador a,Producto p){autorizar(a);comercial.registrarProducto(p);}
    public void reabastecer(Administrador a,ArticuloTienda p,int cantidad,String ubicacion){autorizar(a);comercial.reabastecer(p,cantidad,ubicacion);}
    public void moverArticulo(Administrador a,ArticuloTienda p,String origen,String destino,int cantidad){autorizar(a);comercial.mover(p,origen,destino,cantidad);}
    public Venta pagarMensualidad(Socio socio,double monto){validarSocio(socio);return comercial.mensualidad(socio,monto);}
    public Sugerencia sugerir(Empleado e,String nombre,String tipo,String motivo){
        exigir(e!=null&&usuarios.get(login(e.getLogin()))==e&&e.isActivo(),"Empleado no autorizado");
        return administrativo.sugerir(e,nombre,tipo,motivo);
    }
    public void decidirSugerencia(Administrador a,Sugerencia s,boolean aprobar,Producto producto){autorizar(a);administrativo.decidirSugerencia(a,s,aprobar,producto,comercial);}
    public List<Instalacion> inventarioInstalaciones(Administrador a){autorizar(a);return academias.getInstalaciones();}
    public List<Producto> inventarioProductos(Administrador a){autorizar(a);return comercial.getCatalogo();}
    public List<Reserva> historialReservas(Administrador a){autorizar(a);return academias.getReservas();}
    public List<Venta> historialVentas(Administrador a){autorizar(a);return comercial.getVentas();}
    public List<Partido> historialPartidos(Administrador a){autorizar(a);return deportivo.getPartidos();}
    public List<SesionEntrenamiento> historialEntrenamientos(Administrador a){autorizar(a);return academias.getSesiones();}
    public Map<String,Double> ingresosPorRubro(Administrador a,Date inicio,Date fin){autorizar(a);return comercial.ingresosPorRubro(inicio,fin);}
    public ReporteVentas reporteVentas(Administrador a,String periodo,LocalDate referencia,String rubro){
        autorizar(a);exigir(referencia!=null&&periodo!=null,"Periodo invalido");
        LocalDate inicio;LocalDate fin;
        switch(periodo.trim().toUpperCase(Locale.ROOT)){
            case "DIARIO": inicio=referencia;fin=referencia;break;
            case "SEMANAL":inicio=referencia.minusDays(referencia.getDayOfWeek().getValue()-1);fin=inicio.plusDays(6);break;
            case "MENSUAL":inicio=referencia.withDayOfMonth(1);fin=inicio.plusMonths(1).minusDays(1);break;
            default:throw new IllegalArgumentException("Periodo debe ser DIARIO, SEMANAL o MENSUAL");
        }
        return comercial.reporte(java.util.Date.from(inicio.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                java.util.Date.from(fin.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusMillis(1)),rubro);
    }
    public Map<String,Integer> reporteDeportivo(Administrador a){
        autorizar(a);Map<String,Integer> r=new LinkedHashMap<>();
        r.put("equipos",deportivo.getEquipos().size());r.put("partidosConjunto",deportivo.getPartidos().size());
        r.put("partidosRaqueta",academias.getPartidos().size());r.put("sesiones",academias.getSesiones().size());
        r.put("evaluaciones",academias.getSesiones().stream().mapToInt(s->s.getEvaluaciones().size()).sum());
        r.put("estadisticas",deportivo.getEstadisticas().size()+academias.getEstadisticas().size());
        return Collections.unmodifiableMap(r);
    }
    public ServicioTorneos getTorneos(){if(torneos==null)torneos=new ServicioTorneos();return torneos;}
    private void validarUsuarioTorneo(Usuario u){exigir(u!=null&&u.isActivo()&&usuarios.get(login(u.getLogin()))==u,"Usuario no registrado o inactivo");}
    public void crearTorneo(Administrador a,Torneo t){autorizar(a);getTorneos().crearTorneo(t);}
    public void configurarRankingTorneo(Administrador a,String id,List<String> ranking){autorizar(a);getTorneos().buscarTorneo(id).configurarRanking(ranking);}
    public void configurarRankingElo(Administrador a,String id){autorizar(a);Torneo t=getTorneos().buscarTorneo(id);exigir(t.getModalidad()==ModalidadTorneo.INDIVIDUAL,"ELO automatico solo individual");List<String> r=academias.getInscripciones().stream().filter(i->i.getSocio().isActivo()&&ServicioAcademias.disciplina(i.getDeporte()).equals(ServicioAcademias.disciplina(t.getDeporte()))&&ServicioAcademias.nivel(i.getNivel()).equals(ServicioAcademias.nivel(t.getCategoria().getNombre()))).sorted(Comparator.comparingInt(InscripcionIndividual::getRankingELO).reversed().thenComparing(i->i.getSocio().getLogin())).map(i->ParticipanteTorneo.jugadores(List.of(i.getSocio())).getId()).toList();t.configurarRanking(r);}
    private void validarParticipanteTorneo(Torneo t,ParticipanteTorneo p){
        exigir(p!=null,"Participante obligatorio");List<Usuario> miembros=p.getUsuarios();
        if(t.getModalidad()==ModalidadTorneo.EQUIPOS){Equipo e=p.getEquipo();exigir(e!=null&&e.isActivo()&&deportivo.getEquipos().contains(e)&&e.getDeporte().getNombre().equalsIgnoreCase(t.getDeporte().getNombre())&&e.getCategoria().getNombre().equalsIgnoreCase(t.getCategoria().getNombre()),"Equipo incompatible");exigir(!miembros.isEmpty()&&miembros.size()==e.getInscripciones().size()&&e.getInscripciones().stream().allMatch(i->miembros.contains(i.getSocio())),"Nomina de equipo invalida");int min=t.getDeporte().getNombre().toLowerCase(Locale.ROOT).contains("fut")?7:t.getDeporte().getNombre().toLowerCase(Locale.ROOT).contains("vole")?6:5;exigir(miembros.size()>=min,"Equipo incompleto");}
        else exigir(p.getEquipo()==null&&miembros.size()==(t.getModalidad()==ModalidadTorneo.DOBLES?2:1),"Modalidad incompatible");
        for(Usuario u:miembros){validarUsuarioTorneo(u);exigir(u instanceof Socio||u instanceof Estudiante||u instanceof Externo||u instanceof Entrenador||u instanceof Fisioterapeuta,"Rol no admite torneos");
            if(u instanceof Estudiante e)exigir(e.isMatriculado(),"Estudiante no matriculado");
            if(u instanceof Empleado e){exigir(t.getModalidad()==ModalidadTorneo.INDIVIDUAL,"Empleado solo en modalidad individual");exigir(!administrativo.enTurno(e,t.getFecha()),"Empleado de turno");}
            if(u instanceof Socio so&&t.getModalidad()!=ModalidadTorneo.EQUIPOS){InscripcionIndividual i=academias.buscar(so,t.getDeporte());if(i!=null)exigir(ServicioAcademias.nivel(i.getNivel()).equals(ServicioAcademias.nivel(t.getCategoria().getNombre())),"Nivel incompatible");}
        }
    }
    public InscripcionTorneo inscribirTorneo(Usuario actor,String id,ParticipanteTorneo p){validarUsuarioTorneo(actor);Torneo t=getTorneos().buscarTorneo(id);validarParticipanteTorneo(t,p);exigir(p.contiene(actor),"Solo integrantes pueden inscribirse");return t.inscribir(p);}
    public void desinscribirTorneo(Usuario actor,String id,String participante){validarUsuarioTorneo(actor);Torneo t=getTorneos().buscarTorneo(id);exigir(t.activas().stream().anyMatch(i->i.getParticipante().getId().equals(participante)&&i.getParticipante().contiene(actor)),"Inscripcion ajena");t.desinscribir(participante);}
    public void liberarCuposTorneo(Administrador a,String id){autorizar(a);getTorneos().buscarTorneo(id).liberarCuposPreferenciales();}
    public void iniciarTorneo(Administrador a,String id){autorizar(a);Torneo t=getTorneos().buscarTorneo(id);for(InscripcionTorneo i:t.activas())validarParticipanteTorneo(t,i.getParticipante());t.iniciarTorneo();}
    public void registrarResultadoTorneo(Administrador a,String id,String encuentro,int local,int visitante){autorizar(a);getTorneos().buscarTorneo(id).registrarResultado(encuentro,local,visitante);}
    public void finalizarTorneo(Administrador a,String id){autorizar(a);getTorneos().buscarTorneo(id).finalizarTorneo();}
    public void marcarPremioEntregado(Administrador a,String id){autorizar(a);Torneo t=getTorneos().buscarTorneo(id);exigir(t instanceof TorneoCompetitivo,"Torneo no competitivo");List<PremioEfectivo> p=((TorneoCompetitivo)t).getPremios();exigir(!p.isEmpty(),"Sin premio");p.getFirst().marcarEntregado();}
    public Venta comprarConBono(Usuario s,String codigo,String rubro,Map<Producto,Integer> carrito,double propina,boolean confirma){validarUsuarioTorneo(s);BonoDescuento b=getTorneos().buscarBono(s,codigo);Venta v=comprarConfirmado(s,rubro,carrito,null,b.getPorcentaje(),propina,0,LocalDateTime.now(),confirma);b.redimir();return v;}
    public ServicioDeportivo getDeportivo(){return deportivo;}
    public ServicioAcademias getAcademias(){return academias;}
    public ServicioComercial getComercial(){return comercial;}
    public ServicioAdministrativo getAdministrativo(){return administrativo;}
    public List<Usuario> getUsuarios(){return List.copyOf(usuarios.values());}
    public List<String> getHistorial(){return List.copyOf(historial);}

    /** Guardado atomico en carpeta de datos ajena al codigo fuente. */
    public void guardar(Path ruta) throws IOException {
        Objects.requireNonNull(ruta,"Ruta obligatoria");Path destino=ruta.toAbsolutePath().normalize();
        Path carpeta=destino.getParent();if(carpeta!=null)Files.createDirectories(carpeta);
        Path temporal=Files.createTempFile(carpeta,".seneca-",".tmp");
        try{
            try(ObjectOutputStream salida=new ObjectOutputStream(Files.newOutputStream(temporal))){salida.writeObject(this);}
            try{Files.move(temporal,destino,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}
            catch(AtomicMoveNotSupportedException x){Files.move(temporal,destino,StandardCopyOption.REPLACE_EXISTING);}
        }finally{Files.deleteIfExists(temporal);}
    }
    public static ClubSeneca cargar(Path ruta) throws IOException,ClassNotFoundException {
        try(ObjectInputStream entrada=new ObjectInputStream(Files.newInputStream(ruta))){
            Object o=entrada.readObject();if(!(o instanceof ClubSeneca))throw new IOException("Archivo no es ClubSeneca");return (ClubSeneca)o;
        }
    }
}
