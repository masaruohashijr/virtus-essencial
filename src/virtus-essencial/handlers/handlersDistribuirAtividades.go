package handlers

import (
	"encoding/json"
	"html/template"
	"log"
	"net/http"
	"strconv"
	"strings"
	mdl "virtus-essencial/models"
	sec "virtus-essencial/security"
)

type PlanosCfg struct {
	numPlano   string
	cnpb       string
	podeApagar bool
}

func ListDistribuirAtividadesHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("List Distribuir Atividades Handler")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "distribuirAtividades") {
		log.Println("--------------")
		msg := r.FormValue("msg")
		errMsg := r.FormValue("errMsg")
		warnMsg := r.FormValue("warnMsg")
		var page mdl.PageEntidadesCiclos
		sql := "SELECT DISTINCT d.codigo, b.id_entidade, d.nome, a.abreviatura " +
			" FROM virtus.escritorios a " +
			" LEFT JOIN virtus.jurisdicoes b ON a.id_escritorio = b.id_escritorio " +
			" LEFT JOIN virtus.membros c ON c.id_escritorio = b.id_escritorio " +
			" LEFT JOIN virtus.entidades d ON d.id_entidade = b.id_entidade " +
			" LEFT JOIN virtus.users u ON u.id_user = c.id_usuario " +
			" INNER JOIN virtus.ciclos_entidades e ON e.id_entidade = b.id_entidade " +
			" WHERE (c.id_usuario = ? AND u.id_role in (3,4)) OR (a.id_chefe = ?)"
		log.Println(sql)
		rows, _ := Db.Query(sql, currentUser.Id, currentUser.Id)
		defer rows.Close()
		var entidades []mdl.Entidade
		var entidade mdl.Entidade
		var i = 1
		for rows.Next() {
			rows.Scan(
				&entidade.Codigo,
				&entidade.Id,
				&entidade.Nome,
				&entidade.Escritorio)
			entidade.Order = i
			i++
			entidades = append(entidades, entidade)
		}
		var entidadesCiclos []mdl.Entidade
		for i, entidade := range entidades {
			var ciclosEntidade []mdl.CicloEntidade
			var cicloEntidade mdl.CicloEntidade
			sql = "SELECT b.id_ciclo, b.nome " +
				" FROM virtus.ciclos_entidades a " +
				" LEFT JOIN virtus.ciclos b ON a.id_ciclo = b.id_ciclo " +
				" WHERE a.id_entidade = ? " +
				" ORDER BY id_ciclo_entidade asc"
			rows, _ = Db.Query(sql, entidade.Id)
			defer rows.Close()
			i = 1
			for rows.Next() {
				rows.Scan(&cicloEntidade.Id, &cicloEntidade.Nome)
				cicloEntidade.Order = i
				i++
				ciclosEntidade = append(ciclosEntidade, cicloEntidade)
			}
			entidade.CiclosEntidade = ciclosEntidade
			entidadesCiclos = append(entidadesCiclos, entidade)
		}
		if warnMsg != "" {
			page.WarnMsg = warnMsg
		}
		if errMsg != "" {
			page.ErrMsg = errMsg
		}
		if msg != "" {
			page.Msg = msg
		}
		page.Entidades = entidadesCiclos
		page.AppName = mdl.AppName
		page.Title = "Distribuir Atividades" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/distribuiratividades/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Entidades-Distribuir-Atividades", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func UpdateDistribuirAtividadesHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Distribuir Atividades Handler")
	if r.Method == "POST" && sec.IsAuthenticated(w, r) {
		r.ParseForm()
		faltouConfigurarPlano := false
		for fieldName, value := range r.Form {
			// log.Println("-------------- fieldName: " + fieldName)
			if strings.HasPrefix(fieldName, "AuditorComponente_") {
				fname := fieldName[7:len(fieldName)]
				//log.Println(fname)
				supervisorId := r.FormValue("SupervisorComponenteId")
				//log.Println(supervisorId)
				entidadeId := r.FormValue("Entidade_" + fname)
				//log.Println(entidadeId)
				cicloId := r.FormValue("Ciclo_" + fname)
				//log.Println(cicloId)
				pilarId := r.FormValue("Pilar_" + fname)
				//log.Println(pilarId)
				planosIds := r.FormValue("Planos_" + fname)
				//log.Println("planosIds: " + planosIds)
				componenteId := r.FormValue("Componente_" + fname)
				//log.Println(fieldName + " - value: " + value[0])
				if value[0] != "" {
					sqlStatement := "UPDATE virtus.produtos_componentes SET " +
						" id_auditor=" + value[0] + ", id_supervisor=" + supervisorId +
						" WHERE id_entidade=" + entidadeId +
						" AND id_ciclo=" + cicloId +
						" AND id_pilar=" + pilarId +
						" AND id_componente= " + componenteId
					log.Println(sqlStatement)
					updtForm, _ := Db.Prepare(sqlStatement)
					_, err := updtForm.Exec()
					if err != nil {
						log.Println(err.Error())
					}
				}
				planos := strings.ReplaceAll(planosIds, "_", ",")
				if len(planos) == 0 {
					faltouConfigurarPlano = true
				}
			} else if strings.HasPrefix(fieldName, "IniciaEmComponente_") {
				fname := fieldName[8:len(fieldName)]
				// log.Println(fname)
				partes := strings.Split(fname, "_")
				entidadeId := partes[1]
				// log.Println(entidadeId)
				cicloId := partes[2]
				// log.Println(cicloId)
				pilarId := partes[3]
				// log.Println(pilarId)
				componenteId := partes[4]
				// log.Println(fieldName + " - value: " + value[0])
				if value[0] != "" {
					sqlStatement := "UPDATE virtus.produtos_componentes SET " +
						" inicia_em='" + value[0] + "' " +
						" WHERE id_entidade=" + entidadeId +
						" AND id_ciclo=" + cicloId +
						" AND id_pilar=" + pilarId +
						" AND id_componente= " + componenteId
					log.Println(sqlStatement)
					updtForm, _ := Db.Prepare(sqlStatement)
					_, err := updtForm.Exec()
					if err != nil {
						log.Println(err.Error())
					}
				}
			} else if strings.HasPrefix(fieldName, "TerminaEmComponente_") {
				fname := fieldName[9:len(fieldName)]
				// log.Println(fname)
				partes := strings.Split(fname, "_")
				entidadeId := partes[1]
				// log.Println(entidadeId)
				cicloId := partes[2]
				// log.Println(cicloId)
				pilarId := partes[3]
				// log.Println(pilarId)
				componenteId := partes[4]
				// log.Println(fieldName + " - value: " + value[0])
				if value[0] != "" {
					sqlStatement := "UPDATE virtus.produtos_componentes SET " +
						" termina_em='" + value[0] + "' " +
						" WHERE id_entidade=" + entidadeId +
						" AND id_ciclo=" + cicloId +
						" AND id_pilar=" + pilarId +
						" AND id_componente= " + componenteId
					log.Println(sqlStatement)
					updtForm, _ := Db.Prepare(sqlStatement)
					_, err := updtForm.Exec()
					if err != nil {
						log.Println(err.Error())
					}
				}
			}
		}
		msg := "msg=Os demais produtos dos níveis do ciclo foram criados com Sucesso."
		if faltouConfigurarPlano {
			warnMsg := "warnMsg=Faltou configurar quais os planos que serão avaliados."
			http.Redirect(w, r, "/listDistribuirAtividades?"+msg+"&"+warnMsg, 301)
		} else {
			http.Redirect(w, r, "/listDistribuirAtividades?"+msg, 301)
		}
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func DistribuirAtividadesHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("Distribuir Atividades Handler")
	if sec.IsAuthenticated(w, r) {
		entidadeId := r.FormValue("EntidadeId")
		cicloId := r.FormValue("CicloId")
		var page mdl.PageProdutosComponentes
		sql := " SELECT " +
			" a.id_ciclo, c.nome as ciclo_nome, " +
			" a.id_pilar, d.nome as pilar_nome, " +
			" a.id_componente, e.nome as componente_nome, " +
			" coalesce(b.nome,''), a.id_entidade, " +
			" coalesce(R.configurado,'N'), " +
			" coalesce(e.pga,'') as pga, " +
			" coalesce(h.id_supervisor,0) as super_id, coalesce(f.name,'') as supervisor_nome, " +
			" coalesce(a.id_auditor,0) as audit_id, coalesce(g.name,'') as auditor_nome,  " +
			" coalesce(format(a.inicia_em,'yyyy-MM-dd'),'') as inicia_em, " +
			" coalesce(format(a.termina_em,'yyyy-MM-dd'),'') as termina_em " +
			" FROM virtus.produtos_componentes a " +
			" LEFT JOIN virtus.entidades b ON a.id_entidade = b.id_entidade " +
			" LEFT JOIN virtus.ciclos c ON a.id_ciclo = c.id_ciclo " +
			" LEFT JOIN virtus.pilares d ON a.id_pilar = d.id_pilar " +
			" LEFT JOIN virtus.componentes e ON a.id_componente = e.id_componente " +
			" LEFT JOIN virtus.ciclos_entidades h ON (a.id_entidade = h.id_entidade AND a.id_ciclo = h.id_ciclo) " +
			" LEFT JOIN virtus.users f ON h.id_supervisor = f.id_user " +
			" LEFT JOIN virtus.users g ON a.id_auditor = g.id_user " +
			" LEFT JOIN (select a.id_entidade, a.id_ciclo, a.id_pilar, a.id_componente, " +
			" 	CASE WHEN COUNT(i.id_produto_plano)>0 THEN 'S' ELSE 'N' END AS configurado from virtus.produtos_componentes a " +
			" 	INNER JOIN virtus.produtos_planos i ON (a.id_entidade = i.id_entidade " +
			" 	AND a.id_ciclo = i.id_ciclo " +
			" 	AND a.id_pilar = i.id_pilar " +
			" 	AND a.id_componente = i.id_componente) " +
			" 	GROUP BY a.id_entidade, a.id_ciclo, a.id_pilar, a.id_componente) R " +
			" ON (a.id_entidade = R.id_entidade " +
			" 	AND a.id_ciclo = R.id_ciclo " +
			" 	AND a.id_pilar = R.id_pilar " +
			" 	AND a.id_componente = R.id_componente) " +
			" WHERE a.id_entidade = " + entidadeId + " AND a.id_ciclo = " + cicloId +
			" ORDER BY d.nome, e.nome "
		log.Println(sql)
		rows, _ := Db.Query(sql)
		defer rows.Close()
		var produtos []mdl.ProdutoComponente
		var produto mdl.ProdutoComponente
		var i = 1
		for rows.Next() {
			rows.Scan(
				&produto.CicloId,
				&produto.CicloNome,
				&produto.PilarId,
				&produto.PilarNome,
				&produto.ComponenteId,
				&produto.ComponenteNome,
				&produto.EntidadeNome,
				&produto.EntidadeId,
				&produto.Configurado,
				&produto.SomentePGA,
				&produto.SupervisorId,
				&produto.SupervisorName,
				&produto.AuditorId,
				&produto.AuditorName,
				&produto.IniciaEm,
				&produto.TerminaEm)
			produto.Order = i
			i++
			produtos = append(produtos, produto)
		}
		page.Produtos = produtos
		orderable := ""
		sql = " SELECT " +
			" b.id_usuario, coalesce(c.name,''), UPPER(coalesce(c.name,'')) AS supervisor_nome " +
			" FROM virtus.escritorios a " +
			" LEFT JOIN virtus.membros b ON a.id_escritorio = b.id_escritorio " +
			" LEFT JOIN virtus.users c ON b.id_usuario = c.id_user " +
			" WHERE c.id_role = 3 ORDER BY supervisor_nome "
		log.Println(sql)
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var supervisores []mdl.User
		var supervisor mdl.User
		i = 1
		for rows.Next() {
			rows.Scan(&supervisor.Id, &supervisor.Name, &orderable)
			supervisores = append(supervisores, supervisor)
		}
		page.Supervisores = supervisores

		sql = " SELECT " +
			" a.id_usuario, " +
			" coalesce(b.name,''), " +
			" UPPER(coalesce(b.name,'')) AS orderable " +
			" FROM virtus.integrantes a " +
			" LEFT JOIN virtus.users b " +
			" ON a.id_usuario = b.id_user " +
			" WHERE " +
			" a.id_entidade = " + entidadeId +
			" AND a.id_ciclo = " + cicloId +
			" AND b.id_role = 4 "
		log.Println(sql)
		rows, _ = Db.Query(sql)
		defer rows.Close()
		var auditores []mdl.User
		var auditor mdl.User
		for rows.Next() {
			rows.Scan(&auditor.Id, &auditor.Name, &orderable)
			auditores = append(auditores, auditor)
		}

		sql = " SELECT id_plano, cnpb, id_modalidade, CASE WHEN recurso_garantidor > 1000000 AND recurso_garantidor < 1000000000 THEN concat(format(recurso_garantidor/1000000,'N','pt-br'),' Milhões') WHEN recurso_garantidor > 1000000000 THEN concat(format(recurso_garantidor/1000000000,'N','pt-br'),' Bilhões') ELSE concat(format(recurso_garantidor/1000,'N','pt-br'),' Milhares') END " +
			" FROM virtus.planos WHERE id_entidade = ? AND left(cnpb,1) not in ('4','5') ORDER BY recurso_garantidor DESC "
		log.Println(sql)
		rows, _ = Db.Query(sql, entidadeId)
		defer rows.Close()
		var planos []mdl.Plano
		var plano mdl.Plano
		for rows.Next() {
			rows.Scan(&plano.Id, &plano.CNPB, &plano.Modalidade, &plano.RecursoGarantidor)
			planos = append(planos, plano)
		}
		page.Planos = planos
		page.Supervisores = supervisores
		page.Auditores = auditores
		page.AppName = mdl.AppName
		page.Title = "Distribuir Atividades" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/distribuiratividades/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Distribuir-Atividades", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}

func LoadConfigPlanos(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Config Planos")
	r.ParseForm()
	var entidadeId = r.FormValue("entidadeId")
	var cicloId = r.FormValue("cicloId")
	var pilarId = r.FormValue("pilarId")
	var componenteId = r.FormValue("componenteId")
	var soPGA = r.FormValue("soPGA")
	configPlanos := ListConfigPlanos(entidadeId, cicloId, pilarId, componenteId, soPGA)
	jsonConfigPlanos, _ := json.Marshal(configPlanos)
	w.Write([]byte(jsonConfigPlanos))
	log.Println("JSON Config Planos")
}

func UpdateConfigPlanos(w http.ResponseWriter, r *http.Request) {
	log.Println("Update Config Planos ===>>> ATUALIZANDO")
	r.ParseForm()
	entidadeId := r.FormValue("entidadeId")
	currentUser := GetUserInCookie(w, r)
	cicloId := r.FormValue("cicloId")
	pilarId := r.FormValue("pilarId")
	componenteId := r.FormValue("componenteId")
	planos := r.FormValue("planos")
	superUser := r.FormValue("superUser")
	motivacao := r.FormValue("motivacao")
	planos = strings.TrimSpace(planos)
	array := strings.Split(planos, "_")
	log.Println("planos: " + planos)
	var planosPage []PlanosCfg
	var planoPage PlanosCfg
	sql := " select id_plano, cnpb from virtus.planos where id_entidade = " + entidadeId + " order by cnpb "
	log.Println(sql)
	rows, _ := Db.Query(sql)
	defer rows.Close()
	m := make(map[string]string)
	id := 0
	cnpb := ""
	for rows.Next() {
		rows.Scan(&id, &cnpb)
		m[strconv.Itoa(id)] = cnpb
	}
	for i, valor := range array {
		log.Println("i: " + strconv.Itoa(i))
		if strings.TrimSpace(valor) != "" {
			planoPage.numPlano = valor
			planoPage.cnpb = m[planoPage.numPlano]
			planosPage = append(planosPage, planoPage)
		}
	}
	log.Println("Qtd Page: " + strconv.Itoa(len(planosPage)))

	planos = strings.Join(array, ",")
	if len(planos) > 0 {
		planos = planos[:len(planos)-1]
	}
	sql = " select a.id_plano, c.cnpb, " +
		" case when count(b.id_produto_elemento_historico) = 0 then 1 else 0 end as pode_apagar " +
		" from virtus.produtos_elementos a " +
		" left join virtus.produtos_elementos_historicos b on " +
		" (a.id_entidade = b.id_entidade and a.id_ciclo = b.id_ciclo " +
		" and a.id_pilar = b.id_pilar and a.id_componente = b.id_componente " +
		" and a.id_plano = b.id_plano) " +
		" inner join virtus.planos c on c.id_plano = a.id_plano " +
		" where a.id_entidade = " + entidadeId +
		" and a.id_ciclo = " + cicloId +
		" and a.id_pilar = " + pilarId +
		" and a.id_componente = " + componenteId +
		" group by a.id_plano,c.cnpb "
	log.Println(sql)
	rows, _ = Db.Query(sql)
	defer rows.Close()
	var planosBD []PlanosCfg
	var planoBD PlanosCfg
	var podeApagar int
	for rows.Next() {
		rows.Scan(&planoBD.numPlano, &planoBD.cnpb, &podeApagar)
		if podeApagar == 1 {
			planoBD.podeApagar = true
		} else {
			planoBD.podeApagar = false
		}
		planosBD = append(planosBD, planoBD)
	}
	// Não posso simplesmente apagar eu tenho que testar NOW () no banco PAST (1,2,3,4)
	msgRetorno := ""
	force := false
	log.Println("Qtd BD: " + strconv.Itoa(len(planosBD)))

	configuracaoAnterior := loadConfigPlanos(entidadeId, cicloId, pilarId, componenteId)

	log.Println(configuracaoAnterior)

	if len(planosPage) < len(planosBD) {
		if len(planosPage) == 0 {
			if superUser == "true" {
				force = true
			}
			for _, valor := range planosBD {
				log.Println(valor.cnpb)
				if valor.podeApagar || force {
					log.Println("Removendo o " + valor.cnpb)
					deleteProdutoPlano(entidadeId, cicloId, pilarId, componenteId, valor.numPlano, currentUser)
					msgRetorno += "O plano " + valor.cnpb + " foi removido com Sucesso.\n"
				} else {
					msgRetorno += "O plano " + valor.cnpb + " não pode ser removido por já ter sido avaliado antes.\n"
					log.Println("RoleName: " + currentUser.RoleName)
					if currentUser.RoleName == "Chefe" || currentUser.Role == 2 {
						msgRetorno += "Deseja prosseguir com a remoção?\n"
					}
				}
			}
		} else {
			var diffDB []PlanosCfg = planosBD
			for n := range planosPage {
				if containsPlanoCfg(diffDB, planosPage[n]) {
					diffDB = removePlanoCfg(diffDB, planosPage[n])
				}
			}
			for _, valor := range diffDB {
				if valor.podeApagar || force {
					log.Println("Removendo o " + valor.cnpb)
					deleteProdutoPlano(entidadeId, cicloId, pilarId, componenteId, valor.numPlano, currentUser)
					msgRetorno += "O plano " + valor.cnpb + " foi removido com Sucesso.\n"
				} else {
					msgRetorno += "O plano " + valor.cnpb + " não pode ser removido por já ter sido avaliado antes.\n"
					log.Println("RoleName: " + currentUser.RoleName)
					if currentUser.RoleName == "Chefe" || currentUser.Role == 2 {
						msgRetorno += "Deseja prosseguir com a remoção?\n"
					}
					log.Println(msgRetorno)
				}
			}
		}
	} else {
		log.Println("Registrar Produtos Planos")
		var diffPage []PlanosCfg = planosPage
		for n := range planosBD {
			log.Println("CNPB: " + planosBD[n].cnpb)
			if containsPlanoCfg(diffPage, planosBD[n]) {
				log.Println("Removendo " + planosBD[n].cnpb)
				diffPage = removePlanoCfg(diffPage, planosBD[n])
			}
		}
		var param mdl.ProdutoPlano
		param.EntidadeId, _ = strconv.ParseInt(entidadeId, 10, 64)
		param.CicloId, _ = strconv.ParseInt(cicloId, 10, 64)
		param.PilarId, _ = strconv.ParseInt(pilarId, 10, 64)
		param.ComponenteId, _ = strconv.ParseInt(componenteId, 10, 64)
		var diffDB []PlanosCfg = planosBD
		for n := range planosPage {
			if containsPlanoCfg(diffDB, planosPage[n]) {
				diffDB = removePlanoCfg(diffDB, planosPage[n])
			}
		}
		for _, v := range planosPage {
			log.Println("Registrar diffPage como Produtos Planos")
			log.Println("Plano: " + v.numPlano)
			registrarProdutosPlanos(param, v.numPlano, currentUser)
			msgRetorno += "O plano " + v.cnpb + " foi adicionado com Sucesso.\n"
		}
		for _, valor := range diffDB {
			if valor.podeApagar || force {
				log.Println("Removendo o " + valor.cnpb)
				deleteProdutoPlano(entidadeId, cicloId, pilarId, componenteId, valor.numPlano, currentUser)
				msgRetorno += "O plano " + valor.cnpb + " foi removido com Sucesso.\n"
			} else {
				msgRetorno += "O plano " + valor.cnpb + " não pode ser removido por já ter sido avaliado antes.\n"
				log.Println("RoleName: " + currentUser.RoleName)
				if currentUser.RoleName == "Chefe" || currentUser.Role == 2 {
					msgRetorno += "Deseja prosseguir com a remoção?\n"
				}
				log.Println(msgRetorno)
			}
		}
	}

	registrarConfigPlanosHistorico(entidadeId, cicloId, pilarId, componenteId, currentUser, configuracaoAnterior, motivacao)

	w.Write([]byte(msgRetorno))
	log.Println("JSON Config Planos")
}

func deleteProdutoPlano(entidadeId string, cicloId string, pilarId string, componenteId string, planoId string, currentUser mdl.User) {
	sqlStatement := "DELETE FROM virtus.produtos_itens WHERE " +
		" id_entidade = " + entidadeId +
		" and id_ciclo = " + cicloId +
		" and id_pilar = " + pilarId +
		" and id_componente = " + componenteId +
		" and id_plano = " + planoId
	log.Println(sqlStatement)
	updtForm, _ := Db.Prepare(sqlStatement)
	_, err := updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
	sqlStatement = "DELETE FROM virtus.produtos_elementos_historicos WHERE " +
		" id_entidade = " + entidadeId +
		" and id_ciclo = " + cicloId +
		" and id_pilar = " + pilarId +
		" and id_componente = " + componenteId +
		" and id_plano = " + planoId
	log.Println(sqlStatement)
	updtForm, _ = Db.Prepare(sqlStatement)
	_, err = updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
	sqlStatement = "DELETE FROM virtus.produtos_elementos WHERE " +
		" id_entidade = " + entidadeId +
		" and id_ciclo = " + cicloId +
		" and id_pilar = " + pilarId +
		" and id_componente = " + componenteId +
		" and id_plano = " + planoId
	log.Println(sqlStatement)
	updtForm, _ = Db.Prepare(sqlStatement)
	_, err = updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
	sqlStatement = "DELETE FROM virtus.produtos_tipos_notas WHERE " +
		" id_entidade = " + entidadeId +
		" and id_ciclo = " + cicloId +
		" and id_pilar = " + pilarId +
		" and id_componente = " + componenteId +
		" and id_plano = " + planoId
	log.Println(sqlStatement)
	updtForm, _ = Db.Prepare(sqlStatement)
	_, err = updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
	sqlStatement = "DELETE FROM virtus.produtos_planos WHERE " +
		" id_entidade = " + entidadeId +
		" and id_ciclo = " + cicloId +
		" and id_pilar = " + pilarId +
		" and id_componente = " + componenteId +
		" and id_plano = " + planoId
	log.Println(sqlStatement)
	updtForm, _ = Db.Prepare(sqlStatement)
	_, err = updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
	var produto mdl.ProdutoElemento
	produto.EntidadeId, _ = strconv.ParseInt(entidadeId, 10, 64)
	produto.CicloId, _ = strconv.ParseInt(cicloId, 10, 64)
	produto.PilarId, _ = strconv.ParseInt(pilarId, 10, 64)
	produto.ComponenteId, _ = strconv.ParseInt(componenteId, 10, 64)
	atualizarPesoPlanos(produto, currentUser)
	atualizarPesoComponentes(produto, currentUser)
	atualizarComponenteNota(produto)
	atualizarPilarNota(produto)
	atualizarCicloNota(produto)
	atualizarPesoTiposNotas(produto, currentUser)
	sqlStatement = "UPDATE virtus.produtos_componentes " +
		" SET nota = 0 " +
		" WHERE " +
		" id_entidade = " + entidadeId +
		" and id_ciclo = " + cicloId +
		" and id_pilar = " + pilarId +
		" and id_componente = " + componenteId
	log.Println(sqlStatement)
	updtForm, _ = Db.Prepare(sqlStatement)
	_, err = updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
	sqlStatement = "UPDATE virtus.produtos_pilares " +
		" SET nota = 0 " +
		" WHERE id_ciclo = " + cicloId +
		" and id_pilar = " + pilarId +
		" and id_entidade = " + entidadeId
	log.Println(sqlStatement)
	updtForm, _ = Db.Prepare(sqlStatement)
	_, err = updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
}

func removePlanoCfg(planos []PlanosCfg, planoCfgToBeRemoved PlanosCfg) []PlanosCfg {
	var newPlanosCfg []PlanosCfg
	for i := range planos {
		if planos[i].numPlano != planoCfgToBeRemoved.numPlano {
			newPlanosCfg = append(newPlanosCfg, planos[i])
		}
	}
	return newPlanosCfg
}

func containsPlanoCfg(planosCfg []PlanosCfg, planoCfgCompared PlanosCfg) bool {
	for n := range planosCfg {
		log.Println(planosCfg[n].numPlano)
		log.Println(planoCfgCompared.numPlano)
		if planosCfg[n].numPlano == planoCfgCompared.numPlano {
			return true
		}
	}
	return false
}

func SalvarReprogramacaoComponente(w http.ResponseWriter, r *http.Request) {
	log.Println("Salvar Reprogramação Componente")
	r.ParseForm()
	var entidadeId = r.FormValue("entidadeId")
	var cicloId = r.FormValue("cicloId")
	var pilarId = r.FormValue("pilarId")
	var componenteId = r.FormValue("componenteId")
	motivacao := r.FormValue("motivacao")
	tipoData := r.FormValue("tipoData")
	dataAnterior := r.FormValue("dataAnterior")
	novaData := r.FormValue("novaData")
	var produtoComponente mdl.ProdutoComponente
	produtoComponente.EntidadeId, _ = strconv.ParseInt(entidadeId, 10, 64)
	produtoComponente.CicloId, _ = strconv.ParseInt(cicloId, 10, 64)
	produtoComponente.PilarId, _ = strconv.ParseInt(pilarId, 10, 64)
	produtoComponente.ComponenteId, _ = strconv.ParseInt(componenteId, 10, 64)
	produtoComponente.Motivacao = motivacao
	currentUser := GetUserInCookie(w, r)
	if tipoData == "iniciaEm" {
		produtoComponente.IniciaEm = novaData
		produtoComponente.IniciaEmAnterior = dataAnterior
	} else {
		produtoComponente.TerminaEm = novaData
		produtoComponente.TerminaEmAnterior = dataAnterior
	}
	registrarCronogramaComponente(produtoComponente, currentUser, tipoData)
	registrarHistoricoReprogramacaoComponente(produtoComponente, currentUser, tipoData)
	jsonOK, _ := json.Marshal("OK")
	w.Write(jsonOK)
	log.Println("----------")
}
