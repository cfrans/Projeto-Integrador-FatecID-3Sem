-- Insert padrão de primeiro usuário temporário
INSERT INTO usuario (nome, email, id_funcao, senha_hash)
VALUES (
    'Administrador Temporário',
    'admin',
    4,
    '$2a$10$FymtZ62DreocyVN1wqfnCeejYEPtHQ10ryuOvlF7ArmwX7HUFIk9i'
);