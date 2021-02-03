package handlers

import (
	"log"
	"strconv"
	"strings"
	mdl "virtus-essencial/models"
)

func registrarCronogramaComponente(produto mdl.ProdutoComponente, currentUser mdl.User, tipoData string) {
	sqlStatement := "UPDATE produtos_componentes SET "
	if tipoData == "iniciaEm" {
		sqlStatement += " inicia_em ='" + produto.IniciaEm + "', "
	} else {
		sqlStatement += " termina_em ='" + produto.TerminaEm + "', "
	}
	sqlStatement += " motivacao_reprogramacao ='" + produto.Motivacao + "'" +
		" WHERE entidade_id= " + strconv.FormatInt(produto.EntidadeId, 10) +
		" AND ciclo_id= " + strconv.FormatInt(produto.CicloId, 10) +
		" AND pilar_id= " + strconv.FormatInt(produto.PilarId, 10) +
		" AND componente_id= " + strconv.FormatInt(produto.ComponenteId, 10)
	log.Println(sqlStatement)
	updtForm, _ := Db.Prepare(sqlStatement)
	_, err := updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
}

func registrarAuditorComponente(produto mdl.ProdutoComponente, currentUser mdl.User) {
	sqlStatement := "UPDATE produtos_componentes SET " +
		" auditor_id=" + strconv.FormatInt(produto.AuditorId, 10) + ", justificativa='" + produto.Motivacao + "'" +
		" WHERE entidade_id= " + strconv.FormatInt(produto.EntidadeId, 10) +
		" AND ciclo_id= " + strconv.FormatInt(produto.CicloId, 10) +
		" AND pilar_id= " + strconv.FormatInt(produto.PilarId, 10) +
		" AND componente_id= " + strconv.FormatInt(produto.ComponenteId, 10)
	log.Println(sqlStatement)
	updtForm, _ := Db.Prepare(sqlStatement)
	_, err := updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
}

func registrarNotaElemento(produto mdl.ProdutoElemento, currentUser mdl.User) mdl.ValoresAtuais {
	sqlStatement := "UPDATE produtos_elementos SET nota = " + strconv.Itoa(produto.Nota) + ", " +
		" motivacao_nota = ? , " +
		" tipo_pontuacao_id = (SELECT DISTINCT case when b.supervisor_id = " + strconv.FormatInt(currentUser.Id, 10) +
		" then 3 when 2 = " + strconv.FormatInt(currentUser.Role, 10) + " then 3 else 1 end " +
		" FROM produtos_componentes b WHERE " +
		" entidade_id = b.entidade_id and " +
		" ciclo_id = b.ciclo_id and " +
		" pilar_id = b.pilar_id and " +
		// " a.plano_id = b.plano_id and " +
		" componente_id = b.componente_id) " +
		" WHERE entidade_id = " + strconv.FormatInt(produto.EntidadeId, 10) +
		" AND ciclo_id = " + strconv.FormatInt(produto.CicloId, 10) +
		" AND pilar_id = " + strconv.FormatInt(produto.PilarId, 10) +
		" AND plano_id = " + strconv.FormatInt(produto.PlanoId, 10) +
		" AND componente_id = " + strconv.FormatInt(produto.ComponenteId, 10) +
		" AND elemento_id = " + strconv.FormatInt(produto.ElementoId, 10) +
		" AND nota <> " + strconv.Itoa(produto.Nota)
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec(produto.Motivacao)
	atualizarPesoTiposNotas(produto, currentUser)
	atualizarPesoPlanos(produto, currentUser)
	atualizarPesoComponentes(produto, currentUser)
	// PESOS ATUAIS
	pesosAtuais := loadPesosAtuais(produto)
	pesosAtuais.ElementoPeso = strconv.FormatFloat(produto.Peso, 'f', 2, 64)
	// Recalcular Notas
	atualizarTipoNotaNota(produto)
	atualizarPlanoNota(produto)
	atualizarComponenteNota(produto)
	atualizarPilarNota(produto)
	atualizarCicloNota(produto)
	// NOTAS ATUAIS
	notasAtuais := loadNotasAtuais(produto)
	valoresAtuais := montarValoresAtuais(pesosAtuais, notasAtuais)
	return valoresAtuais
}

func atualizarPilarNota(produto mdl.ProdutoElemento) {
	// PRODUTOS_PILARES
	sqlStatement := "UPDATE produtos_pilares " +
		" SET nota = (select DISTINCT " +
		" round(sum(nota*peso)/sum(peso),2) AS media " +
		" FROM produtos_componentes b " +
		" WHERE " +
		" entidade_id = b.entidade_id " +
		" AND ciclo_id = b.ciclo_id  " +
		" AND pilar_id = b.pilar_id " +
		" AND b.nota IS NOT NULL " +
		" GROUP BY b.entidade_id,  " +
		" b.ciclo_id, " +
		" b.pilar_id ) " +
		" WHERE entidade_id = ? " +
		" AND ciclo_id = ? "
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec(produto.EntidadeId, produto.CicloId)
}

func atualizarComponenteNota(produto mdl.ProdutoElemento) {
	// PRODUTOS_COMPONENTES
	sqlStatement := "UPDATE produtos_componentes " +
		" set nota = (select  " +
		" round(sum(nota*peso)/100,2) as media " +
		" FROM produtos_planos b " +
		" WHERE " +
		" entidade_id = b.entidade_id " +
		" and ciclo_id = b.ciclo_id  " +
		" and pilar_id = b.pilar_id " +
		" and componente_id = b.componente_id " +
		" and id_versao_origem is null " +
		" GROUP BY b.entidade_id,  " +
		" b.ciclo_id, " +
		" b.pilar_id, " +
		" b.componente_id " +
		" HAVING sum(peso)>0) " +
		" WHERE entidade_id = ? " +
		" AND ciclo_id = ? "
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec(produto.EntidadeId, produto.CicloId)
}

func atualizarPlanoNota(produto mdl.ProdutoElemento) {
	// PRODUTOS_PLANOS
	sqlStatement := "UPDATE produtos_planos " +
		" set nota = (select  " +
		" round(sum(nota*peso)/sum(peso),2) as media " +
		" FROM produtos_tipos_notas b " +
		" WHERE " +
		" produtos_planos.entidade_id = b.entidade_id " +
		" AND produtos_planos.ciclo_id = b.ciclo_id  " +
		" AND produtos_planos.pilar_id = b.pilar_id " +
		" AND produtos_planos.componente_id = b.componente_id " +
		" AND produtos_planos.plano_id = b.plano_id " +
		" AND produtos_planos.nota IS NOT NULL " +
		" GROUP BY b.entidade_id,  " +
		" b.ciclo_id, " +
		" b.pilar_id, " +
		" b.plano_id, " +
		" b.componente_id " +
		" HAVING sum(peso)>0) " +
		" WHERE entidade_id = " + strconv.FormatInt(produto.EntidadeId, 10) +
		" AND ciclo_id = " + strconv.FormatInt(produto.CicloId, 10) +
		" AND pilar_id = " + strconv.FormatInt(produto.PilarId, 10) +
		" AND componente_id = " + strconv.FormatInt(produto.ComponenteId, 10) +
		" AND plano_id = " + strconv.FormatInt(produto.PlanoId, 10)
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec()

}

func atualizarTipoNotaNota(produto mdl.ProdutoElemento) {
	// PRODUTOS_TIPOS_NOTAS
	sqlStatement := "UPDATE produtos_tipos_notas " +
		" set nota = (select  " +
		" round(sum(nota*peso)/sum(peso),2) as media " +
		" FROM produtos_elementos b " +
		" WHERE " +
		" produtos_tipos_notas.entidade_id = b.entidade_id " +
		" and produtos_tipos_notas.ciclo_id = b.ciclo_id  " +
		" and produtos_tipos_notas.pilar_id = b.pilar_id " +
		" and produtos_tipos_notas.plano_id = b.plano_id " +
		" and produtos_tipos_notas.componente_id = b.componente_id " +
		" and produtos_tipos_notas.tipo_nota_id = b.tipo_nota_id " +
		" and (b.peso > 0 AND b.nota > 0) " +
		" GROUP BY b.entidade_id,  " +
		" b.ciclo_id, " +
		" b.pilar_id, " +
		" b.plano_id, " +
		" b.componente_id, " +
		" b.tipo_nota_id " +
		" HAVING sum(peso)>0) " +
		" WHERE entidade_id = " + strconv.FormatInt(produto.EntidadeId, 10) +
		" AND ciclo_id = " + strconv.FormatInt(produto.CicloId, 10) +
		" AND pilar_id = " + strconv.FormatInt(produto.PilarId, 10) +
		" AND componente_id = " + strconv.FormatInt(produto.ComponenteId, 10) +
		" AND plano_id = " + strconv.FormatInt(produto.PlanoId, 10) +
		" AND tipo_nota_id = " + strconv.FormatInt(produto.TipoNotaId, 10)
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec()
}

func atualizarCicloNota(produto mdl.ProdutoElemento) {
	// PRODUTOS_CICLOS
	sqlStatement := "UPDATE produtos_ciclos SET nota = R.media FROM " +
		" (SELECT round(sum(nota*peso)/sum(peso), 2) AS media " +
		" FROM produtos_pilares b " +
		" WHERE b.entidade_id = " + strconv.FormatInt(produto.EntidadeId, 10) +
		" AND b.ciclo_id = " + strconv.FormatInt(produto.CicloId, 10) +
		" AND b.nota is not null) R " +
		" WHERE entidade_id = " + strconv.FormatInt(produto.EntidadeId, 10) +
		" AND ciclo_id = " + strconv.FormatInt(produto.CicloId, 10)
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec()

}

func registrarTiposPontuacao(produto mdl.ProdutoElemento, currentUser mdl.User) {
	sqlStatement := "UPDATE produtos_tipos_notas SET " +
		" tipo_pontuacao_id = (SELECT case when b.supervisor_id = ? " +
		" then 3 else 2 end FROM produtos_componentes b where id = b.id) " +
		" WHERE entidade_id = ? " +
		" AND  ciclo_id = ? " +
		" AND  pilar_id = ? " +
		" AND  componente_id = ? " +
		" AND  tipo_nota_id = ? "
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec(
		currentUser.Id,
		produto.EntidadeId,
		produto.CicloId,
		produto.PilarId,
		produto.ComponenteId,
		produto.TipoNotaId)
	sqlStatement = "UPDATE produtos_componentes SET " +
		" tipo_pontuacao_id = (SELECT case when b.supervisor_id = ? " +
		" then 3 else 2 end FROM produtos_componentes b where id = b.id) " +
		" WHERE entidade_id = ? " +
		" AND  ciclo_id = ? " +
		" AND  pilar_id = ? " +
		" AND  componente_id = ? "
	log.Println(sqlStatement)
	updtForm, err = Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec(
		currentUser.Id,
		produto.EntidadeId,
		produto.CicloId,
		produto.PilarId,
		produto.ComponenteId)
	sqlStatement = "UPDATE produtos_pilares SET " +
		" tipo_pontuacao_id = (SELECT case when b.supervisor_id = ? " +
		" then 3 else 2 end FROM produtos_pilares b where id = b.id) " +
		" WHERE entidade_id = ? " +
		" AND  ciclo_id = ? " +
		" AND  pilar_id = ? "
	log.Println(sqlStatement)
	updtForm, err = Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec(
		currentUser.Id,
		produto.EntidadeId,
		produto.CicloId,
		produto.PilarId)
	sqlStatement = "UPDATE produtos_ciclos SET " +
		" tipo_pontuacao_id = (SELECT case when b.supervisor_id = ? " +
		" then 3 else 2 end FROM produtos_ciclos b where id = b.id) " +
		" WHERE entidade_id = ? " +
		" AND  ciclo_id = ? "
	log.Println(sqlStatement)
	updtForm, err = Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec(
		currentUser.Id,
		produto.EntidadeId,
		produto.CicloId)
}

func registrarPesoElemento(produto mdl.ProdutoElemento, currentUser mdl.User) mdl.ValoresAtuais {
	// PESOS ELEMENTOS
	sqlStatement := "UPDATE produtos_elementos SET peso = ?, motivacao_peso = ? " +
		" WHERE entidade_id = ? AND " +
		" ciclo_id = ? AND " +
		" pilar_id = ? AND " +
		" plano_id = ? AND " +
		" componente_id = ? AND " +
		" elemento_id = ? "
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec(produto.Peso,
		produto.Motivacao,
		produto.EntidadeId,
		produto.CicloId,
		produto.PilarId,
		produto.PlanoId,
		produto.ComponenteId,
		produto.ElementoId)
	atualizarPesoTiposNotas(produto, currentUser)
	atualizarPesoPlanos(produto, currentUser)
	atualizarPesoComponentes(produto, currentUser)
	// PESOS ATUAIS
	pesosAtuais := loadPesosAtuais(produto)
	pesosAtuais.ElementoPeso = strconv.FormatFloat(produto.Peso, 'f', 2, 64)
	// Recalcular Notas
	atualizarTipoNotaNota(produto)
	atualizarPlanoNota(produto)
	atualizarComponenteNota(produto)
	atualizarPilarNota(produto)
	atualizarCicloNota(produto)
	// NOTAS ATUAIS
	notasAtuais := loadNotasAtuais(produto)
	valoresAtuais := montarValoresAtuais(pesosAtuais, notasAtuais)
	return valoresAtuais
}

func montarValoresAtuais(ps mdl.PesosAtuais, ns mdl.NotasAtuais) mdl.ValoresAtuais {
	var va mdl.ValoresAtuais
	/* Pesos */
	va.CicloPeso = ps.CicloPeso
	log.Println("ps.CicloPeso: " + ps.CicloPeso)
	va.PilarPeso = ps.PilarPeso
	log.Println("va.PilarPeso: " + ps.PilarPeso)
	va.ComponentePeso = ps.ComponentePeso
	log.Println("va.ComponentePeso: " + ps.ComponentePeso)
	va.PlanoPeso = ps.PlanoPeso
	log.Println("va.PlanoPeso: " + ps.PlanoPeso)
	va.TipoNotaPeso = ps.TipoNotaPeso
	log.Println("va.TipoNotaPeso: " + ps.TipoNotaPeso)
	va.ElementoPeso = ps.ElementoPeso
	log.Println("va.ElementoPeso: " + ps.ElementoPeso)
	/* Notas */
	va.TipoNotaNota = ns.TipoNotaNota
	log.Println("va.TipoNotaNota: " + ns.TipoNotaNota)
	va.PlanoNota = ns.PlanoNota
	log.Println("va.PlanoNota: " + ns.PlanoNota)
	va.ComponenteNota = ns.ComponenteNota
	log.Println("va.ComponenteNota: " + ns.ComponenteNota)
	va.PilarNota = ns.PilarNota
	log.Println("va.PilarNota: " + ns.PilarNota)
	va.CicloNota = ns.CicloNota
	log.Println("va.CicloNota: " + ns.CicloNota)
	return va
}

func atualizarPesoTiposNotas(produto mdl.ProdutoElemento, currentUser mdl.User) {
	// PESOS TIPOS NOTAS
	sqlStatement := "WITH R1 AS  " +
		"  (SELECT entidade_id, " +
		"                  ciclo_id, " +
		"                  pilar_id, " +
		"                  plano_id, " +
		"                  componente_id, " +
		"                  tipo_nota_id, " +
		"                  round(sum(peso), 2) AS TOTAL " +
		"           FROM produtos_elementos " +
		"           GROUP BY entidade_id, " +
		"                    ciclo_id, " +
		"                    pilar_id, " +
		"                    plano_id, " +
		"                    componente_id, " +
		"                    tipo_nota_id), " +
		"  R2 AS (SELECT entidade_id, " +
		"                  ciclo_id, " +
		"                  pilar_id, " +
		"                  plano_id, " +
		"                  componente_id, " +
		"                  tipo_nota_id, " +
		"                  count(1) AS CONTADOR " +
		"           FROM produtos_elementos " +
		"           WHERE peso <> 0 " +
		"           GROUP BY entidade_id, " +
		"                    ciclo_id, " +
		"                    pilar_id, " +
		"                    plano_id, " +
		"                    componente_id, " +
		"                    tipo_nota_id), " +
		"  TMP AS " +
		"       (SELECT r1.entidade_id, " +
		"               r1.ciclo_id, " +
		"               r1.pilar_id, " +
		"               r1.plano_id, " +
		"               r1.componente_id, " +
		"               r1.tipo_nota_id, " +
		"               round((r1.TOTAL/r2.contador), 2) AS PONDERACAO " +
		"        FROM R1         " +
		"        INNER JOIN R2 " +
		"  		ON (r1.entidade_id = r2.entidade_id " +
		"  		AND r1.ciclo_id = r2.ciclo_id " +
		"  		AND r1.pilar_id = r2.pilar_id " +
		"  		AND r1.plano_id = r2.plano_id " +
		"  		AND r1.componente_id = r2.componente_id " +
		"  		AND r1.tipo_nota_id = r2.tipo_nota_id)), " +
		"  T2 as (SELECT entidade_id, " +
		"               pilar_id, " +
		"               plano_id, " +
		"               ciclo_id, " +
		"               componente_id, " +
		"               SUM(PONDERACAO) AS TOTAL_PESOS_TNS " +
		"        FROM TMP " +
		"        GROUP BY entidade_id,pilar_id,plano_id,ciclo_id,componente_id), " +
		"  T1 AS (SELECT A1.entidade_id, " +
		"  	A1.ciclo_id, " +
		"  	A1.pilar_id, " +
		"  	A1.plano_id,                             " +
		"  	A1.componente_id, " +
		"  	A1.tipo_nota_id, " +
		"  	round((A1.PONDERACAO/T2.total_pesos_tns)*100, 2) AS peso " +
		"  	   FROM TMP A1 " +
		"  	   INNER JOIN T2 ON (A1.entidade_id = t2.entidade_id " +
		"  							 AND A1.pilar_id = t2.pilar_id " +
		"  							 AND A1.plano_id = t2.plano_id " +
		"  							 AND A1.ciclo_id = t2.ciclo_id " +
		"  							 AND A1.componente_id = t2.componente_id) " +
		"  	   WHERE A1.componente_id = " + strconv.FormatInt(produto.ComponenteId, 10) +
		"  		 AND A1.pilar_id = " + strconv.FormatInt(produto.PilarId, 10) +
		"  		 AND A1.plano_id = " + strconv.FormatInt(produto.PlanoId, 10) +
		"  		 AND A1.ciclo_id = " + strconv.FormatInt(produto.CicloId, 10) +
		"  		 AND A1.entidade_id = " + strconv.FormatInt(produto.EntidadeId, 10) +
		"  ) " +
		"  UPDATE produtos_tipos_notas  " +
		"  SET peso = round(T1.peso,2) FROM T1 " +
		"  WHERE produtos_tipos_notas.tipo_nota_id = T1.tipo_nota_id " +
		"    AND produtos_tipos_notas.componente_id = T1.componente_id " +
		"    AND produtos_tipos_notas.plano_id = T1.plano_id " +
		"    AND produtos_tipos_notas.pilar_id = T1.pilar_id " +
		"    AND produtos_tipos_notas.ciclo_id = T1.ciclo_id " +
		"    AND produtos_tipos_notas.entidade_id = T1.entidade_id"
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec()
}

func atualizarPesoPlanos(produto mdl.ProdutoElemento, currentUser mdl.User) {
	// PESOS PLANOS
	sqlStatement := "WITH total AS " +
		"      (SELECT a.entidade_id, " +
		"              a.ciclo_id, " +
		"              a.pilar_id, " +
		"              a.componente_id, " +
		"              sum(p.recurso_garantidor) AS total " +
		"       FROM produtos_planos a " +
		"       INNER JOIN planos p ON (p.entidade_id = a.entidade_id " +
		"                               AND p.id = a.plano_id) " +
		"       WHERE a.entidade_id = " + strconv.FormatInt(produto.EntidadeId, 10) +
		"         AND a.ciclo_id = " + strconv.FormatInt(produto.CicloId, 10) +
		"         AND a.pilar_id = " + strconv.FormatInt(produto.PilarId, 10) +
		"         AND a.componente_id = " + strconv.FormatInt(produto.ComponenteId, 10) +
		"       GROUP BY a.entidade_id, " +
		"              a.ciclo_id, " +
		"              a.pilar_id, " +
		"              a.componente_id), " +
		" R1 AS (SELECT a.entidade_id, " +
		"                          a.ciclo_id, " +
		"                          a.pilar_id, " +
		"                          a.plano_id, " +
		"                          a.componente_id, " +
		"                          round(p.recurso_garantidor/t.total, 2)*100 AS peso_percentual " +
		"    FROM produtos_planos a " +
		"    INNER JOIN planos p ON (p.entidade_id = a.entidade_id " +
		"                            AND p.id = a.plano_id) " +
		"    INNER JOIN total t ON (a.entidade_id = t.entidade_id " +
		"                           AND a.ciclo_id = t.ciclo_id " +
		"                           AND a.pilar_id = t.pilar_id " +
		"                           AND a.componente_id = t.componente_id)), " +
		" R2 AS (SELECT a.entidade_id, " +
		"     a.ciclo_id, " +
		"     a.pilar_id, " +
		"     a.plano_id, " +
		"     a.componente_id, " +
		"     round(p.recurso_garantidor/t.total, 2)*100 AS peso_percentual " +
		" FROM produtos_planos a " +
		"    INNER JOIN planos p ON (p.entidade_id = a.entidade_id " +
		"                            AND p.id = a.plano_id) " +
		"    INNER JOIN total t ON (a.entidade_id = t.entidade_id " +
		"                           AND a.ciclo_id = t.ciclo_id " +
		"                           AND a.pilar_id = t.pilar_id " +
		"                           AND a.componente_id = t.componente_id)) " +
		" UPDATE produtos_planos SET peso = round(R2.peso_percentual,2) FROM R2 " +
		" WHERE " +
		" R2.entidade_id = produtos_planos.entidade_id " +
		" AND R2.ciclo_id = produtos_planos.ciclo_id " +
		" AND R2.pilar_id = produtos_planos.pilar_id " +
		" AND R2.componente_id = produtos_planos.componente_id " +
		" and R2.plano_id = produtos_planos.plano_id "
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec()
}

func atualizarPesoComponentes(produto mdl.ProdutoElemento, currentUser mdl.User) {
	// PESOS COMPONENTES
	log.Println("*** ATUALIZAR PESO COMPONENTE ***")
	sqlStatement := "WITH TMP AS " +
		"   (SELECT r1.entidade_id, " +
		"           r1.ciclo_id, " +
		"           r1.pilar_id, " +
		"           r1.plano_id, " +
		"           r1.componente_id, " +
		"           r1.tipo_nota_id, " +
		"           round((r1.TOTAL/r2.contador), 2) AS PONDERACAO " +
		"    FROM " +
		"      (SELECT entidade_id, " +
		"              ciclo_id, " +
		"              pilar_id, " +
		"              plano_id, " +
		"              componente_id, " +
		"              tipo_nota_id, " +
		"              round(sum(peso), 2) AS TOTAL " +
		"       FROM produtos_elementos " +
		"       GROUP BY entidade_id, " +
		"                ciclo_id, " +
		"                pilar_id, " +
		"                plano_id, " +
		"                componente_id, " +
		"                tipo_nota_id) R1 " +
		"    INNER JOIN " +
		"      (SELECT entidade_id, " +
		"              ciclo_id, " +
		"              pilar_id, " +
		"              plano_id, " +
		"              componente_id, " +
		"              tipo_nota_id, " +
		"              count(1) AS CONTADOR " +
		"       FROM produtos_elementos " +
		"       WHERE peso <> 0 " +
		"       GROUP BY entidade_id, " +
		"                ciclo_id, " +
		"                pilar_id, " +
		"                plano_id, " +
		"                componente_id, " +
		"                tipo_nota_id) R2 ON (r1.entidade_id = r2.entidade_id " +
		"                                     AND r1.ciclo_id = r2.ciclo_id " +
		"                                     AND r1.pilar_id = r2.pilar_id " +
		"                                     AND r1.componente_id = r2.componente_id " +
		"                                     AND r1.tipo_nota_id = r2.tipo_nota_id)), " +
		"      T2 AS " +
		"   (SELECT entidade_id, " +
		"           ciclo_id, " +
		"           pilar_id, " +
		"           componente_id, " +
		"           SUM(PONDERACAO) AS TOTAL_PESOS_TNS " +
		"    FROM TMP " +
		"    GROUP BY entidade_id, " +
		"             ciclo_id, " +
		"             pilar_id, " +
		"             componente_id) " +
		" UPDATE produtos_componentes " +
		" SET peso = RR.peso_componente " +
		" FROM " +
		"   (SELECT t1.entidade_id, " +
		"           t1.ciclo_id, " +
		"           t1.pilar_id, " +
		"           t1.componente_id, " +
		"           round((SUM(t1.PONDERACAO*t1.PONDERACAO*p.peso/t2.total_pesos_tns/100)), 2) AS PESO_COMPONENTE " +
		"    FROM TMP T1 " +
		"    INNER JOIN T2 ON (t1.entidade_id = t2.entidade_id " +
		"                      AND t1.pilar_id = t2.pilar_id " +
		"                      AND t1.ciclo_id = t2.ciclo_id " +
		"                      AND t1.componente_id = t2.componente_id) " +
		"    INNER JOIN produtos_planos p ON (t1.entidade_id = p.entidade_id " +
		"                                     AND t1.pilar_id = p.pilar_id " +
		"                                     AND t1.ciclo_id = p.ciclo_id " +
		"                                     AND t1.componente_id = p.componente_id) " +
		"    WHERE t1.componente_id = " + strconv.FormatInt(produto.ComponenteId, 10) +
		"      AND t1.pilar_id = " + strconv.FormatInt(produto.PilarId, 10) +
		"      AND t1.ciclo_id = " + strconv.FormatInt(produto.CicloId, 10) +
		"      AND t1.entidade_id = " + strconv.FormatInt(produto.EntidadeId, 10) +
		"    GROUP BY t1.entidade_id, " +
		"             t1.ciclo_id, " +
		"             t1.pilar_id, " +
		"             t1.componente_id) RR " +
		" WHERE RR.entidade_id = produtos_componentes.entidade_id " +
		"   AND RR.pilar_id = produtos_componentes.pilar_id " +
		"   AND RR.ciclo_id = produtos_componentes.ciclo_id " +
		"   AND RR.componente_id = produtos_componentes.componente_id "
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	_, err = updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
}

func registrarProdutosCiclos(currentUser mdl.User, entidadeId string, cicloId string) {
	sqlStatement := "INSERT INTO produtos_ciclos ( " +
		" entidade_id, " +
		" ciclo_id, " +
		" nota, " +
		" tipo_pontuacao_id, " +
		" author_id, " +
		" criado_em ) " +
		" OUTPUT INSERTED.id " +
		" SELECT " +
		entidadeId + ", " +
		cicloId + ", " +
		" 1, " +
		" ?, " +
		" ?, " +
		" GETDATE() " +
		" FROM ciclos_entidades a " +
		" WHERE NOT EXISTS " +
		"  (SELECT 1 " +
		"   FROM produtos_ciclos b " +
		"   WHERE b.entidade_id = a.entidade_id " +
		"     AND b.ciclo_id = a.ciclo_id) "
	log.Println(sqlStatement)
	produtoCicloId := 0
	err := Db.QueryRow(
		sqlStatement,
		mdl.Calculada,
		currentUser.Id).Scan(&produtoCicloId)
	if err != nil {
		log.Println(err)
	}
	sqlStatement = "INSERT INTO produtos_pilares " +
		" (entidade_id, ciclo_id, pilar_id, peso, nota, tipo_pontuacao_id, author_id, criado_em) " +
		" OUTPUT INSERTED.id " +
		" SELECT " +
		entidadeId + ", " +
		cicloId + ", " +
		" a.pilar_id, " +
		" 0, " +
		" 1, " +
		" ?, " +
		" ?, " +
		" GETDATE() " +
		" FROM pilares_ciclos a " +
		" WHERE NOT EXISTS " +
		"  (SELECT 1 " +
		"   FROM produtos_pilares b " +
		"   WHERE b.entidade_id = " + entidadeId +
		"     AND b.ciclo_id = a.ciclo_id " +
		"     AND b.pilar_id = a.pilar_id)"
	log.Println(sqlStatement)
	produtoPilarId := 0
	err = Db.QueryRow(
		sqlStatement,
		mdl.Calculada,
		currentUser.Id).Scan(&produtoPilarId)
	if err != nil {
		log.Println(err)
	}

	sqlStatement = "INSERT INTO produtos_componentes ( " +
		" entidade_id, " +
		" ciclo_id, " +
		" pilar_id, " +
		" componente_id, " +
		" peso, " +
		" nota, " +
		" tipo_pontuacao_id, " +
		" author_id, " +
		" criado_em ) " +
		" OUTPUT INSERTED.id " +
		" SELECT " + entidadeId + ", " + cicloId + ", a.pilar_id, b.componente_id, " +
		" round(avg(c.peso_padrao),2), 1, " +
		" ?, ?, GETDATE() " +
		" FROM " +
		" PILARES_CICLOS a " +
		" LEFT JOIN COMPONENTES_PILARES b ON (a.pilar_id = b.pilar_id) " +
		" LEFT JOIN ELEMENTOS_COMPONENTES c ON (b.componente_id = c.componente_id) " +
		" WHERE  " +
		" a.ciclo_id = " + cicloId +
		" AND NOT EXISTS " +
		"  (SELECT 1 " +
		"   FROM produtos_componentes c " +
		"   WHERE c.entidade_id = " + entidadeId +
		"     AND c.ciclo_id = a.ciclo_id " +
		"     AND c.pilar_id = a.pilar_id " +
		"     AND c.componente_id = b.componente_id) " +
		" GROUP BY a.ciclo_id,a.pilar_id,b.componente_id ORDER BY 1,2,3,4"
	log.Println(sqlStatement)
	produtoComponenteId := 0
	err = Db.QueryRow(
		sqlStatement,
		mdl.Calculada,
		currentUser.Id).Scan(&produtoComponenteId)
	if err != nil {
		log.Println(err)
	}
}

func registrarProdutosPlanos(param mdl.ProdutoPlano, numPlano string, currentUser mdl.User) {
	sqlStatement := "INSERT INTO produtos_planos ( " +
		" entidade_id, " +
		" ciclo_id, " +
		" pilar_id, " +
		" componente_id, " +
		" plano_id, " +
		//" peso, " +
		" nota, " +
		" tipo_pontuacao_id, " +
		" author_id, " +
		" criado_em ) " +
		" SELECT DISTINCT " + strconv.FormatInt(param.EntidadeId, 10) + ", " +
		strconv.FormatInt(param.CicloId, 10) + ", " +
		strconv.FormatInt(param.PilarId, 10) + ", " +
		strconv.FormatInt(param.ComponenteId, 10) + ", " +
		" p.id, " +
		//" p.recurso_garantidor as peso," +
		" 0 as nota, " +
		" ?, ?, GETDATE() " +
		" FROM " +
		" PILARES_CICLOS a " +
		" LEFT JOIN COMPONENTES_PILARES b ON (a.pilar_id = b.pilar_id) " +
		" LEFT JOIN ELEMENTOS_COMPONENTES c ON (b.componente_id = c.componente_id) " +
		" LEFT JOIN PLANOS p ON (p.entidade_id = " + strconv.FormatInt(param.EntidadeId, 10) +
		" AND p.id = " + numPlano + ") " +
		" WHERE  " +
		" p.entidade_id = " + strconv.FormatInt(param.EntidadeId, 10) +
		" AND p.id = " + numPlano +
		" AND NOT EXISTS " +
		"  (SELECT 1 " +
		"   FROM produtos_planos e " +
		"   WHERE e.entidade_id = " + strconv.FormatInt(param.EntidadeId, 10) +
		"     AND e.ciclo_id = " + strconv.FormatInt(param.CicloId, 10) +
		"     AND e.pilar_id = " + strconv.FormatInt(param.PilarId, 10) +
		"     AND e.componente_id = " + strconv.FormatInt(param.ComponenteId, 10) +
		"     AND e.plano_id IN (" + numPlano + ")) " +
		" ORDER BY 1,2,3,4,5,6"
	log.Println(sqlStatement)
	Db.QueryRow(
		sqlStatement,
		mdl.Calculada,
		currentUser.Id)

	var produto mdl.ProdutoElemento
	produto.EntidadeId = param.EntidadeId
	produto.CicloId = param.CicloId
	produto.PilarId = param.PilarId
	produto.ComponenteId = param.ComponenteId
	atualizarPesoPlanos(produto, currentUser)

	sqlStatement = "INSERT INTO produtos_tipos_notas ( " +
		" entidade_id, " +
		" ciclo_id, " +
		" pilar_id, " +
		" componente_id, " +
		" plano_id, " +
		" tipo_nota_id, " +
		//" peso, " +
		" nota, " +
		" tipo_pontuacao_id, " +
		" author_id, " +
		" criado_em ) " +
		" SELECT p.entidade_id, " +
		" p.ciclo_id, " +
		" p.pilar_id, " +
		" p.componente_id, " +
		" p.plano_id, " +
		" d.tipo_nota_id, " +
		//" 1, " +
		" 0, ?, ?, GETDATE() " +
		" FROM " +
		" PILARES_CICLOS a " +
		" LEFT JOIN COMPONENTES_PILARES b ON a.pilar_id = b.pilar_id " +
		" LEFT JOIN TIPOS_NOTAS_COMPONENTES d ON b.componente_id = d.componente_id " +
		" LEFT JOIN PRODUTOS_PLANOS p ON b.componente_id = p.componente_id " +
		" WHERE  " +
		" a.ciclo_id = " + strconv.FormatInt(param.CicloId, 10) +
		" AND p.entidade_id = " + strconv.FormatInt(param.EntidadeId, 10) +
		" AND p.pilar_id = " + strconv.FormatInt(param.PilarId, 10) +
		" AND p.componente_id = " + strconv.FormatInt(param.ComponenteId, 10) +
		" AND p.plano_id = " + numPlano +
		" AND NOT EXISTS " +
		"  (SELECT 1 " +
		"   FROM produtos_tipos_notas e " +
		"   WHERE e.entidade_id = " + strconv.FormatInt(param.EntidadeId, 10) +
		"     AND e.ciclo_id = a.ciclo_id " +
		"     AND e.pilar_id = a.pilar_id " +
		"     AND e.plano_id = " + numPlano +
		"     AND e.tipo_nota_id = d.tipo_nota_id " +
		"     AND e.componente_id = b.componente_id) " +
		" GROUP BY p.entidade_id, " +
		" p.ciclo_id, " +
		" p.pilar_id, " +
		" p.componente_id, " +
		" p.plano_id, " +
		" d.tipo_nota_id " +
		" ORDER BY 1,2,3,4,5,6"
	log.Println(sqlStatement)
	Db.QueryRow(
		sqlStatement,
		mdl.Calculada,
		currentUser.Id)

	sqlStatement = "INSERT INTO produtos_elementos ( " +
		" entidade_id, " +
		" ciclo_id, " +
		" pilar_id, " +
		" componente_id, " +
		" plano_id, " +
		" tipo_nota_id, " +
		" elemento_id, " +
		" peso," +
		" nota," +
		" tipo_pontuacao_id, " +
		" author_id, " +
		" criado_em ) " +
		" SELECT d.entidade_id, " +
		" d.ciclo_id, " +
		" d.pilar_id, " +
		" d.componente_id, " +
		" d.plano_id, " +
		" c.tipo_nota_id, " +
		" c.elemento_id, " +
		" c.peso_padrao, " +
		" 0, ?, ?, GETDATE() " +
		" FROM " +
		" pilares_ciclos a " +
		" INNER JOIN " +
		" componentes_pilares b ON a.pilar_id = b.pilar_id " +
		" INNER JOIN " +
		" elementos_componentes c ON b.componente_id = c.componente_id " +
		" INNER JOIN " +
		" produtos_planos d ON b.componente_id = d.componente_id " +
		" WHERE " +
		" a.ciclo_id = " + strconv.FormatInt(param.CicloId, 10) +
		" AND d.entidade_id = " + strconv.FormatInt(param.EntidadeId, 10) +
		" AND d.pilar_id = " + strconv.FormatInt(param.PilarId, 10) +
		" AND d.componente_id = " + strconv.FormatInt(param.ComponenteId, 10) +
		" AND d.plano_id = " + numPlano +
		" AND NOT EXISTS " +
		"  (SELECT 1 " +
		"   FROM produtos_elementos e " +
		"   WHERE e.entidade_id = " + strconv.FormatInt(param.EntidadeId, 10) +
		"     AND e.ciclo_id = a.ciclo_id " +
		"     AND e.pilar_id = a.pilar_id " +
		"     AND e.componente_id = b.componente_id " +
		"     AND e.plano_id = " + numPlano +
		"     AND e.elemento_id = c.elemento_id)"
	log.Println(sqlStatement)
	Db.QueryRow(
		sqlStatement,
		mdl.Calculada,
		currentUser.Id)

	sqlStatement = "INSERT INTO produtos_itens ( " +
		" entidade_id, " +
		" ciclo_id, " +
		" pilar_id, " +
		" componente_id, " +
		" plano_id, " +
		" tipo_nota_id, " +
		" elemento_id, " +
		" item_id, " +
		" author_id, " +
		" criado_em ) " +
		" SELECT p.entidade_id, " +
		" p.ciclo_id, " +
		" p.pilar_id, " +
		" p.componente_id, " +
		" p.plano_id, " +
		" c.tipo_nota_id, " +
		" c.elemento_id, d.id, ?, GETDATE() " +
		" FROM pilares_ciclos a " +
		" INNER JOIN componentes_pilares b ON a.pilar_id = b.pilar_id " +
		" INNER JOIN elementos_componentes c ON b.componente_id = c.componente_id " +
		" INNER JOIN itens d ON c.elemento_id = d.elemento_id " +
		" INNER JOIN PRODUTOS_PLANOS p ON b.componente_id = p.componente_id " +
		" WHERE " +
		" a.ciclo_id = " + strconv.FormatInt(param.CicloId, 10) +
		" AND p.entidade_id = " + strconv.FormatInt(param.EntidadeId, 10) +
		" AND p.pilar_id = " + strconv.FormatInt(param.PilarId, 10) +
		" AND p.componente_id = " + strconv.FormatInt(param.ComponenteId, 10) +
		" AND p.plano_id = " + numPlano +
		" AND NOT EXISTS (SELECT 1 " +
		"     FROM produtos_itens e " +
		"     WHERE e.entidade_id = " + strconv.FormatInt(param.EntidadeId, 10) +
		"       AND e.plano_id = " + numPlano +
		"       AND e.ciclo_id = a.ciclo_id " +
		"       AND e.pilar_id = a.pilar_id " +
		"       AND e.componente_id = b.componente_id " +
		"	   AND e.elemento_id = c.elemento_id " +
		"	   AND e.item_id = d.id)"
	log.Println(sqlStatement)
	Db.QueryRow(
		sqlStatement,
		currentUser.Id)

	log.Println("INICIANDO CICLO --  UPDATE NOTA")
	produto.EntidadeId = param.EntidadeId
	produto.CicloId = param.CicloId
	produto.PilarId = param.PilarId
	produto.ComponenteId = param.ComponenteId
	produto.PlanoId, _ = strconv.ParseInt(numPlano, 10, 64)
	// Atualizando os pesos
	atualizarPesoComponentes(produto, currentUser) //
	atualizarComponenteNota(produto)               //
	atualizarPilarNota(produto)                    //
	atualizarCicloNota(produto)                    //
	atualizarPesoTiposNotas(produto, currentUser)
}

func loadNotasAtuais(produto mdl.ProdutoElemento) mdl.NotasAtuais {
	var notasAtuais mdl.NotasAtuais
	sql := " SELECT coalesce(format(a.nota, 'N', 'pt-br'),'.00') AS tipo_nota, " +
		"   coalesce(format(b.nota, 'N', 'pt-br'),'.00') AS plano, " +
		"   coalesce(format(c.nota, 'N', 'pt-br'),'.00') AS componente, " +
		"   coalesce(format(d.nota, 'N', 'pt-br'),'.00') AS pilar, " +
		"   coalesce(format(e.nota, 'N', 'pt-br'),'.00') AS ciclo " +
		"    FROM produtos_tipos_notas a " +
		"    JOIN produtos_planos b ON (a.entidade_id = b.entidade_id  " +
		" 	AND a.ciclo_id = b.ciclo_id " +
		" 	AND a.pilar_id = b.pilar_id  " +
		" 	AND a.componente_id = b.componente_id " +
		" 	AND a.plano_id = b.plano_id) " +
		"    JOIN produtos_componentes c ON (a.entidade_id = c.entidade_id  " +
		" 	AND a.ciclo_id = c.ciclo_id " +
		" 	AND a.pilar_id = c.pilar_id  " +
		" 	AND a.componente_id = c.componente_id) " +
		"    JOIN produtos_pilares d ON (a.entidade_id = d.entidade_id  " +
		" 	AND a.ciclo_id = d.ciclo_id " +
		" 	AND a.pilar_id = d.pilar_id) " +
		"    JOIN produtos_ciclos e ON (a.entidade_id = e.entidade_id  " +
		" 	AND a.ciclo_id = e.ciclo_id) " +
		"    WHERE a.entidade_id = " + strconv.FormatInt(produto.EntidadeId, 10) +
		"      AND a.ciclo_id = " + strconv.FormatInt(produto.CicloId, 10) +
		"      AND a.pilar_id = " + strconv.FormatInt(produto.PilarId, 10) +
		"      AND a.componente_id = " + strconv.FormatInt(produto.ComponenteId, 10) +
		"      AND a.plano_id = " + strconv.FormatInt(produto.PlanoId, 10) +
		"      AND a.tipo_nota_id = " + strconv.FormatInt(produto.TipoNotaId, 10)
	log.Println(sql)
	rows, _ := Db.Query(sql)
	defer rows.Close()
	if rows.Next() {
		rows.Scan(
			&notasAtuais.TipoNotaNota,
			&notasAtuais.PlanoNota,
			&notasAtuais.ComponenteNota,
			&notasAtuais.PilarNota,
			&notasAtuais.CicloNota)
	}
	if strings.HasSuffix(notasAtuais.TipoNotaNota, ".00") {
		notasAtuais.TipoNotaNota = notasAtuais.TipoNotaNota[:len(notasAtuais.TipoNotaNota)-3]
	}
	if strings.HasSuffix(notasAtuais.PlanoNota, ".00") {
		notasAtuais.PlanoNota = notasAtuais.PlanoNota[:len(notasAtuais.PlanoNota)-3]
	}
	if strings.HasSuffix(notasAtuais.ComponenteNota, ".00") {
		notasAtuais.ComponenteNota = notasAtuais.ComponenteNota[:len(notasAtuais.ComponenteNota)-3]
	}
	if strings.HasSuffix(notasAtuais.PilarNota, ".00") {
		notasAtuais.PilarNota = notasAtuais.PilarNota[:len(notasAtuais.PilarNota)-3]
	}
	if strings.HasSuffix(notasAtuais.CicloNota, ".00") {
		notasAtuais.CicloNota = notasAtuais.CicloNota[:len(notasAtuais.CicloNota)-3]
	}
	return notasAtuais
}

func loadPesosAtuais(produto mdl.ProdutoElemento) mdl.PesosAtuais {
	var pesosAtuais mdl.PesosAtuais
	sql := " SELECT coalesce(format(b.peso, 'N', 'pt-br'),'.00') as plano, " +
		"    coalesce(format(c.peso, 'N', 'pt-br'),'.00') as componente, " +
		"	 coalesce(format(d.peso, 'N', 'pt-br'),'.00') as pilar, " +
		"	 string_agg(concat(a.tipo_nota_id,':',format(a.peso,'N','pt-br')),'/') AS tipo_nota " +
		"    FROM produtos_tipos_notas a " +
		"    JOIN produtos_planos b ON (a.entidade_id = b.entidade_id  " +
		" 	AND a.ciclo_id = b.ciclo_id " +
		" 	AND a.pilar_id = b.pilar_id  " +
		" 	AND a.componente_id = b.componente_id " +
		" 	AND a.plano_id = b.plano_id) " +
		"    JOIN produtos_componentes c ON (a.entidade_id = c.entidade_id  " +
		" 	AND a.ciclo_id = c.ciclo_id " +
		" 	AND a.pilar_id = c.pilar_id  " +
		" 	AND a.componente_id = c.componente_id) " +
		"    JOIN produtos_pilares d ON (a.entidade_id = d.entidade_id  " +
		" 	AND a.ciclo_id = d.ciclo_id " +
		" 	AND a.pilar_id = d.pilar_id) " +
		"    WHERE a.entidade_id = " + strconv.FormatInt(produto.EntidadeId, 10) +
		"      AND a.ciclo_id = " + strconv.FormatInt(produto.CicloId, 10) +
		"      AND a.pilar_id = " + strconv.FormatInt(produto.PilarId, 10) +
		"      AND a.componente_id = " + strconv.FormatInt(produto.ComponenteId, 10) +
		"      AND a.plano_id = " + strconv.FormatInt(produto.PlanoId, 10) +
		"      GROUP BY b.peso,c.peso,d.peso "
	log.Println(sql)
	rows, _ := Db.Query(sql)
	defer rows.Close()
	if rows.Next() {
		rows.Scan(
			&pesosAtuais.PlanoPeso,
			&pesosAtuais.ComponentePeso,
			&pesosAtuais.PilarPeso,
			&pesosAtuais.TipoNotaPeso)
	}
	if strings.HasSuffix(pesosAtuais.PlanoPeso, ".00") {
		pesosAtuais.PlanoPeso = pesosAtuais.PlanoPeso[:len(pesosAtuais.PlanoPeso)-3]
	}
	if strings.HasSuffix(pesosAtuais.ComponentePeso, ".00") {
		pesosAtuais.ComponentePeso = pesosAtuais.ComponentePeso[:len(pesosAtuais.ComponentePeso)-3]
	}
	if strings.HasSuffix(pesosAtuais.PilarPeso, ".00") {
		pesosAtuais.PilarPeso = pesosAtuais.PilarPeso[:len(pesosAtuais.PilarPeso)-3]
	}
	if strings.HasSuffix(pesosAtuais.TipoNotaPeso, ".00") {
		pesosAtuais.TipoNotaPeso = pesosAtuais.TipoNotaPeso[:len(pesosAtuais.TipoNotaPeso)-3]
	}
	return pesosAtuais
}

func registrarPesoPilar(produto mdl.ProdutoPilar) {
	// PESOS PILARES
	sqlStatement := "UPDATE produtos_pilares SET peso = ?, motivacao_peso = ? " +
		" WHERE entidade_id = ? AND " +
		" ciclo_id = ? AND " +
		" pilar_id = ? "
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec(produto.Peso,
		produto.Motivacao,
		produto.EntidadeId,
		produto.CicloId,
		produto.PilarId)
}

func getAnalise(rota string) string {
	valores := strings.Split(rota, "_")
	sql := " SELECT 'analise' "
	if valores[1] == "Ciclo" {
		sql = " SELECT analise FROM produtos_ciclos WHERE entidade_id = " + valores[2] + " AND ciclo_id = " + valores[3]
	} else if valores[1] == "Pilar" {
		sql = " SELECT analise FROM produtos_pilares WHERE entidade_id = " + valores[2] + " AND ciclo_id = " + valores[3] +
			" AND pilar_id = " + valores[4]
	} else if valores[1] == "Componente" {
		sql = " SELECT analise FROM produtos_componentes WHERE entidade_id = " + valores[2] + " AND ciclo_id = " + valores[3] +
			" AND pilar_id = " + valores[4] + " AND componente_id = " + valores[5]
	} else if valores[1] == "Plano" {
		sql = " SELECT analise FROM produtos_planos WHERE entidade_id = " + valores[2] + " AND ciclo_id = " + valores[3] +
			" AND pilar_id = " + valores[4] + " AND componente_id = " + valores[5] + " AND plano_id = " + valores[6]
	} else if valores[1] == "TipoNota" {
		sql = " SELECT analise FROM produtos_tipos_notas WHERE entidade_id = " + valores[2] + " AND ciclo_id = " + valores[3] +
			" AND pilar_id = " + valores[4] + " AND componente_id = " + valores[5] + " AND plano_id = " + valores[6] +
			" AND tipo_nota_id = " + valores[7]
	} else if valores[1] == "Elemento" {
		sql = " SELECT analise FROM produtos_elementos WHERE entidade_id = " + valores[2] + " AND ciclo_id = " + valores[3] +
			" AND pilar_id = " + valores[4] + " AND componente_id = " + valores[5] + " AND plano_id = " + valores[6] +
			" AND tipo_nota_id = " + valores[7] + " AND elemento_id = " + valores[8]
	} else if valores[1] == "Item" {
		sql = " SELECT analise FROM produtos_itens WHERE entidade_id = " + valores[2] + " AND ciclo_id = " + valores[3] +
			" AND pilar_id = " + valores[4] + " AND componente_id = " + valores[5] + " AND plano_id = " + valores[6] +
			" AND tipo_nota_id = " + valores[7] + " AND elemento_id = " + valores[8] + " AND item_id = " + valores[9]
	}

	log.Println(sql)
	rows, _ := Db.Query(sql)
	defer rows.Close()
	retorno := ""
	if rows.Next() {
		rows.Scan(&retorno)
	}
	return retorno
}

func setAnalise(rota string, analise string) string {
	valores := strings.Split(rota, "_")
	sqlStatement := " SELECT 'analise' "
	if valores[1] == "Ciclo" {
		sqlStatement = " UPDATE produtos_ciclos SET analise = '" + analise + "' WHERE entidade_id = " + valores[2] + " AND ciclo_id = " + valores[3]
	} else if valores[1] == "Pilar" {
		sqlStatement = " UPDATE produtos_pilares SET analise = '" + analise + "' WHERE entidade_id = " + valores[2] + " AND ciclo_id = " + valores[3] +
			" AND pilar_id = " + valores[4]
	} else if valores[1] == "Componente" {
		sqlStatement = " UPDATE produtos_componentes SET analise = '" + analise + "' WHERE entidade_id = " + valores[2] + " AND ciclo_id = " + valores[3] +
			" AND pilar_id = " + valores[4] + " AND componente_id = " + valores[5]
	} else if valores[1] == "Plano" {
		sqlStatement = " UPDATE produtos_planos SET analise = '" + analise + "' WHERE entidade_id = " + valores[2] + " AND ciclo_id = " + valores[3] +
			" AND pilar_id = " + valores[4] + " AND componente_id = " + valores[5] + " AND plano_id = " + valores[6]
	} else if valores[1] == "TipoNota" {
		sqlStatement = " UPDATE produtos_tipos_notas SET analise = '" + analise + "' WHERE entidade_id = " + valores[2] + " AND ciclo_id = " + valores[3] +
			" AND pilar_id = " + valores[4] + " AND componente_id = " + valores[5] + " AND plano_id = " + valores[6] +
			" AND tipo_nota_id = " + valores[7]
	} else if valores[1] == "Elemento" {
		sqlStatement = " UPDATE produtos_elementos SET analise = '" + analise + "' WHERE entidade_id = " + valores[2] + " AND ciclo_id = " + valores[3] +
			" AND pilar_id = " + valores[4] + " AND componente_id = " + valores[5] + " AND plano_id = " + valores[6] +
			" AND tipo_nota_id = " + valores[7] + " AND elemento_id = " + valores[8]
	} else if valores[1] == "Item" {
		sqlStatement = " UPDATE produtos_itens SET analise = '" + analise + "' WHERE entidade_id = " + valores[2] + " AND ciclo_id = " + valores[3] +
			" AND pilar_id = " + valores[4] + " AND componente_id = " + valores[5] + " AND plano_id = " + valores[6] +
			" AND tipo_nota_id = " + valores[7] + " AND elemento_id = " + valores[8] + " AND item_id = " + valores[9]
	}
	log.Println(sqlStatement)
	updtForm, _ := Db.Prepare(sqlStatement)
	_, err := updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
	return "OK"
}

func getDescricao(rota string) mdl.Descricao {
	valores := strings.Split(rota, "_")
	sql := " SELECT 'descricao' "
	if valores[1] == "Ciclo" {
		sql = " SELECT descricao, referencia FROM ciclos WHERE id = " + valores[3]
	} else if valores[1] == "Pilar" {
		sql = " SELECT descricao, referencia FROM pilares WHERE id = " + valores[4]
	} else if valores[1] == "Componente" {
		sql = " SELECT descricao, referencia FROM componentes WHERE id = " + valores[5]
	} else if valores[1] == "Plano" {
		sql = " SELECT descricao, referencia FROM planos WHERE id = " + valores[6]
	} else if valores[1] == "TipoNota" {
		sql = " SELECT descricao, referencia FROM tipos_notas WHERE id = " + valores[7]
	} else if valores[1] == "Elemento" {
		sql = " SELECT descricao, referencia FROM elementos WHERE id = " + valores[8]
	} else if valores[1] == "Item" {
		sql = " SELECT descricao, referencia FROM itens WHERE id = " + valores[9]
	}
	log.Println(sql)
	rows, _ := Db.Query(sql)
	defer rows.Close()
	var retorno mdl.Descricao
	if rows.Next() {
		rows.Scan(&retorno.Texto, &retorno.Link)
	}
	return retorno
}

func loadConfigPlanos(entidadeId string, cicloId string, pilarId string, componenteId string) string {
	sql := "SELECT planos_configurados FROM (SELECT a.componente_id, string_agg(b.cnpb,', ') planos_configurados " +
		" FROM produtos_planos a " +
		" INNER JOIN planos b ON a.plano_id = b.id " +
		" WHERE a.entidade_id = " + entidadeId + " AND a.ciclo_id = " + cicloId +
		" AND a.pilar_id = " + pilarId + " AND a.componente_id = " + componenteId +
		" GROUP BY a.componente_id) R "
	log.Println(sql)
	rows, _ := Db.Query(sql)
	defer rows.Close()
	var configuracaoPlanos string
	if rows.Next() {
		rows.Scan(&configuracaoPlanos)
	}
	return configuracaoPlanos
}
