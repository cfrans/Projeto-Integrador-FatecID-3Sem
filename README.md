<div align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="assets/logo-dark.png" />
    <source media="(prefers-color-scheme: light)" srcset="assets/logo-light.png" />
    <img alt="SINEPE - Sistema Integrado de Educação Personalizada e Especial" src="assets/logo-light.png" width="400">
  </picture>

  <br>

  ![Status](https://img.shields.io/badge/Status-Em_Desenvolvimento-yellow)
  ![Version](https://img.shields.io/badge/Versão-v0.5.0-blue)
  ![License](https://img.shields.io/badge/Licença-MIT-green)
  ![Java](https://img.shields.io/badge/Java-24-orange)

</div>

---

## 🌟 Sobre o Projeto

O **SINEPE** (Sistema Integrado de Educação Personalizada e Especial) é uma aplicação desenvolvida para auxiliar professores, coordenadores e profissionais especializados na gestão, acompanhamento e intervenção do rendimento acadêmico de alunos com necessidades educacionais especiais.

> 🎯 **Objetivo:** Centralizar informações de rendimento, histórico de intervenções, laudos e o Plano de Acompanhamento Individual (PAI) em uma plataforma única e segura.

---

## ✨ Funcionalidades Principais

* 👥 **Cadastro de Usuários:** Gerenciamento completo de perfis com controle de acesso (Professor, Coordenador, Profissional Especializado e Administrador de TI).
* 🎓 **Cadastro de Alunos e Responsáveis:** Inclusão e gestão de dados cadastrais, anexação de laudos médicos e registro de necessidades especiais.
* 📊 **Gestão de Rendimento:** Sistema para lançamento e consulta de notas trimestrais, avaliação de atitude acadêmica e controle de entregas de trabalhos.
* 🧩 **Registro de Intervenções:** Histórico detalhado de todos os apoios pedagógicos e terapêuticos oferecidos ao aluno.
* 📝 **Plano de Acompanhamento Individual (PAI):** Ferramenta para criação, acompanhamento de metas e finalização de planos personalizados.
* 📈 **Relatórios:** Geração de relatórios estratégicos para visualização de desempenho e efetividade das intervenções.

---

## 🛠️ Tecnologias Utilizadas

O SINEPE é construído sobre uma arquitetura robusta utilizando o ecossistema Java moderno.

| Tecnologia | Descrição |
| :--- | :--- |
| **Linguagem** | Java (JDK 24) |
| **Interface** | JavaFX 24 (Scene Builder) |
| **Gerenciador** | Maven |
| **Banco de Dados** | PostgreSQL (Driver 42.7.8) |
| **Migrações (DB)** | Flyway (Versionamento de Banco) |
| **Segurança** | jBCrypt (Hashing de senhas) |
| **Arquitetura** | MVC (Model-View-Controller) |

---

## 🚀 Como Executar o Projeto

Siga os passos abaixo para baixar, configurar e rodar o projeto localmente.

### Pré-requisitos

Certifique-se de ter instalado em sua máquina:
* **Java Development Kit (JDK):** Versão 24 ou superior.
* **Git:** Para versionamento e clonagem.
* **PostgreSQL:** Instância local do banco de dados rodando.

### 1. Clonar o Repositório

Abra seu terminal e rode o comando abaixo:

```bash
# Clone este repositório
git clone [https://github.com/cfrans/Projeto-Integrador-FatecID-3Sem.git](https://github.com/cfrans/Projeto-Integrador-FatecID-3Sem.git)

# Entre na pasta do projeto
cd Projeto-Integrador-FatecID-3Sem
```

### 2. Configurar Banco de Dados

Crie um banco de dados vazio no PostgreSQL chamado sinepe_db.

Renomeie o arquivo `\src\main\resources\database.properties.example` para `\src\main\resources\database.properties` e altere os dados do banco criado

```bash
# database.properties
db.url=jdbc:postgresql://localhost:5432/sinepe_db
db.user=usuario_do_banco
db.password=senha_do_banco
```

Não é necessário rodar scripts SQL manualmente. O projeto utiliza Flyway para gerenciar as migrações. 

Ao iniciar a aplicação (ou rodar o comando maven), as tabelas serão criadas/atualizadas automaticamente.

### 3. Executar a Aplicação
O projeto utiliza o plugin do JavaFX para Maven. Para rodar, execute no terminal dentro da pasta do projeto:

```bash
mvn clean javafx:run
```

<div align="center"> Desenvolvido pelo Grupo 6 - Fatec (2025) </div>
