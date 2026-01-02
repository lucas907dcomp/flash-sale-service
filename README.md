# ⚡ Flash Sale Inventory Service

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-green)
![Redis](https://img.shields.io/badge/Redis-Distributed_Lock-red)
![AWS](https://img.shields.io/badge/AWS-ECR_%26_EC2-232F3E)

> **High-Performance API** projetada para garantir a integridade de estoque em cenários de alta concorrência (ex: Black Friday), prevenindo o problema de "Overselling" através de Distributed Locks e Virtual Threads.

---

## 🛑 O Problema (Race Condition)
Em sistemas tradicionais, quando milhares de usuários tentam comprar o último item do estoque simultaneamente, ocorre a **Condição de Corrida**:
1. Usuário A lê o estoque (1 unidade).
2. Usuário B lê o estoque (1 unidade) *antes do A terminar*.
3. Ambos finalizam a compra.
4. **Resultado:** O estoque fica negativo (-1) e a empresa vende o que não tem.

---

## 🛡️ A Solução Arquitetural
Este projeto resolve o problema implementando uma estratégia de **Pessimistic Locking Distribuído** utilizando **Redis (Redisson)**.

**Fluxo de Execução:**
1. **Request:** O cliente solicita a compra.
2. **Lock Acquisition:** O sistema tenta adquirir um Lock atômico no Redis (`product-lock-{id}`).
3. **Critical Section:** Se conseguir o lock, uma **única thread** acessa o banco PostgreSQL.
4. **Validation:** Verifica e atualiza o estoque.
5. **Unlock:** Libera o lock para o próximo da fila.
6. **Virtual Threads (Java 21):** Utilizadas para suportar milhares de conexões simultâneas aguardando o lock sem estourar a memória do servidor.

---

## 🚀 Tecnologias & Stack
| Categoria | Tecnologia | Justificativa |
| :--- | :--- | :--- |
| **Lang** | Java 21 | Uso de *Virtual Threads* (Project Loom) para alta taxa de transferência. |
| **Framework** | Spring Boot 3.5 | Produtividade e ecossistema maduro. |
| **Concurrency** | **Redisson** | Implementação robusta de *Distributed Locks* e *Semaphores*. |
| **Database** | PostgreSQL | Consistência transacional (ACID) para a persistência final. |
| **Cache** | Redis | Gerenciamento de Locks e Cache de leitura. |
| **Testing** | k6 | Testes de Carga e Stress Testing. |
| **Infra** | Docker & AWS | Containerização e CI/CD via GitHub Actions e AWS ECR. |

---

## 📊 Resultados do Teste de Carga (Stress Test)
O sistema foi submetido a um teste de estresse simulando um cenário de venda relâmpago.

**Cenário:**
- **Estoque Inicial:** 100 unidades (iPhone 15).
- **Carga:** 500 usuários simultâneos (Ramp-up agressivo).
- **Ferramenta:** k6 + Docker.

**Resultados Oficiais:**
```text
✓ status is 200 (Comprou).............: 100   (100% de Precisão)
✓ status is 409 (Sem Estoque/Lock)....: 2848  (Barrados corretamente)
✗ status is 500 (Erro Crítico)........: 0     (Zero falhas de sistema)
```

**Conclusão:** O mecanismo de lock garantiu **consistência estrita**. O estoque final no banco de dados foi exatamente `0`, sem nenhuma venda duplicada.

---

## 🛠️ Como Rodar Localmente

### Pré-requisitos
- Docker & Docker Compose
- Java 21

### Passo a Passo
1. **Clone o repositório:**
   ```bash
   git clone https://github.com/lucas907dcomp/flash-sale-service.git
   ```

2. **Suba a infraestrutura (Redis + Postgres):**
   ```bash
   docker-compose up -d
   ```

3. **Execute a aplicação:**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Acesse o Dashboard do Redis:**
    - URL: `http://localhost:8081`

---

## ☁️ CI/CD & Deploy
O projeto conta com um pipeline automatizado no **GitHub Actions**:
1. **Build:** Compilação e Testes.
2. **Containerize:** Criação da imagem Docker.
3. **Push:** Upload automático para **AWS ECR (Elastic Container Registry)**.

---
Desenvolvido por **Lucas Aragão**
[GitHub](https://github.com/lucas907dcomp)