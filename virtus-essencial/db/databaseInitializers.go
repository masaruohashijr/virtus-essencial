package db

import (
	"database/sql"
	"log"
	hd "virtus-essencial/handlers"
)

var db *sql.DB

func Initialize() {
	db = hd.Db
	createSeq()
	createSeqHistoricos()
	createTable()
	createTablesHistoricos()
	createPKey()
	createFeatures()
	createRoles()
	createRoleFeatures()
	createEscritorios()
	createUsers()
	createMembros()
	createStatusZERO()
	createEntidades()
	createPlanos()
	createJurisdicoes()
	updateRoles()
	updateFeatures()
	createFKey()
	createUniqueKey()
	createCicloCompleto()
	//	/* remover 18/01/2021 */
	//	ajustesEmChamados()
	//	ajustesEmTiposNotas()
	//	ajustesEmPerfis()
}

func ajustesEmPerfis() {
	dml := "DELETE FROM features_roles a WHERE a.role_id IN (2,3,4) AND a.feature_id = (SELECT b.id from features b WHERE b.code = 'createEntidade')"
	log.Println(dml)
	db.Exec(dml)
}

func ajustesEmTiposNotas() {
	dml := "DELETE FROM TIPOS_NOTAS_COMPONENTES WHERE " +
		" componente_id = (SELECT ID FROM COMPONENTES WHERE NOME = 'Eficiência Operacional') " +
		" AND tipo_nota_id <> (SELECT ID FROM TIPOS_NOTAS WHERE LETRA = 'A')"
	log.Println(dml)
	db.Exec(dml)
}

func ajustesEmChamados() {
	ddl := "ALTER TABLE CHAMADOS ADD COLUMN IF NOT EXISTS acompanhamento character varying(4000)"
	log.Println(ddl)
	db.Exec(ddl)
}

func createStatusZERO() {
	query := "INSERT INTO status (id, name, stereotype, description, author_id, created_at)" +
		" SELECT 0, '-', '', '', 1, GETDATE() " +
		" WHERE NOT EXISTS (SELECT id FROM status WHERE id = 0)"
	//log.Println(query)
	db.Exec(query)
}
