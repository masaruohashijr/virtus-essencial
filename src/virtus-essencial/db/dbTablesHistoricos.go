package db

import (
	"log"
)

func createTablesHistoricos() {
	// Table PRODUTOS_CICLOS
	stmt := "IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE name = 'produtos_ciclos_historicos' AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.produtos_ciclos_historicos (" +
		" id_produto_ciclo_historico integer DEFAULT NEXT VALUE FOR id_produtos_ciclos_historicos_seq, " +
		" id_entidade integer," +
		" id_ciclo integer," +
		" id_tipo_pontuacao integer," +
		" nota double precision," +
		" motivacao varchar(4000)," +
		" id_supervisor integer," +
		" id_auditor integer," +
		" id_author integer," +
		" criado_em datetime," +
		" id_versao_origem integer," +
		" id_status integer)" +
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
		" CREATE TABLE virtus.produtos_pilares_historicos (" +
		" id_produto_pilar_historico integer DEFAULT NEXT VALUE FOR id_produtos_pilares_historicos_seq, " +
		" id_entidade integer," +
		" id_ciclo integer," +
		" id_pilar integer," +
		" id_tipo_pontuacao integer," +
		" peso double precision," +
		" nota double precision," +
		" tipo_alteracao character(1)," +
		" motivacao_peso varchar(4000)," +
		" motivacao_nota varchar(4000)," +
		" id_supervisor integer," +
		" id_auditor integer," +
		" id_author integer," +
		" criado_em datetime," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table PRODUTOS_COMPONENTES HISTORICOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_componentes_historicos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.produtos_componentes_historicos (" +
		" id_produto_componente_historico integer DEFAULT NEXT VALUE FOR id_produtos_componentes_historicos_seq," +
		" id_entidade integer," +
		" id_ciclo integer," +
		" id_pilar integer," +
		" id_plano integer," +
		" id_componente integer," +
		" id_tipo_pontuacao integer," +
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
		" id_supervisor integer," +
		" id_auditor integer," +
		" auditor_anterior_id integer," +
		" inicia_em datetime," +
		" termina_em datetime," +
		" inicia_em_anterior datetime," +
		" termina_em_anterior datetime," +
		" id_author integer," +
		" criado_em datetime," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table PRODUTOS_ELEMENTOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_elementos_historicos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.produtos_elementos_historicos (" +
		" id_produto_elemento_historico integer DEFAULT NEXT VALUE FOR id_produtos_elementos_historicos_seq," +
		" id_entidade integer," +
		" id_ciclo integer," +
		" id_pilar integer," +
		" id_plano integer," +
		" id_componente integer," +
		" id_tipo_nota integer," +
		" id_elemento integer," +
		" id_tipo_pontuacao integer," +
		" peso double precision," +
		" nota double precision," +
		" tipo_alteracao character(1)," +
		" motivacao_peso varchar(4000)," +
		" motivacao_nota varchar(4000)," +
		" justificativa varchar(4000)," +
		" id_supervisor integer," +
		" id_auditor integer," +
		" id_author integer," +
		" criado_em datetime," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table PRODUTOS_ITENS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_itens_historicos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.produtos_itens_historicos (" +
		" id_produto_item_historico integer DEFAULT NEXT VALUE FOR id_produtos_itens_historicos_seq," +
		" id_entidade integer," +
		" id_ciclo integer," +
		" id_pilar integer," +
		" id_plano integer," +
		" id_componente integer," +
		" id_tipo_nota integer," +
		" id_elemento integer," +
		" id_item integer," +
		" avaliacao varchar(4000)," +
		" anexo varchar(4000)," +
		" id_author integer," +
		" criado_em datetime," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table PRODUTOS_TIPOS_PLANOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_planos_historicos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.produtos_planos_historicos (" +
		" id_produto_plano_historico integer DEFAULT NEXT VALUE FOR id_produtos_planos_historicos_seq," +
		" id_entidade integer," +
		" id_ciclo integer," +
		" id_pilar integer," +
		" id_componente integer," +
		" id_plano integer," +
		" id_tipo_pontuacao integer," +
		" peso double precision," +
		" nota double precision," +
		" anexo varchar(4000)," +
		" id_author integer," +
		" criado_em datetime," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table PRODUTOS_TIPOS_NOTAS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_tipos_notas_historicos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.produtos_tipos_notas_historicos (" +
		" id_produto_tipo_nota_historico integer DEFAULT NEXT VALUE FOR id_produtos_tipos_notas_historicos_seq," +
		" id_entidade integer," +
		" id_ciclo integer," +
		" id_pilar integer," +
		" id_componente integer," +
		" id_tipo_nota integer," +
		" id_tipo_pontuacao integer," +
		" peso double precision," +
		" nota double precision," +
		" anexo varchar(4000)," +
		" id_author integer," +
		" criado_em datetime," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")
}
