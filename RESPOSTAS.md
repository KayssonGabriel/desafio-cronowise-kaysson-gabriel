# Respostas Conceituais e Análise de Código

## Bloco 1: Lógica e Raciocínio

### 1.3 Pergunta Conceitual
> **Pergunta:** Você tem uma lista com 1 milhão de registros. Precisa verificar se um CPF específico já existe nessa lista. Você faria isso com:
> (a) Percorrer a lista do início ao fim comparando cada item
> (b) Transformar a lista em um conjunto (set/HashSet) e usar busca direta

> Explique em 3–5 linhas por que uma abordagem é melhor que a outra e em que situação você escolheria cada uma.

#### Resposta:
A melhor abordagem seria a **(b) Transformar a lista em um conjunto (Set/HashSet) e usar busca direta.**

**Justificativa:** Buscar um elemento em uma Lista tradicional exige uma varredura linear com complexidade de tempo O(N),
o que se torna extremamente lento para 1 milhão de registros. O `HashSet` utiliza uma tabela (hash table),
permitindo buscas diretas em tempo constante O(1), sendo a estrutura ideal para validações massivas de unicidade.
Eu escolheria o Set quando a prioridade fosse a velocidade de validação ou a garantia de unicidade (como não permitir CPFs duplicados).
Por outro lado, escolheria a Lista apenas se a ordem exata de inserção dos dados fosse fundamental para a regra de negócio ou se o
armazenamento de elementos repetidos fosse esperado.

---

## Bloco 2: Leitura de Código Python

**O trecho abaixo simula parte de um robô da CronoWise que consulta um sistema externo de crédito.
Leia com atenção antes de responder.**

```python
import time
import logging
from enum import Enum

logger = logging.getLogger(__name__)

class ErrorType(Enum):
    TEMPORARY = "TEMPORARY"
    PERMANENT = "PERMANENT"

class ConsultaError(Exception):
    def __init__(self, message: str, error_type: ErrorType):
        self.error_type = error_type
        super().__init__(message)

def consultar_credito(cpf_hash: str, tentativa: int = 1) -> dict:
    MAX_TENTATIVAS = 3
    ESPERA_BASE = 2  # segundos

    try:
        resultado = _chamar_servico_externo(cpf_hash)
        return {"status": "OK", "dados": resultado}

    except ConsultaError as e:
        if e.error_type == ErrorType.PERMANENT:
            logger.error(f"Erro permanente para CPF {cpf_hash}: {e}")
            return {"status": "ERRO_PERMANENTE", "dados": None}

        if tentativa >= MAX_TENTATIVAS:
            logger.warning(f"Máximo de tentativas atingido para CPF {cpf_hash}")
            return {"status": "ERRO_TEMPORARIO_ESGOTADO", "dados": None}

        espera = ESPERA_BASE ** tentativa
        logger.info(f"Tentativa {tentativa} falhou. Aguardando {espera}s...")
        time.sleep(espera)
        return consultar_credito(cpf_hash, tentativa + 1)

    except Exception as e:
        logger.critical(f"Erro inesperado: {e}")
        raise
```

### 2.1 O que esse código faz?
> **Pergunta:** Descreva em até 5 linhas o comportamento geral desta função.
> O que ela recebe, o que tenta fazer, e o que retorna em cada cenário possível?

#### Resposta:
A função recebe o hash do CPF e o número da tentativa atual. Tenta consultar um serviço externo:
se houver sucesso, retorna os dados (OK); se houver erro permanente, aborta e retorna ERRO_PERMANENTE;
se houver erro temporário, aguarda um tempo progressivamente maior (calculado por ESPERA_BASE ** tentativa) e tenta
novamente até o limite de 3 vezes, retornando ERRO_TEMPORARIO_ESGOTADO caso todas falhem.

### 2.2 Rastreando a execução
> **Pergunta:** Simule mentalmente o que acontece quando consultar_credito("abc123") é chamado e o serviço externo retorna
um ConsultaError do tipo TEMPORARY nas 3 primeiras chamadas.

> * Quantas vezes a função é chamada no total?
> * Quais são os tempos de espera entre cada tentativa?
> * Qual é o retorno final?

#### Resposta:
* **Chamadas no total:** 3 vezes (tentativa 1, tentativa 2 e tentativa 3).
* **Tempos de espera:** `2` segundos (após a 1ª chamada) e `4` segundos (após a 2ª chamada). Na 3ª tentativa, o limite é atingido
e a função retorna antes de executar um novo *sleep*.
* **Retorno final:** `{"status": "ERRO_TEMPORARIO_ESGOTADO", "dados": None}`.

### 2.3 Identificando um problema
> **Pergunta:** Há um problema de design nesta implementação que pode causar falhas em produção em cenários de alta carga.
Você consegue identificar? Descreva o problema e como você corrigiria. (Dica: pense no mecanismo que a função usa para chamar a si mesma.)

#### Resposta:
**Problema:** A função utiliza recursão combinada com `time.sleep()`, o que introduz um bloqueio síncrono. Em um cenário de alta carga
(ex: milhares de consultas simultâneas), reter a *thread* "dormindo" por vários segundos esgotará rapidamente o *pool* de conexões/threads do servidor,
causando "inanição" e derrubando a aplicação. Além disso, recursões profundas desnecessárias podem estourar a pilha de chamadas.

**Correção:** A operação deve ser refatorada para utilizar um laço iterativo (`while` ou `for`) com chamadas assíncronas e não bloqueantes
(ex: `asyncio.sleep()` no Python).

### 2.4 Proposta de melhoria
> **Pergunta:** Se você precisasse adicionar um timeout máximo de 30 segundos para toda a operação (incluindo todas as tentativas e esperas),
como faria? Não precisa escrever o código completo — descreva a abordagem em texto ou pseudocódigo.

#### Resposta:
Eu armazenaria o *timestamp* inicial logo na primeira execução (ex: `start_time = time.time()`) e o passaria adiante como um parâmetro na recursão
(ou o controlaria no escopo externo). Antes de executar o `time.sleep()` e disparar a próxima chamada, eu validaria se o tempo atual somado ao tempo
previsto de espera ultrapassa os 30 segundos (ex: `if (time.time() - start_time) + espera > 30`). Caso ultrapasse, a função aborta o *retry*
imediatamente e retorna uma falha de *timeout*, protegendo o sistema.

---

## Bloco 3:  Leitura de Código: Java + React

### 3.1 Java: Entendendo uma classe de domínio

```java
public class SolicitacaoCredito {

    private final String requestId;
    private final String cpfHash;
    private StatusSolicitacao status;
    private int tentativas;
    private LocalDateTime ultimaAtualizacao;

    private static final int MAX_TENTATIVAS = 3;

    public SolicitacaoCredito(String requestId, String cpfHash) {
        this.requestId = requestId;
        this.cpfHash = cpfHash;
        this.status = StatusSolicitacao.PENDENTE;
        this.tentativas = 0;
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public boolean podeRetentar() {
        return this.status == StatusSolicitacao.ERRO_TEMPORARIO
            && this.tentativas < MAX_TENTATIVAS;
    }

    public void registrarTentativa(boolean sucesso) {
        this.tentativas++;
        this.ultimaAtualizacao = LocalDateTime.now();

        if (sucesso) {
            this.status = StatusSolicitacao.CONCLUIDA;
        } else if (this.tentativas >= MAX_TENTATIVAS) {
            this.status = StatusSolicitacao.FALHA_DEFINITIVA;
        } else {
            this.status = StatusSolicitacao.ERRO_TEMPORARIO;
        }
    }
}
```

> **Pergunta:**: Após o código abaixo ser executado, qual será o valor de status e tentativas?

```java
SolicitacaoCredito s = new SolicitacaoCredito("req-001", "hash-abc");
s.registrarTentativa(false);
s.registrarTentativa(false);
System.out.println(s.podeRetentar()); // O que imprime?
s.registrarTentativa(false);
System.out.println(s.podeRetentar()); // O que imprime agora?
```

> **Pergunta:**:
> * O que cada `println` imprime e por quê?
> * Qual o estado final (`status` e `tentativas`) após as três chamadas?

#### Resposta:
* **Primeiro `println` (imprime `true`):** Após as duas primeiras chamadas com erro (`sucesso = false`), a variável `tentativas` é incrementada para `2` (que ainda é menor que o limite de 3). O código cai no bloco `else` final, definindo o status como `ERRO_TEMPORARIO`. Ao chamar `podeRetentar()`, o método retorna verdadeiro pois ambas as condições (o status ser temporário E o número de tentativas ser menor que o máximo) são satisfeitas.
* **Segundo `println` (imprime `false`):** Na terceira chamada malsucedida, o contador `tentativas` chega a `3`. Desta vez, o método `registrarTentativa` entra na condição `else if (this.tentativas >= MAX_TENTATIVAS)`, alterando o status definitivamente para `FALHA_DEFINITIVA`. Ao avaliar `podeRetentar()`, ele retorna falso, pois o status mudou e o limite de tentativas foi esgotado (3 não é menor que 3).
* **Estado final:** O status final será `StatusSolicitacao.FALHA_DEFINITIVA` e o número de tentativas será `3`.

### 3.2 React/TypeScript: Lendo um componente

```TypeScript
import { useState, useEffect } from "react";

type Solicitacao = {
  id: string;
  status: "PENDENTE" | "CONCLUIDA" | "FALHA_DEFINITIVA";
  cpfHash: string;
};

export function PainelSolicitacoes({ tenantId }: { tenantId: string }) {
  const [solicitacoes, setSolicitacoes] = useState<Solicitacao[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function carregar() {
      const res = await fetch(`/api/solicitacoes?tenant=${tenantId}`);
      const data = await res.json();
      setSolicitacoes(data);
      setLoading(false);
    }
    carregar();
  }, [tenantId]);

  const pendentes = solicitacoes.filter(s => s.status === "PENDENTE");
  const falhas    = solicitacoes.filter(s => s.status === "FALHA_DEFINITIVA");

  if (loading) return <p>Carregando...</p>;

  return (
    <div>
      <h2>Solicitações — Tenant {tenantId}</h2>
      <p>Pendentes: {pendentes.length}</p>
      <p>Falhas definitivas: {falhas.length}</p>
      <ul>
        {solicitacoes.map(s => (
          <li key={s.id}>
            {s.id} — {s.status}
          </li>
        ))}
      </ul>
    </div>
  );
}
```

> **Pergunta:**:

> * (a) O que acontece na tela quando o componente é renderizado pela primeira vez, antes de a requisição completar?
> * (b) Se tenantId mudar de "tenant-a" para "tenant-b" enquanto o componente está na tela, o que acontece? Por quê?
> * (c) Há um problema neste componente que pode causar um bug visual. Você consegue identificar?
> * (Dica: pense no estado loading e o que acontece quando tenantId muda.)

#### Resposta:
* **(a) Primeira renderização:** O usuário verá exclusivamente a mensagem `<p>Carregando...</p>` na tela. Isso ocorre porque o estado `loading` é inicializado como `true` (`useState(true)`), acionando o *Early Return* (retorno antecipado) e impedindo a renderização do resto da interface.
* **(b) Mudança de tenantId:** O React detectará a mudança na dependência do `useEffect` (através do array `[tenantId]`) e executará a função `carregar()` novamente. Isso disparará um novo *fetch* na API buscando os dados referentes ao novo ID.
* **(c) Bug Visual Identificado (*Stale Data*):** Quando o `tenantId` muda e o `useEffect` é disparado novamente, o componente refaz a requisição, mas o estado de `loading` **não é alterado de volta para `true`** antes do *fetch*. Como consequência, o usuário continuará vendo os dados antigos do "tenant-a" na tela enquanto a requisição do "tenant-b" trafega pela rede, gerando uma troca brusca de dados sem *feedback* visual de carregamento. A correção estrutural é adicionar um `setLoading(true)` logo na primeira linha dentro do `useEffect`, antes do `fetch`.