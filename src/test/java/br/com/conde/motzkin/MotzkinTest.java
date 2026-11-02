package br.com.conde.motzkin;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MotzkinTest {

    @Test
    @DisplayName("os primeiros numeros de Motzkin, que se conta no dedo")
    void osPrimeiros() {
        BigInteger[] m = Motzkin.porConvolucao(9);
        assertThat(m[0]).isEqualTo(BigInteger.ONE);
        assertThat(m[1]).isEqualTo(BigInteger.ONE);
        assertThat(m[2]).isEqualTo(BigInteger.TWO);
        assertThat(m[3]).isEqualTo(BigInteger.valueOf(4));
        assertThat(m[4]).isEqualTo(BigInteger.valueOf(9));
        assertThat(m[5]).isEqualTo(BigInteger.valueOf(21));
        assertThat(m[6]).isEqualTo(BigInteger.valueOf(51));
        assertThat(m[7]).isEqualTo(BigInteger.valueOf(127));
        assertThat(m[8]).isEqualTo(BigInteger.valueOf(323));
        assertThat(m[9]).isEqualTo(BigInteger.valueOf(835));
    }

    @Test
    @DisplayName("os tres caminhos dao a mesma sequencia ate n = 60")
    void osTresCaminhosConcordam() {
        int ate = 60;
        BigInteger[] conv = Motzkin.porConvolucao(ate);
        BigInteger[] tres = Motzkin.porTresTermos(ate);
        BigInteger[] cat = Motzkin.porCatalan(ate);
        for (int n = 0; n <= ate; n++) {
            assertThat(tres[n]).as("tres termos em n = %d", n).isEqualTo(conv[n]);
            assertThat(cat[n]).as("Catalan em n = %d", n).isEqualTo(conv[n]);
        }
    }

    @Test
    @DisplayName("a recorrencia de tres termos divide exato em todo n")
    void divisaoSempreExata() {
        // se deixasse resto em algum n, porTresTermos levantaria excecao
        assertThat(Motzkin.porTresTermos(200)).hasSize(201);
    }

    @Test
    @DisplayName("os numeros de Catalan sao os de sempre")
    void catalan() {
        BigInteger[] c = Motzkin.catalan(10);
        assertThat(c[0]).isEqualTo(BigInteger.ONE);
        assertThat(c[1]).isEqualTo(BigInteger.ONE);
        assertThat(c[2]).isEqualTo(BigInteger.TWO);
        assertThat(c[3]).isEqualTo(BigInteger.valueOf(5));
        assertThat(c[4]).isEqualTo(BigInteger.valueOf(14));
        assertThat(c[5]).isEqualTo(BigInteger.valueOf(42));
        assertThat(c[10]).isEqualTo(BigInteger.valueOf(16796));
    }

    @Test
    @DisplayName("a razao entre termos vizinhos sobe e nunca chega a 3")
    void aRazaoSobeParaTres() {
        BigInteger[] m = Motzkin.porConvolucao(80);
        double anterior = 0;
        for (int n = 5; n <= 80; n++) {
            double razao = m[n].doubleValue() / m[n - 1].doubleValue();
            assertThat(razao).as("n = %d", n).isGreaterThan(anterior);
            assertThat(razao).as("n = %d", n).isLessThan(3.0);
            anterior = razao;
        }
    }

    @Test
    @DisplayName("binomial usa a simetria e os casos de borda")
    void binomial() {
        assertThat(Motzkin.binomial(5, 0)).isEqualTo(BigInteger.ONE);
        assertThat(Motzkin.binomial(5, 5)).isEqualTo(BigInteger.ONE);
        assertThat(Motzkin.binomial(5, 2)).isEqualTo(BigInteger.TEN);
        assertThat(Motzkin.binomial(5, 7)).isEqualTo(BigInteger.ZERO);
        assertThat(Motzkin.binomial(5, -1)).isEqualTo(BigInteger.ZERO);
    }
}
