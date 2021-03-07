package db

import (
	"database/sql"
	"log"
	hd "virtus-essencial/handlers"
)

var db *sql.DB

func Initialize() {
	db = hd.Db
	createSchema()
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
	cicloAux := montarCicloAnual()
	createCicloCompleto(cicloAux)
	cicloAux2 := montarCicloBienal()
	createCicloCompleto(cicloAux2)
	cicloAux3 := montarCicloTrienal()
	createCicloCompleto(cicloAux3)
	//	/* remover 18/01/2021 */
	//	ajustesEmChamados()
	//	ajustesEmTiposNotas()
	//	ajustesEmPerfis()
}

func ajustesEmPerfis() {
	dml := "DELETE FROM virtus.features_roles a WHERE a.id_role IN (2,3,4) AND a.id_feature = (SELECT b.id_feature FROM virtus.features b WHERE b.code = 'createEntidade')"
	log.Println(dml)
	db.Exec(dml)
}

func ajustesEmTiposNotas() {
	dml := "DELETE FROM virtus.TIPOS_NOTAS_COMPONENTES WHERE " +
		" id_componente = (SELECT id_componente FROM virtus.COMPONENTES WHERE NOME = 'Eficiência Operacional') " +
		" AND id_tipo_nota <> (SELECT id_tipo_nota FROM virtus.TIPOS_NOTAS WHERE LETRA = 'A')"
	log.Println(dml)
	db.Exec(dml)
}

func ajustesEmChamados() {
	ddl := "ALTER TABLE virtus.CHAMADOS ADD COLUMN IF NOT EXISTS acompanhamento character varying(4000)"
	log.Println(ddl)
	db.Exec(ddl)
}

func createStatusZERO() {
	query := "INSERT INTO virtus.status (id_status, name, stereotype, description, id_author, created_at)" +
		" SELECT 0, '-', '', '', 1, GETDATE() " +
		" WHERE NOT EXISTS (SELECT id_status FROM virtus.status WHERE id_status = 0)"
	//log.Println(query)
	db.Exec(query)
}

func createSchema() {
	query := "IF NOT EXISTS (SELECT 1 FROM sys.schemas WHERE name = 'virtus') " +
		" BEGIN EXEC('CREATE SCHEMA virtus') END"
	//log.Println(query)
	db.Exec(query)
}
