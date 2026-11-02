package br.com.conde.motzkin;

import java.math.BigInteger;

/**
 * Numeros de Motzkin, por tres caminhos que nao se falam.
 *
 * <p>Um caminho de Motzkin de comprimento n sai de (0,0), chega em (n,0), anda
 * sempre para a direita com passos de SUBIR, PLANO ou DESCER, e nunca passa
 * para baixo do eixo. M_n conta quantos desses existem.
 *
 * <pre>
 * n   0  1  2  3  4   5   6   7    8    9
 * M_n 1  1  2  4  9  21  51  127  323  835
 * </pre>
 *
 * <p>Os tres caminhos:
 *
 * <ul>
 *   <li><b>convolucao</b>: ou o caminho comeca com um passo plano, ou comeca
 *       subindo e volta ao eixo em algum lugar, partindo o problema em dois;
 *   <li><b>recorrencia de tres termos</b>: uma formula fechada em M_(n-1) e
 *       M_(n-2), com uma divisao que precisa ser exata;
 *   <li><b>soma com Catalan</b>: escolha onde ficam os passos nao planos e
 *       conte os caminhos de subida e descida com os numeros de Catalan.
 * </ul>
 */
public final class Motzkin {

    private Motzkin() {
    }

    /**
     * Pela convolucao: M_n = M_(n-1) + soma de M_k * M_(n-2-k).
     *
     * <p>O primeiro termo e o caso em que o caminho comeca plano. O resto e o
     * caso em que ele sobe no primeiro passo e desce de volta ao eixo no passo
     * k+2, deixando um caminho de tamanho k por dentro e outro de tamanho
     * n-2-k depois.
     */
    public static BigInteger[] porConvolucao(int ate) {
        BigInteger[] m = new BigInteger[ate + 1];
        m[0] = BigInteger.ONE;
        if (ate >= 1) {
            m[1] = BigInteger.ONE;
        }
        for (int n = 2; n <= ate; n++) {
            BigInteger soma = m[n - 1];
            for (int k = 0; k <= n - 2; k++) {
                soma = soma.add(m[k].multiply(m[n - 2 - k]));
            }
            m[n] = soma;
        }
        return m;
    }

    /**
     * Pela recorrencia de tres termos:
     * (n + 2) * M_n = (2n + 1) * M_(n-1) + 3(n - 1) * M_(n-2).
     *
     * <p>A divisao por (n+3) tem que ser exata. Se um dia deixar de ser, isto
     * levanta excecao em vez de truncar e seguir com um numero errado.
     */
    public static BigInteger[] porTresTermos(int ate) {
        BigInteger[] m = new BigInteger[ate + 1];
        m[0] = BigInteger.ONE;
        if (ate >= 1) {
            m[1] = BigInteger.ONE;
        }
        for (int n = 2; n <= ate; n++) {
            BigInteger a = BigInteger.valueOf(2L * n + 1).multiply(m[n - 1]);
            BigInteger b = BigInteger.valueOf(3L * (n - 1)).multiply(m[n - 2]);
            BigInteger[] divisao = a.add(b).divideAndRemainder(BigInteger.valueOf(n + 2L));
            if (divisao[1].signum() != 0) {
                throw new ArithmeticException(
                        "a recorrencia de tres termos deixou resto em n = " + n);
            }
            m[n] = divisao[0];
        }
        return m;
    }

    /**
     * Pela soma com Catalan: M_n = soma de C(n, 2k) * Catalan_k.
     *
     * <p>Escolhe-se quais 2k passos nao sao planos, e os caminhos de subida e
     * descida desses 2k passos sao contados por Catalan_k.
     */
    public static BigInteger[] porCatalan(int ate) {
        BigInteger[] catalan = catalan(ate / 2 + 1);
        BigInteger[] m = new BigInteger[ate + 1];
        for (int n = 0; n <= ate; n++) {
            BigInteger soma = BigInteger.ZERO;
            for (int k = 0; 2 * k <= n; k++) {
                soma = soma.add(binomial(n, 2 * k).multiply(catalan[k]));
            }
            m[n] = soma;
        }
        return m;
    }

    public static BigInteger[] catalan(int ate) {
        BigInteger[] c = new BigInteger[ate + 1];
        c[0] = BigInteger.ONE;
        for (int n = 1; n <= ate; n++) {
            BigInteger soma = BigInteger.ZERO;
            for (int k = 0; k < n; k++) {
                soma = soma.add(c[k].multiply(c[n - 1 - k]));
            }
            c[n] = soma;
        }
        return c;
    }

    public static BigInteger binomial(int n, int k) {
        if (k < 0 || k > n) {
            return BigInteger.ZERO;
        }
        int menor = Math.min(k, n - k);
        BigInteger valor = BigInteger.ONE;
        for (int i = 1; i <= menor; i++) {
            valor = valor.multiply(BigInteger.valueOf(n - menor + i))
                    .divide(BigInteger.valueOf(i));
        }
        return valor;
    }
}
