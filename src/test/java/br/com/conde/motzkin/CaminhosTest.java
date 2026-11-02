package br.com.conde.motzkin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CaminhosTest {

    @Test
    @DisplayName("os caminhos pequenos, desenhados um a um")
    void caminhosPequenos() {
        assertThat(Caminhos.desenhar(0)).containsExactly("");
        assertThat(Caminhos.desenhar(1)).containsExactly("-");
        assertThat(Caminhos.desenhar(2)).containsExactlyInAnyOrder("/\\", "--");
        assertThat(Caminhos.desenhar(3)).hasSize(4);
        assertThat(Caminhos.desenhar(4)).hasSize(9);
    }

    @Test
    @DisplayName("nenhum caminho valido passa para baixo do eixo")
    void nuncaAbaixoDoEixo() {
        for (int n = 1; n <= 10; n++) {
            for (String caminho : Caminhos.desenhar(n)) {
                int altura = 0;
                for (char c : caminho.toCharArray()) {
                    if (c == Caminhos.SOBE) {
                        altura++;
                    } else if (c == Caminhos.DESCE) {
                        altura--;
                    }
                    assertThat(altura).as("caminho %s", caminho).isGreaterThanOrEqualTo(0);
                }
                assertThat(altura).as("caminho %s termina fora do eixo", caminho).isZero();
            }
        }
    }

    @Test
    @DisplayName("a enumeracao bate com a convolucao em todo n ate 12")
    void enumeracaoBateComFormula() {
        var m = Motzkin.porConvolucao(12);
        for (int n = 0; n <= 12; n++) {
            assertThat(Caminhos.contar(n)).as("n = %d", n).isEqualTo(m[n].longValueExact());
        }
    }

    @Test
    @DisplayName("3^n sequencias sao testadas, e quase todas sao jogadas fora")
    void quaseTudoEhDescartado() {
        assertThat(Caminhos.quantasSequencias(10)).isEqualTo(59_049);
        assertThat(Caminhos.contar(10)).isEqualTo(2_188);
        // menos de 4% das sequencias de passos formam caminho valido
        assertThat(100.0 * 2_188 / 59_049).isLessThan(4.0);
    }

    @Test
    @DisplayName("o teto existe e e respeitado, em vez de travar")
    void teto() {
        assertThat(Caminhos.quantasSequencias(40)).isEqualTo(Long.MAX_VALUE);
        assertThatThrownBy(() -> Caminhos.contar(40))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("demais");
    }

    @Test
    @DisplayName("a soma das alturas devolve o total de caminhos")
    void alturasSomamOTotal() {
        int n = 10;
        long soma = 0;
        for (int h = 0; h <= Caminhos.alturaMaxima(n); h++) {
            soma += Caminhos.porAltura(n, h);
        }
        assertThat(soma).isEqualTo(Caminhos.contar(n));
    }

    @Test
    @DisplayName("so um caminho e todo plano, e so um alcanca a altura maxima")
    void osDoisExtremos() {
        for (int n = 2; n <= 10; n += 2) {
            assertThat(Caminhos.porAltura(n, 0)).as("n = %d, altura 0", n).isEqualTo(1);
            assertThat(Caminhos.porAltura(n, Caminhos.alturaMaxima(n)))
                    .as("n = %d, altura maxima", n).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("o caminho todo plano aparece na lista desenhada")
    void oCaminhoPlano() {
        List<String> caminhos = Caminhos.desenhar(5);
        assertThat(caminhos).contains("-----");
        assertThat(caminhos).hasSize(21);
    }
}
