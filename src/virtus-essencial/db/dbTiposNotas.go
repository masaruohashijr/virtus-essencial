package db

import (
	"log"
)

func createTiposNotas() {
	stmtTiposNotas := " INSERT INTO virtus.tipos_notas ( " +
		" nome, descricao, letra, cor_letra, id_author, criado_em, id_status) " +
		" SELECT 'Risco', 'Descrição da Nota de Risco', 'R', 'ED7864', 1, GETDATE(), 0 " +
		" WHERE NOT EXISTS (SELECT id_tipo_nota FROM virtus.tipos_notas WHERE letra = 'R')"
	log.Println(stmtTiposNotas)
	db.Exec(stmtTiposNotas)
	stmtTiposNotas = " INSERT INTO virtus.tipos_notas ( " +
		" nome, descricao, letra, cor_letra, id_author, criado_em, id_status) " +
		" SELECT 'Controle', 'Descrição da Nota de Controle', 'C', 'EDBC64', 1, GETDATE(), 0 " +
		" WHERE NOT EXISTS (SELECT id_tipo_nota FROM virtus.tipos_notas WHERE letra = 'C')"
	log.Println(stmtTiposNotas)
	db.Exec(stmtTiposNotas)
	stmtTiposNotas = " INSERT INTO virtus.tipos_notas ( " +
		" nome, descricao, letra, cor_letra, id_author, criado_em, id_status) " +
		" SELECT 'Avaliação', 'Descrição da Avaliação', 'A', '6495ED', 1, GETDATE(), 0 " +
		" WHERE NOT EXISTS (SELECT id_tipo_nota FROM virtus.tipos_notas WHERE letra = 'A')"
	log.Println(stmtTiposNotas)
	db.Exec(stmtTiposNotas)
}
