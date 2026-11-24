package seneca.consola;

import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class EntradaConsola {
    private final Scanner scanner;
    private final PrintStream salida;

    public EntradaConsola(Scanner scanner, PrintStream salida) {
        this.scanner = scanner;
        this.salida = salida;
    }

    public String leerTexto(String mensaje) {
        while (true) {
            salida.print(mensaje);
            if (!scanner.hasNextLine()) throw new NoSuchElementException("Entrada finalizada");
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) return texto;
            salida.println("Ingrese un texto no vacío.");
        }
    }

    public int leerEntero(String mensaje, int minimo, int maximo) {
        if (minimo > maximo) throw new IllegalArgumentException("Rango inválido");
        while (true) {
            try {
                int valor = Integer.parseInt(leerTexto(mensaje));
                if (valor >= minimo && valor <= maximo) return valor;
            } catch (NumberFormatException e) {}
            salida.println("Ingrese un entero entre " + minimo + " y " + maximo + ".");
        }
    }

    public double leerDecimal(String mensaje) {
        while (true) {
            try {
                double valor = Double.parseDouble(leerTexto(mensaje).replace(',', '.'));
                if (Double.isFinite(valor)) return valor;
            } catch (NumberFormatException e) {}
            salida.println("Ingrese un número válido.");
        }
    }
}
