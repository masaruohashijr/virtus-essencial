package handlers

import (
	//"encoding/json"
	"html/template"
	"log"
	"net/http"
	"strconv"
	mdl "virtus-essencial/models"
	sec "virtus-essencial/security"
)

func ListSobreHandler(w http.ResponseWriter, r *http.Request) {
	log.Println("About")
	currentUser := GetUserInCookie(w, r)
	if sec.IsAuthenticated(w, r) && HasPermission(currentUser, "listChamados") {
		chamadoEndStatusId := GetEndStatus("chamado")
		sql := "SELECT " +
			" a.id_chamado, " +
			" coalesce(a.titulo,''), " +
			" coalesce(a.descricao,''), " +
			" coalesce(a.id_responsavel,0), " +
			" coalesce(d.name,'') as responsavel_name, " +
			" coalesce(a.id_relator, 0), " +
			" coalesce(e.name,'') as relator_name, " +
			" coalesce(format(a.inicia_em,'dd/MM/yyyy'),''), " +
			" coalesce(format(a.pronto_em,'dd/MM/yyyy'),''), " +
			" a.id_tipo_chamado, " +
			//			" case " +
			//			"   when a.id_tipo_chamado = 'A' then 'Adequação' " +
			//			"   when a.id_tipo_chamado = 'C' then 'Correção' " +
			//			"   when a.id_tipo_chamado = 'D' then 'Dúvida' " +
			//			"   when a.id_tipo_chamado = 'M' then 'Melhoria' " +
			//			"   when a.id_tipo_chamado = 'S' then 'Sugestão' " +
			//			"   else 'Tarefa' " +
			//			" end, " +
			" a.id_prioridade, " +
			//			" case " +
			//			"   when a.prioridade_id = 'A' then 'Alta' " +
			//			"   when a.prioridade_id = 'M' then 'Média' " +
			//			"   else 'Baixa' " +
			//			" end, " +
			" coalesce(a.estimativa,0), " +
			" coalesce(a.id_author,0), " +
			" coalesce(b.name,''), " +
			" coalesce(format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'),''), " +
			" coalesce(c.name,'') as cstatus, " +
			" coalesce(a.id_status,0), " +
			" coalesce(a.id_versao_origem,0) " +
			" FROM virtus.chamados a " +
			" LEFT JOIN virtus.users b ON a.id_author = b.id_user " +
			" LEFT JOIN virtus.status c ON a.id_status = c.id_status " +
			" LEFT JOIN virtus.users d ON a.id_responsavel = d.id_user " +
			" LEFT JOIN virtus.users e ON a.id_relator = e.id_user " +
			" WHERE a.id_status = " + strconv.Itoa(chamadoEndStatusId) +
			" order by a.id_chamado desc"
		log.Println(sql)
		rows, _ := Db.Query(sql)
		defer rows.Close()
		var chamados []mdl.Chamado
		var chamado mdl.Chamado
		var i = 1
		for rows.Next() {
			rows.Scan(
				&chamado.Id,
				&chamado.Titulo,
				&chamado.Descricao,
				&chamado.ResponsavelId,
				&chamado.ResponsavelName,
				&chamado.RelatorId,
				&chamado.RelatorName,
				&chamado.IniciaEm,
				&chamado.ProntoEm,
				&chamado.TipoChamadoId,
				&chamado.PrioridadeId,
				&chamado.Estimativa,
				&chamado.AuthorId,
				&chamado.AuthorName,
				&chamado.C_CriadoEm,
				&chamado.CStatus,
				&chamado.StatusId,
				&chamado.IdVersaoOrigem)
			chamado.Order = i
			i++
			if chamado.TipoChamadoId == "A" {
				chamado.TipoChamadoId = "Adequação"
			} else if chamado.TipoChamadoId == "C" {
				chamado.TipoChamadoId = "Correção"
			} else if chamado.TipoChamadoId == "D" {
				chamado.TipoChamadoId = "Dúvida"
			} else if chamado.TipoChamadoId == "M" {
				chamado.TipoChamadoId = "Melhoria"
			} else if chamado.TipoChamadoId == "S" {
				chamado.TipoChamadoId = "Sugestão"
			} else {
				chamado.TipoChamadoId = "Tarefa"
			}

			if chamado.PrioridadeId == "E" {
				chamado.PrioridadeId = "Essencial"
			} else if chamado.PrioridadeId == "A" {
				chamado.PrioridadeId = "Alta"
			} else if chamado.PrioridadeId == "M" {
				chamado.PrioridadeId = "Média"
			} else if chamado.PrioridadeId == "B" {
				chamado.PrioridadeId = "Baixa"
			} else {
				chamado.PrioridadeId = "Desejável"
			}
			chamados = append(chamados, chamado)
		}
		var page mdl.PageChamados
		page.Chamados = chamados
		page.AppName = mdl.AppName
		page.Title = "Sobre" + mdl.Ambiente
		page.LoggedUser = BuildLoggedUser(GetUserInCookie(w, r))
		var tmpl = template.Must(template.ParseGlob("tiles/sobre/*"))
		tmpl.ParseGlob("tiles/*")
		tmpl.ExecuteTemplate(w, "Main-Sobre", page)
	} else {
		http.Redirect(w, r, "/logout", 301)
	}
}
