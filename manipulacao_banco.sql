--------------------------------------- CÓDIGO ORIGINAL DO PEDRO
CREATE TABLE coordenador (
    id_coordenador SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    segmento VARCHAR(255) NOT NULL
);

CREATE TABLE PAI (
    id_PAI SERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    meta VARCHAR(255) NOT NULL,
    recurso_necessario VARCHAR(255) NOT NULL,
    prazo_revisao DATE NOT NULL,
    data DATE NOT NULL,
    status VARCHAR(255) NOT NULL,
    id_coordenador INT REFERENCES coordenador(id_coordenador)
);

CREATE TABLE professor (
    id_professor SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    sala VARCHAR(255) NOT NULL
);

CREATE TABLE responsavel (
    id_responsavel SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    parentesco VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    telefone VARCHAR(255) NOT NULL
);

CREATE TABLE aluno (
    id_aluno SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    data_nascimento DATE NOT NULL,
    serie_turma VARCHAR(255) NOT NULL,
    RA INT UNIQUE NOT NULL,
    id_professor INT REFERENCES professor(id_professor),
    id_responsavel INT REFERENCES responsavel(id_responsavel),
    idPAI INT REFERENCES PAI(id_PAI),
    idcoordenador INT REFERENCES coordenador(id_coordenador)
);

CREATE TABLE disciplina (
    id_disciplina SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    id_professor INT REFERENCES professor(id_professor)
);

CREATE TABLE rendimento (
    id_rendimento SERIAL PRIMARY KEY,
    avaliacao_1 INT NOT NULL,
    avaliacao_2 INT NOT NULL,
    trimestre INT NOT NULL,
    consideracoes VARCHAR(255) NOT NULL,
    simulado INT NOT NULL,
    atitude_academica INT NOT NULL,
    id_disciplina INT REFERENCES disciplina(id_disciplina),
    id_aluno INT REFERENCES aluno(id_aluno)
);

CREATE TABLE profissional_especializado (
    id_profissional_especializado SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cargo VARCHAR(255) NOT NULL,
    telefone VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

CREATE TABLE laudo (
    id_laudo SERIAL PRIMARY KEY,
    numero INT UNIQUE NOT NULL,
    data DATE,
    descricao VARCHAR(255) NOT NULL,
    tipo VARCHAR(255) NOT NULL,
    id_aluno INT REFERENCES aluno(id_aluno),
    id_profissional_especializado INT REFERENCES profissional_especializado(id_profissional_especializado)
);

CREATE TABLE intervencao (
    id_intervencao SERIAL PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    tipo VARCHAR(255) NOT NULL,
    data DATE NOT NULL,
    id_aluno INT REFERENCES aluno(id_aluno),
    id_profissional_especializado INT REFERENCES profissional_especializado(id_profissional_especializado),
    id_professor INT REFERENCES professor(id_professor)
);

------------------------ FIM DO CÓDIGO DO PEDRO
--------ABAIXO TEMOS A CRIAÇAO DOS LOGINS, COM INSERTS DE EXEMPLO PARA USARMOS POR HORA

CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nome_acesso VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL, -- Depois pesquisar sobre 'bcrypt' para salvar isso
    funcao VARCHAR(50) NOT NULL
);

-- Inserindo usuários de exemplo (senha '123' por enquanto)
INSERT INTO usuario (nome_acesso, email, funcao, senha_hash) VALUES
('Coordenadora Ana', 'ana@escola.com', 'Coordenador', '123'),
('Professora Bia', 'bia@escola.com', 'Professor', '123'),
('Admin', 'admin@escola.com', 'Administrador', 'admin');

-------------------------------------------------------------------------------------------
------------------------ALGUNS INSERTS PARA SIMULARMOS O RESTANTE, ESSES AINDA NAO TEM CRUD
-------------------------------------------------------------------------------------------

-- COORDENADOR (Para a tela de PAI)
INSERT INTO coordenador (nome, segmento) VALUES
('Ana Maria Braga', 'Ensino Fundamental I'),
('Carlos Alberto Nobrega', 'Ensino Fundamental II'),
('Silvio Santos', 'Ensino Médio');

-- PROFESSOR (Para as telas de Aluno, Intervenção, Disciplina)
INSERT INTO professor (nome, sala) VALUES
('Beatriz Souza', 'Sala 101'),
('Marcos Silva', 'Sala 205'),
('Juliana Pereira', 'Sala 302');

-- PROFISSIONAL ESPECIALIZADO (Para a tela de Cadastro/Laudo)
INSERT INTO profissional_especializado (nome, cargo, telefone, email) VALUES
('Dr. Drauzio Varella', 'Neurologista', '11999998888', 'drauzio@med.com'),
('Dr. Roberto Almeida', 'Psicopedagogo', '19988887777', 'roberto@psico.com'),
('Dra. Carla Antunes', 'Fonoaudióloga', '21977776666', 'carla@fono.com');

INSERT INTO disciplina (nome, id_professor) VALUES
('Matemática', 1), -- (Professora Beatriz)
('Português', 2),  -- (Professor Marcos)
('História', 2),    -- (Professor Marcos)
('Ciências', 3);  -- (Professora Juliana)

------------------------------ SELECTS DE TESTE
SELECT * FROM aluno;
SELECT * FROM coordenador;
SELECT * FROM disciplina;
SELECT * FROM intervencao;
SELECT * FROM laudo;
SELECT * FROM pai;
SELECT * FROM professor;
SELECT * FROM profissional_especializado;
SELECT * FROM rendimento;
SELECT * FROM responsavel;


----------------------------------------------------------- novo banco

-- Tabela: funcao
CREATE TABLE funcao (
    id_funcao SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

-- Tabela: usuario
CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    id_funcao INT NOT NULL,
    CONSTRAINT fk_usuario_funcao
        FOREIGN KEY (id_funcao)
        REFERENCES funcao (id_funcao)
);

CREATE TABLE tipo_responsavel (
    id_tipo_responsavel SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

-- Tabela: responsavel
CREATE TABLE responsavel (
    id_responsavel SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    id_tipo_responsavel INT NOT NULL,
    CONSTRAINT fk_tipo_responsavel
        FOREIGN KEY (id_tipo_responsavel)
        REFERENCES tipo_responsavel (id_tipo_responsavel)
);

CREATE TABLE serie_turma (
    id_serie_turma SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

-- Tabela: aluno
CREATE TABLE aluno (
    id_aluno SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    ra VARCHAR(20) UNIQUE,
    data_nascimento DATE,
    id_responsavel INT NOT NULL,
    CONSTRAINT fk_aluno_responsavel
        FOREIGN KEY (id_responsavel)
        REFERENCES responsavel (id_responsavel),
    id_serie_turma INT NOT NULL,
    CONSTRAINT fk_serie_turma
        FOREIGN KEY (id_serie_turma)
        REFERENCES serie_turma (id_serie_turma)
);

-- Tabela: PAI
CREATE TABLE pai (
    id_pai SERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descricao TEXT,
    meta VARCHAR(100) NOT NULL,
    recurso_necessario VARCHAR(100) NOT NULL,
    prazo_revisao DATE NOT NULL,
    status VARCHAR(50),
    id_aluno INT NOT NULL,
    id_usuario INT NOT NULL,
    CONSTRAINT fk_pai_aluno
        FOREIGN KEY (id_aluno)
        REFERENCES aluno (id_aluno),
    CONSTRAINT fk_pai_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
);

-- Tabela: laudo
CREATE TABLE laudo (
    id_laudo SERIAL PRIMARY KEY,
    numero VARCHAR(50) NOT NULL,
    data DATE NOT NULL,
    descricao TEXT,
    tipo VARCHAR(100) NOT NULL,
    id_aluno INT NOT NULL,
    CONSTRAINT fk_laudo_aluno
        FOREIGN KEY (id_aluno)
        REFERENCES aluno (id_aluno)
);

-- Tabela: intervencao
CREATE TABLE intervencao (
    id_intervencao SERIAL PRIMARY KEY,
    observacao TEXT NOT NULL,
    titulo VARCHAR(100) NOT NULL,
    data DATE NOT NULL,
    id_aluno INT NOT NULL,
    id_usuario INT NOT NULL,
    CONSTRAINT fk_intervencao_aluno
        FOREIGN KEY (id_aluno)
        REFERENCES aluno (id_aluno),
    CONSTRAINT fk_intervencao_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
);

-- Tabela: materia
CREATE TABLE materia (
    id_materia SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);


CREATE TABLE tipo_participacao (
    id_tipo_participacao SERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL
);


-- Tabela: rendimento
CREATE TABLE rendimento (
    id_rendimento SERIAL PRIMARY KEY,
    avaliacao1 NUMERIC(2,1) NOT NULL,
    avaliacao2 NUMERIC(2,1) NOT NULL,
    simulado NUMERIC(2,1) NOT NULL,
    atitude_academica NUMERIC(2,1) NOT NULL,
    data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    justificativa_participacao TEXT,
    justificativa_entrega TEXT,
    entrega VARCHAR(50) NOT NULL,
    id_materia INT NOT NULL,
    id_aluno INT NOT NULL,
    id_usuario INT NOT NULL,
    id_tipo_participacao INT NOT NULL,
    CONSTRAINT fk_rendimento_materia
        FOREIGN KEY (id_materia)
        REFERENCES materia (id_materia),
    CONSTRAINT fk_rendimento_aluno
        FOREIGN KEY (id_aluno)
        REFERENCES aluno (id_aluno),
    CONSTRAINT fk_rendimento_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario),
    CONSTRAINT fk_rendimento_tipo_participacao
        FOREIGN KEY (id_tipo_participacao)
        REFERENCES tipo_participacao (id_tipo_participacao)
);