package db

import (
	"fmt"
	"log"
	//"math"
	"math/rand"
	"strconv"
	"strings"
)

type ItemAux struct {
	Nome      string
	Descricao string
}

type ElementoAux struct {
	Nome      string
	Descricao string
	Itens     []ItemAux
}

type TipoNotaAux struct {
	Nome      string
	Descricao string
	Elementos []ElementoAux
}

type ComponenteAux struct {
	Nome       string
	Descricao  string
	TiposNotas []TipoNotaAux
}

type PilarAux struct {
	Nome        string
	Descricao   string
	Componentes []ComponenteAux
}

type CicloAux struct {
	Nome      string
	Descricao string
	Pilares   []PilarAux
}

func createCicloCompleto(cicloAux CicloAux) {

	//createTiposNotas()
	//	var tiposSalvos = make(map[string]int)
	//	var elementosSalvos = make(map[string]int)
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

	stmtElementosComponentes := " INSERT INTO " +
		" virtus.elementos_componentes( " +
		" id_elemento, " +
		" id_componente, " +
		" id_tipo_nota, " +
		" peso_padrao, " +
		" id_author, " +
		" criado_em ) %s"

	stmtPilaresCiclos := "INSERT INTO " +
		" virtus.pilares_ciclos( " +
		" id_ciclo, " +
		" id_pilar, " +
		" tipo_media, " +
		" peso_padrao, " +
		" id_author, " +
		" criado_em ) %s"

	stmtComponentesPilares := " INSERT INTO " +
		" virtus.componentes_pilares( " +
		" id_pilar, " +
		" id_componente, " +
		" tipo_media, " +
		" peso_padrao, " +
		" id_author, " +
		" criado_em ) %s"

	var unsavedPilaresCiclos []string
	var unsavedComponentesPilares []string
	var unsavedElementosComponentes []string
	nome := cicloAux.Nome
	descricao := cicloAux.Descricao
	stmt := " INSERT INTO virtus.ciclos(nome, descricao, id_author, criado_em, id_status) " +
		" OUTPUT INSERTED.id_ciclo " +
		" SELECT '" + nome + "', '" + descricao + "', " + strconv.Itoa(autor) + ", GETDATE(), " +
		strconv.Itoa(statusZero) +
		" WHERE NOT EXISTS (SELECT id_ciclo FROM virtus.ciclos WHERE nome = '" + nome + "' )"
	row := db.QueryRow(stmt)
	err := row.Scan(&idCiclo)
	if idCiclo == 0 {
		db.QueryRow("SELECT id_ciclo FROM virtus.ciclos WHERE nome = '" + nome + "' ").Scan(&idCiclo)
	}

	log.Println("***************************************************")
	log.Println("INICIANDO O CICLO " + strconv.Itoa(idCiclo))
	log.Println("***************************************************")

	pesoPadrao = 100
	max := 100
	qtdPilares := len(cicloAux.Pilares)
	for j := 0; j < qtdPilares; j++ {
		idPilar = 0
		nome = cicloAux.Pilares[j].Nome
		stmt := " INSERT INTO virtus.pilares(nome, descricao, id_author, criado_em, id_status) OUTPUT INSERTED.id_pilar " +
			" SELECT ?, ?, ?, GETDATE(), ? WHERE NOT EXISTS (SELECT id_pilar FROM virtus.pilares WHERE nome = '" + nome + "' )"
		descricao = cicloAux.Pilares[j].Descricao
		db.QueryRow(stmt, nome, descricao, autor, statusZero).Scan(&idPilar)
		if idPilar == 0 {
			sql := "SELECT id_pilar FROM virtus.pilares WHERE nome = '" + nome + "' "
			log.Println(sql)
			db.QueryRow(sql).Scan(&idPilar)
		}
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
			" WHERE NOT EXISTS ( SELECT id_pilar_ciclo FROM virtus.pilares_ciclos WHERE id_ciclo = " + strconv.Itoa(idCiclo) + " AND id_pilar = " + strconv.Itoa(idPilar) + " ) "
		unsavedPilaresCiclos = append(unsavedPilaresCiclos, stmt)
		log.Println(stmt)
		qtdComponentes := len(cicloAux.Pilares[j].Componentes)
		for k := 0; k < qtdComponentes; k++ {
			nome = cicloAux.Pilares[j].Componentes[k].Nome
			idComponente = 0
			idElemento = 0
			stmt := " INSERT INTO virtus.componentes(nome, descricao, id_author, criado_em, id_status) OUTPUT INSERTED.id_componente " +
				" SELECT ?, ?, ?, GETDATE(), ? WHERE NOT EXISTS (SELECT id_componente FROM virtus.componentes WHERE nome = '" + nome + "' ) "
			descricao = cicloAux.Pilares[j].Componentes[k].Descricao
			db.QueryRow(stmt, nome, descricao, autor, statusZero).Scan(&idComponente)
			if idComponente == 0 {
				sql := "SELECT id_componente FROM virtus.componentes WHERE nome = '" + nome + "' "
				log.Println(sql)
				db.QueryRow(sql).Scan(&idComponente)
			}
			log.Println("idComponente: " + strconv.Itoa(idComponente) + " - " + nome)
			stmt = " SELECT " + strconv.Itoa(idPilar) + ", " +
				strconv.Itoa(idComponente) + ", " +
				strconv.Itoa(tipoMedia) + ", " +
				strconv.Itoa(pesoPadrao) + ", " +
				strconv.Itoa(autor) + ", " +
				" GETDATE() " +
				" WHERE NOT EXISTS ( SELECT id_componente_pilar FROM virtus.componentes_pilares WHERE id_componente = " + strconv.Itoa(idComponente) + " AND id_pilar = " + strconv.Itoa(idPilar) + " ) "
			unsavedComponentesPilares = append(unsavedComponentesPilares, stmt)

			qtdTiposNotas := len(cicloAux.Pilares[j].Componentes[k].TiposNotas)

			for l := 0; l < qtdTiposNotas; l++ {
				idTipoNota = 0
				nome = cicloAux.Pilares[j].Componentes[k].TiposNotas[l].Nome
				letra := nome[0:1]
				descricao = cicloAux.Pilares[j].Componentes[k].TiposNotas[l].Descricao
				corletra := "C0D0C0"
				stmt := " INSERT INTO virtus.tipos_notas ( " +
					" nome, descricao, letra, cor_letra, id_author, criado_em, id_status) OUTPUT INSERTED.id_tipo_nota " +
					" SELECT  ?, ?, ?, ?, ?, GETDATE(), ? " +
					" WHERE NOT EXISTS (SELECT id_tipo_nota FROM virtus.tipos_notas WHERE letra = ?) "
				db.QueryRow(stmt, nome, descricao, letra, corletra, autor, statusZero, letra).Scan(&idTipoNota)
				if idTipoNota == 0 {
					sql := "SELECT id_tipo_nota FROM virtus.tipos_notas WHERE letra = '" + letra + "' "
					log.Println(sql)
					db.QueryRow(sql).Scan(&idTipoNota)
				}
				log.Println("idTipoNota: " + strconv.Itoa(idTipoNota) + " - " + nome)

				stmt = " INSERT INTO virtus.tipos_notas_componentes(id_componente, id_tipo_nota, id_author, criado_em, id_status) OUTPUT INSERTED.id_tipo_nota_componente " +
					" SELECT ?, ?, ?, GETDATE(), ? WHERE NOT EXISTS (SELECT id_tipo_nota FROM virtus.tipos_notas_componentes WHERE id_componente = ? " +
					" AND id_tipo_nota = ? ) "
				db.QueryRow(stmt, idComponente, idTipoNota, autor, statusZero, idComponente, idTipoNota).Scan(&idTipoNotaComponente)
				log.Println("idTipoNotaComponente: " + strconv.Itoa(idTipoNotaComponente))

				qtdElementos := len(cicloAux.Pilares[j].Componentes[k].TiposNotas[l].Elementos)
				for m := 0; m < qtdElementos; m++ {
					idElemento = 0
					nome = cicloAux.Pilares[j].Componentes[k].TiposNotas[l].Elementos[m].Nome
					stmt := " INSERT INTO virtus.elementos(nome, descricao, id_author, criado_em, id_status) OUTPUT INSERTED.id_elemento " +
						" SELECT ?, ?, ?, GETDATE(), ?  WHERE NOT EXISTS (SELECT id_elemento FROM virtus.elementos WHERE nome = '" + nome + "' ) "
					descricao = cicloAux.Pilares[j].Componentes[k].TiposNotas[l].Elementos[m].Descricao
					db.QueryRow(stmt, nome, descricao, autor, statusZero).Scan(&idElemento)
					if idElemento == 0 {
						sql := "SELECT id_elemento FROM virtus.elementos WHERE nome = '" + nome + "' "
						log.Println(sql)
						db.QueryRow(sql).Scan(&idElemento)
					}
					log.Println("idElemento: " + strconv.Itoa(idElemento) + " - " + nome)
					pesoPadrao = 1
					stmt = " SELECT " + strconv.Itoa(idElemento) + ", " +
						strconv.Itoa(idComponente) + ", " +
						strconv.Itoa(idTipoNota) + ", " +
						strconv.Itoa(pesoPadrao) + ", " +
						strconv.Itoa(autor) + ", " +
						" GETDATE() " +
						" WHERE NOT EXISTS ( SELECT id_elemento_componente FROM virtus.elementos_componentes WHERE id_elemento = " +
						strconv.Itoa(idElemento) + " AND id_componente = " +
						strconv.Itoa(idComponente) + " AND id_tipo_nota = " + strconv.Itoa(idTipoNota) + " ) "
					log.Println(stmt)
					unsavedElementosComponentes = append(unsavedElementosComponentes, stmt)
					qtdItens := len(cicloAux.Pilares[j].Componentes[k].TiposNotas[l].Elementos[m].Itens)
					for n := 0; n < qtdItens; n++ {
						idItem = 0
						nome = cicloAux.Pilares[j].Componentes[k].TiposNotas[l].Elementos[m].Itens[n].Nome
						stmt := " INSERT INTO virtus.itens(id_elemento, nome, descricao, id_author, criado_em, id_status) OUTPUT INSERTED.id_item " +
							" SELECT ?, ?, CAST(? AS VARCHAR(MAX)), ?, GETDATE(), ? WHERE NOT EXISTS (SELECT id_item FROM virtus.itens WHERE nome = '" + nome + "' and id_elemento = " + strconv.Itoa(idElemento) + " )"
						descricao = cicloAux.Pilares[j].Componentes[k].TiposNotas[l].Elementos[m].Itens[n].Descricao
						row := db.QueryRow(stmt, idElemento, nome, descricao, autor, statusZero)
						err = row.Scan(&idItem)
						if err != nil {
							log.Println(err.Error())
						}
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
		"         (SELECT id_componente, " +
		"                 SUM(peso_padrao) AS total " +
		"          FROM virtus.elementos_componentes a " +
		"          WHERE id_componente = a.id_componente " +
		"          GROUP BY id_componente), " +
		" TMP2 AS (SELECT tmp.id_componente AS id_componente, " +
		"     a.id_tipo_nota, " +
		"     tmp.total AS total, " +
		"     sum(a.peso_padrao) AS peso_padrao " +
		"       FROM virtus.elementos_componentes a " +
		"       LEFT JOIN TMP ON a.id_componente = TMP.id_componente " +
		"       GROUP BY tmp.id_componente, " +
		"                a.id_tipo_nota, " +
		"                tmp.total), " +
		" R2 AS (SELECT a.id_componente, a.id_tipo_nota, " +
		" a.peso_padrao*100/b.total as peso_padrao_percentual FROM TMP2 a LEFT JOIN TMP b ON a.id_componente = b.id_componente) " +
		" UPDATE virtus.tipos_notas_componentes  " +
		" SET peso_padrao = R2.peso_padrao_percentual " +
		" FROM R2 " +
		" WHERE tipos_notas_componentes.id_componente = R2.id_componente " +
		"   AND tipos_notas_componentes.id_tipo_nota = R2.id_tipo_nota "
	log.Println("stmt UPDATE virtus.tipos notas componentes: " + stmt)
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

func montarCicloAnual() CicloAux {
	var cicloAux CicloAux
	var pilarRC PilarAux
	var pilarG PilarAux
	//var pilarEF PilarAux
	var pilares []PilarAux
	var componentes []ComponenteAux
	componentes = make([]ComponenteAux, 0)

	pilarRC.Nome = "Riscos e Controles"
	componenteRiscoDeCredito := initRiscoDeCredito()
	componenteRiscoDeMercado := initRiscoDeMercado()
	componenteRiscoDeLiquidez := initRiscoDeLiquidez()
	componenteRiscoAtuarial := initRiscoAtuarial()
	componentes = append(componentes, componenteRiscoAtuarial, componenteRiscoDeCredito, componenteRiscoDeMercado, componenteRiscoDeLiquidez)
	pilarRC.Componentes = componentes

	pilarG.Nome = "Governança"
	componente := initGovernanca()
	componentes = make([]ComponenteAux, 0)
	componentes = append(componentes, componente)
	pilarG.Componentes = componentes

	/*pilarEF.Nome = "Econômico-Financeiro"
	componenteSolvencia := initEconomicoFinanceiro("Solvência")
	componenteInvestimentosAtivos := initEconomicoFinanceiro("Investimentos/Ativos")
	componenteAtuarial := initEconomicoFinanceiro("Atuarial")
	componenteResultados := initEconomicoFinanceiro("Resultados")
	componenteEficienciaOperacional := initEconomicoFinanceiro("Eficiência Operacional")
	componentes = make([]ComponenteAux, 0)
	componentes = append(componentes, componenteSolvencia, componenteInvestimentosAtivos, componenteAtuarial, componenteResultados, componenteEficienciaOperacional)
	pilarEF.Componentes = componentes
	pilares = append(pilares, pilarRC, pilarG, pilarEF)*/
	pilares = append(pilares, pilarRC, pilarG)
	cicloAux.Nome = "Ciclo Anual 2021"
	cicloAux.Pilares = pilares
	return cicloAux
}

func montarCicloBienal() CicloAux {
	var cicloAux CicloAux
	var pilarRC PilarAux
	//	var pilarG PilarAux
	//	var pilarEF PilarAux
	var pilares []PilarAux
	var componentes []ComponenteAux

	pilarRC.Nome = "Riscos e Controles"
	componenteRiscoDeCredito := initRiscoDeCredito()
	componenteRiscoDeMercado := initRiscoDeMercado()
	componenteRiscoDeLiquidez := initRiscoDeLiquidez()
	componenteRiscoAtuarial := initRiscoAtuarial()
	componentes = append(componentes, componenteRiscoDeCredito, componenteRiscoDeMercado, componenteRiscoDeLiquidez, componenteRiscoAtuarial)
	pilarRC.Componentes = componentes
	pilares = append(pilares, pilarRC)
	cicloAux.Nome = "Ciclo Bienal 2021-2022"
	cicloAux.Pilares = pilares
	return cicloAux
}

func montarCicloTrienal() CicloAux {
	var cicloAux CicloAux
	var pilarRC PilarAux
	//	var pilarG PilarAux
	//	var pilarEF PilarAux
	var pilares []PilarAux
	var componentes []ComponenteAux

	pilarRC.Nome = "Riscos e Controles"
	componenteRiscoDeCredito := initRiscoDeCredito()
	componenteRiscoDeMercado := initRiscoDeMercado()
	componenteRiscoDeLiquidez := initRiscoDeLiquidez()
	componenteRiscoAtuarial := initRiscoAtuarial()
	componentes = append(componentes, componenteRiscoDeCredito, componenteRiscoDeMercado, componenteRiscoDeLiquidez, componenteRiscoAtuarial)
	pilarRC.Componentes = componentes
	pilares = append(pilares, pilarRC)
	cicloAux.Nome = "Ciclo Trienal 2021-2023"
	cicloAux.Pilares = pilares
	return cicloAux
}
