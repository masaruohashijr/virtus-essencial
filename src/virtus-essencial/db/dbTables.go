package db

import (
	"log"
)

func createTable() {
	// Table ACTIONS
	stmt := "IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE name = 'actions' AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.actions (" +
		" id_action integer DEFAULT NEXT VALUE FOR id_actions_seq NOT NULL, " +
		" name varchar(255) NOT NULL, " +
		" id_origin_status integer, " +
		" id_destination_status integer, " +
		" other_than bit, " +
		" description varchar(MAX)," +
		" id_author integer," +
		" created_at datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END "
	_, err := db.Exec(stmt)
	if err != nil {
		log.Println(err.Error())
	}

	// Table ACTIONS_STATUS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'actions_status') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.actions_status (" +
		" id_action_status integer DEFAULT NEXT VALUE FOR id_actions_status_seq NOT NULL," +
		" id_action integer," +
		" id_origin_status integer," +
		" id_destination_status integer)" +
		" END ")

	// Table ACTIVITIES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'activities') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.activities (" +
		" id_activity integer DEFAULT NEXT VALUE FOR id_activities_seq NOT NULL," +
		" id_workflow integer," +
		" id_action integer," +
		" id_expiration_action integer," +
		" expiration_time_days integer," +
		" start_at datetime ," +
		" end_at datetime )" +
		" END ")

	// Table ACTIVITIES_ROLES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'activities_roles') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.activities_roles (" +
		" id_activity_role integer DEFAULT NEXT VALUE FOR id_activities_roles_seq NOT NULL," +
		" id_activity integer," +
		" id_role integer)" +
		" END ")

	// Table CHAMADOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'chamados') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.chamados (" +
		" id_chamado integer DEFAULT NEXT VALUE FOR id_chamados_seq NOT NULL," +
		" titulo varchar(255) NOT NULL," +
		" descricao varchar(MAX)," +
		" acompanhamento varchar(MAX)," +
		" id_responsavel integer," +
		" id_relator integer," +
		" id_tipo_chamado character(1)," +
		" id_prioridade character(1)," +
		" estimativa integer," +
		" inicia_em datetime ," +
		" pronto_em datetime ," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table CHAMADOS_VERSOES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'activities_roles') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.activities_roles (" +
		" id_chamado_versao integer DEFAULT NEXT VALUE FOR id_chamados_versoes_seq NOT NULL," +
		" id_activity integer," +
		" id_role integer)" +
		" END ")

	// Table CICLOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'ciclos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.ciclos (" +
		" id_ciclo integer DEFAULT NEXT VALUE FOR id_ciclos_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" descricao varchar(MAX)," +
		" referencia varchar(500)," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table CICLOS_ENTIDADES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'ciclos_entidades') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.ciclos_entidades (" +
		" id_ciclo_entidade integer DEFAULT NEXT VALUE FOR id_ciclos_entidades_seq NOT NULL," +
		" id_ciclo integer," +
		" id_entidade integer," +
		" tipo_media integer," +
		" id_supervisor integer," + // TODO FK
		" inicia_em datetime ," +
		" termina_em datetime ," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table COMENTARIOS ANOTACOES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'comentarios_anotacoes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.comentarios_anotacoes (" +
		" id_comentario_anotacao integer DEFAULT NEXT VALUE FOR id_comentarios_anotacoes_seq NOT NULL," +
		" id_anotacao integer," +
		" texto varchar(MAX)," +
		" referencia varchar(255)," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table COMENTARIOS CHAMADOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'comentarios_chamados') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.comentarios_chamados (" +
		" id_comentario_chamado integer DEFAULT NEXT VALUE FOR id_comentarios_chamados_seq NOT NULL," +
		" id_chamado integer," +
		" texto varchar(MAX)," +
		" referencia varchar(255)," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table COMPONENTES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'componentes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.componentes (" +
		" id_componente integer DEFAULT NEXT VALUE FOR id_componentes_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" descricao varchar(MAX)," +
		" referencia varchar(500)," +
		" pga varchar(1)," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table COMPONENTES_PILARES
	stmt = "IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE name = 'componentes_pilares' AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.componentes_pilares (" +
		" id_componente_pilar integer DEFAULT NEXT VALUE FOR id_componentes_pilares_seq NOT NULL," +
		" id_componente integer, " +
		" id_pilar integer," +
		" tipo_media integer," +
		" peso_padrao integer," +
		" sonda varchar (255)," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END "
	_, err = db.Exec(stmt)
	if err != nil {
		log.Println(err.Error())
	}

	// Table ELEMENTOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'elementos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.elementos (" +
		" id_elemento integer DEFAULT NEXT VALUE FOR id_elementos_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" descricao varchar(MAX)," +
		" referencia varchar(500)," +
		" peso integer DEFAULT 1 NOT NULL," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_status integer)" +
		" END ")

	// Table ELEMENTOS_COMPONENTES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'elementos_componentes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.elementos_componentes (" +
		" id_elemento_componente integer DEFAULT NEXT VALUE FOR id_elementos_componentes_seq NOT NULL," +
		" id_componente integer," +
		" id_elemento integer," +
		" id_tipo_nota integer," +
		" peso_padrao integer," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table ENTIDADES
	stmt = "IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE name = 'entidades' AND type in (N'U')) " +
		" BEGIN " +
		" CREATE TABLE virtus.entidades (" +
		" id_entidade integer DEFAULT NEXT VALUE FOR id_entidades_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" descricao varchar(MAX)," +
		" sigla varchar(25)," +
		" codigo varchar(MAX)," +
		" situacao varchar(30)," +
		" ESI BIT," +
		" municipio varchar(255)," +
		" sigla_uf character(2)," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
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
		" CREATE TABLE virtus.escritorios (" +
		" id_escritorio integer DEFAULT NEXT VALUE FOR id_escritorios_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" abreviatura character (4)," +
		" descricao varchar(MAX)," +
		" id_chefe integer," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table FEATURES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'features') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.features  (" +
		" id_feature integer DEFAULT NEXT VALUE FOR id_features_seq NOT NULL," +
		" name varchar(255) NOT NULL," +
		" code varchar(255) NOT NULL," +
		" description varchar(MAX)," +
		" id_author integer," +
		" created_at datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table FEATURES_ROLES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'features_roles') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.features_roles (" +
		" id_feature_role int DEFAULT NEXT VALUE FOR id_features_roles_seq," +
		" id_feature int NOT NULL," +
		" id_role int NOT NULL)" +
		" END ")

	// Table FEATURES_ACTIVITIES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'features_activities') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.features_activities (" +
		" id_feature_activity integer DEFAULT NEXT VALUE FOR id_features_activities_seq NOT NULL," +
		" id_feature integer," +
		" id_activity integer)" +
		" END ")

	// Table INTEGRANTES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'integrantes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.integrantes (" +
		" id_integrante integer DEFAULT NEXT VALUE FOR id_integrantes_seq NOT NULL," +
		" id_entidade integer," +
		" id_ciclo integer," +
		" id_usuario integer," +
		" inicia_em datetime ," +
		" termina_em datetime ," +
		" motivacao varchar(MAX)," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table ITENS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'itens') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.itens (" +
		" id_item integer DEFAULT NEXT VALUE FOR id_itens_seq NOT NULL," +
		" id_elemento integer," +
		" nome varchar(255) NOT NULL," +
		" descricao varchar(MAX)," +
		" referencia varchar(500)," +
		" criado_em datetime ," +
		" id_author integer," +
		" id_status integer)" +
		" END ")

	// Table JURISDICOES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'jurisdicoes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.jurisdicoes (" +
		" id_jurisdicao integer DEFAULT NEXT VALUE FOR id_jurisdicoes_seq NOT NULL," +
		" id_escritorio integer," +
		" id_entidade integer," +
		" inicia_em datetime ," +
		" termina_em datetime ," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table MEMBROS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'membros') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.membros (" +
		" id_membro integer DEFAULT NEXT VALUE FOR id_membros_seq NOT NULL," +
		" id_escritorio integer," +
		" id_usuario integer," +
		" inicia_em datetime ," +
		" termina_em datetime ," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table PILARES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'pilares') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.pilares (" +
		" id_pilar integer DEFAULT NEXT VALUE FOR id_pilares_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" descricao varchar(MAX)," +
		" referencia varchar(500)," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table PILARES_CICLOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'pilares_ciclos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.pilares_ciclos (" +
		" id_pilar_ciclo integer DEFAULT NEXT VALUE FOR id_pilares_ciclos_seq NOT NULL," +
		" id_pilar integer," +
		" id_ciclo integer," +
		" tipo_media integer," +
		" peso_padrao double precision," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table PLANOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'planos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.planos (" +
		" id_plano integer DEFAULT NEXT VALUE FOR id_planos_seq NOT NULL," +
		" id_entidade integer," +
		" nome varchar(255)," +
		" descricao varchar(MAX)," +
		" referencia varchar(500)," +
		" cnpb varchar(255)," +
		" legislacao varchar(255)," +
		" situacao varchar(255)," +
		" recurso_garantidor double precision," +
		" id_modalidade character(2)," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table PROCESSOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'processos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.processos (" +
		" id_processo integer DEFAULT NEXT VALUE FOR id_processos_seq NOT NULL," +
		" id_questao integer," +
		" numero varchar(255) NOT NULL," +
		" descricao varchar(MAX)," +
		" referencia varchar(255)," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table PRODUTOS_CICLOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_ciclos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.produtos_ciclos (" +
		" id_produto_ciclo integer DEFAULT NEXT VALUE FOR id_produtos_ciclos_seq NOT NULL," +
		" id_entidade integer," +
		" id_ciclo integer," +
		" id_tipo_pontuacao integer," +
		" nota double precision," +
		" analise varchar(MAX)," +
		" motivacao varchar(MAX)," +
		" id_supervisor integer," +
		" id_auditor integer," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table PRODUTOS_PILARES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_pilares') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.produtos_pilares (" +
		" id_produto_pilar integer DEFAULT NEXT VALUE FOR id_produtos_pilares_seq NOT NULL," +
		" id_entidade integer," +
		" id_ciclo integer," +
		" id_pilar integer," +
		" id_tipo_pontuacao integer," +
		" peso double precision," +
		" nota double precision," +
		" analise varchar(MAX)," +
		" motivacao_peso varchar(MAX)," +
		" motivacao_nota varchar(MAX)," +
		" id_supervisor integer," +
		" id_auditor integer," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table PRODUTOS_COMPONENTES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_componentes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.produtos_componentes (" +
		" id_produto_componente integer DEFAULT NEXT VALUE FOR id_produtos_componentes_seq NOT NULL," +
		" id_entidade integer," +
		" id_ciclo integer," +
		" id_pilar integer," +
		" id_componente integer," +
		" id_tipo_pontuacao integer," +
		" peso double precision," +
		" nota double precision," +
		" analise varchar(MAX)," +
		" motivacao_peso varchar(MAX)," +
		" motivacao_nota varchar(MAX)," +
		" motivacao_reprogramacao character varying(MAX)," +
		" justificativa varchar(MAX)," +
		" id_supervisor integer," +
		" id_auditor integer," +
		" id_author integer," +
		" inicia_em DATE ," +
		" termina_em DATE ," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table PRODUTOS_ELEMENTOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_elementos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.produtos_elementos (" +
		" id_produto_elemento integer DEFAULT NEXT VALUE FOR id_produtos_elementos_seq NOT NULL," +
		" id_entidade integer," +
		" id_ciclo integer," +
		" id_pilar integer," +
		" id_componente integer," +
		" id_plano integer," +
		" id_tipo_nota integer," +
		" id_elemento integer," +
		" id_tipo_pontuacao integer," +
		" peso double precision," +
		" nota double precision," +
		" analise varchar(MAX)," +
		" motivacao_peso varchar(MAX)," +
		" motivacao_nota varchar(MAX)," +
		" justificativa varchar(MAX)," +
		" id_supervisor integer," +
		" id_auditor integer," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table PRODUTOS_ITENS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_itens') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.produtos_itens (" +
		" id_produto_item integer DEFAULT NEXT VALUE FOR id_produtos_itens_seq NOT NULL," +
		" id_entidade integer," +
		" id_ciclo integer," +
		" id_pilar integer," +
		" id_componente integer," +
		" id_plano integer," +
		" id_tipo_nota integer," +
		" id_elemento integer," +
		" id_item integer," +
		" analise varchar(MAX)," +
		" anexo varchar(MAX)," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table PRODUTOS_PLANOS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_planos') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.produtos_planos (" +
		" id_produto_plano integer DEFAULT NEXT VALUE FOR id_produtos_planos_seq NOT NULL," +
		" id_entidade integer," +
		" id_ciclo integer," +
		" id_pilar integer," +
		" id_componente integer," +
		" id_plano integer," +
		" id_tipo_pontuacao integer," +
		" peso double precision," +
		" nota double precision," +
		" analise varchar(MAX)," +
		" motivacao_peso varchar(MAX)," +
		" motivacao_nota varchar(MAX)," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table PRODUTOS_TIPOS_NOTAS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'produtos_tipos_notas') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.produtos_tipos_notas (" +
		" id_produto_tipo_nota integer DEFAULT NEXT VALUE FOR id_produtos_tipos_notas_seq NOT NULL," +
		" id_entidade integer," +
		" id_ciclo integer," +
		" id_pilar integer," +
		" id_componente integer," +
		" id_plano integer," +
		" id_tipo_nota integer," +
		" id_tipo_pontuacao integer," +
		" peso double precision," +
		" nota double precision," +
		" analise varchar(MAX)," +
		" anexo varchar(MAX)," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table ANOTACOES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'anotacoes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.anotacoes (" +
		" id_anotacao integer DEFAULT NEXT VALUE FOR id_anotacoes_seq NOT NULL," +
		" id_entidade integer," +
		" id_ciclo integer," +
		" id_pilar integer," +
		" id_componente integer," +
		" id_plano integer," +
		" id_tipo_nota integer," +
		" id_elemento integer," +
		" id_item integer," +
		" assunto varchar(255) NOT NULL," +
		" risco character(1)," +
		" tendencia character(1)," +
		" id_relator integer," +
		" id_responsavel integer," +
		" descricao varchar(MAX)," +
		" matriz varchar(255)," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table ANOTACOES_RADARES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'anotacoes_radares') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.anotacoes_radares (" +
		" id_anotacao_radar integer DEFAULT NEXT VALUE FOR id_anotacoes_radares_seq NOT NULL," +
		" id_radar integer," +
		" id_anotacao integer," +
		" observacoes varchar(MAX)," +
		" registro_ata varchar(MAX)," +
		" ultima_atualizacao datetime ," +
		" id_ultimo_atualizador integer," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table RADARES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'radares') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.radares  (" +
		" id_radar integer DEFAULT NEXT VALUE FOR id_radares_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" descricao varchar(MAX)," +
		" referencia varchar(255)," +
		" data_radar datetime ," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table ROLES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'roles') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.roles  (" +
		" id_role integer DEFAULT NEXT VALUE FOR id_roles_seq NOT NULL," +
		" name varchar(255) NOT NULL," +
		" description varchar(MAX)," +
		" id_author integer," +
		" created_at datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table VERSOES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'versoes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.versoes (" +
		" id_versao integer DEFAULT NEXT VALUE FOR id_versoes_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" objetivo varchar(MAX)," +
		" definicao_pronto varchar(MAX)," +
		" inicia_em datetime ," +
		" termina_em datetime ," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table STATUS
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE name = 'status' AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.status  (" +
		" id_status integer DEFAULT NEXT VALUE FOR id_status_seq NOT NULL," +
		" name varchar(255) NOT NULL," +
		" description varchar(MAX)," +
		" id_author integer," +
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
		" CREATE TABLE virtus.tipos_notas (" +
		" id_tipo_nota integer DEFAULT NEXT VALUE FOR id_tipos_notas_seq NOT NULL," +
		" nome varchar(255) NOT NULL," +
		" descricao varchar(MAX)," +
		" referencia varchar(500)," +
		" letra character(1) NOT NULL," +
		" cor_letra character(6)," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table TIPOS_NOTAS_COMPONENTES
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'tipos_notas_componentes') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.tipos_notas_componentes (" +
		" id_tipo_nota_componente integer DEFAULT NEXT VALUE FOR id_tipos_notas_componentes_seq NOT NULL," +
		" id_componente integer," +
		" id_tipo_nota integer," +
		" peso_padrao double precision," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table USERS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'users') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.users (" +
		" id_user integer DEFAULT NEXT VALUE FOR id_users_seq NOT NULL," +
		" name varchar(255)," +
		" username varchar(255) NOT NULL," +
		" password varchar(255) NOT NULL," +
		" email varchar(255)," +
		" mobile varchar(255)," +
		" id_role integer," +
		" id_author integer," +
		" criado_em datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")

	// Table WORKFLOWS
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects " +
		" WHERE object_id = OBJECT_ID(N'workflows') AND type in (N'U'))" +
		" BEGIN" +
		" CREATE TABLE virtus.workflows  (" +
		" id_workflow integer DEFAULT NEXT VALUE FOR id_workflows_seq NOT NULL," +
		" name varchar(255) NOT NULL," +
		" description varchar(MAX)," +
		" entity_type varchar(50)," +
		" start_at datetime ," +
		" end_at datetime ," +
		" id_author integer," +
		" created_at datetime ," +
		" id_versao_origem integer," +
		" id_status integer)" +
		" END ")
}
