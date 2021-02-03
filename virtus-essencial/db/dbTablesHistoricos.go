package db

import (
	"log"
)

func createTablesHistoricos() {
	// Table PRODUTOS_CICLOS
	stmt := "IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_ciclos_historicos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE produtos_ciclos_historicos (" +
		" id integer DEFAULT NEXT VALUE FOR produtos_ciclos_historicos_id_seq, " +
		" entidade_id integer," +
		" ciclo_id integer," +
		" tipo_pontuacao_id integer," +
		" nota double precision," +
		" motivacao varchar(4000)," +
		" supervisor_id integer," +
		" auditor_id integer," +
		" author_id integer," +
		" criado_em datetime," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END "
	//log.Println(stmt)
	_, err := db.Exec(stmt)
	if err != nil {
		log.Println(err.Error())
	}

	// Table PRODUTOS_PILARES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_pilares_historicos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE produtos_pilares_historicos (" +
		" id integer DEFAULT NEXT VALUE FOR produtos_pilares_historicos_id_seq, " +
		" entidade_id integer," +
		" ciclo_id integer," +
		" pilar_id integer," +
		" tipo_pontuacao_id integer," +
		" peso double precision," +
		" nota double precision," +
		" tipo_alteracao character(1)," +
		" motivacao_peso varchar(4000)," +
		" motivacao_nota varchar(4000)," +
		" supervisor_id integer," +
		" auditor_id integer," +
		" author_id integer," +
		" criado_em datetime," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table PRODUTOS_COMPONENTES HISTORICOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_componentes_historicos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE produtos_componentes_historicos (" +
		" id integer DEFAULT NEXT VALUE FOR produtos_componentes_historicos_id_seq," +
		" entidade_id integer," +
		" ciclo_id integer," +
		" pilar_id integer," +
		" plano_id integer," +
		" componente_id integer," +
		" tipo_pontuacao_id integer," +
		" peso double precision," +
		" nota double precision," +
		" config varchar(300)," +
		" config_anterior varchar(300)," +
		" tipo_alteracao character(1)," +
		" motivacao_config varchar(4000)," +
		" motivacao_reprogramacao varchar(4000)," +
		" motivacao_peso varchar(4000)," +
		" motivacao_nota varchar(4000)," +
		" justificativa varchar(4000)," +
		" supervisor_id integer," +
		" auditor_id integer," +
		" auditor_anterior_id integer," +
		" inicia_em datetime," +
		" termina_em datetime," +
		" inicia_em_anterior datetime," +
		" termina_em_anterior datetime," +
		" author_id integer," +
		" criado_em datetime," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table PRODUTOS_ELEMENTOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_elementos_historicos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE produtos_elementos_historicos (" +
		" id integer DEFAULT NEXT VALUE FOR produtos_elementos_historicos_id_seq," +
		" entidade_id integer," +
		" ciclo_id integer," +
		" pilar_id integer," +
		" plano_id integer," +
		" componente_id integer," +
		" tipo_nota_id integer," +
		" elemento_id integer," +
		" tipo_pontuacao_id integer," +
		" peso double precision," +
		" nota double precision," +
		" tipo_alteracao character(1)," +
		" motivacao_peso varchar(4000)," +
		" motivacao_nota varchar(4000)," +
		" justificativa varchar(4000)," +
		" supervisor_id integer," +
		" auditor_id integer," +
		" author_id integer," +
		" criado_em datetime," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table PRODUTOS_ITENS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_itens_historicos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE produtos_itens_historicos (" +
		" id integer DEFAULT NEXT VALUE FOR produtos_itens_historicos_id_seq," +
		" entidade_id integer," +
		" ciclo_id integer," +
		" pilar_id integer," +
		" plano_id integer," +
		" componente_id integer," +
		" tipo_nota_id integer," +
		" elemento_id integer," +
		" item_id integer," +
		" avaliacao varchar(4000)," +
		" anexo varchar(4000)," +
		" author_id integer," +
		" criado_em datetime," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table PRODUTOS_TIPOS_PLANOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_planos_historicos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE produtos_planos_historicos (" +
		" id integer DEFAULT NEXT VALUE FOR produtos_planos_historicos_id_seq," +
		" entidade_id integer," +
		" ciclo_id integer," +
		" pilar_id integer," +
		" componente_id integer," +
		" plano_id integer," +
		" tipo_pontuacao_id integer," +
		" peso double precision," +
		" nota double precision," +
		" anexo varchar(4000)," +
		" author_id integer," +
		" criado_em datetime," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table PRODUTOS_TIPOS_NOTAS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_tipos_notas_historicos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE produtos_tipos_notas_historicos (" +
		" id integer DEFAULT NEXT VALUE FOR produtos_tipos_notas_historicos_id_seq," +
		" entidade_id integer," +
		" ciclo_id integer," +
		" pilar_id integer," +
		" componente_id integer," +
		" tipo_nota_id integer," +
		" tipo_pontuacao_id integer," +
		" peso double precision," +
		" nota double precision," +
		" anexo varchar(4000)," +
		" author_id integer," +
		" criado_em datetime," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")
}
