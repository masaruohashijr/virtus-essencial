package handlers

import (
	"encoding/json"
	"log"
	"net/http"
	"strconv"
	mdl "virtus-essencial/models"
)

func registrarConfigPlanosHistorico(entidadeId string, cicloId string, pilarId string, componenteId string, currentUser mdl.User, configuracaoAnterior string, motivacao string) {
	sqlStatement := " INSERT INTO virtus.produtos_componentes_historicos( " +
		"	id_entidade,  " +
		"	id_ciclo,  " +
		"	id_pilar,  " +
		"	id_componente,  " +
		"	tipo_alteracao,  " +
		" 	config, " +
		"	config_anterior, " +
		"	motivacao_config, " +
		"	id_author, " +
		"	criado_em, " +
		"	id_versao_origem, " +
		"	id_status) " +
		" SELECT " +
		"	a.id_entidade, " +
		"	a.id_ciclo, " +
		"	a.id_pilar, " +
		"	a.id_componente, " +
		"	'P', " +
		"	COALESCE(cfg.planos_configurados,''), " +
		"	'" + configuracaoAnterior + "', " +
		"	'" + motivacao + "', " +
		strconv.FormatInt(currentUser.Id, 10) + ", " +
		"	GETDATE(),  " +
		"	a.id_author,  " +
		"	a.id_status " +
		"	FROM virtus.produtos_componentes a " +
		"	LEFT JOIN (SELECT pp.id_entidade, pp.id_ciclo, pp.id_pilar, pp.id_componente, string_agg(pl.cnpb,', ') planos_configurados " +
		"	FROM virtus.produtos_planos pp INNER JOIN virtus.planos pl ON pp.id_plano = pl.id_plano GROUP BY " +
		" 	pp.id_entidade, " +
		" 	pp.id_ciclo, " +
		" 	pp.id_pilar, " +
		" 	pp.id_componente) cfg " +
		" 	ON ( cfg.id_entidade = a.id_entidade AND cfg.id_pilar =  a.id_pilar AND cfg.id_componente = a.id_componente ) " +
		"	WHERE a.id_entidade = " + entidadeId + " AND " +
		"	a.id_ciclo = " + cicloId + " AND " +
		"	a.id_pilar = " + pilarId + " AND " +
		"	a.id_componente = " + componenteId
	log.Println(sqlStatement)
	Db.QueryRow(sqlStatement)
}

func registrarHistoricoReprogramacaoComponente(produto mdl.ProdutoComponente, currentUser mdl.User, tipoData string) {
	sqlStatement := "INSERT INTO virtus.produtos_componentes_historicos( " +
		"	id_entidade,  " +
		"	id_ciclo,  " +
		"	id_pilar,  " +
		"	id_componente,  " +
		"	id_tipo_pontuacao,  " +
		"	peso,  " +
		"	nota,  " +
		"	tipo_alteracao,  " +
		"	motivacao_reprogramacao,  " +
		"	id_supervisor,  " +
		"	id_auditor,  "
	if tipoData == "iniciaEm" {
		sqlStatement += "	inicia_em, "
		sqlStatement += "	inicia_em_anterior, "
	} else {
		sqlStatement += "	termina_em, "
		sqlStatement += "	termina_em_anterior, "
	}
	sqlStatement += "	id_author,  " +
		"	criado_em,  " +
		"	id_versao_origem,  " +
		"	id_status) " +
		"SELECT  " +
		"	id_entidade,  " +
		"	id_ciclo,  " +
		"	id_pilar,  " +
		"	id_componente,  " +
		"	id_tipo_pontuacao,  " +
		"	peso,  " +
		"	nota,  "
	if tipoData == "iniciaEm" {
		sqlStatement += "	'I',  "
	} else {
		sqlStatement += "	'T',  "
	}
	sqlStatement += "	motivacao_reprogramacao,  " +
		"	id_supervisor,  " +
		"	id_auditor,  "
	if tipoData == "iniciaEm" {
		sqlStatement += " '" + produto.IniciaEm + "',  "
		sqlStatement += " '" + produto.IniciaEmAnterior + "',  "
	} else {
		sqlStatement += " '" + produto.TerminaEm + "',  "
		sqlStatement += " '" + produto.TerminaEmAnterior + "',  "
	}
	sqlStatement += strconv.FormatInt(currentUser.Id, 10) + ",  " +
		"	GETDATE(),  " +
		"	id_author,  " +
		"	id_status " +
		"	FROM virtus.produtos_componentes " +
		"	WHERE id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) + " AND " +
		"	id_ciclo = " + strconv.FormatInt(produto.CicloId, 10) + " AND " +
		"	id_pilar = " + strconv.FormatInt(produto.PilarId, 10) + " AND " +
		"	id_componente = " + strconv.FormatInt(produto.ComponenteId, 10)
	log.Println(sqlStatement)
	Db.QueryRow(sqlStatement)
}

func registrarHistoricoAuditorComponente(produto mdl.ProdutoComponente, currentUser mdl.User) {
	sqlStatement := "INSERT INTO virtus.produtos_componentes_historicos( " +
		"	id_entidade,  " +
		"	id_ciclo,  " +
		"	id_pilar,  " +
		"	id_componente,  " +
		"	id_tipo_pontuacao,  " +
		"	peso,  " +
		"	nota,  " +
		"	tipo_alteracao,  " +
		"	justificativa,  " +
		"	id_supervisor,  " +
		"	id_auditor,  " +
		"	auditor_anterior_id,  " +
		"	id_author,  " +
		"	criado_em,  " +
		"	id_versao_origem,  " +
		"	id_status) " +
		"SELECT  " +
		"	id_entidade,  " +
		"	id_ciclo,  " +
		"	id_pilar,  " +
		"	id_componente,  " +
		"	id_tipo_pontuacao,  " +
		"	peso,  " +
		"	nota,  " +
		"	'R',  " +
		"	justificativa,  " +
		"	id_supervisor,  " +
		"	id_auditor,  " +
		strconv.FormatInt(produto.AuditorAnteriorId, 10) + ",  " +
		strconv.FormatInt(currentUser.Id, 10) + ",  " +
		"	GETDATE(),  " +
		"	id_author,  " +
		"	id_status " +
		"	FROM virtus.produtos_componentes " +
		"	WHERE id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) + " AND " +
		"	id_ciclo = " + strconv.FormatInt(produto.CicloId, 10) + " AND " +
		"	id_pilar = " + strconv.FormatInt(produto.PilarId, 10) + " AND " +
		"	id_componente = " + strconv.FormatInt(produto.ComponenteId, 10)
	log.Println(sqlStatement)
	Db.QueryRow(sqlStatement)
}

func registrarHistoricoNotaElemento(produto mdl.ProdutoElemento, currentUser mdl.User) {
	sqlStatement := "INSERT INTO virtus.produtos_elementos_historicos( " +
		"	id_entidade,  " +
		"	id_ciclo,  " +
		"	id_pilar,  " +
		"	id_plano,  " +
		"	id_componente,  " +
		"   id_tipo_nota," +
		"   id_elemento," +
		"	id_tipo_pontuacao,  " +
		"	peso,  " +
		"	nota,  " +
		"	tipo_alteracao,  " +
		"	motivacao_nota,  " +
		"	id_supervisor,  " +
		"	id_auditor,  " +
		"	id_author,  " +
		"	criado_em,  " +
		"	id_versao_origem,  " +
		"	id_status) " +
		"SELECT  " +
		"	id_entidade,  " +
		"	id_ciclo,  " +
		"	id_pilar,  " +
		"	id_plano,  " +
		"	id_componente,  " +
		"	id_tipo_nota,  " +
		"	id_elemento,  " +
		"	id_tipo_pontuacao,  " +
		"	peso,  " +
		"	nota,  " +
		"	'N',  " +
		"	motivacao_nota,  " +
		"	id_supervisor,  " +
		"	id_auditor,  " +
		"	" + strconv.FormatInt(currentUser.Id, 10) + ",  " +
		"	GETDATE(),  " +
		"	id_author,  " +
		"	id_status " +
		"	FROM virtus.produtos_elementos " +
		"	WHERE id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) + " AND " +
		"	id_ciclo = " + strconv.FormatInt(produto.CicloId, 10) + " AND " +
		"	id_pilar = " + strconv.FormatInt(produto.PilarId, 10) + " AND " +
		"	id_plano = " + strconv.FormatInt(produto.PlanoId, 10) + " AND " +
		"	id_componente = " + strconv.FormatInt(produto.ComponenteId, 10) + " AND " +
		"	id_tipo_nota = " + strconv.FormatInt(produto.TipoNotaId, 10) + " AND " +
		"	id_elemento = " + strconv.FormatInt(produto.ElementoId, 10)
	log.Println(sqlStatement)
	Db.QueryRow(sqlStatement)
}

func registrarHistoricoPesoElemento(produto mdl.ProdutoElemento, currentUser mdl.User) {
	sqlStatement := "INSERT INTO virtus.produtos_elementos_historicos( " +
		"	id_entidade,  " +
		"	id_ciclo,  " +
		"	id_pilar,  " +
		"	id_plano,  " +
		"	id_componente,  " +
		"   id_tipo_nota," +
		"   id_elemento," +
		"	id_tipo_pontuacao,  " +
		"	peso,  " +
		"	nota,  " +
		"	tipo_alteracao,  " +
		"	motivacao_peso,  " +
		"	id_supervisor,  " +
		"	id_auditor,  " +
		"	id_author,  " +
		"	criado_em,  " +
		"	id_versao_origem,  " +
		"	id_status) " +
		"SELECT  " +
		"	id_entidade,  " +
		"	id_ciclo,  " +
		"	id_pilar,  " +
		"	id_plano,  " +
		"	id_componente,  " +
		"	id_tipo_nota,  " +
		"	id_elemento,  " +
		"	id_tipo_pontuacao,  " +
		"	peso,  " +
		"	nota,  " +
		"	'P',  " +
		"	motivacao_peso,  " +
		"	id_supervisor,  " +
		"	id_auditor,  " +
		"	" + strconv.FormatInt(currentUser.Id, 10) + ",  " +
		"	GETDATE(),  " +
		"	id_author,  " +
		"	id_status " +
		"	FROM virtus.produtos_elementos " +
		"	WHERE id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) + " AND " +
		"	id_ciclo = " + strconv.FormatInt(produto.CicloId, 10) + " AND " +
		"	id_pilar = " + strconv.FormatInt(produto.PilarId, 10) + " AND " +
		//"	id_plano = " + strconv.FormatInt(produto.PlanoId, 10) + " AND " +
		"	id_componente = " + strconv.FormatInt(produto.ComponenteId, 10) + " AND " +
		"	id_tipo_nota = " + strconv.FormatInt(produto.TipoNotaId, 10) + " AND " +
		"	id_elemento = " + strconv.FormatInt(produto.ElementoId, 10)
	log.Println(sqlStatement)
	Db.QueryRow(sqlStatement)
}

func LoadHistoricosElemento(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Historicos do Elemento")
	r.ParseForm()
	var entidadeId = r.FormValue("entidadeId")
	var cicloId = r.FormValue("cicloId")
	var pilarId = r.FormValue("pilarId")
	var planoId = r.FormValue("planoId")
	var componenteId = r.FormValue("componenteId")
	var elementoId = r.FormValue("elementoId")
	var filtro mdl.Historico
	filtro.EntidadeId = entidadeId
	filtro.CicloId = cicloId
	filtro.PilarId = pilarId
	filtro.PlanoId = planoId
	filtro.ComponenteId = componenteId
	filtro.ElementoId = elementoId
	historicos := ListHistoricosElemento(filtro)
	jsonHistoricos, _ := json.Marshal(historicos)
	w.Write([]byte(jsonHistoricos))
	log.Println("JSON Históricos do Elemento")
}

func ListHistoricosElemento(filtro mdl.Historico) []mdl.Historico {
	log.Println("List Históricos do Elemento")
	sql := "SELECT " +
		"a.id_produto_elemento_historico, " +
		"a.id_entidade, " +
		"a.id_ciclo, " +
		"a.id_pilar, " +
		"a.id_plano, " +
		"a.id_componente, " +
		"a.id_elemento, " +
		"a.peso, " +
		"a.id_tipo_pontuacao, " +
		"a.nota, " +
		"a.id_author, " +
		"coalesce(b.name,''), " +
		"coalesce(format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'),'') as alterado_em, " +
		"case when tipo_alteracao = 'P' then a.motivacao_peso else a.motivacao_nota end, " +
		"case when tipo_alteracao = 'P' then 'Peso' else 'Nota' end " +
		"FROM virtus.produtos_elementos_historicos a " +
		"LEFT JOIN virtus.users b ON a.id_author = b.id_user " +
		"WHERE a.id_entidade = " + filtro.EntidadeId + " AND " +
		"a.id_ciclo = " + filtro.CicloId + " AND " +
		"a.id_pilar = " + filtro.PilarId + " AND " +
		"a.id_plano = " + filtro.PlanoId + " AND " +
		"a.id_componente = " + filtro.ComponenteId + " AND " +
		"a.id_elemento = " + filtro.ElementoId + " ORDER BY a.criado_em DESC "
	log.Println(sql)
	rows, _ := Db.Query(sql)
	defer rows.Close()
	var historicos []mdl.Historico
	var historico mdl.Historico
	for rows.Next() {
		rows.Scan(
			&historico.Id,
			&historico.EntidadeId,
			&historico.CicloId,
			&historico.PilarId,
			&historico.PlanoId,
			&historico.ComponenteId,
			&historico.ElementoId,
			&historico.Peso,
			&historico.Metodo,
			&historico.Nota,
			&historico.AutorId,
			&historico.AutorNome,
			&historico.AlteradoEm,
			&historico.Motivacao,
			&historico.TipoAlteracao)
		switch historico.Metodo {
		case "1":
			historico.Metodo = "Manual"
		case "2":
			historico.Metodo = "Calculada"
		case "3":
			historico.Metodo = "Ajustada"
		}
		historicos = append(historicos, historico)
		log.Println(historico)
	}
	return historicos
}

func LoadHistoricosComponente(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Historicos do Componente")
	r.ParseForm()
	var entidadeId = r.FormValue("entidadeId")
	var cicloId = r.FormValue("cicloId")
	var pilarId = r.FormValue("pilarId")
	var componenteId = r.FormValue("componenteId")
	var filtro mdl.Historico
	filtro.EntidadeId = entidadeId
	filtro.CicloId = cicloId
	filtro.PilarId = pilarId
	filtro.ComponenteId = componenteId
	historicos := ListHistoricosComponente(filtro)
	jsonHistoricos, _ := json.Marshal(historicos)
	w.Write([]byte(jsonHistoricos))
	log.Println("JSON Históricos do Componente")
}

func ListHistoricosComponente(filtro mdl.Historico) []mdl.Historico {
	log.Println("List Históricos do Componente")
	sql :=
		"SELECT  " +
			"	a.id_produto_componente_historico,  " +
			"	id_entidade,  " +
			"	id_ciclo,  " +
			"	id_pilar,  " +
			"	id_componente,  " +
			"	coalesce(format(inicia_em,'dd/MM/yyyy'),'') as inicia_em,  " +
			"	coalesce(format(inicia_em_anterior,'dd/MM/yyyy'),'') as inicia_em_anterior,  " +
			"	coalesce(format(termina_em,'dd/MM/yyyy'),'') as termina_em,  " +
			"	coalesce(format(termina_em_anterior,'dd/MM/yyyy'),'') as termina_em_anterior,  " +
			"	coalesce(config,'') as config,  " +
			"	coalesce(config_anterior,'') as config_anterior,  " +
			"	coalesce(peso,0),  " +
			"	coalesce(id_tipo_pontuacao,0),  " +
			"	coalesce(nota,0),  " +
			"	tipo_alteracao,  " +
			"	coalesce(id_auditor,0),  " +
			"	coalesce(auditor_anterior_id,0),  " +
			"	a.id_author,  " +
			"	coalesce(b.name,'') as author_name, " +
			"	coalesce(format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'),'') as alterado_em,  " +
			"	case " +
			" 		when tipo_alteracao = 'R' then justificativa " +
			"       when tipo_alteracao = 'I' then motivacao_reprogramacao " +
			"       when tipo_alteracao = 'T' then motivacao_reprogramacao " +
			"       when tipo_alteracao = 'P' then motivacao_config " +
			"	end as motivacao " +
			"	FROM virtus.produtos_componentes_historicos a " +
			"	LEFT JOIN virtus.users b ON a.id_author = b.id_user " +
			"	WHERE a.id_entidade = " + filtro.EntidadeId + " AND " +
			"   a.id_ciclo = " + filtro.CicloId + " AND " +
			"	a.id_pilar = " + filtro.PilarId + " AND " +
			"	a.id_componente = " + filtro.ComponenteId +
			" 	ORDER BY a.criado_em DESC "
	log.Println(sql)
	rows, _ := Db.Query(sql)
	defer rows.Close()
	var historicos []mdl.Historico
	var historico mdl.Historico
	for rows.Next() {
		rows.Scan(
			&historico.Id,
			&historico.EntidadeId,
			&historico.CicloId,
			&historico.PilarId,
			&historico.ComponenteId,
			&historico.IniciaEm,
			&historico.IniciaEmAnterior,
			&historico.TerminaEm,
			&historico.TerminaEmAnterior,
			&historico.Config,
			&historico.ConfigAnterior,
			&historico.Peso,
			&historico.Metodo,
			&historico.Nota,
			&historico.TipoAlteracao,
			&historico.AuditorNovoId,
			&historico.AuditorAnteriorId,
			&historico.AutorId,
			&historico.AutorNome,
			&historico.AlteradoEm,
			&historico.Motivacao)
		switch historico.Metodo {
		case "1":
			historico.Metodo = "Manual"
		case "2":
			historico.Metodo = "Calculada"
		case "3":
			historico.Metodo = "Ajustada"
		}
		historicos = append(historicos, historico)
		log.Println(historico)
	}
	return historicos
}

func registrarHistoricoPesoPilar(produto mdl.ProdutoPilar, currentUser mdl.User) {
	log.Println("========== registrarHistoricoPesoPilar ===========")
	sqlStatement := "INSERT INTO virtus.produtos_pilares_historicos( " +
		"	id_entidade,  " +
		"	id_ciclo,  " +
		"	id_pilar,  " +
		"	id_tipo_pontuacao,  " +
		"	peso,  " +
		"	nota,  " +
		"	tipo_alteracao,  " +
		"	motivacao_peso,  " +
		"	id_supervisor,  " +
		"	id_auditor,  " +
		"	id_author,  " +
		"	criado_em,  " +
		"	id_versao_origem,  " +
		"	id_status) " +
		"SELECT  " +
		"	id_entidade,  " +
		"	id_ciclo,  " +
		"	id_pilar,  " +
		"	id_tipo_pontuacao,  " +
		"	peso,  " +
		"	nota,  " +
		"	'P',  " +
		"	motivacao_peso,  " +
		"	id_supervisor,  " +
		"	id_auditor,  " +
		"	" + strconv.FormatInt(currentUser.Id, 10) + ",  " +
		"	GETDATE(),  " +
		"	id_author,  " +
		"	id_status " +
		"	FROM virtus.produtos_pilares " +
		"	WHERE id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) + " AND " +
		"	id_ciclo = " + strconv.FormatInt(produto.CicloId, 10) + " AND " +
		"	id_pilar = " + strconv.FormatInt(produto.PilarId, 10)
	log.Println(sqlStatement)
	Db.QueryRow(sqlStatement)
}

func LoadHistoricosPilar(w http.ResponseWriter, r *http.Request) {
	log.Println("Load Historicos do Pilar")
	r.ParseForm()
	var entidadeId = r.FormValue("entidadeId")
	var cicloId = r.FormValue("cicloId")
	var pilarId = r.FormValue("pilarId")
	var filtro mdl.Historico
	filtro.EntidadeId = entidadeId
	filtro.CicloId = cicloId
	filtro.PilarId = pilarId
	historicos := ListHistoricosPilar(filtro)
	jsonHistoricos, _ := json.Marshal(historicos)
	w.Write([]byte(jsonHistoricos))
	log.Println("JSON Históricos do Pilar")
}

func ListHistoricosPilar(filtro mdl.Historico) []mdl.Historico {
	log.Println("List Históricos do Pilar")
	sql :=
		"SELECT  " +
			"	a.id_produto_pilare_historico,  " +
			"	id_entidade,  " +
			"	id_ciclo,  " +
			"	id_pilar,  " +
			"	coalesce(peso,0),  " +
			"	id_tipo_pontuacao,  " +
			"	coalesce(nota,0),  " +
			"	tipo_alteracao,  " +
			"	a.id_author,  " +
			"	coalesce(b.name,'') as author_name, " +
			"	coalesce(format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'),'') as alterado_em,  " +
			"	motivacao_peso  " +
			"	FROM virtus.produtos_pilares_historicos a " +
			"	LEFT JOIN virtus.users b ON a.id_author = b.id_user " +
			"	WHERE a.id_entidade = " + filtro.EntidadeId + " AND " +
			"   a.id_ciclo = " + filtro.CicloId + " AND " +
			"	a.id_pilar = " + filtro.PilarId +
			" 	ORDER BY a.criado_em DESC "
	log.Println(sql)
	rows, _ := Db.Query(sql)
	defer rows.Close()
	var historicos []mdl.Historico
	var historico mdl.Historico
	for rows.Next() {
		rows.Scan(
			&historico.Id,
			&historico.EntidadeId,
			&historico.CicloId,
			&historico.PilarId,
			&historico.Peso,
			&historico.Metodo,
			&historico.Nota,
			&historico.TipoAlteracao,
			&historico.AutorId,
			&historico.AutorNome,
			&historico.AlteradoEm,
			&historico.Motivacao)
		switch historico.Metodo {
		case "1":
			historico.Metodo = "Manual"
		case "2":
			historico.Metodo = "Calculada"
		case "3":
			historico.Metodo = "Ajustada"
		}
		historicos = append(historicos, historico)
		log.Println(historico)
	}
	return historicos
}
