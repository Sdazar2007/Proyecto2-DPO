import java.io.*;
import java.util.*;
import seneca.consola.EntradaConsola;
public class PruebaEntrada {
    public static void main(String[] args) {
        var out = new ByteArrayOutputStream();
        var entrada = new EntradaConsola(new Scanner("\ntexto\nabc\n99\n4\nNaN\nInfinity\n1,5\n"),new PrintStream(out));
        if (!entrada.leerTexto("").equals("texto")) throw new AssertionError();
        if (entrada.leerEntero("",1,5)!=4) throw new AssertionError();
        if (entrada.leerDecimal("")!=1.5) throw new AssertionError();
        try { entrada.leerTexto(""); throw new AssertionError(); } catch(NoSuchElementException expected) {}
        System.out.println("EntradaConsola: 4 verificaciones superadas.");
    }
}
