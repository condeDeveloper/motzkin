package br.com.conde.motzkin;

import java.util.ArrayList;
import java.util.List;

/**
 * O juiz: constroi TODOS os caminhos possiveis, um por um, e joga fora os que
 * descem abaixo do eixo ou nao terminam na altura zero.
 *
 * <p>Nao conhece recorrencia, nao conhece Catalan, nao conhece formula. Anda
 * pelas 3^n sequencias de passos e conta as que sobrevivem. E por isso que ele
 * vale como juiz, e e por isso que ele so vai ate uns 15 passos.
 */
public final class Caminhos {

    /** Teto de sequencias a enumerar: 3^n cresce depressa. */
    public static final long TETO = 30_000_000L;

    public static final char SOBE = '/';
    public static final char PLANO = '-';
    public static final char DESCE = '\\';

    private Caminhos() {
    }

    public static long quantasSequencias(int n) {
        long total = 1;
        for (int i = 0; i < n; i++) {
            if (total > TETO / 3) {
                return Long.MAX_VALUE;
            }
            total *= 3;
        }
        return total;
    }

    /** Quantos caminhos de Motzkin de comprimento n existem, contados na marra. */
    public static long contar(int n) {
        long total = quantasSequencias(n);
        if (total == Long.MAX_VALUE) {
            throw new IllegalArgumentException("sequencias demais para enumerar: 3^" + n);
        }
        long validos = 0;
        for (long codigo = 0; codigo < total; codigo++) {
            if (valido(codigo, n)) {
                validos++;
            }
        }
        return validos;
    }

    /** Decodifica o codigo na base tres e confere as duas regras. */
    private static boolean valido(long codigo, int n) {
        long resto = codigo;
        int altura = 0;
        for (int i = 0; i < n; i++) {
            int passo = (int) (resto % 3);
            resto /= 3;
            if (passo == 0) {
                altura++;
            } else if (passo == 2) {
                altura--;
                if (altura < 0) {
                    return false;
                }
            }
        }
        return altura == 0;
    }

    /** Os caminhos de comprimento n, desenhados, para os casos pequenos. */
    public static List<String> desenhar(int n) {
        long total = quantasSequencias(n);
        if (total == Long.MAX_VALUE) {
            throw new IllegalArgumentException("sequencias demais: 3^" + n);
        }
        List<String> achados = new ArrayList<>();
        for (long codigo = 0; codigo < total; codigo++) {
            if (!valido(codigo, n)) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            long resto = codigo;
            for (int i = 0; i < n; i++) {
                int passo = (int) (resto % 3);
                resto /= 3;
                sb.append(passo == 0 ? SOBE : passo == 1 ? PLANO : DESCE);
            }
            achados.add(sb.toString());
        }
        return achados;
    }

    /** Quantos dos caminhos de comprimento n sao so passos planos. */
    public static int quantosSaoPlanos(int n) {
        return 1;
    }

    /** A altura maxima alcancada por algum caminho de comprimento n. */
    public static int alturaMaxima(int n) {
        return n / 2;
    }

    /** Quantos caminhos de comprimento n tem altura maxima igual a h. */
    public static long porAltura(int n, int h) {
        long total = quantasSequencias(n);
        if (total == Long.MAX_VALUE) {
            throw new IllegalArgumentException("sequencias demais: 3^" + n);
        }
        long quantos = 0;
        for (long codigo = 0; codigo < total; codigo++) {
            long resto = codigo;
            int altura = 0;
            int maior = 0;
            boolean serve = true;
            for (int i = 0; i < n && serve; i++) {
                int passo = (int) (resto % 3);
                resto /= 3;
                if (passo == 0) {
                    altura++;
                    maior = Math.max(maior, altura);
                } else if (passo == 2) {
                    altura--;
                    if (altura < 0) {
                        serve = false;
                    }
                }
            }
            if (serve && altura == 0 && maior == h) {
                quantos++;
            }
        }
        return quantos;
    }
}
