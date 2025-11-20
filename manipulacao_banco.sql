----------------------------------------------------------- Schema atualizado (19 nov 25)

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

---- Inserts obrigatórios
-- Matérias padrão
INSERT INTO materia (nome) VALUES
    ('Português'),
    ('Redação'),
    ('Matemática'),
    ('Ciências'),
    ('História'),
    ('Geografia'),
    ('Educação física'),
    ('Inglês');

-- Funções padrão
INSERT INTO funcao (nome) VALUES
('Coordenador'),
('Professor'),
('Administrador'),
('T.I.');

-- Tipos de participação padrão
INSERT INTO tipo_participacao (nome) VALUES
('Muito Alta'),
('Alta'),
('Média'),
('Baixa'),
('Nenhuma');

-- Tipos padrão de responsável
INSERT INTO tipo_responsavel (nome) VALUES
('Mãe'),
('Pai'),
('Avó/Avô'),
('Responsável Legal'),
('Outro');
----

-- Séries padrão
-- (a definir)

-------------------------------------------------------- INSERTS PARA TESTE
-- 1. Inserindo Séries/Turmas
INSERT INTO serie_turma (nome) VALUES
('1º Ano A'),
('5º Ano B'),
('3º Ano C');

-- 2. Inserindo Usuários
INSERT INTO usuario (nome, email, id_funcao, senha_hash) VALUES
('Admin Padrão', 'admin@escola.com', 4, 'admin123');    -- ID 1

-- 3. Inserindo Responsáveis (Pais/Mães)
INSERT INTO responsavel (nome, telefone, email, id_tipo_responsavel) VALUES
('Maria da Silva', '(11) 99999-1111', 'maria.silva@email.com', 1), -- Mãe
('Carlos Souza', '(11) 98888-2222', 'carlos.souza@email.com', 2),   -- Pai
('Ana Pereira', '(11) 97777-3333', 'ana.pereira@email.com', 3);     -- Avó

SELECT * FROM aluno st ;

-- 4. Inserindo Alunos (Vinculados aos responsáveis e turmas acima)
INSERT INTO aluno (nome, ra, data_nascimento, id_responsavel, id_serie_turma) VALUES
('Joãozinho da Silva', 'RA2024001', '2015-05-10', 1, 1), -- Filho da Maria, 1º Ano
('Pedrinho Souza', 'RA2024002', '2011-08-20', 2, 2),     -- Filho do Carlos, 5º Ano
('Mariana Pereira', 'RA2024003', '2013-02-15', 3, 3);    -- Neta da Ana, 3º Ano

-- 5. Inserindo Laudos
INSERT INTO laudo (numero, data, descricao, tipo, id_aluno) VALUES
('L-1001', '2024-01-10', 'Diagnóstico de TDAH com predominância em desatenção.', 'Neurológico', 1),
('L-1002', '2023-11-05', 'Acompanhamento fonoaudiológico para dislalia.', 'Fonoaudiológico', 2),
('L-1003', '2024-02-20', 'TEA Nível 1 de suporte.', 'Multidisciplinar', 3);

-- OBS: Na execução do dia 19/11 não estava listando todos esses inserts, apenas um. Analisar o motivo.
-- 6. Inserindo PAI (Plano de Atendimento Individual)
INSERT INTO pai (titulo, descricao, meta, meta2, meta3, recurso_necessario, prazo_revisao, status, id_aluno, id_usuario) VALUES
('Plano de Leitura', 'Foco na alfabetização e reconhecimento de sílabas complexas.', 'Ler 10 palavras novas', 'Ler frases simples', 'Interpretar texto curto', 'Jogos Pedagógicos', '2024-06-30', 'Em Andamento', 1, 1),
('Plano Comportamental', 'Aluno apresenta dificuldade em permanecer sentado.', 'Permanecer 20min sentado', 'Pedir vez para falar', 'Organizar material', 'Cronômetro visual', '2024-05-15', 'Revisão', 2, 1),
('Plano de Socialização', 'Incentivar interação com colegas no recreio.', 'Brincar com 1 colega', 'Participar de grupo', 'Iniciar conversa', 'Mediação do monitor', '2024-07-01', 'Iniciado', 3, 1);

-- 7. Inserindo Intervenções
INSERT INTO intervencao (observacao, titulo, data, id_aluno, id_usuario) VALUES
('Aluno estava muito agitado hoje, realizado exercício de respiração.', 'Agitação em sala', '2024-03-10', 1, 1),
('Conversa com a família sobre a falta de tarefa de casa.', 'Reunião Rápida', '2024-03-12', 2, 1),
('Realizada adaptação da prova de matemática (fonte maior e menos questões).', 'Adaptação Curricular', '2024-03-15', 3, 1);


-- 8. Inserindo Rendimento
INSERT INTO rendimento
(avaliacao1, avaliacao2, simulado, atitude_academica, justificativa_participacao, justificativa_entrega, entrega, id_materia, id_aluno, id_usuario, id_tipo_participacao)
VALUES
-- Aluno 1, Português (id 1), Participação Média (id 3)
(7.5, 8.0, 7.0, 9.0, 'Participa quando estimulado.', 'Entregou no prazo.', 'Sim', 1, 1, 1, 3),

-- Aluno 2, Matemática (id 3), Participação Baixa (id 4)
(5.0, 6.5, 6.0, 6.0, 'Distrai-se com facilidade.', 'Entregou com atraso.', 'Sim', 3, 2, 1, 4),

-- Aluno 3, História (id 5), Participação Alta (id 2)
(9.0, 9.5, 8.5, 10.0, 'Sempre levanta a mão.', 'Trabalho impecável.', 'Sim', 5, 3, 1, 2);

-------------------------------------------------------- Querys úteis

-- Query para limpar o banco mantendo apenas os dados padrão
TRUNCATE TABLE
	aluno,
	intervencao,
	laudo,
	pai,
	rendimento,
	responsavel,
	serie_turma
RESTART IDENTITY CASCADE;