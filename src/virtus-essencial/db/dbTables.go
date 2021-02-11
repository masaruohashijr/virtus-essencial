package db

import (
	"log"
)

func createTable() {
	// Table ACTIONS
	stmt := "IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'actions') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  actions (" +
		" id integer DEFAULT NEXT VALUE FOR actions_id_seq NOT NULL, " +
		" name varchar(255) NOT NULL, " +
		" origin_status_id integer, " +
		" destination_status_id integer, " +
		" other_than bit, " +
		" description varchar(4000)," +
		" author_id integer," +
		" created_at datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END "
	_, err := db.Exec(stmt)
	if err != nil {
		log.Println(err.Error())
	}

	// Table ACTIONS_STATUS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'actions_status') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE actions_status (" +
		" id integer DEFAULT NEXT VALUE FOR actions_status_id_seq NOT NULL," +
		" action_id integer," +
		" origin_status_id integer," +
		" destination_status_id integer)" +
		" END ")

	// Table ACTIVITIES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'activities') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE activities (" +
		" id integer DEFAULT NEXT VALUE FOR activities_id_seq NOT NULL," +
		" workflow_id integer," +
		" action_id integer," +
		" expiration_action_id integer," +
		" expiration_time_days integer," +
		" start_at datetime ," +
		" end_at datetime )" +
		" END ")

	// Table ACTIVITIES_ROLES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'activities_roles') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE activities_roles (" +
		" id integer DEFAULT NEXT VALUE FOR activities_roles_id_seq NOT NULL," +
		" activity_id integer," +
		" role_id integer)" +
		" END ")

	// Table CHAMADOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'chamados') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE chamados (" +
		" id integer DEFAULT NEXT VALUE FOR chamados_id_seq NOT NULL," +
		" titulo varchar(255) NOT NULL," +
		" descricao varchar(4000)," +
		" acompanhamento varchar(4000)," +
		" responsavel_id integer," +
		" relator_id integer," +
		" tipo_chamado_id character(1)," +
		" prioridade_id character(1)," +
		" estimativa integer," +
		" inicia_em datetime ," +
		" pronto_em datetime ," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table CHAMADOS_VERSOES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'activities_roles') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE activities_roles (" +
		" id integer DEFAULT NEXT VALUE FOR chamados_versoes_id_seq NOT NULL," +
		" activity_id integer," +
		" role_id integer)" +
		" END ")

	// Table CICLOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'ciclos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  ciclos (" +
		" id integer DEFAULT NEXT VALUE FOR ciclos_id_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" descricao varchar(4000)," +
		" referencia varchar(500)," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table CICLOS_ENTIDADES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'ciclos_entidades') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE ciclos_entidades (" +
		" id integer DEFAULT NEXT VALUE FOR ciclos_entidades_id_seq NOT NULL," +
		" ciclo_id integer," +
		" entidade_id integer," +
		" tipo_media integer," +
		" supervisor_id integer," + // TODO FK
		" inicia_em datetime ," +
		" termina_em datetime ," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table COMENTARIOS ANOTACOES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'comentarios_anotacoes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  comentarios_anotacoes (" +
		" id integer DEFAULT NEXT VALUE FOR comentarios_anotacoes_id_seq NOT NULL," +
		" anotacao_id integer," +
		" texto varchar(4000)," +
		" referencia varchar(255)," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table COMENTARIOS CHAMADOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'comentarios_chamados') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  comentarios_chamados (" +
		" id integer DEFAULT NEXT VALUE FOR comentarios_chamados_id_seq NOT NULL," +
		" chamado_id integer," +
		" texto varchar(4000)," +
		" referencia varchar(255)," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table COMPONENTES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'componentes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  componentes (" +
		" id integer DEFAULT NEXT VALUE FOR componentes_id_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" descricao varchar(4000)," +
		" referencia varchar(500)," +
		" pga varchar(1)," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table COMPONENTES_PILARES
	stmt = "IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'componentes_pilares') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE componentes_pilares (" +
		" id integer DEFAULT NEXT VALUE FOR componentes_pilares_id_seq NOT NULL," +
		" componente_id integer, " +
		" pilar_id integer," +
		" tipo_media integer," +
		" peso_padrao integer," +
		" sonda varchar (255)," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END "
	_, err = db.Exec(stmt)
	if err != nil {
		log.Println(err.Error())
	}

	// Table ELEMENTOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'elementos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  elementos (" +
		" id integer DEFAULT NEXT VALUE FOR elementos_id_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" descricao varchar(4000)," +
		" referencia varchar(500)," +
		" peso integer DEFAULT 1 NOT NULL," +
		" author_id integer," +
		" criado_em datetime ," +
		" status_id integer)" +
		" END ")

	// Table ELEMENTOS_COMPONENTES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'elementos_componentes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE elementos_componentes (" +
		" id integer DEFAULT NEXT VALUE FOR elementos_componentes_id_seq NOT NULL," +
		" componente_id integer," +
		" elemento_id integer," +
		" tipo_nota_id integer," +
		" peso_padrao integer," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table ENTIDADES
	stmt = "IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'entidades') AND type in (N'U')) " +
		" BEGIN " +
		" CREATE TABLE entidades (" +
		" id integer DEFAULT NEXT VALUE FOR entidades_id_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" descricao varchar(4000)," +
		" sigla varchar(25)," +
		" codigo varchar(4000)," +
		" situacao varchar(30)," +
		" ESI BIT," +
		" municipio varchar(255)," +
		" sigla_uf character(2)," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END "
		//	log.Println(stmt)
	_, err = db.Exec(stmt)
	if err != nil {
		log.Println(err.Error())
	}

	// Table ESCRITORIOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'escritorios') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  escritorios (" +
		" id integer DEFAULT NEXT VALUE FOR escritorios_id_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" abreviatura character (4)," +
		" descricao varchar(4000)," +
		" chefe_id integer," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table FEATURES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'features') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  features  (" +
		" id integer DEFAULT NEXT VALUE FOR features_id_seq NOT NULL," +
		" name varchar(255) NOT NULL," +
		" code varchar(255) NOT NULL," +
		" description varchar(4000)," +
		" author_id integer," +
		" created_at datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table FEATURES_ROLES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'features_roles') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE features_roles (" +
		" id int DEFAULT NEXT VALUE FOR features_roles_id_seq," +
		" feature_id int NOT NULL," +
		" role_id int NOT NULL," +
		" CONSTRAINT pkey_features_roles PRIMARY KEY NONCLUSTERED (id))" +
		" END ")

	// Table FEATURES_ACTIVITIES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'features_activities') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE features_activities (" +
		" id integer DEFAULT NEXT VALUE FOR features_activities_id_seq NOT NULL," +
		" feature_id integer," +
		" activity_id integer)" +
		" END ")

	// Table INTEGRANTES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'integrantes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE integrantes (" +
		" id integer DEFAULT NEXT VALUE FOR integrantes_id_seq NOT NULL," +
		" entidade_id integer," +
		" ciclo_id integer," +
		" usuario_id integer," +
		" inicia_em datetime ," +
		" termina_em datetime ," +
		" motivacao varchar(4000)," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table ITENS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'itens') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  itens (" +
		" id integer DEFAULT NEXT VALUE FOR itens_id_seq NOT NULL," +
		" elemento_id integer," +
		" nome varchar(255) NOT NULL," +
		" descricao varchar(4000)," +
		" referencia varchar(500)," +
		" criado_em datetime ," +
		" author_id integer," +
		" status_id integer)" +
		" END ")

	// Table JURISDICOES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'jurisdicoes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE jurisdicoes (" +
		" id integer DEFAULT NEXT VALUE FOR jurisdicoes_id_seq NOT NULL," +
		" escritorio_id integer," +
		" entidade_id integer," +
		" inicia_em datetime ," +
		" termina_em datetime ," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table MEMBROS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'membros') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE membros (" +
		" id integer DEFAULT NEXT VALUE FOR membros_id_seq NOT NULL," +
		" escritorio_id integer," +
		" usuario_id integer," +
		" inicia_em datetime ," +
		" termina_em datetime ," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table PILARES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'pilares') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  pilares (" +
		" id integer DEFAULT NEXT VALUE FOR pilares_id_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" descricao varchar(4000)," +
		" referencia varchar(500)," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table PILARES_CICLOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'pilares_ciclos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE pilares_ciclos (" +
		" id integer DEFAULT NEXT VALUE FOR pilares_ciclos_id_seq NOT NULL," +
		" pilar_id integer," +
		" ciclo_id integer," +
		" tipo_media integer," +
		" peso_padrao double precision," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table PLANOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'planos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  planos (" +
		" id integer DEFAULT NEXT VALUE FOR planos_id_seq NOT NULL," +
		" entidade_id integer," +
		" nome varchar(255)," +
		" descricao varchar(4000)," +
		" referencia varchar(500)," +
		" cnpb varchar(255)," +
		" legislacao varchar(255)," +
		" situacao varchar(255)," +
		" recurso_garantidor double precision," +
		" modalidade_id character(2)," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table PROCESSOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'processos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  processos (" +
		" id integer DEFAULT NEXT VALUE FOR processos_id_seq NOT NULL," +
		" questao_id integer," +
		" numero varchar(255) NOT NULL," +
		" descricao varchar(4000)," +
		" referencia varchar(255)," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table PRODUTOS_CICLOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_ciclos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  produtos_ciclos (" +
		" id integer DEFAULT NEXT VALUE FOR produtos_ciclos_id_seq NOT NULL," +
		" entidade_id integer," +
		" ciclo_id integer," +
		" tipo_pontuacao_id integer," +
		" nota double precision," +
		" analise varchar(4000)," +
		" motivacao varchar(4000)," +
		" supervisor_id integer," +
		" auditor_id integer," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table PRODUTOS_PILARES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_pilares') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  produtos_pilares (" +
		" id integer DEFAULT NEXT VALUE FOR produtos_pilares_id_seq NOT NULL," +
		" entidade_id integer," +
		" ciclo_id integer," +
		" pilar_id integer," +
		" tipo_pontuacao_id integer," +
		" peso double precision," +
		" nota double precision," +
		" analise varchar(4000)," +
		" motivacao_peso varchar(4000)," +
		" motivacao_nota varchar(4000)," +
		" supervisor_id integer," +
		" auditor_id integer," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table PRODUTOS_COMPONENTES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_componentes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  produtos_componentes (" +
		" id integer DEFAULT NEXT VALUE FOR produtos_componentes_id_seq NOT NULL," +
		" entidade_id integer," +
		" ciclo_id integer," +
		" pilar_id integer," +
		" componente_id integer," +
		" tipo_pontuacao_id integer," +
		" peso double precision," +
		" nota double precision," +
		" analise varchar(4000)," +
		" motivacao_peso varchar(4000)," +
		" motivacao_nota varchar(4000)," +
		" motivacao_reprogramacao character varying(4000)," +
		" justificativa varchar(4000)," +
		" supervisor_id integer," +
		" auditor_id integer," +
		" author_id integer," +
		" inicia_em DATE ," +
		" termina_em DATE ," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table PRODUTOS_ELEMENTOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_elementos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  produtos_elementos (" +
		" id integer DEFAULT NEXT VALUE FOR produtos_elementos_id_seq NOT NULL," +
		" entidade_id integer," +
		" ciclo_id integer," +
		" pilar_id integer," +
		" componente_id integer," +
		" plano_id integer," +
		" tipo_nota_id integer," +
		" elemento_id integer," +
		" tipo_pontuacao_id integer," +
		" peso double precision," +
		" nota double precision," +
		" analise varchar(4000)," +
		" motivacao_peso varchar(4000)," +
		" motivacao_nota varchar(4000)," +
		" justificativa varchar(4000)," +
		" supervisor_id integer," +
		" auditor_id integer," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table PRODUTOS_ITENS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_itens') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  produtos_itens (" +
		" id integer DEFAULT NEXT VALUE FOR produtos_itens_id_seq NOT NULL," +
		" entidade_id integer," +
		" ciclo_id integer," +
		" pilar_id integer," +
		" componente_id integer," +
		" plano_id integer," +
		" tipo_nota_id integer," +
		" elemento_id integer," +
		" item_id integer," +
		" analise varchar(4000)," +
		" anexo varchar(4000)," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table PRODUTOS_PLANOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_planos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  produtos_planos (" +
		" id integer DEFAULT NEXT VALUE FOR produtos_planos_id_seq NOT NULL," +
		" entidade_id integer," +
		" ciclo_id integer," +
		" pilar_id integer," +
		" componente_id integer," +
		" plano_id integer," +
		" tipo_pontuacao_id integer," +
		" peso double precision," +
		" nota double precision," +
		" analise varchar(4000)," +
		" motivacao_peso varchar(4000)," +
		" motivacao_nota varchar(4000)," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table PRODUTOS_TIPOS_NOTAS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_tipos_notas') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  produtos_tipos_notas (" +
		" id integer DEFAULT NEXT VALUE FOR produtos_tipos_notas_id_seq NOT NULL," +
		" entidade_id integer," +
		" ciclo_id integer," +
		" pilar_id integer," +
		" componente_id integer," +
		" plano_id integer," +
		" tipo_nota_id integer," +
		" tipo_pontuacao_id integer," +
		" peso double precision," +
		" nota double precision," +
		" analise varchar(4000)," +
		" anexo varchar(4000)," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table ANOTACOES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'anotacoes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  anotacoes (" +
		" id integer DEFAULT NEXT VALUE FOR anotacoes_id_seq NOT NULL," +
		" entidade_id integer," +
		" ciclo_id integer," +
		" pilar_id integer," +
		" componente_id integer," +
		" plano_id integer," +
		" tipo_nota_id integer," +
		" elemento_id integer," +
		" item_id integer," +
		" assunto varchar(255) NOT NULL," +
		" risco character(1)," +
		" tendencia character(1)," +
		" relator_id integer," +
		" responsavel_id integer," +
		" descricao varchar(4000)," +
		" matriz varchar(255)," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table ANOTACOES_RADARES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'anotacoes_radares') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  anotacoes_radares (" +
		" id integer DEFAULT NEXT VALUE FOR anotacoes_radares_id_seq NOT NULL," +
		" radar_id integer," +
		" anotacao_id integer," +
		" observacoes varchar(4000)," +
		" registro_ata varchar(4000)," +
		" ultima_atualizacao datetime ," +
		" ultimo_atualizador_id integer," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table RADARES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'radares') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  radares  (" +
		" id integer DEFAULT NEXT VALUE FOR radares_id_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" descricao varchar(4000)," +
		" referencia varchar(255)," +
		" data_radar datetime ," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table ROLES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'roles') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  roles  (" +
		" id integer DEFAULT NEXT VALUE FOR roles_id_seq NOT NULL," +
		" name varchar(255) NOT NULL," +
		" description varchar(4000)," +
		" author_id integer," +
		" created_at datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table VERSOES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'versoes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  versoes (" +
		" id integer DEFAULT NEXT VALUE FOR versoes_id_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" objetivo varchar(4000)," +
		" definicao_pronto varchar(4000)," +
		" inicia_em datetime ," +
		" termina_em datetime ," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table STATUS
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'status') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  status  (" +
		" id integer DEFAULT NEXT VALUE FOR status_id_seq NOT NULL," +
		" name varchar(255) NOT NULL," +
		" description varchar(4000)," +
		" author_id integer," +
		" created_at datetime ," +
		" id_versao_origem integer," +
		" status_id integer," +
		" stereotype varchar(255))" +
		" END ")
	if err != nil {
		log.Println(err.Error())
	}

	// Table TIPOS DE NOTAS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'tipos_notas') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  tipos_notas (" +
		" id integer DEFAULT NEXT VALUE FOR tipos_notas_id_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" descricao varchar(255)," +
		" referencia varchar(500)," +
		" letra character(1) NOT NULL," +
		" cor_letra character(6)," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table TIPOS_NOTAS_COMPONENTES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'tipos_notas_componentes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE tipos_notas_componentes (" +
		" id integer DEFAULT NEXT VALUE FOR tipos_notas_componentes_id_seq NOT NULL," +
		" componente_id integer," +
		" tipo_nota_id integer," +
		" peso_padrao double precision," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table USERS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'users') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  users (" +
		" id integer DEFAULT NEXT VALUE FOR users_id_seq NOT NULL," +
		" name varchar(255)," +
		" username varchar(255) NOT NULL," +
		" password varchar(255) NOT NULL," +
		" email varchar(255)," +
		" mobile varchar(255)," +
		" role_id integer," +
		" author_id integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")

	// Table WORKFLOWS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'workflows') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE  workflows  (" +
		" id integer DEFAULT NEXT VALUE FOR workflows_id_seq NOT NULL," +
		" name varchar(255) NOT NULL," +
		" description varchar(4000)," +
		" entity_type varchar(50)," +
		" start_at datetime ," +
		" end_at datetime ," +
		" author_id integer," +
		" created_at datetime ," +
		" id_versao_origem integer," +
		" status_id integer)" +
		" END ")
}
