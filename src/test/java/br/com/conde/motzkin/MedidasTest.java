package br.com.conde.motzkin;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Os numeros do README, presos. Todos sairam do {@link Medidor}. */
class MedidasTest {

    @Test
    @DisplayName("M_30 vale 1.697.385.471.211")
    void oTrigesimo() {
        BigInteger[] m = Motzkin.porConvolucao(Medidor.ATE);
        assertThat(m[Medidor.ATE]).isEqualTo(new BigInteger("1697385471211"));
        assertThat(m[20]).isEqualTo(BigInteger.valueOf(50_852_019));
        assertThat(m[25]).isEqualTo(BigInteger.valueOf(9_043_402_501L));
    }

    @Test
    @DisplayName("a razao em n = 30 vale 2,858789")
    void aRazaoEmTrinta() {
        BigInteger[] m = Motzkin.porConvolucao(Medidor.ATE);
        double razao = m[30].doubleValue() / m[29].doubleValue();
        assertThat(razao).isCloseTo(2.858789, org.assertj.core.data.Offset.offset(0.000001));
    }

    @Test
    @DisplayName("a enumeracao testa 2.391.484 sequencias de passos ate n = 13")
    void quantasSequenciasOJuizTesta() {
        long total = 0;
        for (int n = 0; n <= Medidor.ATE_JUIZ; n++) {
            total += Caminhos.quantasSequencias(n);
        }
        assertThat(total).isEqualTo(2_391_484L);
    }

    @Test
    @DisplayName("as tres formulas batem com a enumeracao em todo n ate 13")
    void juiz() {
        BigInteger[] conv = Motzkin.porConvolucao(Medidor.ATE_JUIZ);
        BigInteger[] tres = Motzkin.porTresTermos(Medidor.ATE_JUIZ);
        BigInteger[] cat = Motzkin.porCatalan(Medidor.ATE_JUIZ);
        for (int n = 0; n <= Medidor.ATE_JUIZ; n++) {
            long bruto = Caminhos.contar(n);
            assertThat(conv[n].longValueExact()).as("convolucao em n = %d", n).isEqualTo(bruto);
            assertThat(tres[n].longValueExact()).as("tres termos em n = %d", n).isEqualTo(bruto);
            assertThat(cat[n].longValueExact()).as("Catalan em n = %d", n).isEqualTo(bruto);
        }
        assertThat(conv[13].longValueExact()).isEqualTo(41_835);
    }

    @Test
    @DisplayName("a distribuicao de alturas com doze passos tem pico na altura 2")
    void distribuicaoDeAlturas() {
        int n = 12;
        long[] esperado = {1, 2047, 7753, 4644, 990, 75, 1};
        long soma = 0;
        for (int h = 0; h <= 6; h++) {
            long quantos = Caminhos.porAltura(n, h);
            assertThat(quantos).as("altura %d", h).isEqualTo(esperado[h]);
            soma += quantos;
        }
        assertThat(soma).isEqualTo(15_511);
        assertThat(Motzkin.porConvolucao(12)[12].longValueExact()).isEqualTo(15_511);
    }

    @Test
    @DisplayName("quase metade dos caminhos de doze passos tem altura exatamente 2")
    void oPicoNaAltura2() {
        long naAltura2 = Caminhos.porAltura(12, 2);
        assertThat(100.0 * naAltura2 / 15_511)
                .isCloseTo(49.984, org.assertj.core.data.Offset.offset(0.001));
        // e a altura 2 e mais comum que a 1 e a 3 juntas? nao: so que a maior
        assertThat(naAltura2).isGreaterThan(Caminhos.porAltura(12, 1));
        assertThat(naAltura2).isGreaterThan(Caminhos.porAltura(12, 3));
    }

    @Test
    @DisplayName("os dois extremos da distribuicao tem exatamente um caminho cada")
    void osExtremos() {
        assertThat(Caminhos.porAltura(12, 0)).isEqualTo(1);
        assertThat(Caminhos.porAltura(12, 6)).isEqualTo(1);
        assertThat(Caminhos.alturaMaxima(12)).isEqualTo(6);
    }

    @Test
    @DisplayName("a enumeracao joga fora 96% das sequencias em n = 10")
    void quantoSeJogaFora() {
        long sequencias = Caminhos.quantasSequencias(10);
        long validos = Caminhos.contar(10);
        assertThat(sequencias).isEqualTo(59_049);
        assertThat(validos).isEqualTo(2_188);
        assertThat(100.0 * (sequencias - validos) / sequencias)
                .isCloseTo(96.295, org.assertj.core.data.Offset.offset(0.001));
    }
}
