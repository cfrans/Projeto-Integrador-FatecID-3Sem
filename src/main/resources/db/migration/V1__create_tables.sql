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
    senha_hash VARCHAR(100),
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
    meta2 VARCHAR(255) NOT NULL,
    meta3 VARCHAR(255) NOT NULL,
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
    avaliacao1 NUMERIC(3,1) NOT NULL,
    avaliacao2 NUMERIC(3,1) NOT NULL,
    simulado NUMERIC(3,1) NOT NULL,
    atitude_academica NUMERIC(3,1) NOT NULL,
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