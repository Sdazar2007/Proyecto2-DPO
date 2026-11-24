package seneca.parte1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RegresionProyecto1Test {
    @Test void parte1() { assertDoesNotThrow(() -> PruebasParte1.main(new String[0])); }
    @Test void avanzadas() { assertDoesNotThrow(() -> PruebasAvanzadas.main(new String[0])); }
    @Test void competenciasOriginales() { assertDoesNotThrow(() -> PruebasTorneos.main(new String[0])); }
    @Test void auditoria() { assertDoesNotThrow(() -> PruebasAuditoriaIntegral.main(new String[0])); }
    @Test void regresion() { assertDoesNotThrow(() -> PruebasRiesgosExtras.main(new String[0])); }
    @Test void integracion() { assertDoesNotThrow(() -> { PruebasIntegracionCompleta.ejecutar(); }); }
    @Test void persistencia(@TempDir Path carpeta) {
        Path archivo = carpeta.resolve("club.dat");
        assertDoesNotThrow(() -> PruebaPersistenciaIntegral.main(new String[]{"escribir", archivo.toString()}));
        assertDoesNotThrow(() -> PruebaPersistenciaIntegral.main(new String[]{"leer", archivo.toString()}));
    }
}
