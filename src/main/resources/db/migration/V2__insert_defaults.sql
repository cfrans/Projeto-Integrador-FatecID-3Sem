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
('T.I.'),
('Profissional Especializado');

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
INSERT INTO serie_turma (nome) VALUES
('5º Ano'),
('6º Ano'),
('7º Ano'),
('8º Ano');