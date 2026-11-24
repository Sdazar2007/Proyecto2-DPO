package seneca.parte1;

import java.io.Serializable;
import java.math.*;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/** Parte 3: tienda, cafeteria, impuestos, promociones, puntos, inventario y reportes. */
public final class ServicioComercial implements Serializable {
    private static final long serialVersionUID=1L;
    private final List<Producto> catalogo=new ArrayList<>();
    private final List<Venta> ventas=new ArrayList<>();
    private final Map<ArticuloTienda,Map<String,Integer>> existencias=new IdentityHashMap<>();
    private final List<Reabastecimiento> reabastecimientos=new ArrayList<>();
    private final Set<Socio> portadoresBebidaCaliente=Collections.newSetFromMap(new IdentityHashMap<>());
    private final List<String> avisosAlergenos=new ArrayList<>();

    private static void exigir(boolean ok,String m){if(!ok)throw new IllegalArgumentException(m);}
    private static BigDecimal dinero(double v){exigir(Double.isFinite(v),"Monto invalido");return BigDecimal.valueOf(v).setScale(2,RoundingMode.HALF_UP);}
    private static BigDecimal redondear(BigDecimal b){return b.setScale(2,RoundingMode.HALF_UP);}
    private static String nombre(String n){exigir(n!=null&&!n.isBlank(),"Nombre obligatorio");return n.trim();}
    public void registrarProducto(Producto p){
        exigir(p!=null && p.getPrecio()>0 && Double.isFinite(p.getPrecio()),"Precio invalido");
        nombre(p.getNombre());
        exigir(p instanceof ArticuloTienda || p instanceof Bebida || p instanceof Snack,"Producto desconocido");
        exigir(catalogo.stream().noneMatch(x->x.getNombre().equalsIgnoreCase(p.getNombre())),"Producto duplicado");
        if(p instanceof ArticuloTienda){ArticuloTienda a=(ArticuloTienda)p;exigir(a.getCantidad()>=0,"Stock negativo");
            Map<String,Integer> ubicaciones=new HashMap<>();ubicaciones.put("TIENDA",a.getCantidad());existencias.put(a,ubicaciones);}
        p.setActivo(true);catalogo.add(p);
    }
    public int stock(ArticuloTienda a,String ubicacion){return existencias.getOrDefault(a,Map.of()).getOrDefault(nombre(ubicacion).toUpperCase(Locale.ROOT),0);}
    public void reabastecer(ArticuloTienda a,int cantidad,String ubicacion){
        exigir(existencias.containsKey(a) && cantidad>0,"Articulo o cantidad invalido");
        String u=nombre(ubicacion).toUpperCase(Locale.ROOT);
        Map<String,Integer> m=existencias.get(a);int nuevoStock=Math.addExact(a.getCantidad(),cantidad);
        int nuevaUbicacion=Math.addExact(m.getOrDefault(u,0),cantidad);
        m.put(u,nuevaUbicacion);a.setCantidad(nuevoStock);
        reabastecimientos.add(new Reabastecimiento(new Date(),cantidad,a,u));
    }
    public void mover(ArticuloTienda a,String origen,String destino,int cantidad){
        exigir(existencias.containsKey(a)&&cantidad>0,"Movimiento invalido");
        String o=nombre(origen).toUpperCase(Locale.ROOT),d=nombre(destino).toUpperCase(Locale.ROOT);
        exigir(!o.equals(d),"Ubicaciones iguales");
        Map<String,Integer> m=existencias.get(a);exigir(m.getOrDefault(o,0)>=cantidad,"Stock insuficiente en origen");
        int nuevoDestino=Math.addExact(m.getOrDefault(d,0),cantidad);
        m.put(o,m.get(o)-cantidad);m.put(d,nuevoDestino);
    }
    public void entregarBebidaCaliente(Socio s){exigir(s!=null,"Socio invalido");portadoresBebidaCaliente.add(s);}
    public boolean tieneBebidaCaliente(Socio s){return portadoresBebidaCaliente.contains(s);}
    public void bebidaTerminada(Socio s){portadoresBebidaCaliente.remove(s);}
    public List<String> getAvisosAlergenos(){return List.copyOf(avisosAlergenos);}
    public List<String> advertencias(Map<Producto,Integer> compra){
        List<String> salida=new ArrayList<>();
        for(Producto p:compra.keySet())if(p instanceof Snack sn && sn.getAlergenos()!=null&&!sn.getAlergenos().isBlank())
            salida.add(p.getNombre()+": "+sn.getAlergenos());
        return List.copyOf(salida);
    }
    /** descuento 15% empleado propio / 8% socio con codigo / promocion no acumulable. */
    public Venta comprar(Usuario comprador,String rubro,Map<Producto,Integer> cantidades,
          Empleado descuentoEmpleado,double promocion,double propinaPorcentaje,int puntosRedimir,boolean enZonaRestringida){
        exigir(comprador!=null&&comprador.isActivo()&&comprador.getLogin()!=null,"Comprador no habilitado");
        String tipo=nombre(rubro).toUpperCase(Locale.ROOT);
        exigir(Set.of("TIENDA","CAFETERIA").contains(tipo),"Rubro no admitido");
        exigir(cantidades!=null&&!cantidades.isEmpty(),"Carrito vacio");
        exigir(Double.isFinite(promocion) && promocion>=0&&promocion<=1,"Promocion invalida");
        exigir(Double.isFinite(propinaPorcentaje) && propinaPorcentaje>=0&&propinaPorcentaje<=1,"Propina invalida");
        if(tipo.equals("TIENDA"))exigir(propinaPorcentaje==0,"No hay propina de tienda");
        exigir(puntosRedimir>=0,"Puntos negativos");
        double tasaDescuento=promocion;
        if(descuentoEmpleado!=null){
            exigir(descuentoEmpleado.isActivo()&&descuentoEmpleado.getCodigoDescuento()!=null&&!descuentoEmpleado.getCodigoDescuento().isBlank(),"Codigo de descuento invalido");
            exigir(promocion==0,"Descuentos no acumulables");
            if(comprador==descuentoEmpleado)tasaDescuento=0.15;
            else {exigir(comprador instanceof Socio,"Codigo compartido solo para socios");tasaDescuento=0.08;}
        }
        exigir(!(comprador instanceof Administrador && puntosRedimir>0),"Administrador no redime puntos");
        BigDecimal subtotal=BigDecimal.ZERO;
        boolean caliente=false;
        List<ItemVenta> lineas=new ArrayList<>();
        for(Map.Entry<Producto,Integer> e:cantidades.entrySet()){
            Producto p=e.getKey();Integer q=e.getValue();
            exigir(p!=null&&q!=null&&q>0&&catalogo.contains(p)&&p.isActivo(),"Producto o cantidad invalida");
            exigir(Double.isFinite(p.getPrecio())&&p.getPrecio()>0,"Precio invalido");
            if(tipo.equals("TIENDA")){
                exigir(p instanceof ArticuloTienda,"Tienda solo admite articulos");
                exigir(stock((ArticuloTienda)p,"TIENDA")>=q,"Sin inventario suficiente");
            } else {
                exigir(p instanceof Bebida || p instanceof Snack,"Cafeteria solo admite bebidas y snacks");
                if(p instanceof Bebida && ((Bebida)p).isEsCaliente())caliente=true;
            }
            subtotal=subtotal.add(dinero(p.getPrecio()).multiply(BigDecimal.valueOf(q)));
            lineas.add(new ItemVenta(q,p,p.getPrecio()));
        }
        exigir(!(caliente && enZonaRestringida),"Prohibido despachar caliente en padel o mesa techada");
        if(comprador instanceof Socio)exigir(((Socio)comprador).getPuntos()>=puntosRedimir,"Puntos insuficientes");
        else exigir(puntosRedimir==0,"Solo socios redimen puntos");
        BigDecimal desc=redondear(subtotal.multiply(BigDecimal.valueOf(tasaDescuento)));
        BigDecimal neto=subtotal.subtract(desc);
        // Los puntos se redimen contra el precio antes de impuesto, sin permitir saldo negativo.
        exigir(BigDecimal.valueOf(puntosRedimir).compareTo(neto)<=0,"Redencion supera el precio");
        neto=neto.subtract(BigDecimal.valueOf(puntosRedimir));
        BigDecimal impuesto=redondear(neto.multiply(BigDecimal.valueOf(tipo.equals("TIENDA")?0.19:0.08)));
        BigDecimal propina=tipo.equals("CAFETERIA")?redondear(neto.multiply(BigDecimal.valueOf(propinaPorcentaje))):BigDecimal.ZERO;
        BigDecimal total=redondear(neto.add(impuesto).add(propina));
        // Comprobacion anticipada de desbordamientos para evitar efectos parciales.
        int puntosNuevos=total.multiply(BigDecimal.valueOf(0.02)).setScale(0,RoundingMode.DOWN).intValueExact();
        Integer saldoNuevo=null;
        if(comprador instanceof Socio so)saldoNuevo=Math.addExact(so.getPuntos()-puntosRedimir,puntosNuevos);
        // Mutaciones solamente despues de todas las validaciones (operacion atomica).
        for(ItemVenta it:lineas)if(it.getProducto() instanceof ArticuloTienda){
            ArticuloTienda a=(ArticuloTienda)it.getProducto();
            Map<String,Integer> m=existencias.get(a);m.put("TIENDA",m.get("TIENDA")-it.getCantidad());a.setCantidad(a.getCantidad()-it.getCantidad());
        }
        Venta v=new Venta(new Date(),subtotal.doubleValue(),desc.doubleValue()+puntosRedimir,impuesto.doubleValue(),total.doubleValue(),
                propina.doubleValue(),tipo,comprador,lineas,puntosRedimir);
        ventas.add(v);
        if(comprador instanceof Socio s){
            s.setPuntos(saldoNuevo);
            if(caliente)entregarBebidaCaliente(s);
        }
        avisosAlergenos.addAll(advertencias(cantidades));
        return v;
    }
    public Venta mensualidad(Socio socio,double valor){
        exigir(socio!=null&&socio.isActivo()&&valor>0&&Double.isFinite(valor),"Mensualidad invalida");
        double total=dinero(valor).doubleValue();
        int nuevos=BigDecimal.valueOf(total).multiply(BigDecimal.valueOf(0.02)).setScale(0,RoundingMode.DOWN).intValueExact();
        int saldo=Math.addExact(socio.getPuntos(),nuevos);
        Venta v=new Venta(new Date(),total,0,0,total,0,"MENSUALIDAD",socio,new ArrayList<>(),0);ventas.add(v);
        socio.setPuntos(saldo);return v;
    }
    public List<Venta> getVentas(){return List.copyOf(ventas);}
    public List<Producto> getCatalogo(){return List.copyOf(catalogo);}
    public List<Reabastecimiento> getReabastecimientos(){return List.copyOf(reabastecimientos);}
    public List<Venta> ventasEnPeriodo(Date desde,Date hasta,String rubro){
        exigir(desde!=null&&hasta!=null&&!desde.after(hasta),"Periodo invalido");
        return ventas.stream().filter(v->!v.getFecha().before(desde)&&!v.getFecha().after(hasta)
             &&(rubro==null||v.getRubro().equalsIgnoreCase(rubro))).toList();
    }
    public ReporteVentas reporte(Date desde,Date hasta,String rubro){
        List<Venta> seleccion=ventasEnPeriodo(desde,hasta,rubro);
        double total=seleccion.stream().mapToDouble(Venta::getTotal).sum();
        ReporteVentas r=new ReporteVentas(desde.toString()+" - "+hasta.toString(),dinero(total).doubleValue(),rubro==null?"TODOS":rubro,desde,hasta);
        return r;
    }
    public Map<String,Double> ingresosPorRubro(Date desde,Date hasta){
        Map<String,Double> total=new LinkedHashMap<>();
        for(Venta v:ventasEnPeriodo(desde,hasta,null))total.merge(v.getRubro(),v.getTotal(),Double::sum);
        return Collections.unmodifiableMap(total);
    }
}
