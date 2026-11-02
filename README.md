# motzkin

Ande da esquerda para a direita. A cada passo você pode **subir**, ficar **plano** ou
**descer**. Comece na altura zero, termine na altura zero, e nunca desça abaixo do eixo.

Com 4 passos existem exatamente **9** caminhos assim:

```
----    /\--    -/\-    --/\    /-\-
/--\    -/-\    /\/\    //\\
```

Esses são os números de Motzkin: `1, 1, 2, 4, 9, 21, 51, 127, 323, 835, ...`

Este repositório calcula a sequência por **três recorrências que não se falam** e confere
as três contra a construção literal de todos os caminhos.

```bash
mvn -B -ntp verify                             # 22 testes
mvn -B -ntp exec:java -Dexec.args="tabela"     # os números até n = 30
mvn -B -ntp exec:java -Dexec.args="desenho"    # todos os caminhos pequenos
mvn -B -ntp exec:java -Dexec.args="juiz"       # as três fórmulas x a enumeração
mvn -B -ntp exec:java -Dexec.args="altura"     # caminhos por altura máxima
mvn -B -ntp exec:java -Dexec.args="fundo"      # até n = 15 (job próprio)
```

Java 21, sem dependência fora de JUnit e AssertJ.

## Os três caminhos

**Convolução.** Ou o caminho começa com um passo plano — e sobra um caminho de tamanho
`n-1` — ou ele sobe no primeiro passo e volta ao eixo em algum lugar, partindo o problema
em dois pedaços independentes.

**Recorrência de três termos.** `(n+2)·M_n = (2n+1)·M_(n-1) + 3(n-1)·M_(n-2)`. Duas
multiplicações e uma divisão, sem somatório nenhum.

**Soma com Catalan.** `M_n = Σ C(n, 2k) · Catalan_k`. Escolha quais `2k` passos não são
planos; os caminhos de subida e descida desses passos são contados por Catalan.

## O juiz constrói os caminhos

A `Caminhos` percorre todas as `3^n` sequências de passos possíveis e joga fora as que
descem abaixo do eixo ou não terminam na altura zero. Não conhece recorrência, não
conhece Catalan, não conhece fórmula.

```
n     convolução   três termos   Catalan   enumeração   3^n testados
0     1            1             1         1            1
4     9            9             9         9            81
8     323          323           323       323          6.561
10    2188         2188          2188      2188         59.049
13    41835        41835         41835     41835        1.594.323
```

**2.391.484 sequências de passos** construídas e julgadas uma a uma, e as três fórmulas
batem com a contagem em todas. E quase tudo é descartado: em `n = 10`, das 59.049
sequências só **2.188 sobrevivem** — 96,3% vão fora.

## A razão

```
n     M_n                 M_n / M_(n-1)
20    50.852.019          2,79418
25    9.043.402.501       2,83250
30    1.697.385.471.211   2,85879
```

A razão sobe sempre, nunca desce, e **nunca chega a 3**. O limite é exatamente 3, e ela se
aproxima por baixo tão devagar que em `n = 30` ainda está em 2,8588. Um teste varre até
`n = 80` confirmando as duas coisas: estritamente crescente e estritamente abaixo de 3.

## Por altura

Com 12 passos, os 15.511 caminhos se distribuem assim pela altura máxima que alcançam:

```
altura   caminhos   fatia
0        1          0,006%
1        2047       13,197%
2        7753       49,984%
3        4644       29,940%
4        990        6,383%
5        75         0,484%
6        1          0,006%
```

**Metade dos caminhos tem altura máxima exatamente 2.** Não 1, que seria o mais econômico,
nem 3, que é o meio do intervalo possível. E os dois extremos têm **um caminho cada**: o
todo plano, e o que sobe seis vezes e desce seis vezes sem respirar.

## O que as medidas me corrigiram

**Escrevi a recorrência de três termos com os índices deslocados.** Usei
`(n+3)·M_n = (2n+3)·M_(n-1) + 3n·M_(n-2)`, que é a forma que se vê escrita para a
sequência indexada a partir de 1. Com a indexação daqui, a conta certa é
`(n+2)·M_n = (2n+1)·M_(n-1) + 3(n-1)·M_(n-2)`.

O erro apareceu **na primeira execução**, em `n = 2`, porque a divisão por `(n+2)` levanta
exceção quando deixa resto em vez de truncar. Se eu tivesse usado divisão inteira comum,
`13 / 5` teria virado `2` — que por acaso é o valor certo de `M_2` — e o erro só apareceria
alguns termos adiante, misturado com os outros. Essa guarda custou três linhas e economizou
a tarde.

**Esperei o pico da distribuição de alturas no meio.** Com 12 passos a altura pode ir de 0
a 6, e eu chutaria 3. O pico está em **2**, com praticamente metade de todos os caminhos.

**O juiz não escala, e isso não é defeito.** `3^n` passa de 30 milhões em `n = 16`. A
enumeração recusa o que não cabe no teto em vez de travar, e a tabela diz **até onde** foi
conferido — 13 no CI, 15 no job de fundo — nunca "todos".

## Quatro achados

1. As três recorrências e a construção literal dos caminhos concordaram em **2.391.484
   sequências de passos** testadas uma a uma, sem divergência.
2. **Metade dos caminhos de 12 passos tem altura máxima exatamente 2** (49,984%), e os dois
   extremos — o todo plano e o que sobe até 6 — têm **um caminho cada**.
3. A razão `M_n/M_(n-1)` sobe sempre e **nunca alcança 3**: em `n = 30` ainda está em
   **2,8588**, e a monotonia foi conferida até `n = 80`.
4. A enumeração **joga fora 96,3%** do que constrói: em `n = 10`, das 59.049 sequências de
   passos só 2.188 formam caminho válido.
