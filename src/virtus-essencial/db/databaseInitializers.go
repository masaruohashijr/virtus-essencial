package db

import (
	"database/sql"
	"fmt"
	"log"
	"strconv"
	hd "virtus-essencial/handlers"
)

var db *sql.DB

func Initialize() {
	db = hd.Db
	log.Println("INITIALIZE")
	createPlanosELETROS()
	createPlanosFACHESF()
	createSchema()
	createSeq()
	createSeqHistoricos()
	createTable()
	createTablesHistoricos()
	log.Println("TABELAS CRIADAS")
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
	ajustesEmChamados()
	ajustesEmTiposNotas()
	ajustesEmPerfis()
}

func NewFeature(featureName string, featureCode string) {
	db = hd.Db
	dml := "INSERT INTO virtus.features (name, code, id_author, created_at, id_status) SELECT '" +
		featureName + "', '" + featureCode + "', 1, GETDATE(), 0 " +
		" WHERE NOT EXISTS (SELECT 1 FROM virtus.features WHERE code = '" + featureCode + "')"
	log.Println(dml)
	db.Exec(dml)
}

func NewStatus(statusName string, stereotype string) {
	db = hd.Db
	dml := "INSERT INTO virtus.status (name, stereotype, id_author, created_at, status_id) SELECT '" +
		statusName + "', '" + stereotype + "', 1, GETDATE(), 0 " +
		" WHERE NOT EXISTS (SELECT 1 FROM virtus.status WHERE name = '" + statusName + "')"
	log.Println(dml)
	db.Exec(dml)
}

func IniciarStatusProdutosComponentes(statusInicial string) {
	db = hd.Db
	sqlStatement := "update virtus.produtos_componentes " +
		" set id_status = (SELECT TOP 1 a.id_status " +
		" FROM virtus.status a WHERE UPPER(a.name) = UPPER('" + statusInicial + "'))" +
		" WHERE id_status = 0 OR id_status IS NULL"
	updtForm, err := db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	_, err = updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
	log.Println("UPDATE: " + sqlStatement)

}

func NewWorkflow(workflowName, entityType string) int {
	db = hd.Db
	dml := "INSERT INTO virtus.workflows(name, " +
		"entity_type, " +
		"id_author, created_at) " +
		"OUTPUT INSERTED.id_workflow SELECT '" + workflowName + "', '" + entityType + "', " +
		"1, GETDATE() " +
		"WHERE NOT EXISTS (SELECT 1 FROM virtus.workflows WHERE UPPER(name) = UPPER('" + workflowName + "'))"
	log.Println(dml)
	workflowId := 0
	err := db.QueryRow(dml).Scan(&workflowId)
	if err != nil {
		log.Println(err.Error())
	}
	return workflowId
}

func AddActivity(workflowId int, actionName string, features []string, roles []string) {
	dml := "INSERT INTO virtus.activities(id_workflow, id_action) " +
		" OUTPUT INSERTED.id_activity " +
		" SELECT " + strconv.Itoa(workflowId) + ", " +
		" (SELECT id_action FROM virtus.actions WHERE UPPER(name) = UPPER('" + actionName + "'))" +
		" WHERE NOT EXISTS (SELECT 1 FROM virtus.activities WHERE id_workflow = " + strconv.Itoa(workflowId) +
		" AND id_action = (SELECT id_action FROM virtus.actions WHERE UPPER(name) = UPPER('" + actionName + "')))"
	log.Println(dml)
	activityId := 0
	err := db.QueryRow(dml).Scan(&activityId)
	if err != nil {
		log.Println(err.Error())
	}
	for _, role := range roles {
		dml = "INSERT INTO virtus.activities_roles(id_activity, id_role) SELECT " + strconv.Itoa(activityId) + ", " +
			" (SELECT id_role FROM virtus.roles WHERE UPPER(name) = UPPER('" + role + "'))" +
			" WHERE NOT EXISTS (SELECT 1 FROM virtus.activities_roles WHERE id_activity = " + strconv.Itoa(activityId) +
			" AND id_role = (SELECT id_role FROM virtus.roles WHERE UPPER(name) = UPPER('" + role + "')))"
		log.Println(dml)
		_, err := db.Exec(dml)
		if err != nil {
			log.Println(err.Error())
		}
	}
	for _, feature := range features {
		dml = "INSERT INTO virtus.features_activities(id_activity, id_feature) SELECT " + strconv.Itoa(activityId) + ", " +
			" (SELECT id_feature FROM virtus.features WHERE UPPER(name) = UPPER('" + feature + "'))" +
			" WHERE NOT EXISTS (SELECT 1 FROM virtus.features_activities WHERE id_activity = " + strconv.Itoa(activityId) +
			" AND id_feature = (SELECT id_feature FROM virtus.features WHERE UPPER(name) = UPPER('" + feature + "')))"
		log.Println(dml)
		db.Exec(dml)
		_, err := db.Exec(dml)
		if err != nil {
			log.Println(err.Error())
		}
	}
}

func NewAction(actionName, originStatus, destinationStatus string) {
	db = hd.Db
	dml := "INSERT INTO virtus.actions(name, " +
		" id_origin_status, id_destination_status, other_than, " +
		" description, id_author, created_at) " +
		" OUTPUT INSERTED.id_action SELECT '" + actionName + "', " +
		" (SELECT id_status FROM virtus.status WHERE UPPER(name) = UPPER('" + originStatus + "')), " +
		" (SELECT id_status FROM virtus.status WHERE UPPER(name) = UPPER('" + destinationStatus + "')), " +
		" 0, '', 1, GETDATE() " +
		" WHERE NOT EXISTS (SELECT 1 FROM virtus.actions WHERE UPPER(name) = UPPER('" + actionName + "'))"
	log.Println(dml)
	actionId := 0
	err := db.QueryRow(dml).Scan(&actionId)
	if err != nil {
		log.Println(err.Error())
	}
	dml = "INSERT INTO virtus.actions_status(id_action,id_origin_status,id_destination_status) " +
		" SELECT " + strconv.Itoa(actionId) + ", (SELECT id_status FROM virtus.status WHERE UPPER(name) LIKE UPPER('" + originStatus + "')), " +
		" (SELECT id_status FROM virtus.status WHERE UPPER(name) LIKE UPPER('" + destinationStatus + "')), "
	log.Println(dml)
	db.Exec(dml)
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
	db.Exec(query)
}

func createSchema() {
	query := "IF NOT EXISTS (SELECT 1 FROM sys.schemas WHERE name = 'virtus') " +
		" BEGIN EXEC('CREATE SCHEMA virtus') END"
	log.Println(query)
	_, err := db.Exec(query)
	if err != nil {
		fmt.Println(err.Error())
	}
}

func IniciarProdutoComponenteStatusEmAberto() {
	sqlStatement := "update virtus.produtos_componentes " +
		" SET id_status = (SELECT TOP 1 a.id_status " +
		" FROM virtus.status a WHERE UPPER(a.name) = UPPER('Em Aberto')) " +
		" WHERE id_status = 0 OR id_status IS NULL"
	updtForm, err := db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	_, err = updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
	log.Println("UPDATE: " + sqlStatement)
}
func IniciarProdutoComponenteStatusAguardandoInicio() {
	sqlStatement := "UPDATE virtus.produtos_componentes " +
		" SET id_status = " +
		"  (SELECT TOP 1 a.id_status " +
		"   FROM virtus.status a " +
		"   WHERE UPPER(a.name) = UPPER('Aguardando Início')) " +
		" WHERE id_status = (SELECT TOP 1 a.id_status " +
		"   FROM virtus.status a " +
		"   WHERE UPPER(a.name) = UPPER('Em Aberto')) " +
		" AND inicia_em is not null " +
		" AND termina_em is not null"
	updtForm, err := db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	_, err = updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
	log.Println("UPDATE: " + sqlStatement)
}
