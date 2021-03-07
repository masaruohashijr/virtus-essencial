package handlers

import (
	"log"
	"strconv"
	"strings"
	mdl "virtus-essencial/models"
)

func registrarCronogramaComponente(produto mdl.ProdutoComponente, currentUser mdl.User, tipoData string) {
	sqlStatement := "UPDATE virtus.produtos_componentes SET "
	if tipoData == "iniciaEm" {
		sqlStatement += " inicia_em ='" + produto.IniciaEm + "', "
	} else {
		sqlStatement += " termina_em ='" + produto.TerminaEm + "', "
	}
	sqlStatement += " motivacao_reprogramacao ='" + produto.Motivacao + "'" +
		" WHERE id_entidade= " + strconv.FormatInt(produto.EntidadeId, 10) +
		" AND id_ciclo= " + strconv.FormatInt(produto.CicloId, 10) +
		" AND id_pilar= " + strconv.FormatInt(produto.PilarId, 10) +
		" AND id_componente= " + strconv.FormatInt(produto.ComponenteId, 10)
	log.Println(sqlStatement)
	updtForm, _ := Db.Prepare(sqlStatement)
	_, err := updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
}

func registrarAuditorComponente(produto mdl.ProdutoComponente, currentUser mdl.User) {
	sqlStatement := "UPDATE virtus.produtos_componentes SET " +
		" id_auditor=" + strconv.FormatInt(produto.AuditorId, 10) + ", justificativa='" + produto.Motivacao + "'" +
		" WHERE id_entidade= " + strconv.FormatInt(produto.EntidadeId, 10) +
		" AND id_ciclo= " + strconv.FormatInt(produto.CicloId, 10) +
		" AND id_pilar= " + strconv.FormatInt(produto.PilarId, 10) +
		" AND id_componente= " + strconv.FormatInt(produto.ComponenteId, 10)
	log.Println(sqlStatement)
	updtForm, _ := Db.Prepare(sqlStatement)
	_, err := updtForm.Exec()
	if err != nil {
		log.Println(err.Error())
	}
}

func registrarNotaElemento(produto mdl.ProdutoElemento, currentUser mdl.User) mdl.ValoresAtuais {
	sqlStatement := "UPDATE virtus.produtos_elementos SET nota = " + strconv.Itoa(produto.Nota) + ", " +
		" motivacao_nota = ? , " +
		" id_tipo_pontuacao = (SELECT DISTINCT case when b.id_supervisor = " + strconv.FormatInt(currentUser.Id, 10) +
		" then 3 when 2 = " + strconv.FormatInt(currentUser.Role, 10) + " then 3 else 1 end " +
		" FROM virtus.produtos_componentes b WHERE " +
		" id_entidade = b.id_entidade and " +
		" id_ciclo = b.id_ciclo and " +
		" id_pilar = b.id_pilar and " +
		// " a.id_plano = b.id_plano and " +
		" id_componente = b.id_componente) " +
		" WHERE id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) +
		" AND id_ciclo = " + strconv.FormatInt(produto.CicloId, 10) +
		" AND id_pilar = " + strconv.FormatInt(produto.PilarId, 10) +
		" AND id_plano = " + strconv.FormatInt(produto.PlanoId, 10) +
		" AND id_componente = " + strconv.FormatInt(produto.ComponenteId, 10) +
		" AND id_elemento = " + strconv.FormatInt(produto.ElementoId, 10) +
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
	sqlStatement := "UPDATE virtus.produtos_pilares " +
		" SET nota = (select " +
		" round(sum(nota*peso)/sum(peso),2) AS media " +
		" FROM virtus.produtos_componentes b " +
		" WHERE " +
		" produtos_pilares.id_entidade = b.id_entidade " +
		" AND produtos_pilares.id_ciclo = b.id_ciclo  " +
		" AND produtos_pilares.id_pilar = b.id_pilar " +
		" AND (b.nota IS NOT NULL AND b.nota <> 0) " +
		" GROUP BY b.id_entidade,  " +
		" b.id_ciclo, " +
		" b.id_pilar ) " +
		" WHERE id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) +
		" AND id_ciclo = " + strconv.FormatInt(produto.CicloId, 10) +
		" AND id_pilar = " + strconv.FormatInt(produto.PilarId, 10)
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

func atualizarComponenteNota(produto mdl.ProdutoElemento) {
	// PRODUTOS_COMPONENTES
	log.Println("***** ATUALIZAR NOTA DO COMPONENTE")
	sqlStatement := " WITH T1 AS " +
		"   (SELECT id_entidade, " +
		"           id_ciclo, " +
		"           id_pilar, " +
		"           id_plano, " +
		"           id_componente, " +
		"           id_tipo_nota, " +
		"           round(avg(peso), 2) AS peso_tn " +
		"    FROM virtus.produtos_elementos " +
		"    WHERE peso <> 0 " +
		"    GROUP BY id_entidade, " +
		"             id_ciclo, " +
		"             id_pilar, " +
		"             id_plano, " +
		"             id_componente, " +
		"             id_tipo_nota), " +
		"      T2 AS " +
		"   (SELECT id_entidade, " +
		"           id_ciclo, " +
		"           id_pilar, " +
		"           id_componente, " +
		"           id_plano, " +
		"           SUM(peso_tn) AS soma_pesos_tipos_notas " +
		"    FROM T1 " +
		"    GROUP BY id_entidade, " +
		"             id_ciclo, " +
		"             id_pilar, " +
		"             id_componente, " +
		"             id_plano), " +
		"      T3 AS " +
		"   (SELECT T1.id_entidade, " +
		"           T1.id_ciclo, " +
		"           T1.id_pilar, " +
		"           T1.id_componente, " +
		"           T1.id_plano, " +
		"           T1.id_tipo_nota, " +
		"           T1.peso_tn, " +
		"           T2.soma_pesos_tipos_notas, " +
		"           round(T1.peso_tn*100/T2.soma_pesos_tipos_notas, 2) AS ponderacao_tipo " +
		"    FROM T1 " +
		"    INNER JOIN T2 ON (T1.id_entidade = T2.id_entidade " +
		"                      AND T1.id_ciclo = T2.id_ciclo " +
		"                      AND T1.id_pilar = T2.id_pilar " +
		"                      AND T1.id_componente = T2.id_componente " +
		"                      AND T1.id_plano = T2.id_plano) " +
		"    GROUP BY T1.id_entidade, " +
		"             T1.id_ciclo, " +
		"             T1.id_pilar, " +
		"             T1.id_componente, " +
		"             T1.id_plano, " +
		"             T1.id_tipo_nota, " +
		"             T1.peso_tn, " +
		"             T2.soma_pesos_tipos_notas), " +
		"      T4 AS " +
		"   (SELECT T3.id_entidade, " +
		"           T3.id_ciclo, " +
		"           T3.id_pilar, " +
		"           T3.id_componente, " +
		"           T3.id_plano, " +
		"           SUM(ponderacao_tipo*peso_tn/100) AS total_peso_plano " +
		"    FROM T3 " +
		"    INNER JOIN virtus.produtos_planos p ON (p.id_entidade = T3.id_entidade " +
		"                                     AND p.id_ciclo = T3.id_ciclo " +
		"                                     AND p.id_pilar = T3.id_pilar " +
		"                                     AND p.id_componente = T3.id_componente " +
		"                                     AND p.id_plano = T3.id_plano) " +
		" 	GROUP BY T3.id_entidade, " +
		"           T3.id_ciclo, " +
		"           T3.id_pilar, " +
		"           T3.id_componente, " +
		"           T3.id_plano), " +
		"      T5 AS " +
		"   (SELECT T4.id_entidade, " +
		"           T4.id_ciclo, " +
		"           T4.id_pilar, " +
		"           T4.id_componente, " +
		"           T4.id_plano, " +
		"           p.peso/100 AS ponderacao_plano " +
		"    FROM virtus.produtos_planos p " +
		"    INNER JOIN T4 on   " +
		"    (T4.id_entidade = p.id_entidade " +
		"      AND T4.id_ciclo = p.id_ciclo " +
		"      AND T4.id_pilar = p.id_pilar " +
		"      AND T4.id_componente = p.id_componente " +
		" 	 AND T4.id_plano = p.id_plano) " +
		"    GROUP BY T4.id_entidade, " +
		"             T4.id_ciclo, " +
		"             T4.id_pilar, " +
		"             T4.id_componente, " +
		"             T4.id_plano, " +
		" 			p.peso), " +
		" 	T6 AS  " +
		" (SELECT t4.id_entidade, " +
		"           t4.id_ciclo, " +
		"           t4.id_pilar, " +
		"           t4.id_componente, " +
		" 		  sum(t4.total_peso_plano * t5.ponderacao_plano) as denominador " +
		"    FROM T4  " +
		"    INNER JOIN T5 ON (t4.id_entidade = t5.id_entidade " +
		"                      AND t4.id_ciclo = t5.id_ciclo " +
		"                      AND t4.id_pilar = t5.id_pilar " +
		"                      AND t4.id_componente = t5.id_componente " +
		" 					 AND t4.id_plano = t5.id_plano) " +
		"    GROUP BY t4.id_entidade, " +
		"             t4.id_ciclo, " +
		"             t4.id_pilar, " +
		"             t4.id_componente), " +
		" T7 AS " +
		" (SELECT p.id_entidade, " +
		"           p.id_ciclo, " +
		"           p.id_pilar, " +
		"           p.id_componente, " +
		" 		  sum(p.nota * t4.total_peso_plano * t5.ponderacao_plano)/denominador as nota_componente " +
		"    FROM virtus.produtos_planos p " +
		"    INNER JOIN T4 ON (t4.id_entidade = p.id_entidade " +
		"                      AND t4.id_ciclo = p.id_ciclo " +
		"                      AND t4.id_pilar = p.id_pilar " +
		"                      AND t4.id_componente = p.id_componente " +
		" 					 AND t4.id_plano = p.id_plano) " +
		"    INNER JOIN T5 ON (t4.id_entidade = t5.id_entidade " +
		"                      AND t4.id_ciclo = t5.id_ciclo " +
		"                      AND t4.id_pilar = t5.id_pilar " +
		"                      AND t4.id_componente = t5.id_componente " +
		" 					 AND t4.id_plano = t5.id_plano) " +
		"    INNER JOIN T6 ON (t6.id_entidade = t5.id_entidade " +
		"                      AND t6.id_ciclo = t5.id_ciclo " +
		"                      AND t6.id_pilar = t5.id_pilar " +
		"                      AND t6.id_componente = t5.id_componente) " +
		"    GROUP BY p.id_entidade, " +
		"             p.id_ciclo, " +
		"             p.id_pilar, " +
		"             p.id_componente, " +
		" 			T6.denominador) " +
		" UPDATE virtus.produtos_componentes  " +
		" 	SET nota = ( SELECT round(T7.nota_componente,2)  " +
		" 	FROM T7  " +
		" 	WHERE produtos_componentes.id_componente = T7.id_componente  " +
		" 	AND produtos_componentes.id_pilar = T7.id_pilar  " +
		" 	AND produtos_componentes.id_ciclo = T7.id_ciclo  " +
		" 	AND produtos_componentes.id_entidade = T7.id_entidade  " +
		" 	GROUP BY T7.id_entidade, T7.id_ciclo, T7.id_pilar, T7.id_componente, T7.nota_componente) " +
		" WHERE produtos_componentes.id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) +
		"   AND produtos_componentes.id_ciclo = " + strconv.FormatInt(produto.CicloId, 10) +
		"   AND produtos_componentes.id_pilar = " + strconv.FormatInt(produto.PilarId, 10) +
		"   AND produtos_componentes.id_componente = " + strconv.FormatInt(produto.ComponenteId, 10)
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

func atualizarPlanoNota(produto mdl.ProdutoElemento) {
	// PRODUTOS_PLANOS
	log.Println("***** ATUALIZAR NOTA DO PLANO")
	sqlStatement := "UPDATE virtus.produtos_planos " +
		" set nota = (select  " +
		" round(sum(nota*peso)/sum(peso),2) as media " +
		" FROM virtus.produtos_tipos_notas b " +
		" WHERE " +
		" produtos_planos.id_entidade = b.id_entidade " +
		" AND produtos_planos.id_ciclo = b.id_ciclo  " +
		" AND produtos_planos.id_pilar = b.id_pilar " +
		" AND produtos_planos.id_componente = b.id_componente " +
		" AND produtos_planos.id_plano = b.id_plano " +
		" GROUP BY b.id_entidade,  " +
		" b.id_ciclo, " +
		" b.id_pilar, " +
		" b.id_plano, " +
		" b.id_componente " +
		" HAVING sum(peso)>0) " +
		" WHERE id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) +
		" AND id_ciclo = " + strconv.FormatInt(produto.CicloId, 10) +
		" AND id_pilar = " + strconv.FormatInt(produto.PilarId, 10) +
		" AND id_componente = " + strconv.FormatInt(produto.ComponenteId, 10) +
		" AND id_plano = " + strconv.FormatInt(produto.PlanoId, 10)
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

func atualizarTipoNotaNota(produto mdl.ProdutoElemento) {
	// PRODUTOS_TIPOS_NOTAS
	log.Println("***** ATUALIZAR NOTA DO TIPO DA NOTA")
	sqlStatement := " WITH T1 AS " +
		"   (SELECT id_entidade, " +
		"           id_ciclo, " +
		"           id_pilar, " +
		"           id_plano, " +
		"           id_componente, " +
		"           id_tipo_nota, " +
		"           peso*nota AS produtos " +
		"    FROM virtus.produtos_elementos), " +
		"      T2 AS " +
		"   (SELECT id_entidade, " +
		"           id_ciclo, " +
		"           id_pilar, " +
		"           id_plano, " +
		"           id_componente, " +
		"           id_tipo_nota, " +
		"           SUM(peso) AS soma_pesos_elementos " +
		"    FROM virtus.produtos_elementos " +
		"    GROUP BY id_entidade, " +
		"             id_ciclo, " +
		"             id_pilar, " +
		"             id_plano, " +
		"             id_componente, " +
		"             id_tipo_nota), " +
		"      T3 AS " +
		"   (SELECT T1.id_entidade, " +
		"           T1.id_ciclo, " +
		"           T1.id_pilar, " +
		"           T1.id_componente, " +
		"           t1.id_plano, " +
		"           t1.id_tipo_nota, " +
		"           SUM(T1.produtos)/T2.soma_pesos_elementos AS nota_tn " +
		"    FROM T1 " +
		"    INNER JOIN T2 ON (T1.id_entidade = T2.id_entidade " +
		"                      AND T1.id_ciclo = T2.id_ciclo " +
		"                      AND T1.id_pilar = T2.id_pilar " +
		"                      AND T1.id_componente = T2.id_componente " +
		"                      AND t1.id_plano = t2.id_plano " +
		"                      AND t1.id_tipo_nota = t2.id_tipo_nota) " +
		"    WHERE T1.id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) +
		"      AND T1.id_ciclo = " + strconv.FormatInt(produto.CicloId, 10) +
		"      AND T1.id_pilar = " + strconv.FormatInt(produto.PilarId, 10) +
		"      AND T1.id_componente = " + strconv.FormatInt(produto.ComponenteId, 10) +
		"      AND t1.id_plano = " + strconv.FormatInt(produto.PlanoId, 10) +
		"    GROUP BY t1.id_entidade, " +
		"             t1.id_ciclo, " +
		"             t1.id_pilar, " +
		"             t1.id_plano, " +
		"             t1.id_componente, " +
		"             t1.id_tipo_nota, " +
		"             t2.soma_pesos_elementos) " +
		" UPDATE virtus.produtos_tipos_notas " +
		" SET nota = round(T3.nota_tn, 2) " +
		" FROM T3 " +
		" WHERE produtos_tipos_notas.id_tipo_nota = T3.id_tipo_nota " +
		"   AND produtos_tipos_notas.id_componente = T3.id_componente " +
		"   AND produtos_tipos_notas.id_plano = T3.id_plano " +
		"   AND produtos_tipos_notas.id_pilar = T3.id_pilar " +
		"   AND produtos_tipos_notas.id_ciclo = T3.id_ciclo " +
		"   AND produtos_tipos_notas.id_entidade = T3.id_entidade"
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec()
}

func atualizarCicloNota(produto mdl.ProdutoElemento) {
	// PRODUTOS_CICLOS
	log.Println(">>>>> ATUALIZAR NOTA DO CICLO")
	sqlStatement := "UPDATE virtus.produtos_ciclos SET nota = R.media FROM " +
		" (SELECT round(sum(nota*peso/100),2) AS media " +
		" FROM virtus.produtos_pilares b " +
		" WHERE b.id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) +
		" AND b.id_ciclo = " + strconv.FormatInt(produto.CicloId, 10) +
		" AND b.nota <> 0 AND b.nota IS NOT NULL) R " +
		" WHERE id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) +
		" AND id_ciclo = " + strconv.FormatInt(produto.CicloId, 10)
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec()

}

func registrarTiposPontuacao(produto mdl.ProdutoElemento, currentUser mdl.User) {
	sqlStatement := "UPDATE virtus.produtos_tipos_notas SET " +
		" id_tipo_pontuacao = (SELECT case when b.id_supervisor = ? " +
		" then 3 else 2 end FROM virtus.produtos_tipos_notas b where id_tipo_nota = b.id_tipo_nota) " +
		" WHERE id_entidade = ? " +
		" AND  id_ciclo = ? " +
		" AND  id_pilar = ? " +
		" AND  id_componente = ? " +
		" AND  id_tipo_nota = ? "
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
	sqlStatement = "UPDATE virtus.produtos_componentes SET " +
		" id_tipo_pontuacao = (SELECT case when b.id_supervisor = ? " +
		" then 3 else 2 end FROM virtus.produtos_componentes b where id_produto_componente = b.id_produto_componente) " +
		" WHERE id_entidade = ? " +
		" AND  id_ciclo = ? " +
		" AND  id_pilar = ? " +
		" AND  id_componente = ? "
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
	sqlStatement = "UPDATE virtus.produtos_pilares SET " +
		" id_tipo_pontuacao = (SELECT case when b.id_supervisor = ? " +
		" then 3 else 2 end FROM virtus.produtos_pilares b where id_produto_pilar = b.id_produto_pilar) " +
		" WHERE id_entidade = ? " +
		" AND  id_ciclo = ? " +
		" AND  id_pilar = ? "
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
	sqlStatement = "UPDATE virtus.produtos_ciclos SET " +
		" id_tipo_pontuacao = (SELECT case when b.id_supervisor = ? " +
		" then 3 else 2 end FROM virtus.produtos_ciclos b where id_produto_ciclo = b.id_produto_ciclo) " +
		" WHERE id_entidade = ? " +
		" AND  id_ciclo = ? "
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
	sqlStatement := "UPDATE virtus.produtos_elementos SET peso = ?, motivacao_peso = ? " +
		" WHERE id_entidade = ? AND " +
		" id_ciclo = ? AND " +
		" id_pilar = ? AND " +
		" id_plano = ? AND " +
		" id_componente = ? AND " +
		" id_elemento = ? "
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
	log.Println(">>>>> PESOS TIPOS NOTAS")
	sqlStatement := "WITH R1 AS  " +
		"  (SELECT id_entidade, " +
		"                  id_ciclo, " +
		"                  id_pilar, " +
		"                  id_plano, " +
		"                  id_componente, " +
		"                  id_tipo_nota, " +
		"                  round(sum(peso), 2) AS TOTAL " +
		"           FROM virtus.produtos_elementos " +
		"           GROUP BY id_entidade, " +
		"                    id_ciclo, " +
		"                    id_pilar, " +
		"                    id_plano, " +
		"                    id_componente, " +
		"                    id_tipo_nota), " +
		"  R2 AS (SELECT id_entidade, " +
		"                  id_ciclo, " +
		"                  id_pilar, " +
		"                  id_plano, " +
		"                  id_componente, " +
		"                  id_tipo_nota, " +
		"                  count(1) AS CONTADOR " +
		"           FROM virtus.produtos_elementos " +
		"           WHERE peso <> 0 " +
		"           GROUP BY id_entidade, " +
		"                    id_ciclo, " +
		"                    id_pilar, " +
		"                    id_plano, " +
		"                    id_componente, " +
		"                    id_tipo_nota), " +
		"  TMP AS " +
		"       (SELECT r1.id_entidade, " +
		"               r1.id_ciclo, " +
		"               r1.id_pilar, " +
		"               r1.id_plano, " +
		"               r1.id_componente, " +
		"               r1.id_tipo_nota, " +
		"               CASE WHEN r2.contador IS NULL THEN 0 ELSE round((r1.TOTAL/r2.contador), 2) END AS PONDERACAO " +
		"        FROM R1         " +
		"        LEFT JOIN R2 " +
		"  		ON (r1.id_entidade = r2.id_entidade " +
		"  		AND r1.id_ciclo = r2.id_ciclo " +
		"  		AND r1.id_pilar = r2.id_pilar " +
		"  		AND r1.id_plano = r2.id_plano " +
		"  		AND r1.id_componente = r2.id_componente " +
		"  		AND r1.id_tipo_nota = r2.id_tipo_nota)), " +
		"  T2 as (SELECT id_entidade, " +
		"               id_pilar, " +
		"               id_plano, " +
		"               id_ciclo, " +
		"               id_componente, " +
		"               SUM(PONDERACAO) AS TOTAL_PESOS_TNS " +
		"        FROM TMP " +
		"        GROUP BY id_entidade,id_pilar,id_plano,id_ciclo,id_componente), " +
		"  T1 AS (SELECT A1.id_entidade, " +
		"  	A1.id_ciclo, " +
		"  	A1.id_pilar, " +
		"  	A1.id_plano,                             " +
		"  	A1.id_componente, " +
		"  	A1.id_tipo_nota, " +
		"  	CASE WHEN T2.total_pesos_tns = 0 THEN 0 ELSE round((A1.PONDERACAO/T2.total_pesos_tns)*100, 2) end AS peso " +
		"  	   FROM TMP A1 " +
		"  	   INNER JOIN T2 ON (A1.id_entidade = t2.id_entidade " +
		"  							 AND A1.id_pilar = t2.id_pilar " +
		"  							 AND A1.id_plano = t2.id_plano " +
		"  							 AND A1.id_ciclo = t2.id_ciclo " +
		"  							 AND A1.id_componente = t2.id_componente) " +
		"  	   WHERE A1.id_componente = " + strconv.FormatInt(produto.ComponenteId, 10) +
		"  		 AND A1.id_pilar = " + strconv.FormatInt(produto.PilarId, 10) +
		"  		 AND A1.id_plano = " + strconv.FormatInt(produto.PlanoId, 10) +
		"  		 AND A1.id_ciclo = " + strconv.FormatInt(produto.CicloId, 10) +
		"  		 AND A1.id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) +
		"  ) " +
		"  UPDATE virtus.produtos_tipos_notas  " +
		"  SET peso = round(T1.peso,2) FROM T1 " +
		"  WHERE produtos_tipos_notas.id_tipo_nota = T1.id_tipo_nota " +
		"    AND produtos_tipos_notas.id_componente = T1.id_componente " +
		"    AND produtos_tipos_notas.id_plano = T1.id_plano " +
		"    AND produtos_tipos_notas.id_pilar = T1.id_pilar " +
		"    AND produtos_tipos_notas.id_ciclo = T1.id_ciclo " +
		"    AND produtos_tipos_notas.id_entidade = T1.id_entidade"
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec()
}

func atualizarPesoPlanos(produto mdl.ProdutoElemento, currentUser mdl.User) {
	// PESOS PLANOS
	log.Println(">>>>> PESOS PLANOS")
	sqlStatement := "WITH total AS " +
		"      (SELECT a.id_entidade, " +
		"              a.id_ciclo, " +
		"              a.id_pilar, " +
		"              a.id_componente, " +
		"              sum(p.recurso_garantidor) AS total " +
		"       FROM virtus.produtos_planos a " +
		"       INNER JOIN virtus.planos p ON (p.id_entidade = a.id_entidade " +
		"                               AND p.id_plano = a.id_plano) " +
		"       WHERE a.id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) +
		"         AND a.id_ciclo = " + strconv.FormatInt(produto.CicloId, 10) +
		"         AND a.id_pilar = " + strconv.FormatInt(produto.PilarId, 10) +
		"         AND a.id_componente = " + strconv.FormatInt(produto.ComponenteId, 10) +
		"       GROUP BY a.id_entidade, " +
		"              a.id_ciclo, " +
		"              a.id_pilar, " +
		"              a.id_componente), " +
		" R1 AS (SELECT a.id_entidade, " +
		"                          a.id_ciclo, " +
		"                          a.id_pilar, " +
		"                          a.id_plano, " +
		"                          a.id_componente, " +
		"                          round(p.recurso_garantidor/t.total, 2)*100 AS peso_percentual " +
		"    FROM virtus.produtos_planos a " +
		"    INNER JOIN virtus.planos p ON (p.id_entidade = a.id_entidade " +
		"                            AND p.id_plano = a.id_plano) " +
		"    INNER JOIN total t ON (a.id_entidade = t.id_entidade " +
		"                           AND a.id_ciclo = t.id_ciclo " +
		"                           AND a.id_pilar = t.id_pilar " +
		"                           AND a.id_componente = t.id_componente)), " +
		" R2 AS (SELECT a.id_entidade, " +
		"     a.id_ciclo, " +
		"     a.id_pilar, " +
		"     a.id_plano, " +
		"     a.id_componente, " +
		"     round(p.recurso_garantidor/t.total, 2)*100 AS peso_percentual " +
		" FROM virtus.produtos_planos a " +
		"    INNER JOIN virtus.planos p ON (p.id_entidade = a.id_entidade " +
		"                            AND p.id_plano = a.id_plano) " +
		"    INNER JOIN total t ON (a.id_entidade = t.id_entidade " +
		"                           AND a.id_ciclo = t.id_ciclo " +
		"                           AND a.id_pilar = t.id_pilar " +
		"                           AND a.id_componente = t.id_componente)) " +
		" UPDATE virtus.produtos_planos SET peso = round(R2.peso_percentual,2) FROM R2 " +
		" WHERE " +
		" R2.id_entidade = produtos_planos.id_entidade " +
		" AND R2.id_ciclo = produtos_planos.id_ciclo " +
		" AND R2.id_pilar = produtos_planos.id_pilar " +
		" AND R2.id_componente = produtos_planos.id_componente " +
		" and R2.id_plano = produtos_planos.id_plano "
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
	sqlStatement := " WITH T1 AS " +
		"   (SELECT id_entidade, " +
		"           id_ciclo, " +
		"           id_pilar, " +
		"           id_plano, " +
		"           id_componente, " +
		"           id_tipo_nota, " +
		"           round(avg(peso), 2) AS peso_tn " +
		"    FROM virtus.produtos_elementos " +
		"    WHERE (peso IS NOT NULL and peso <> 0) " +
		"    GROUP BY id_entidade, " +
		"             id_ciclo, " +
		"             id_pilar, " +
		"             id_plano, " +
		"             id_componente, " +
		"             id_tipo_nota), " +
		"      T2 AS " +
		"   (SELECT id_entidade, " +
		"           id_ciclo, " +
		"           id_pilar, " +
		"           id_componente, " +
		"           id_plano, " +
		"           SUM(peso_tn) AS soma_pesos_tipos_notas " +
		"    FROM T1 " +
		"    GROUP BY id_entidade, " +
		"             id_ciclo, " +
		"             id_pilar, " +
		"             id_componente, " +
		"             id_plano), " +
		"      T3 AS " +
		"   (SELECT T1.id_entidade, " +
		"           T1.id_ciclo, " +
		"           T1.id_pilar, " +
		"           T1.id_componente, " +
		"           T1.id_plano, " +
		"           T1.id_tipo_nota, " +
		"           T1.peso_tn, " +
		"           T2.soma_pesos_tipos_notas, " +
		"           round(T1.peso_tn*100/T2.soma_pesos_tipos_notas, 2) AS ponderacao_tipo " +
		"    FROM T1 " +
		"    INNER JOIN T2 ON (T1.id_entidade = T2.id_entidade " +
		"                      AND T1.id_ciclo = T2.id_ciclo " +
		"                      AND T1.id_pilar = T2.id_pilar " +
		"                      AND T1.id_componente = T2.id_componente " +
		"                      AND T1.id_plano = T2.id_plano) " +
		"    GROUP BY T1.id_entidade, " +
		"             T1.id_ciclo, " +
		"             T1.id_pilar, " +
		"             T1.id_componente, " +
		"             T1.id_plano, " +
		"             T1.id_tipo_nota, " +
		"             T1.peso_tn, " +
		"             T2.soma_pesos_tipos_notas), " +
		"      T4 AS " +
		"   (SELECT T3.id_entidade, " +
		"           T3.id_ciclo, " +
		"           T3.id_pilar, " +
		"           T3.id_componente, " +
		"           T3.id_plano, " +
		"           ponderacao_tipo, " +
		"           peso_tn, " +
		"           soma_pesos_tipos_notas, " +
		"           ponderacao_tipo*peso_tn/100 AS peso_plano, " +
		"           p.peso/100 AS ponderacao_plano " +
		"    FROM T3 " +
		"    INNER JOIN virtus.produtos_planos p ON (p.id_entidade = T3.id_entidade " +
		"                                     AND p.id_ciclo = T3.id_ciclo " +
		"                                     AND p.id_pilar = T3.id_pilar " +
		"                                     AND p.id_componente = T3.id_componente " +
		"                                     AND p.id_plano = T3.id_plano)), " +
		"      T5 AS " +
		"   (SELECT T4.id_entidade, " +
		"           T4.id_ciclo, " +
		"           T4.id_pilar, " +
		"           T4.id_componente, " +
		"           sum(peso_plano*ponderacao_plano) AS peso_componente " +
		"    FROM T4 " +
		"    GROUP BY T4.id_entidade, " +
		"             T4.id_ciclo, " +
		"             T4.id_pilar, " +
		"             T4.id_componente) " +
		" UPDATE virtus.produtos_componentes " +
		" SET peso = " +
		"   (SELECT DISTINCT round(T5.peso_componente,2) " +
		"    FROM T5 " +
		"    WHERE id_entidade = virtus.produtos_componentes.id_entidade " +
		"      AND id_ciclo = virtus.produtos_componentes.id_ciclo " +
		"      AND id_pilar = virtus.produtos_componentes.id_pilar " +
		"      AND id_componente = virtus.produtos_componentes.id_componente) " +
		" WHERE id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) +
		" 	AND id_ciclo = " + strconv.FormatInt(produto.CicloId, 10) +
		" 	AND id_pilar = " + strconv.FormatInt(produto.PilarId, 10) +
		" 	AND id_componente = " + strconv.FormatInt(produto.ComponenteId, 10)
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
	sqlStatement := "INSERT INTO virtus.produtos_ciclos ( " +
		" id_entidade, " +
		" id_ciclo, " +
		" nota, " +
		" id_tipo_pontuacao, " +
		" id_author, " +
		" criado_em ) " +
		" OUTPUT INSERTED.id_produto_ciclo " +
		" SELECT " +
		entidadeId + ", " +
		cicloId + ", " +
		" 0 as nota, " +
		" ?, " +
		" ?, " +
		" GETDATE() " +
		" FROM virtus.ciclos_entidades a " +
		" WHERE NOT EXISTS " +
		"  (SELECT 1 " +
		"   FROM virtus.produtos_ciclos b " +
		"   WHERE b.id_entidade = a.id_entidade " +
		"     AND b.id_ciclo = a.id_ciclo) "
	log.Println(sqlStatement)
	produtoCicloId := 0
	err := Db.QueryRow(
		sqlStatement,
		mdl.Calculada,
		currentUser.Id).Scan(&produtoCicloId)
	if err != nil {
		log.Println(err)
	}
}

func registrarProdutosPilares(currentUser mdl.User, entidadeId string, cicloId string) {
	sqlStatement := "INSERT INTO virtus.produtos_pilares " +
		" (id_entidade, id_ciclo, id_pilar, peso, nota, id_tipo_pontuacao, id_author, criado_em) " +
		" OUTPUT INSERTED.id_produto_pilar " +
		" SELECT " +
		entidadeId + ", " +
		cicloId + ", " +
		" a.id_pilar, " +
		" 0 as peso, " +
		" 0 as nota, " +
		" ?, " +
		" ?, " +
		" GETDATE() " +
		" FROM virtus.pilares_ciclos a " +
		" WHERE NOT EXISTS " +
		"  (SELECT 1 " +
		"   FROM virtus.produtos_pilares b " +
		"   WHERE b.id_entidade = " + entidadeId +
		"     AND b.id_ciclo = a.id_ciclo " +
		"     AND b.id_pilar = a.id_pilar)" +
		" AND a.id_ciclo = " + cicloId
	log.Println(sqlStatement)
	produtoPilarId := 0
	err := Db.QueryRow(
		sqlStatement,
		mdl.Calculada,
		currentUser.Id).Scan(&produtoPilarId)
	if err != nil {
		log.Println(err)
	}
}

func registrarProdutosComponentes(currentUser mdl.User, entidadeId string, cicloId string) {
	sqlStatement := "INSERT INTO virtus.produtos_componentes ( " +
		" id_entidade, " +
		" id_ciclo, " +
		" id_pilar, " +
		" id_componente, " +
		" peso, " +
		" nota, " +
		" id_tipo_pontuacao, " +
		" id_author, " +
		" criado_em ) " +
		" OUTPUT INSERTED.id_produto_componente " +
		" SELECT " + entidadeId + ", " + cicloId + ", a.id_pilar, b.id_componente, " +
		" round(avg(c.peso_padrao),2), 0 as nota, " +
		" ?, ?, GETDATE() " +
		" FROM " +
		" virtus.PILARES_CICLOS a " +
		" LEFT JOIN virtus.COMPONENTES_PILARES b ON (a.id_pilar = b.id_pilar) " +
		" LEFT JOIN virtus.ELEMENTOS_COMPONENTES c ON (b.id_componente = c.id_componente) " +
		" WHERE  " +
		" a.id_ciclo = " + cicloId +
		" AND NOT EXISTS " +
		"  (SELECT 1 " +
		"   FROM virtus.produtos_componentes c " +
		"   WHERE c.id_entidade = " + entidadeId +
		"     AND c.id_ciclo = a.id_ciclo " +
		"     AND c.id_pilar = a.id_pilar " +
		"     AND c.id_componente = b.id_componente) " +
		" GROUP BY a.id_ciclo,a.id_pilar,b.id_componente ORDER BY 1,2,3,4"
	log.Println(sqlStatement)
	produtoComponenteId := 0
	err := Db.QueryRow(
		sqlStatement,
		mdl.Calculada,
		currentUser.Id).Scan(&produtoComponenteId)
	if err != nil {
		log.Println(err)
	}
}

func registrarProdutosPlanos(param mdl.ProdutoPlano, numPlano string, currentUser mdl.User) {
	sqlStatement := "INSERT INTO virtus.produtos_planos ( " +
		" id_entidade, " +
		" id_ciclo, " +
		" id_pilar, " +
		" id_componente, " +
		" id_plano, " +
		//" peso, " +
		" nota, " +
		" id_tipo_pontuacao, " +
		" id_author, " +
		" criado_em ) " +
		" SELECT DISTINCT " + strconv.FormatInt(param.EntidadeId, 10) + ", " +
		strconv.FormatInt(param.CicloId, 10) + ", " +
		strconv.FormatInt(param.PilarId, 10) + ", " +
		strconv.FormatInt(param.ComponenteId, 10) + ", " +
		" p.id_plano, " +
		//" p.recurso_garantidor as peso," +
		" 0 as nota, " +
		" ?, ?, GETDATE() " +
		" FROM " +
		" virtus.PILARES_CICLOS a " +
		" LEFT JOIN virtus.COMPONENTES_PILARES b ON (a.id_pilar = b.id_pilar) " +
		" LEFT JOIN virtus.ELEMENTOS_COMPONENTES c ON (b.id_componente = c.id_componente) " +
		" LEFT JOIN virtus.PLANOS p ON (p.id_entidade = " + strconv.FormatInt(param.EntidadeId, 10) +
		" AND p.id_plano = " + numPlano + ") " +
		" WHERE  " +
		" p.id_entidade = " + strconv.FormatInt(param.EntidadeId, 10) +
		" AND p.id_plano = " + numPlano +
		" AND NOT EXISTS " +
		"  (SELECT 1 " +
		"   FROM virtus.produtos_planos e " +
		"   WHERE e.id_entidade = " + strconv.FormatInt(param.EntidadeId, 10) +
		"     AND e.id_ciclo = " + strconv.FormatInt(param.CicloId, 10) +
		"     AND e.id_pilar = " + strconv.FormatInt(param.PilarId, 10) +
		"     AND e.id_componente = " + strconv.FormatInt(param.ComponenteId, 10) +
		"     AND e.id_plano IN (" + numPlano + ")) " +
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

	sqlStatement = "INSERT INTO virtus.produtos_tipos_notas ( " +
		" id_entidade, " +
		" id_ciclo, " +
		" id_pilar, " +
		" id_componente, " +
		" id_plano, " +
		" id_tipo_nota, " +
		//" peso, " +
		" nota, " +
		" id_tipo_pontuacao, " +
		" id_author, " +
		" criado_em ) " +
		" SELECT p.id_entidade, " +
		" p.id_ciclo, " +
		" p.id_pilar, " +
		" p.id_componente, " +
		" p.id_plano, " +
		" d.id_tipo_nota, " +
		//" 1, " +
		" 0, ?, ?, GETDATE() " +
		" FROM " +
		" virtus.pilares_ciclos a " +
		" LEFT JOIN virtus.COMPONENTES_PILARES b ON a.id_pilar = b.id_pilar " +
		" LEFT JOIN virtus.TIPOS_NOTAS_COMPONENTES d ON b.id_componente = d.id_componente " +
		" LEFT JOIN virtus.PRODUTOS_PLANOS p ON b.id_componente = p.id_componente " +
		" WHERE  " +
		" a.id_ciclo = " + strconv.FormatInt(param.CicloId, 10) +
		" AND p.id_entidade = " + strconv.FormatInt(param.EntidadeId, 10) +
		" AND p.id_pilar = " + strconv.FormatInt(param.PilarId, 10) +
		" AND p.id_componente = " + strconv.FormatInt(param.ComponenteId, 10) +
		" AND p.id_plano = " + numPlano +
		" AND NOT EXISTS " +
		"  (SELECT 1 " +
		"   FROM virtus.produtos_tipos_notas e " +
		"   WHERE e.id_entidade = " + strconv.FormatInt(param.EntidadeId, 10) +
		"     AND e.id_ciclo = a.id_ciclo " +
		"     AND e.id_pilar = a.id_pilar " +
		"     AND e.id_plano = " + numPlano +
		"     AND e.id_tipo_nota = d.id_tipo_nota " +
		"     AND e.id_componente = b.id_componente) " +
		" GROUP BY p.id_entidade, " +
		" p.id_ciclo, " +
		" p.id_pilar, " +
		" p.id_componente, " +
		" p.id_plano, " +
		" d.id_tipo_nota " +
		" ORDER BY 1,2,3,4,5,6"
	log.Println(sqlStatement)
	Db.QueryRow(
		sqlStatement,
		mdl.Calculada,
		currentUser.Id)

	sqlStatement = "INSERT INTO virtus.produtos_elementos ( " +
		" id_entidade, " +
		" id_ciclo, " +
		" id_pilar, " +
		" id_componente, " +
		" id_plano, " +
		" id_tipo_nota, " +
		" id_elemento, " +
		" peso," +
		" nota," +
		" id_tipo_pontuacao, " +
		" id_author, " +
		" criado_em ) " +
		" SELECT d.id_entidade, " +
		" d.id_ciclo, " +
		" d.id_pilar, " +
		" d.id_componente, " +
		" d.id_plano, " +
		" c.id_tipo_nota, " +
		" c.id_elemento, " +
		" c.peso_padrao, " +
		" 0, ?, ?, GETDATE() " +
		" FROM " +
		" virtus.pilares_ciclos a " +
		" INNER JOIN " +
		" virtus.componentes_pilares b ON a.id_pilar = b.id_pilar " +
		" INNER JOIN " +
		" virtus.elementos_componentes c ON b.id_componente = c.id_componente " +
		" INNER JOIN " +
		" virtus.produtos_planos d ON b.id_componente = d.id_componente " +
		" WHERE " +
		" a.id_ciclo = " + strconv.FormatInt(param.CicloId, 10) +
		" AND d.id_entidade = " + strconv.FormatInt(param.EntidadeId, 10) +
		" AND d.id_pilar = " + strconv.FormatInt(param.PilarId, 10) +
		" AND d.id_componente = " + strconv.FormatInt(param.ComponenteId, 10) +
		" AND d.id_plano = " + numPlano +
		" AND NOT EXISTS " +
		"  (SELECT 1 " +
		"   FROM virtus.produtos_elementos e " +
		"   WHERE e.id_entidade = " + strconv.FormatInt(param.EntidadeId, 10) +
		"     AND e.id_ciclo = a.id_ciclo " +
		"     AND e.id_pilar = a.id_pilar " +
		"     AND e.id_componente = b.id_componente " +
		"     AND e.id_plano = " + numPlano +
		"     AND e.id_elemento = c.id_elemento)"
	log.Println(sqlStatement)
	Db.QueryRow(
		sqlStatement,
		mdl.Calculada,
		currentUser.Id)

	sqlStatement = "INSERT INTO virtus.produtos_itens ( " +
		" id_entidade, " +
		" id_ciclo, " +
		" id_pilar, " +
		" id_componente, " +
		" id_plano, " +
		" id_tipo_nota, " +
		" id_elemento, " +
		" id_item, " +
		" id_author, " +
		" criado_em ) " +
		" SELECT p.id_entidade, " +
		" p.id_ciclo, " +
		" p.id_pilar, " +
		" p.id_componente, " +
		" p.id_plano, " +
		" c.id_tipo_nota, " +
		" c.id_elemento, d.id_item, ?, GETDATE() " +
		" FROM virtus.pilares_ciclos a " +
		" INNER JOIN virtus.componentes_pilares b ON a.id_pilar = b.id_pilar " +
		" INNER JOIN virtus.elementos_componentes c ON b.id_componente = c.id_componente " +
		" INNER JOIN virtus.itens d ON c.id_elemento = d.id_elemento " +
		" INNER JOIN virtus.produtos_planos p ON b.id_componente = p.id_componente " +
		" WHERE " +
		" a.id_ciclo = " + strconv.FormatInt(param.CicloId, 10) +
		" AND p.id_entidade = " + strconv.FormatInt(param.EntidadeId, 10) +
		" AND p.id_pilar = " + strconv.FormatInt(param.PilarId, 10) +
		" AND p.id_componente = " + strconv.FormatInt(param.ComponenteId, 10) +
		" AND p.id_plano = " + numPlano +
		" AND NOT EXISTS (SELECT 1 " +
		"     FROM virtus.produtos_itens e " +
		"     WHERE e.id_entidade = " + strconv.FormatInt(param.EntidadeId, 10) +
		"       AND e.id_plano = " + numPlano +
		"       AND e.id_ciclo = a.id_ciclo " +
		"       AND e.id_pilar = a.id_pilar " +
		"       AND e.id_componente = b.id_componente " +
		"	   AND e.id_elemento = c.id_elemento " +
		"	   AND e.id_item = d.id_item)"
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
		"    FROM virtus.produtos_tipos_notas a " +
		"    JOIN virtus.produtos_planos b ON (a.id_entidade = b.id_entidade  " +
		" 	AND a.id_ciclo = b.id_ciclo " +
		" 	AND a.id_pilar = b.id_pilar  " +
		" 	AND a.id_componente = b.id_componente " +
		" 	AND a.id_plano = b.id_plano) " +
		"    JOIN virtus.produtos_componentes c ON (a.id_entidade = c.id_entidade  " +
		" 	AND a.id_ciclo = c.id_ciclo " +
		" 	AND a.id_pilar = c.id_pilar  " +
		" 	AND a.id_componente = c.id_componente) " +
		"    JOIN virtus.produtos_pilares d ON (a.id_entidade = d.id_entidade  " +
		" 	AND a.id_ciclo = d.id_ciclo " +
		" 	AND a.id_pilar = d.id_pilar) " +
		"    JOIN virtus.produtos_ciclos e ON (a.id_entidade = e.id_entidade  " +
		" 	AND a.id_ciclo = e.id_ciclo) " +
		"    WHERE a.id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) +
		"      AND a.id_ciclo = " + strconv.FormatInt(produto.CicloId, 10) +
		"      AND a.id_pilar = " + strconv.FormatInt(produto.PilarId, 10) +
		"      AND a.id_componente = " + strconv.FormatInt(produto.ComponenteId, 10) +
		"      AND a.id_plano = " + strconv.FormatInt(produto.PlanoId, 10) +
		"      AND a.id_tipo_nota = " + strconv.FormatInt(produto.TipoNotaId, 10)
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
		"	 string_agg(concat(a.id_tipo_nota,':',format(a.peso,'N','pt-br')),'/') AS tipo_nota " +
		"    FROM virtus.produtos_tipos_notas a " +
		"    JOIN virtus.produtos_planos b ON (a.id_entidade = b.id_entidade  " +
		" 	AND a.id_ciclo = b.id_ciclo " +
		" 	AND a.id_pilar = b.id_pilar  " +
		" 	AND a.id_componente = b.id_componente " +
		" 	AND a.id_plano = b.id_plano) " +
		"    JOIN virtus.produtos_componentes c ON (a.id_entidade = c.id_entidade  " +
		" 	AND a.id_ciclo = c.id_ciclo " +
		" 	AND a.id_pilar = c.id_pilar  " +
		" 	AND a.id_componente = c.id_componente) " +
		"    JOIN virtus.produtos_pilares d ON (a.id_entidade = d.id_entidade  " +
		" 	AND a.id_ciclo = d.id_ciclo " +
		" 	AND a.id_pilar = d.id_pilar) " +
		"    WHERE a.id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) +
		"      AND a.id_ciclo = " + strconv.FormatInt(produto.CicloId, 10) +
		"      AND a.id_pilar = " + strconv.FormatInt(produto.PilarId, 10) +
		"      AND a.id_componente = " + strconv.FormatInt(produto.ComponenteId, 10) +
		"      AND a.id_plano = " + strconv.FormatInt(produto.PlanoId, 10) +
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

func registrarPesoPilar(param mdl.ProdutoPilar) string {
	// PESOS PILARES
	log.Println("=====> REGISTRAR PESO PILAR")
	sqlStatement := "UPDATE virtus.produtos_pilares SET peso = ?, motivacao_peso = ? " +
		" WHERE id_entidade = ? AND " +
		" id_ciclo = ? AND " +
		" id_pilar = ? "
	log.Println(sqlStatement)
	updtForm, err := Db.Prepare(sqlStatement)
	if err != nil {
		log.Println(err.Error())
	}
	updtForm.Exec(param.Peso,
		param.Motivacao,
		param.EntidadeId,
		param.CicloId,
		param.PilarId)
	var produto mdl.ProdutoElemento
	produto.EntidadeId = param.EntidadeId
	produto.CicloId = param.CicloId
	atualizarCicloNota(produto)

	sql := " SELECT coalesce(format(e.nota, 'N', 'pt-br'),'.00') AS ciclo " +
		"    FROM virtus.produtos_ciclos e " +
		"    WHERE e.id_entidade = " + strconv.FormatInt(produto.EntidadeId, 10) +
		"      AND e.id_ciclo = " + strconv.FormatInt(produto.CicloId, 10)
	log.Println(sql)
	rows, _ := Db.Query(sql)
	notaCiclo := ""
	defer rows.Close()
	if rows.Next() {
		rows.Scan(&notaCiclo)
	}
	log.Println(">>>>> NOTA DO CICLO É ")
	log.Println(notaCiclo)
	return notaCiclo
}

func getAnalise(rota string) string {
	valores := strings.Split(rota, "_")
	sql := " SELECT 'analise' "
	if valores[1] == "Ciclo" {
		sql = " SELECT analise FROM virtus.produtos_ciclos WHERE id_entidade = " + valores[2] + " AND id_ciclo = " + valores[3]
	} else if valores[1] == "Pilar" {
		sql = " SELECT analise FROM virtus.produtos_pilares WHERE id_entidade = " + valores[2] + " AND id_ciclo = " + valores[3] +
			" AND id_pilar = " + valores[4]
	} else if valores[1] == "Componente" {
		sql = " SELECT analise FROM virtus.produtos_componentes WHERE id_entidade = " + valores[2] + " AND id_ciclo = " + valores[3] +
			" AND id_pilar = " + valores[4] + " AND id_componente = " + valores[5]
	} else if valores[1] == "Plano" {
		sql = " SELECT analise FROM virtus.produtos_planos WHERE id_entidade = " + valores[2] + " AND id_ciclo = " + valores[3] +
			" AND id_pilar = " + valores[4] + " AND id_componente = " + valores[5] + " AND id_plano = " + valores[6]
	} else if valores[1] == "TipoNota" {
		sql = " SELECT analise FROM virtus.produtos_tipos_notas WHERE id_entidade = " + valores[2] + " AND id_ciclo = " + valores[3] +
			" AND id_pilar = " + valores[4] + " AND id_componente = " + valores[5] + " AND id_plano = " + valores[6] +
			" AND id_tipo_nota = " + valores[7]
	} else if valores[1] == "Elemento" {
		sql = " SELECT analise FROM virtus.produtos_elementos WHERE id_entidade = " + valores[2] + " AND id_ciclo = " + valores[3] +
			" AND id_pilar = " + valores[4] + " AND id_componente = " + valores[5] + " AND id_plano = " + valores[6] +
			" AND id_tipo_nota = " + valores[7] + " AND id_elemento = " + valores[8]
	} else if valores[1] == "Item" {
		sql = " SELECT analise FROM virtus.produtos_itens WHERE id_entidade = " + valores[2] + " AND id_ciclo = " + valores[3] +
			" AND id_pilar = " + valores[4] + " AND id_componente = " + valores[5] + " AND id_plano = " + valores[6] +
			" AND id_tipo_nota = " + valores[7] + " AND id_elemento = " + valores[8] + " AND id_item = " + valores[9]
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
		sqlStatement = " UPDATE virtus.produtos_ciclos SET analise = '" + analise + "' WHERE id_entidade = " + valores[2] + " AND id_ciclo = " + valores[3]
	} else if valores[1] == "Pilar" {
		sqlStatement = " UPDATE virtus.produtos_pilares SET analise = '" + analise + "' WHERE id_entidade = " + valores[2] + " AND id_ciclo = " + valores[3] +
			" AND id_pilar = " + valores[4]
	} else if valores[1] == "Componente" {
		sqlStatement = " UPDATE virtus.produtos_componentes SET analise = '" + analise + "' WHERE id_entidade = " + valores[2] + " AND id_ciclo = " + valores[3] +
			" AND id_pilar = " + valores[4] + " AND id_componente = " + valores[5]
	} else if valores[1] == "Plano" {
		sqlStatement = " UPDATE virtus.produtos_planos SET analise = '" + analise + "' WHERE id_entidade = " + valores[2] + " AND id_ciclo = " + valores[3] +
			" AND id_pilar = " + valores[4] + " AND id_componente = " + valores[5] + " AND id_plano = " + valores[6]
	} else if valores[1] == "TipoNota" {
		sqlStatement = " UPDATE virtus.produtos_tipos_notas SET analise = '" + analise + "' WHERE id_entidade = " + valores[2] + " AND id_ciclo = " + valores[3] +
			" AND id_pilar = " + valores[4] + " AND id_componente = " + valores[5] + " AND id_plano = " + valores[6] +
			" AND id_tipo_nota = " + valores[7]
	} else if valores[1] == "Elemento" {
		sqlStatement = " UPDATE virtus.produtos_elementos SET analise = '" + analise + "' WHERE id_entidade = " + valores[2] + " AND id_ciclo = " + valores[3] +
			" AND id_pilar = " + valores[4] + " AND id_componente = " + valores[5] + " AND id_plano = " + valores[6] +
			" AND id_tipo_nota = " + valores[7] + " AND id_elemento = " + valores[8]
	} else if valores[1] == "Item" {
		sqlStatement = " UPDATE virtus.produtos_itens SET analise = '" + analise + "' WHERE id_entidade = " + valores[2] + " AND id_ciclo = " + valores[3] +
			" AND id_pilar = " + valores[4] + " AND id_componente = " + valores[5] + " AND id_plano = " + valores[6] +
			" AND id_tipo_nota = " + valores[7] + " AND id_elemento = " + valores[8] + " AND id_item = " + valores[9]
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
		sql = " SELECT descricao, referencia FROM virtus.ciclos WHERE id_ciclo = " + valores[3]
	} else if valores[1] == "Pilar" {
		sql = " SELECT descricao, referencia FROM virtus.pilares WHERE id_pilar = " + valores[4]
	} else if valores[1] == "Componente" {
		sql = " SELECT descricao, referencia FROM virtus.componentes WHERE id_componente = " + valores[5]
	} else if valores[1] == "Plano" {
		sql = " SELECT descricao, referencia FROM virtus.planos WHERE id_plano = " + valores[6]
	} else if valores[1] == "TipoNota" {
		sql = " SELECT descricao, referencia FROM virtus.tipos_notas WHERE id_tipo_nota = " + valores[7]
	} else if valores[1] == "Elemento" {
		sql = " SELECT descricao, referencia FROM virtus.elementos WHERE id_elemento = " + valores[8]
	} else if valores[1] == "Item" {
		sql = " SELECT descricao, referencia FROM virtus.itens WHERE id_item = " + valores[9]
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
	sql := "SELECT planos_configurados FROM (SELECT a.id_componente, string_agg(b.cnpb,', ') planos_configurados " +
		" FROM virtus.produtos_planos a " +
		" INNER JOIN virtus.planos b ON a.id_plano = b.id_plano " +
		" WHERE a.id_entidade = " + entidadeId + " AND a.id_ciclo = " + cicloId +
		" AND a.id_pilar = " + pilarId + " AND a.id_componente = " + componenteId +
		" GROUP BY a.id_componente) R "
	log.Println(sql)
	rows, _ := Db.Query(sql)
	defer rows.Close()
	var configuracaoPlanos string
	if rows.Next() {
		rows.Scan(&configuracaoPlanos)
	}
	return configuracaoPlanos
}
