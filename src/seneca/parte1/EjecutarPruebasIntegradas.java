package seneca.parte1;

/** Entrada unica para las 3 partes: Parte 1 y pruebas nuevas de Parte 2 + 3. */
public final class EjecutarPruebasIntegradas {
    public static void main(String[] args) throws Exception {
        EjecutarTodasLasPruebas.main(new String[0]);
        int nuevas=PruebasIntegracionCompleta.ejecutar();
        System.out.println("======================================================");
        System.out.println("PRUEBAS DE LAS 3 PARTES OK: " + (677+nuevas) + " verificaciones en una JVM.");
        System.out.println("Persistencia entre procesos: ejecutar PROBAR_TODO_WINDOWS.bat o PROBAR_TODO_LINUX.sh.");
        System.out.println("======================================================");
    }
}
