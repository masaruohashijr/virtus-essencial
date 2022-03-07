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
	//createPlanosELETROS()
	//createPlanosFACHESF()
	log.Println("INITIALIZE")
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
	log.Println(query)
	_, err := db.Exec(query)
	if err != nil {
		fmt.Println(err.Error())
	}
}

type MigrarCiclo struct {
	Sigla        string
	cicloOrigem  int
	cicloDestino int
}

func loadMigrarCiclos() []*MigrarCiclo {
	migrarCiclos := []*MigrarCiclo{}
	MigrarCiclo1 := &MigrarCiclo{"BANESPREV", 1, 2}
	MigrarCiclo2 := &MigrarCiclo{"SP-PREVCOM", 1, 2}
	MigrarCiclo3 := &MigrarCiclo{"VIVEST", 3, 2}
	MigrarCiclo4 := &MigrarCiclo{"VALIA", 3, 2}
	MigrarCiclo5 := &MigrarCiclo{"SISTEL", 3, 2}
	MigrarCiclo6 := &MigrarCiclo{"FUNDACAO COPEL", 3, 2}
	MigrarCiclo7 := &MigrarCiclo{"FATL", 3, 2}
	migrarCiclos = append(migrarCiclos, MigrarCiclo1)
	migrarCiclos = append(migrarCiclos, MigrarCiclo2)
	migrarCiclos = append(migrarCiclos, MigrarCiclo3)
	migrarCiclos = append(migrarCiclos, MigrarCiclo4)
	migrarCiclos = append(migrarCiclos, MigrarCiclo5)
	migrarCiclos = append(migrarCiclos, MigrarCiclo6)
	migrarCiclos = append(migrarCiclos, MigrarCiclo7)
	return migrarCiclos
}

func getIdEntidade(m *MigrarCiclo) (int, error) {
	sql := "SELECT ID_ENTIDADE FROM VIRTUS.ENTIDADES WHERE SIGLA = ?"
	var idEntidade int
	log.Println(sql)
	row := db.QueryRow(sql, m.Sigla)
	err := row.Scan(&idEntidade)
	return idEntidade, err
}

func MigracaoCiclos() error {
	db = hd.Db
	var migracoes []*MigrarCiclo = loadMigrarCiclos()
	for _, migracao := range migracoes {
		idEntidade, err := getIdEntidade(migracao)
		if err != nil {
			return err
		}
		m := *migracao
		updateProdutos(idEntidade, m.cicloOrigem, m.cicloDestino)
	}
	return nil
}

func updateProdutos(idEntidade, idCicloOrigem, idCicloDestino int) {
	tabelas := []string{
		"ciclos_entidades",
		"produtos_ciclos",
		"produtos_pilares",
		"produtos_componentes",
		"produtos_tipos_notas",
		"produtos_planos",
		"produtos_elementos",
		"produtos_itens",
		"produtos_ciclos_historicos",
		"produtos_pilares_historicos",
		"produtos_componentes_historicos",
		"produtos_elementos_historicos",
		"produtos_itens_historicos",
		"integrantes",
		"anotacoes",
	}
	entidade := strconv.Itoa(idEntidade)
	cicloOrigem := strconv.Itoa(idCicloOrigem)
	cicloDestino := strconv.Itoa(idCicloDestino)

	for _, t := range tabelas {
		stmt := "UPDATE virtus." + t + " SET id_ciclo = " + cicloDestino + " WHERE id_entidade = " + entidade + " AND id_ciclo = " + cicloOrigem
		db.Exec(stmt)
		println(stmt)
	}
}
