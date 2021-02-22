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
	dml := "DELETE FROM features_roles a WHERE a.id_role IN (2,3,4) AND a.id_feature = (SELECT b.id_feature from features b WHERE b.code = 'createEntidade')"
	log.Println(dml)
	db.Exec(dml)
}

func ajustesEmTiposNotas() {
	dml := "DELETE FROM TIPOS_NOTAS_COMPONENTES WHERE " +
		" id_componente = (SELECT ID FROM COMPONENTES WHERE NOME = 'Eficiência Operacional') " +
		" AND id_tipo_nota <> (SELECT ID FROM TIPOS_NOTAS WHERE LETRA = 'A')"
	log.Println(dml)
	db.Exec(dml)
}

func ajustesEmChamados() {
	ddl := "ALTER TABLE CHAMADOS ADD COLUMN IF NOT EXISTS acompanhamento character varying(4000)"
	log.Println(ddl)
	db.Exec(ddl)
}

func createStatusZERO() {
	query := "INSERT INTO status (id_status, name, stereotype, description, id_author, created_at)" +
		" SELECT 0, '-', '', '', 1, GETDATE() " +
		" WHERE NOT EXISTS (SELECT id_status FROM status WHERE id = 0)"
	//log.Println(query)
	db.Exec(query)
}
