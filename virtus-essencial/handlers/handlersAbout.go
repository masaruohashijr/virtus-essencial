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
			" a.id, " +
			" coalesce(a.titulo,''), " +
			" coalesce(a.descricao,''), " +
			" coalesce(a.responsavel_id,0), " +
			" coalesce(d.name,'') as responsavel_name, " +
			" coalesce(a.relator_id, 0), " +
			" coalesce(e.name,'') as relator_name, " +
			" coalesce(format(a.inicia_em,'dd/MM/yyyy'),''), " +
			" coalesce(format(a.pronto_em,'dd/MM/yyyy'),''), " +
			" case " +
			"   when a.tipo_chamado_id = 'A' then 'Adequação' " +
			"   when a.tipo_chamado_id = 'C' then 'Correção' " +
			"   when a.tipo_chamado_id = 'D' then 'Dúvida' " +
			"   when a.tipo_chamado_id = 'M' then 'Melhoria' " +
			"   when a.tipo_chamado_id = 'S' then 'Sugestão' " +
			"   else 'Tarefa' " +
			" end, " +
			" case " +
			"   when a.prioridade_id = 'A' then 'Alta' " +
			"   when a.prioridade_id = 'M' then 'Média' " +
			"   else 'Baixa' " +
			" end, " +
			" coalesce(a.estimativa,0), " +
			" coalesce(a.author_id,0), " +
			" coalesce(b.name,''), " +
			" coalesce(format(a.criado_em, 'dd/MM/yyyy HH:mm:ss'),''), " +
			" coalesce(c.name,'') as cstatus, " +
			" coalesce(a.status_id,0), " +
			" coalesce(a.id_versao_origem,0) " +
			" FROM chamados a " +
			" LEFT JOIN users b ON a.author_id = b.id " +
			" LEFT JOIN status c ON a.status_id = c.id " +
			" LEFT JOIN users d ON a.responsavel_id = d.id " +
			" LEFT JOIN users e ON a.relator_id = e.id " +
			" WHERE a.status_id = " + strconv.Itoa(chamadoEndStatusId) +
			" order by a.id desc"
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
