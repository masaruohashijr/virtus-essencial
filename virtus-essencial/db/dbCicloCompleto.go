package db

import (
	"fmt"
	"log"
	//"math"
	"math/rand"
	"strconv"
	"strings"
)

type ElementoESI struct {
	Nome  string
	Itens []string
}

type TipoNotaESI struct {
	Nome      string
	Elementos []ElementoESI
}

type ComponenteESI struct {
	Nome       string
	TiposNotas []TipoNotaESI
}

type PilarESI struct {
	Nome        string
	Componentes []ComponenteESI
}

type CicloESI struct {
	Nome    string
	Pilares []PilarESI
}

func createCicloCompleto() {

	//createTiposNotas()
	var tiposSalvos = make(map[string]int)
	var elementosSalvos = make(map[string]int)
	autor := 1
	tipoMedia := 1
	statusZero := 0
	idCiclo := 0
	idPilar := 0
	idComponente := 0
	idTipoNota := 0
	idTipoNotaComponente := 0
	idElemento := 0
	idItem := 0
	pesoPadrao := 1
	cicloESI := montarCiclo()

	stmtElementosComponentes := " INSERT INTO " +
		" elementos_componentes( " +
		" elemento_id, " +
		" componente_id, " +
		" tipo_nota_id, " +
		" peso_padrao, " +
		" author_id, " +
		" criado_em ) %s"

	stmtPilaresCiclos := "INSERT INTO " +
		" pilares_ciclos( " +
		" ciclo_id, " +
		" pilar_id, " +
		" tipo_media, " +
		" peso_padrao, " +
		" author_id, " +
		" criado_em ) %s"

	stmtComponentesPilares := " INSERT INTO " +
		" componentes_pilares( " +
		" pilar_id, " +
		" componente_id, " +
		" tipo_media, " +
		" peso_padrao, " +
		" author_id, " +
		" criado_em ) %s"

	var unsavedPilaresCiclos []string
	var unsavedComponentesPilares []string
	var unsavedElementosComponentes []string
	nome := cicloESI.Nome
	descricao := "Descricao do " + nome
	stmt := " INSERT INTO ciclos(nome, descricao, author_id, criado_em, status_id) OUTPUT INSERTED.ID " +
		" SELECT '" + nome + "', '" + descricao + "', " + strconv.Itoa(autor) + ", GETDATE(), " +
		strconv.Itoa(statusZero) +
		" WHERE NOT EXISTS (SELECT id FROM ciclos WHERE nome = '" + nome + "' )"
	//log.Println(stmt)
	row := db.QueryRow(stmt)
	err := row.Scan(&idCiclo)
	/*if err != nil {
		log.Println(err.Error())
	}*/

	if idCiclo == 0 {
		log.Println("INICIANDO O CICLO COMPLETO")
		return
	}

	pesoPadrao = 100
	max := 100
	qtdPilares := len(cicloESI.Pilares)
	for j := 0; j < qtdPilares; j++ {
		nome = cicloESI.Pilares[j].Nome
		stmt := " INSERT INTO pilares(nome, descricao, author_id, criado_em, status_id) OUTPUT INSERTED.ID " +
			" SELECT ?, ?, ?, GETDATE(), ? WHERE NOT EXISTS (SELECT id FROM pilares WHERE nome = '" + nome + "' )"
		descricao = "Descricao do " + nome
		db.QueryRow(stmt, nome, descricao, autor, statusZero).Scan(&idPilar)
		log.Println("idPilar: " + strconv.Itoa(idPilar) + " - " + nome)
		pesoPadrao = rand.Intn(max)
		if j < qtdPilares-1 {
			max = max - pesoPadrao
		} else {
			pesoPadrao = max
		}
		stmt = " SELECT " + strconv.Itoa(idCiclo) + ", " +
			strconv.Itoa(idPilar) + ", " +
			strconv.Itoa(tipoMedia) + ", " +
			strconv.Itoa(pesoPadrao) + ", " +
			strconv.Itoa(autor) + ", " +
			" GETDATE() " +
			" WHERE NOT EXISTS ( SELECT id FROM pilares_ciclos WHERE ciclo_id = " + strconv.Itoa(idCiclo) + " AND pilar_id = " + strconv.Itoa(idPilar) + " ) "
		unsavedPilaresCiclos = append(unsavedPilaresCiclos, stmt)
		log.Println(stmt)
		qtdComponentes := len(cicloESI.Pilares[j].Componentes)
		for k := 0; k < qtdComponentes; k++ {
			nome = cicloESI.Pilares[j].Componentes[k].Nome
			idComponente = 0
			idElemento = 0
			stmt := " INSERT INTO componentes(nome, descricao, author_id, criado_em, status_id) OUTPUT INSERTED.ID " +
				" SELECT ?, ?, ?, GETDATE(), ? WHERE NOT EXISTS (SELECT id FROM componentes WHERE nome = '" + nome + "' ) "
			descricao = "Descricao do " + nome
			db.QueryRow(stmt, nome, descricao, autor, statusZero).Scan(&idComponente)
			log.Println("idComponente: " + strconv.Itoa(idComponente) + " - " + nome)
			stmt = " SELECT " + strconv.Itoa(idPilar) + ", " +
				strconv.Itoa(idComponente) + ", " +
				strconv.Itoa(tipoMedia) + ", " +
				strconv.Itoa(pesoPadrao) + ", " +
				strconv.Itoa(autor) + ", " +
				" GETDATE() " +
				" WHERE NOT EXISTS ( SELECT id FROM componentes_pilares WHERE componente_id = " + strconv.Itoa(idComponente) + " AND pilar_id = " + strconv.Itoa(idPilar) + " ) "
			unsavedComponentesPilares = append(unsavedComponentesPilares, stmt)

			qtdTiposNotas := len(cicloESI.Pilares[j].Componentes[k].TiposNotas)

			for l := 0; l < qtdTiposNotas; l++ {
				idTipoNota = 0
				nome = cicloESI.Pilares[j].Componentes[k].TiposNotas[l].Nome
				letra := nome[0:1]
				descricao = "Descricao do " + nome
				corletra := "C0D0C0"
				stmt := " INSERT INTO tipos_notas ( " +
					" nome, descricao, letra, cor_letra, author_id, criado_em, status_id) OUTPUT INSERTED.ID " +
					" SELECT  ?, ?, ?, ?, ?, GETDATE(), ? " +
					" WHERE NOT EXISTS (SELECT id FROM tipos_notas WHERE letra = ?) "
				db.QueryRow(stmt, nome, descricao, letra, corletra, autor, statusZero, letra).Scan(&idTipoNota)
				if idTipoNota != 0 {
					tiposSalvos[letra] = idTipoNota
				} else {
					idTipoNota = tiposSalvos[letra]
				}
				log.Println("idTipoNota: " + strconv.Itoa(idTipoNota) + " - " + nome)

				stmt = " INSERT INTO tipos_notas_componentes(componente_id, tipo_nota_id, author_id, criado_em, status_id) OUTPUT INSERTED.ID " +
					" SELECT ?, ?, ?, GETDATE(), ? WHERE NOT EXISTS (SELECT id FROM tipos_notas_componentes WHERE componente_id = ? " +
					" AND tipo_nota_id = ? ) "
				db.QueryRow(stmt, idComponente, idTipoNota, autor, statusZero, idComponente, idTipoNota).Scan(&idTipoNotaComponente)
				log.Println("idTipoNotaComponente: " + strconv.Itoa(idTipoNotaComponente))

				qtdElementos := len(cicloESI.Pilares[j].Componentes[k].TiposNotas[l].Elementos)
				for m := 0; m < qtdElementos; m++ {
					nome = cicloESI.Pilares[j].Componentes[k].TiposNotas[l].Elementos[m].Nome
					stmt := " INSERT INTO elementos(nome, descricao, author_id, criado_em, status_id) OUTPUT INSERTED.ID " +
						" SELECT ?, ?, ?, GETDATE(), ? "
					descricao = "Descricao do " + nome
					db.QueryRow(stmt, nome, descricao, autor, statusZero).Scan(&idElemento)
					if idElemento != 0 {
						elementosSalvos[nome] = idElemento
					} else {
						idElemento = elementosSalvos[nome]
					}
					log.Println("idElemento: " + strconv.Itoa(idElemento) + " - " + nome)
					pesoPadrao = 1
					stmt = " SELECT " + strconv.Itoa(idElemento) + ", " +
						strconv.Itoa(idComponente) + ", " +
						strconv.Itoa(idTipoNota) + ", " +
						strconv.Itoa(pesoPadrao) + ", " +
						strconv.Itoa(autor) + ", " +
						" GETDATE() " +
						" WHERE NOT EXISTS ( SELECT id FROM elementos_componentes WHERE elemento_id = " +
						strconv.Itoa(idElemento) + " AND componente_id = " +
						strconv.Itoa(idComponente) + " AND tipo_nota_id = " + strconv.Itoa(idTipoNota) + " ) "
					log.Println(stmt)
					unsavedElementosComponentes = append(unsavedElementosComponentes, stmt)
					qtdItens := len(cicloESI.Pilares[j].Componentes[k].TiposNotas[l].Elementos[m].Itens)
					for n := 0; n < qtdItens; n++ {
						nome = cicloESI.Pilares[j].Componentes[k].TiposNotas[l].Elementos[m].Itens[n]
						stmt := " INSERT INTO itens(elemento_id, nome, descricao, author_id, criado_em, status_id) OUTPUT INSERTED.ID " +
							" SELECT ?, ?, ?, ?, GETDATE(), ? WHERE NOT EXISTS (SELECT id FROM itens WHERE nome = '" + nome + "' and elemento_id = " + strconv.Itoa(idElemento) + " )"
						descricao = "Descricao do " + nome
						db.QueryRow(stmt, idElemento, nome, descricao, autor, statusZero).Scan(&idItem)
						log.Println("idItem: " + strconv.Itoa(idItem) + " - " + nome)
					}
				}
			}
		}
		BulkInsert(unsavedElementosComponentes, stmtElementosComponentes)
		BulkInsert(unsavedComponentesPilares, stmtComponentesPilares)
		BulkInsert(unsavedPilaresCiclos, stmtPilaresCiclos)
	}
	stmt = " WITH TMP AS " +
		"         (SELECT componente_id, " +
		"                 SUM(peso_padrao) AS total " +
		"          FROM elementos_componentes a " +
		"          WHERE componente_id = a.componente_id " +
		"          GROUP BY componente_id), " +
		" TMP2 AS (SELECT tmp.componente_id AS componente_id, " +
		"     a.tipo_nota_id, " +
		"     tmp.total AS total, " +
		"     sum(a.peso_padrao) AS peso_padrao " +
		"       FROM elementos_componentes a " +
		"       LEFT JOIN TMP ON a.componente_id = TMP.componente_id " +
		"       GROUP BY tmp.componente_id, " +
		"                a.tipo_nota_id, " +
		"                tmp.total), " +
		" R2 AS (SELECT a.componente_id, a.tipo_nota_id, " +
		" a.peso_padrao*100/b.total as peso_padrao_percentual FROM TMP2 a LEFT JOIN TMP b ON a.componente_id = b.componente_id) " +
		" UPDATE tipos_notas_componentes  " +
		" SET peso_padrao = R2.peso_padrao_percentual " +
		" FROM R2 " +
		" WHERE tipos_notas_componentes.componente_id = R2.componente_id " +
		"   AND tipos_notas_componentes.tipo_nota_id = R2.tipo_nota_id "
	log.Println("stmt UPDATE tipos notas componentes: " + stmt)
	updtForm, err := db.Prepare(stmt)
	if err != nil {
		log.Println(err.Error())
	}
	_, err = updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
}

func BulkInsert(unsaved []string, pStmt string) {
	stmt := fmt.Sprintf(pStmt, strings.Join(unsaved, " UNION "))
	log.Println(stmt)
	db.Exec(stmt)
}

func montarCiclo() CicloESI {
	var cicloESI CicloESI
	var pilarRC PilarESI
	var pilarG PilarESI
	var pilarEF PilarESI
	var pilares []PilarESI
	var componentes []ComponenteESI

	pilarRC.Nome = "Riscos e Controles"
	componenteRiscoDeCredito := initRiscoDeCredito()
	componenteRiscoDeMercado := initRiscoDeMercado()
	componenteRiscoDeLiquidez := initRiscoDeLiquidez()
	componenteRiscoAtuarial := initRiscoAtuarial()
	componentes = append(componentes, componenteRiscoDeCredito, componenteRiscoDeMercado, componenteRiscoDeLiquidez, componenteRiscoAtuarial)
	pilarRC.Componentes = componentes

	pilarG.Nome = "Governança"
	componente := initGovernanca()
	componentes = make([]ComponenteESI, 0)
	componentes = append(componentes, componente)
	pilarG.Componentes = componentes

	pilarEF.Nome = "Econômico-Financeiro"
	componenteSolvencia := initEconomicoFinanceiro("Solvência")
	componenteInvestimentosAtivos := initEconomicoFinanceiro("Investimentos/Ativos")
	componenteAtuarial := initEconomicoFinanceiro("Atuarial")
	componenteResultados := initEconomicoFinanceiro("Resultados")
	componenteEficienciaOperacional := initEconomicoFinanceiro("Eficiência Operacional")
	componentes = make([]ComponenteESI, 0)
	componentes = append(componentes, componenteSolvencia, componenteInvestimentosAtivos, componenteAtuarial, componenteResultados, componenteEficienciaOperacional)
	pilarEF.Componentes = componentes
	pilares = append(pilares, pilarRC, pilarG, pilarEF)
	cicloESI.Nome = "Ciclo Anual 2021"
	cicloESI.Pilares = pilares
	return cicloESI
}
