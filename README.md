# Desafio técnico CronoWise

Solução do desafio técnico para a vaga de estágio em desenvolvimento. O código Java implementa os dois exercícios práticos do Bloco 1 e mostra os resultados no console.

- [FilaProcessamentoService.java](src/main/java/com/cronowise/service/FilaProcessamentoService.java) ordena solicitações urgentes antes das normais, com desempate pelo timestamp.
- [AnomaliaService.java](src/main/java/com/cronowise/service/AnomaliaService.java) registra o índice da terceira medição consecutiva acima de 2.000 ms e reinicia a contagem a cada alerta.
- [Main.java](src/main/java/com/cronowise/Main.java) executa os exemplos, que devem produzir `[4, 2, 1, 3]` e `[3, 7]`.

## Como executar

Use JDK 17 ou superior. O [pom.xml](pom.xml) configura a compilação para Java 17:

```bash
mvn compile
java -cp target/classes com.cronowise.Main
```

Para compilar diretamente com o JDK, execute no PowerShell a partir da raiz:

```powershell
$fontes = (Get-ChildItem src/main/java -Recurse -Filter *.java).FullName
javac -encoding UTF-8 -d build $fontes
java -cp build com.cronowise.Main
```

## Respostas conceituais

As respostas teóricas dos Blocos 1, 2 e 3 estão em [RESPOSTAS.md](RESPOSTAS.md).
