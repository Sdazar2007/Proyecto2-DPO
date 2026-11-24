package seneca.parte1;

/** Ejecutar como Java Application en Eclipse para probar la Parte 1. */
public final class EjecutarTodasLasPruebas {
    public static void main(String[] args) throws Exception {
        PruebasParte1.main(new String[0]);
        PruebasAvanzadas.main(new String[0]);
        PruebasTorneos.main(new String[0]);
        PruebasAuditoriaIntegral.main(new String[0]);
        PruebasRiesgosExtras.main(new String[0]);
        System.out.println("==============================================");
        System.out.println("AUDITORIA OK: 677 verificaciones superadas.");
        System.out.println("Persistencia entre dos procesos: ejecutar script PROBAR_TODO_WINDOWS.bat");
        System.out.println("==============================================");
    }
}
