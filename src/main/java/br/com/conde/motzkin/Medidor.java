package br.com.conde.motzkin;

import java.math.BigInteger;
import java.util.List;

/**
 * Roda as tabelas do README.
 *
 * <pre>
 * mvn -B -ntp exec:java -Dexec.args="tabela"
 * mvn -B -ntp exec:java -Dexec.args="desenho"
 * mvn -B -ntp exec:java -Dexec.args="juiz"
 * mvn -B -ntp exec:java -Dexec.args="altura"
 * mvn -B -ntp exec:java -Dexec.args="fundo"     (enumeracao cara, job proprio)
 * </pre>
 */
public final class Medidor {

    public static final int ATE = 30;
    public static final int ATE_JUIZ = 13;
    public static final int ATE_FUNDO = 15;

    public static void main(String[] args) {
        String comando = args.length > 0 ? args[0] : "tudo";
        switch (comando) {
            case "tabela" -> tabela();
            case "desenho" -> desenho();
            case "juiz" -> juiz();
            case "altura" -> altura();
            case "fundo" -> fundo();
            case "tudo" -> {
                tabela();
                desenho();
                juiz();
                altura();
            }
            default -> {
                System.out.println("comando desconhecido: " + comando);
                System.exit(2);
            }
        }
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println(texto);
        System.out.println("-".repeat(texto.length()));
    }

    private static void tabela() {
        titulo("Os numeros de Motzkin ate n = " + ATE);
        BigInteger[] conv = Motzkin.porConvolucao(ATE);
        BigInteger[] tres = Motzkin.porTresTermos(ATE);
        BigInteger[] cat = Motzkin.porCatalan(ATE);
        System.out.printf("%-5s %-22s %-10s %s%n", "n", "M_n", "razao", "os tres caminhos");
        for (int n = 0; n <= ATE; n++) {
            String razao = n == 0 ? "-" : String.format("%.5f",
                    conv[n].doubleValue() / conv[n - 1].doubleValue());
            boolean iguais = conv[n].equals(tres[n]) && conv[n].equals(cat[n]);
            System.out.printf("%-5d %-22s %-10s %s%n", n, conv[n], razao, iguais ? "iguais" : "DIFEREM");
        }
        System.out.println();
        System.out.printf("a razao M_n/M_(n-1) se aproxima de 3 por baixo: em n = %d ela vale %.6f%n",
                ATE, conv[ATE].doubleValue() / conv[ATE - 1].doubleValue());
    }

    private static void desenho() {
        titulo("Todos os caminhos pequenos, desenhados");
        for (int n = 0; n <= 4; n++) {
            List<String> caminhos = Caminhos.desenhar(n);
            System.out.printf("n = %d  (%d caminhos)%n", n, caminhos.size());
            for (String c : caminhos) {
                System.out.println("   " + (c.isEmpty() ? "(vazio)" : c));
            }
        }
    }

    private static void juiz() {
        titulo("As tres formulas contra a enumeracao, ate n = " + ATE_JUIZ);
        BigInteger[] conv = Motzkin.porConvolucao(ATE_JUIZ);
        BigInteger[] tres = Motzkin.porTresTermos(ATE_JUIZ);
        BigInteger[] cat = Motzkin.porCatalan(ATE_JUIZ);
        System.out.printf("%-5s %-12s %-12s %-12s %-14s %s%n",
                "n", "convolucao", "tres termos", "Catalan", "enumeracao", "3^n testados");
        long totalEnumerado = 0;
        for (int n = 0; n <= ATE_JUIZ; n++) {
            long bruto = Caminhos.contar(n);
            long sequencias = Caminhos.quantasSequencias(n);
            totalEnumerado += sequencias;
            boolean iguais = conv[n].longValueExact() == bruto
                    && tres[n].longValueExact() == bruto
                    && cat[n].longValueExact() == bruto;
            System.out.printf("%-5d %-12s %-12s %-12s %-14d %-12d %s%n",
                    n, conv[n], tres[n], cat[n], bruto, sequencias, iguais ? "" : "  DIFEREM");
            if (!iguais) {
                System.exit(1);
            }
        }
        System.out.printf("%nsequencias de passos testadas uma a uma: %d%n", totalEnumerado);
    }

    private static void altura() {
        titulo("Caminhos por altura maxima");
        int n = 12;
        System.out.printf("comprimento %d, total %s%n%n", n, Motzkin.porConvolucao(n)[n]);
        System.out.printf("%-10s %-14s %s%n", "altura", "caminhos", "fatia");
        long total = 0;
        for (int h = 0; h <= Caminhos.alturaMaxima(n); h++) {
            total += Caminhos.porAltura(n, h);
        }
        for (int h = 0; h <= Caminhos.alturaMaxima(n); h++) {
            long quantos = Caminhos.porAltura(n, h);
            System.out.printf("%-10d %-14d %.3f%%%n", h, quantos, 100.0 * quantos / total);
        }
        System.out.printf("%na altura maxima possivel e %d, e ela e alcancada por %d caminho(s)%n",
                Caminhos.alturaMaxima(n), Caminhos.porAltura(n, Caminhos.alturaMaxima(n)));
    }

    private static void fundo() {
        titulo("Enumeracao ate n = " + ATE_FUNDO);
        long comeco = System.nanoTime();
        BigInteger[] conv = Motzkin.porConvolucao(ATE_FUNDO);
        long totalSequencias = 0;
        for (int n = 0; n <= ATE_FUNDO; n++) {
            long bruto = Caminhos.contar(n);
            totalSequencias += Caminhos.quantasSequencias(n);
            if (conv[n].longValueExact() != bruto) {
                System.out.printf("MEDIDA MUDOU: em n = %d a formula diz %s e a enumeracao %d%n",
                        n, conv[n], bruto);
                System.exit(1);
            }
        }
        System.out.printf("n de 0 a %d conferidos contra a enumeracao, todos iguais%n", ATE_FUNDO);
        System.out.printf("sequencias de passos testadas uma a uma: %d%n", totalSequencias);
        System.out.printf("M_%d = %s%n", ATE_FUNDO, conv[ATE_FUNDO]);
        System.out.printf("%ntempo: %.1f s%n", (System.nanoTime() - comeco) / 1e9);
    }
}
