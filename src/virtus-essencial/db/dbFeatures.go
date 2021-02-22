package db

import (
	//	"log"
	"strconv"
	"strings"
)

func createFeatures() {
	db.Exec("INSERT INTO features (id, name, code) SELECT 1, 'Listar Workflows', 'listWorkflows' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 1)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 2, 'Criar Workflow', 'createWorkflow' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 2)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 3, 'Listar Elementos', 'listElementos' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 3)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 4, 'Criar Elemento', 'createElemento' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 4)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 5, 'Listar Usuários', 'listUsers' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 5)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 6, 'Criar Usuário', 'createUser' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 6)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 7, 'Listar Perfis', 'listRoles' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 7)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 8, 'Criar Perfil', 'createRole' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 8)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 9, 'Listar Status', 'listStatus' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 9)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 10, 'Criar Status', 'createStatus' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 10)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 11, 'Listar Funcionalidades', 'listFeatures' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 11)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 12, 'Criar Funcionalidade', 'createFeature' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 12)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 13, 'Listar Ações', 'listActions' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 13)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 14, 'Criar Ação', 'createAction' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 14)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 15, 'Criar Item', 'createItem' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 15)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 16, 'Listar Itens', 'listItens' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 16)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 17, 'Listar Componentes', 'listComponentes' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 17)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 18, 'Criar Componente', 'createComponente' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 18)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 19, 'Listar Pilares', 'listPilares' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 19)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 20, 'Criar Pilar', 'createPilar' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 20)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 21, 'Listar Ciclos', 'listCiclos' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 21)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 22, 'Criar Ciclo', 'createCiclo' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 22)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 23, 'Listar Entidades', 'listEntidades' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 23)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 24, 'Criar Entidade', 'createEntidade' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 24)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 25, 'Listar Planos', 'listPlanos' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 25)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 26, 'Criar Plano', 'createPlano' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 26)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 27, 'Listar Escritórios', 'listEscritorios' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 27)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 28, 'Criar Escritório', 'createEscritorio' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 28)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 29, 'Listar Tipos de Notas', 'listTiposNotas' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 29)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 30, 'Criar Tipo de Nota', 'createTipoNota' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 30)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 31, 'Designar Equipes', 'designarEquipes' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 31)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 32, 'Distribuir Atividades', 'distribuirAtividades' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 32)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 33, 'Avaliar Planos', 'avaliarPlanos' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 33)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 34, 'Visualizar Matriz', 'viewMatriz' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 34)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 35, 'Home Chefe', 'homeChefe' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 35)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 36, 'Home Supervisor', 'homeSupervisor' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 36)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 37, 'Home Auditor', 'homeAuditor' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 37)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 38, 'Listar Radares', 'listRadares' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 38)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 39, 'Criar Radar', 'createRadar' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 39)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 40, 'Listar Chamados', 'listChamados' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 40)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 41, 'Criar Chamado', 'createChamado' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 41)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 42, 'Listar Versões', 'listVersoes' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 42)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 43, 'Criar Versão', 'createVersao' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 43)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 44, 'Listar Anotações', 'listAnotacoes' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 44)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 45, 'Criar Anotação', 'createAnotacao' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 45)")
	db.Exec("INSERT INTO features (id, name, code) SELECT 46, 'Visualizar Entidade', 'viewEntidade' WHERE NOT EXISTS (SELECT 1 FROM features WHERE id = 46)")
}

func updateFeatures() {
	query := " UPDATE features SET id_author = 1, created_at = GETDATE(), id_status = 0 " +
		" WHERE id <= 60 AND EXISTS (SELECT 1 FROM features a WHERE a.id_author IS NULL)"
	db.Exec(query)
	//log.Println(query)
}

func createRoleFeatures() {
	stmt1 := " INSERT INTO features_roles (id_role, id_feature) "
	stmt2 := ""
	for j := 1; j <= 60; j++ {
		roleId := "1"
		featureId := strconv.Itoa(j)
		stmt2 = stmt2 + " SELECT " + roleId + ", " + featureId + " WHERE NOT EXISTS (SELECT 1 FROM features_roles WHERE id_feature = " + featureId + " AND id_role = " + roleId + ") UNION "
	}
	pos := strings.LastIndex(stmt2, "UNION")
	stmt2 = stmt2[:pos]
	//log.Println(stmt1 + stmt2)
	db.Exec(stmt1 + stmt2)
	stmt2 = ""
	for j := 1; j <= 60; j++ {
		roleId := "6"
		featureId := strconv.Itoa(j)
		stmt2 = stmt2 + " SELECT " + roleId + ", " + featureId + " WHERE NOT EXISTS (SELECT 1 FROM features_roles WHERE id_feature = " + featureId + " AND id_role = " + roleId + ") UNION "
	}
	pos = strings.LastIndex(stmt2, "UNION")
	stmt2 = stmt2[:pos]
	//	log.Println(stmt1 + stmt2)
	db.Exec(stmt1 + stmt2)
	stmt1 = " INSERT INTO features_roles (id_role, id_feature) " +
		" SELECT 2, a.id FROM features a " +
		" WHERE NOT EXISTS ( " +
		" SELECT 1  " +
		" FROM features_roles b " +
		" WHERE b.id_role = 2 AND b.id_feature = a.id) " +
		" AND a.code IN ('designarEquipes','distribuirAtividades','avaliarPlanos','viewMatriz','listEntidades','viewEntidade','homeSupervisor','homeChefe','homeAuditor','listChamados','createChamado','listAnotacoes','createAnotacao') "
		//	log.Println(stmt1)
	db.Exec(stmt1)
	stmt1 = " INSERT INTO features_roles (id_role, id_feature) " +
		" SELECT 3, a.id FROM features a " +
		" WHERE NOT EXISTS ( " +
		" SELECT 1  " +
		" FROM features_roles b " +
		" WHERE b.id_role = 3 AND b.id_feature = a.id) " +
		" AND a.code IN ('distribuirAtividades','avaliarPlanos','viewMatriz','listEntidades','viewEntidade','homeSupervisor','listChamados','createChamado','listAnotacoes','createAnotacao') "
		//	log.Println(stmt1)
	db.Exec(stmt1)
	stmt1 = " INSERT INTO features_roles (id_role, id_feature) " +
		" SELECT 4, a.id FROM features a " +
		" WHERE NOT EXISTS ( " +
		" SELECT 1  " +
		" FROM features_roles b " +
		" WHERE b.id_role = 4 AND b.id_feature = a.id) " +
		" AND a.code IN ('avaliarPlanos','viewMatriz','listEntidades','viewEntidade','homeAuditor','listChamados','createChamado','listAnotacoes','createAnotacao') "
		//	log.Println(stmt1)
	db.Exec(stmt1)
	stmt1 = " INSERT INTO features_roles (id_role, id_feature) " +
		" SELECT 5, a.id FROM features a " +
		" WHERE NOT EXISTS ( " +
		" SELECT 1  " +
		" FROM features_roles b " +
		" WHERE b.id_role = 5 AND b.id_feature = a.id) " +
		" AND (SUBSTRING(a.code,1,4) = 'list' OR a.code IN ('createChamado','viewEntidade','viewMatriz'))"
	//log.Println(stmt1)
	db.Exec(stmt1)
}
