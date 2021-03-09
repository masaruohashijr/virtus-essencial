package db

import (
	"log"
)

func createUniqueKey() {
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'action_status_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.actions_status" +
		" ADD CONSTRAINT action_status_unique_key UNIQUE (id_action, id_origin_status, id_destination_status) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'feature_role_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.features_roles" +
		" ADD CONSTRAINT feature_role_unique_key UNIQUE (id_role, id_feature) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'username_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.users" +
		" ADD CONSTRAINT username_unique_key UNIQUE (username) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'action_role_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.activities_roles" +
		" ADD CONSTRAINT action_role_unique_key UNIQUE (id_activity, id_role) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'features_activities_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.features_activities" +
		" ADD CONSTRAINT features_activities_unique_key UNIQUE (id_activity, id_feature) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'jurisdicoes_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.jurisdicoes" +
		" ADD CONSTRAINT jurisdicoes_unique_key UNIQUE (id_escritorio, id_entidade) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'membros_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.membros" +
		" ADD CONSTRAINT membros_unique_key UNIQUE (id_escritorio, id_usuario) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'ciclos_entidades_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.ciclos_entidades" +
		" ADD CONSTRAINT ciclos_entidades_unique_key UNIQUE (id_entidade, id_ciclo) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'pilares_ciclos_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.pilares_ciclos" +
		" ADD CONSTRAINT pilares_ciclos_unique_key UNIQUE (id_ciclo, id_pilar) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'componentes_pilares_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.componentes_pilares" +
		" ADD CONSTRAINT componentes_pilares_unique_key UNIQUE (id_pilar, id_componente) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'elementos_componentes_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.elementos_componentes" +
		" ADD CONSTRAINT elementos_componentes_unique_key UNIQUE (id_componente, id_elemento, id_tipo_nota) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'tipos_notas_componentes_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.tipos_notas_componentes" +
		" ADD CONSTRAINT tipos_notas_componentes_unique_key UNIQUE (id_componente, id_tipo_nota) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'produtos_ciclos_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.produtos_ciclos" +
		" ADD CONSTRAINT produtos_ciclos_unique_key UNIQUE (id_entidade, id_ciclo) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'produtos_pilares_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.produtos_pilares" +
		" ADD CONSTRAINT produtos_pilares_unique_key UNIQUE (id_entidade, id_ciclo, id_pilar) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'produtos_componentes_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.produtos_componentes" +
		" ADD CONSTRAINT produtos_componentes_unique_key UNIQUE (id_entidade, id_ciclo, id_pilar, id_componente) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'produtos_planos_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.produtos_planos" +
		" ADD CONSTRAINT produtos_planos_unique_key UNIQUE (id_entidade, id_ciclo, id_pilar, id_componente, id_plano) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'produtos_tipos_notas_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.produtos_tipos_notas" +
		" ADD CONSTRAINT produtos_tipos_notas_unique_key UNIQUE (id_entidade, id_ciclo, id_pilar, id_componente, id_plano, id_tipo_nota) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'produtos_elementos_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.produtos_elementos" +
		" ADD CONSTRAINT produtos_elementos_unique_key UNIQUE (id_entidade, id_ciclo, id_pilar, id_componente, id_plano, id_tipo_nota, id_elemento) END")
	db.Exec(" IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'produtos_itens_unique_key' AND type in (N'UQ')) BEGIN " +
		" ALTER TABLE virtus.produtos_itens" +
		" ADD CONSTRAINT produtos_itens_unique_key UNIQUE (id_entidade, id_ciclo, id_pilar, id_componente, id_plano, id_tipo_nota, id_elemento, id_item) END")
}

func createFKey() {
	// ACTIONS
	log.Println("FOREIGN KEYS")
	_, err := db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'destination_status_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.actions " +
		" ADD CONSTRAINT destination_status_fkey FOREIGN KEY (id_destination_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END ")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'origin_status_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.actions " +
		" ADD CONSTRAINT origin_status_fkey FOREIGN KEY (id_origin_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	//  ACTIONS_STATUS
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'actions_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.actions_status " +
		" ADD CONSTRAINT actions_fkey FOREIGN KEY (id_action)" +
		" REFERENCES virtus.actions (id_action) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'origin_actions_status_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.actions_status " +
		" ADD CONSTRAINT origin_actions_status_fkey FOREIGN KEY (id_origin_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'destination_actions_status_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.actions_status " +
		" ADD CONSTRAINT destination_actions_status_fkey FOREIGN KEY (id_destination_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	// ACTIVITIES
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'action_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.activities ADD CONSTRAINT action_fkey FOREIGN KEY (id_action)" +
		" REFERENCES virtus.actions (id_action) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	/*	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
			" 'expiration_action_fkey' AND type in (N'F')) BEGIN " +
			" ALTER TABLE virtus.activities ADD CONSTRAINT expiration_action_fkey FOREIGN KEY (id_expiration_action)" +
			" REFERENCES virtus.actions (id_action) ON DELETE NO ACTION ON UPDATE NO ACTION END")
		if err != nil {
			log.Println(err.Error())
		}*/

	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'workflow_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.activities ADD CONSTRAINT workflow_fkey FOREIGN KEY (id_workflow)" +
		" REFERENCES virtus.workflows (id_workflow) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	// ACTIVITIES_ROLES
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'activities_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.activities_roles " +
		" ADD CONSTRAINT activities_fkey FOREIGN KEY (id_activity)" +
		" REFERENCES virtus.activities (id_activity) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'roles_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.activities_roles " +
		" ADD CONSTRAINT roles_fkey FOREIGN KEY (id_role)" +
		" REFERENCES virtus.roles (id_role) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	// CICLOS
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_ciclos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.ciclos" +
		" ADD CONSTRAINT authors_ciclos_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_ciclos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.ciclos" +
		" ADD CONSTRAINT status_ciclos_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	// CICLOS ENTIDADES
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'entidades_ciclos_entidades_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.ciclos_entidades" +
		" ADD CONSTRAINT entidades_ciclos_entidades_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES virtus.entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'ciclos_ciclos_entidades_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.ciclos_entidades" +
		" ADD CONSTRAINT ciclos_ciclos_entidades_fkey FOREIGN KEY (id_ciclo)" +
		" REFERENCES virtus.ciclos (id_ciclo) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_ciclos_entidades_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.ciclos_entidades" +
		" ADD CONSTRAINT authors_ciclos_entidades_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_ciclos_entidades_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.ciclos_entidades" +
		" ADD CONSTRAINT status_ciclos_entidades_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	// COMPONENTES
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_componentes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.componentes" +
		" ADD CONSTRAINT authors_componentes_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_componentes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.componentes" +
		" ADD CONSTRAINT status_componentes_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")

	// COMPONENTES PILARES
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'componentes_componentes_pilares_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.componentes_pilares" +
		" ADD CONSTRAINT componentes_componentes_pilares_fkey FOREIGN KEY (id_componente)" +
		" REFERENCES virtus.componentes (id_componente) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'pilares_componentes_pilares_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.componentes_pilares" +
		" ADD CONSTRAINT pilares_componentes_pilares_fkey FOREIGN KEY (id_pilar)" +
		" REFERENCES virtus.pilares (id_pilar) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_componentes_pilares_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.componentes_pilares" +
		" ADD CONSTRAINT authors_componentes_pilares_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")

	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_componentes_pilares_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.componentes_pilares" +
		" ADD CONSTRAINT status_componentes_pilares_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	// ELEMENTOS
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'users_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.elementos" +
		" ADD CONSTRAINT users_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_elementos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.elementos" +
		" ADD CONSTRAINT status_elementos_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")

	// ELEMENTOS_COMPONENTES
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'tipos_notas_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.elementos_componentes" +
		" ADD CONSTRAINT tipos_notas_fkey FOREIGN KEY (id_tipo_nota)" +
		" REFERENCES virtus.tipos_notas (id_tipo_nota) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'elementos_elementos_componentes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.elementos_componentes" +
		" ADD CONSTRAINT elementos_elementos_componentes_fkey FOREIGN KEY (id_elemento)" +
		" REFERENCES virtus.elementos (id_elemento) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}

	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'componentes_elementos_componentes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.elementos_componentes" +
		" ADD CONSTRAINT componentes_elementos_componentes_fkey FOREIGN KEY (id_componente)" +
		" REFERENCES virtus.componentes (id_componente) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_elementos_componentes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.elementos_componentes" +
		" ADD CONSTRAINT authors_elementos_componentes_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_elementos_componentes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.elementos_componentes" +
		" ADD CONSTRAINT status_elementos_componentes_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// ENTIDADES
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_entidades_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.entidades" +
		" ADD CONSTRAINT authors_entidades_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_entidades_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.entidades" +
		" ADD CONSTRAINT status_entidades_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// ESCRITÓRIOS
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_escritorios_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.escritorios" +
		" ADD CONSTRAINT authors_escritorios_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_escritorios_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.escritorios" +
		" ADD CONSTRAINT status_escritorios_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// FEATURES_ACTIVITIES
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'activities_features_activities_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.features_activities " +
		" ADD CONSTRAINT activities_features_activities_fkey FOREIGN KEY (id_activity)" +
		" REFERENCES virtus.activities (id_activity) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'features_features_activities_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.features_activities " +
		" ADD CONSTRAINT features_features_activities_fkey FOREIGN KEY (id_feature)" +
		" REFERENCES virtus.features (id_feature) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// FEATURES_ROLES
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'features_features_roles_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.features_roles " +
		" ADD CONSTRAINT features_features_roles_fkey FOREIGN KEY (id_feature)" +
		" REFERENCES virtus.features (id_feature) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'roles_features_roles_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.features_roles " +
		" ADD CONSTRAINT roles_features_roles_fkey FOREIGN KEY (id_role)" +
		" REFERENCES virtus.roles (id_role) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// ITENS
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'elementos_itens_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.itens" +
		" ADD CONSTRAINT elementos_itens_fkey FOREIGN KEY (id_elemento)" +
		" REFERENCES virtus.elementos (id_elemento) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// PILARES
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_pilares_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.pilares" +
		" ADD CONSTRAINT authors_pilares_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_pilares_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.pilares" +
		" ADD CONSTRAINT status_pilares_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// PILARES_CICLOS
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'pilares_pilares_ciclos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.pilares_ciclos" +
		" ADD CONSTRAINT pilares_pilares_ciclos_fkey FOREIGN KEY (id_pilar)" +
		" REFERENCES virtus.pilares (id_pilar) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'ciclos_pilares_ciclos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.pilares_ciclos" +
		" ADD CONSTRAINT ciclos_pilares_ciclos_fkey FOREIGN KEY (id_ciclo)" +
		" REFERENCES virtus.ciclos (id_ciclo) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_pilares_ciclos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.pilares_ciclos" +
		" ADD CONSTRAINT authors_pilares_ciclos_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_pilares_ciclos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.pilares_ciclos" +
		" ADD CONSTRAINT status_pilares_ciclos_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// PLANOS
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'entidades_planos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.planos" +
		" ADD CONSTRAINT entidades_planos_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES virtus.entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_planos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.planos" +
		" ADD CONSTRAINT authors_planos_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_planos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.planos" +
		" ADD CONSTRAINT status_planos_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_CICLOS
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'entidades_produtos_ciclos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_ciclos" +
		" ADD CONSTRAINT entidades_produtos_ciclos_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES virtus.entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'ciclos_produtos_ciclos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_ciclos" +
		" ADD CONSTRAINT ciclos_produtos_ciclos_fkey FOREIGN KEY (id_ciclo)" +
		" REFERENCES virtus.ciclos (id_ciclo) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_produtos_ciclos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_ciclos" +
		" ADD CONSTRAINT authors_produtos_ciclos_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_produtos_ciclos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_ciclos" +
		" ADD CONSTRAINT status_produtos_ciclos_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_PILARES
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'entidades_produtos_pilares_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_pilares" +
		" ADD CONSTRAINT entidades_produtos_pilares_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES virtus.entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'ciclos_produtos_pilares_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_pilares" +
		" ADD CONSTRAINT ciclos_produtos_pilares_fkey FOREIGN KEY (id_ciclo)" +
		" REFERENCES virtus.ciclos (id_ciclo) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'pilares_produtos_pilares_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_pilares" +
		" ADD CONSTRAINT pilares_produtos_pilares_fkey FOREIGN KEY (id_pilar)" +
		" REFERENCES virtus.pilares (id_pilar) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_produtos_pilares_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_pilares" +
		" ADD CONSTRAINT authors_produtos_pilares_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_produtos_pilares_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_pilares" +
		" ADD CONSTRAINT status_produtos_pilares_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_COMPONENTES
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'entidades_produtos_componentes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_componentes" +
		" ADD CONSTRAINT entidades_produtos_componentes_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES virtus.entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'ciclos_produtos_componentes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_componentes" +
		" ADD CONSTRAINT ciclos_produtos_componentes_fkey FOREIGN KEY (id_ciclo)" +
		" REFERENCES virtus.ciclos (id_ciclo) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'pilares_produtos_componentes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_componentes" +
		" ADD CONSTRAINT pilares_produtos_componentes_fkey FOREIGN KEY (id_pilar)" +
		" REFERENCES virtus.pilares (id_pilar) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'componentes_produtos_componentes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_componentes" +
		" ADD CONSTRAINT componentes_produtos_componentes_fkey FOREIGN KEY (id_componente)" +
		" REFERENCES virtus.componentes (id_componente) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_produtos_componentes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_componentes" +
		" ADD CONSTRAINT authors_produtos_componentes_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_produtos_componentes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_componentes" +
		" ADD CONSTRAINT status_produtos_componentes_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_TIPOS_NOTAS
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'entidades_produtos_tipos_notas_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_tipos_notas" +
		" ADD CONSTRAINT entidades_produtos_tipos_notas_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES virtus.entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'ciclos_produtos_tipos_notas_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_tipos_notas" +
		" ADD CONSTRAINT ciclos_produtos_tipos_notas_fkey FOREIGN KEY (id_ciclo)" +
		" REFERENCES virtus.ciclos (id_ciclo) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'pilares_produtos_tipos_notas_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_tipos_notas" +
		" ADD CONSTRAINT pilares_produtos_tipos_notas_fkey FOREIGN KEY (id_pilar)" +
		" REFERENCES virtus.pilares (id_pilar) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'componentes_produtos_tipos_notas_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_tipos_notas" +
		" ADD CONSTRAINT componentes_produtos_tipos_notas_fkey FOREIGN KEY (id_componente)" +
		" REFERENCES virtus.componentes (id_componente) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'tipos_notas_produtos_tipos_notas_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_tipos_notas" +
		" ADD CONSTRAINT tipos_notas_produtos_tipos_notas_fkey FOREIGN KEY (id_tipo_nota)" +
		" REFERENCES virtus.tipos_notas (id_tipo_nota) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_produtos_tipos_notas_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_tipos_notas" +
		" ADD CONSTRAINT authors_produtos_tipos_notas_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_produtos_tipos_notas_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_tipos_notas" +
		" ADD CONSTRAINT status_produtos_tipos_notas_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_ELEMENTOS
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'entidades_produtos_elementos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_elementos" +
		" ADD CONSTRAINT entidades_produtos_elementos_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES virtus.entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'ciclos_produtos_elementos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_elementos" +
		" ADD CONSTRAINT ciclos_produtos_elementos_fkey FOREIGN KEY (id_ciclo)" +
		" REFERENCES virtus.ciclos (id_ciclo) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'pilares_produtos_elementos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_elementos" +
		" ADD CONSTRAINT pilares_produtos_elementos_fkey FOREIGN KEY (id_pilar)" +
		" REFERENCES virtus.pilares (id_pilar) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'componentes_produtos_elementos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_elementos" +
		" ADD CONSTRAINT componentes_produtos_elementos_fkey FOREIGN KEY (id_componente)" +
		" REFERENCES virtus.componentes (id_componente) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'tipos_notas_produtos_elementos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_elementos" +
		" ADD CONSTRAINT tipos_notas_produtos_elementos_fkey FOREIGN KEY (id_tipo_nota)" +
		" REFERENCES virtus.tipos_notas (id_tipo_nota) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'elementos_produtos_elementos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_elementos" +
		" ADD CONSTRAINT elementos_produtos_elementos_fkey FOREIGN KEY (id_elemento)" +
		" REFERENCES virtus.elementos (id_elemento) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_produtos_elementos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_elementos" +
		" ADD CONSTRAINT authors_produtos_elementos_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_produtos_elementos_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_elementos" +
		" ADD CONSTRAINT status_produtos_elementos_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// PRODUTOS_ITENS
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'entidades_produtos_itens_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_itens" +
		" ADD CONSTRAINT entidades_produtos_itens_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES virtus.entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'ciclos_produtos_itens_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_itens" +
		" ADD CONSTRAINT ciclos_produtos_itens_fkey FOREIGN KEY (id_ciclo)" +
		" REFERENCES virtus.ciclos (id_ciclo) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'pilares_produtos_itens_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_itens" +
		" ADD CONSTRAINT pilares_produtos_itens_fkey FOREIGN KEY (id_pilar)" +
		" REFERENCES virtus.pilares (id_pilar) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'componentes_produtos_itens_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_itens" +
		" ADD CONSTRAINT componentes_produtos_itens_fkey FOREIGN KEY (id_componente)" +
		" REFERENCES virtus.componentes (id_componente) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'elementos_produtos_itens_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_itens" +
		" ADD CONSTRAINT elementos_produtos_itens_fkey FOREIGN KEY (id_elemento)" +
		" REFERENCES virtus.elementos (id_elemento) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'itens_produtos_itens_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_itens" +
		" ADD CONSTRAINT itens_produtos_itens_fkey FOREIGN KEY (id_item)" +
		" REFERENCES virtus.itens (id_item) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_produtos_itens_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_itens" +
		" ADD CONSTRAINT authors_produtos_itens_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_produtos_itens_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.produtos_itens" +
		" ADD CONSTRAINT status_produtos_itens_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// TIPOS_NOTAS
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_tipos_notas_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.tipos_notas" +
		" ADD CONSTRAINT authors_tipos_notas_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// TIPOS_NOTAS_COMPONENTES
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'tipos_notas_tipos_notas_componentes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.tipos_notas_componentes" +
		" ADD CONSTRAINT tipos_notas_tipos_notas_componentes_fkey FOREIGN KEY (id_tipo_nota)" +
		" REFERENCES virtus.tipos_notas (id_tipo_nota) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'componentes_tipos_notas_componentes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.tipos_notas_componentes" +
		" ADD CONSTRAINT componentes_tipos_notas_componentes_fkey FOREIGN KEY (id_componente)" +
		" REFERENCES virtus.componentes (id_componente) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_tipos_notas_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.tipos_notas" +
		" ADD CONSTRAINT status_tipos_notas_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// USERS
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'roles_users_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.users " +
		" ADD CONSTRAINT roles_users_fkey FOREIGN KEY (id_role)" +
		" REFERENCES virtus.roles (id_role) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_users_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.users" +
		" ADD CONSTRAINT authors_users_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_users_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.users" +
		" ADD CONSTRAINT status_users_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// JURISDICOES
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'entidades_jurisdicoes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.jurisdicoes" +
		" ADD CONSTRAINT entidades_jurisdicoes_fkey FOREIGN KEY (id_entidade)" +
		" REFERENCES virtus.entidades (id_entidade) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'escritorios_jurisdicoes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.jurisdicoes" +
		" ADD CONSTRAINT escritorios_jurisdicoes_fkey FOREIGN KEY (id_escritorio)" +
		" REFERENCES virtus.escritorios (id_escritorio) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_jurisdicoes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.jurisdicoes" +
		" ADD CONSTRAINT authors_jurisdicoes_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_jurisdicoes_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.jurisdicoes" +
		" ADD CONSTRAINT status_jurisdicoes_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	// MEMBROS
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'usuarios_membros_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.membros" +
		" ADD CONSTRAINT usuarios_membros_fkey FOREIGN KEY (id_usuario)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'escritorios_membros_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.membros" +
		" ADD CONSTRAINT escritorios_membros_fkey FOREIGN KEY (id_escritorio)" +
		" REFERENCES virtus.escritorios (id_escritorio) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'authors_membros_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.membros" +
		" ADD CONSTRAINT authors_membros_fkey FOREIGN KEY (id_author)" +
		" REFERENCES virtus.users (id_user) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
	_, err = db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'F')) BEGIN " +
		" ALTER TABLE virtus.membros" +
		" ADD CONSTRAINT status_membros_fkey FOREIGN KEY (id_status)" +
		" REFERENCES virtus.status (id_status) ON DELETE NO ACTION ON UPDATE NO ACTION END")
	if err != nil {
		log.Println(err.Error())
	}
}

func createPKey() {
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.actions ADD CONSTRAINT actions_pkey PRIMARY KEY (id_action) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.actions_status ADD CONSTRAINT actions_status_pkey PRIMARY KEY (id_action_status) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.activities ADD CONSTRAINT activities_pkey PRIMARY KEY (id_activity) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.activities_roles ADD CONSTRAINT activities_roles_pkey PRIMARY KEY (id_activity_role) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.anotacoes ADD CONSTRAINT anotacoes_pkey PRIMARY KEY (id_anotacao) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.anotacoes_radares ADD CONSTRAINT anotacoes_radares_pkey PRIMARY KEY (id_anotacao_radar) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.chamados ADD CONSTRAINT chamados_pkey PRIMARY KEY (id_chamado) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.chamados_versoes ADD CONSTRAINT chamados_versoes_pkey PRIMARY KEY (id_chamado_versao) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.ciclos ADD CONSTRAINT ciclos_pkey PRIMARY KEY (id_ciclo) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.ciclos_entidades ADD CONSTRAINT ciclos_pkey PRIMARY KEY (id_ciclo_entidade) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.comentarios_chamados ADD CONSTRAINT comentarios_chamados_pkey PRIMARY KEY (id_comentario_chamado) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.componentes ADD CONSTRAINT componentes_pkey PRIMARY KEY (id_componente) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.componentes_pilares ADD CONSTRAINT componentes_pilares_pkey PRIMARY KEY (id_componente_pilar) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.elementos ADD CONSTRAINT elementos_pkey PRIMARY KEY (id_elemento) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.elementos_componentes ADD CONSTRAINT elementos_componentes_pkey PRIMARY KEY (id_elemento_componente) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.entidades ADD CONSTRAINT entidades_pkey PRIMARY KEY (id_entidade) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.entidades_ciclos ADD CONSTRAINT entidades_ciclos_pkey PRIMARY KEY (id_entidade_ciclo) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.escritorios ADD CONSTRAINT escritorios_pkey PRIMARY KEY (id_escritorio) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.features ADD CONSTRAINT features_pkey PRIMARY KEY (id_feature) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.features_activities ADD CONSTRAINT features_activities_pkey PRIMARY KEY (id_feature_activity) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.features_roles ADD CONSTRAINT features_roles_pkey PRIMARY KEY (id_feature_role) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.integrantes ADD CONSTRAINT integrantes_pkey PRIMARY KEY (id_integrante) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.itens ADD CONSTRAINT itens_pkey PRIMARY KEY (id_item) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.jurisdicoes ADD CONSTRAINT jurisdicoes_pkey PRIMARY KEY (id_jurisdicao) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.membros ADD CONSTRAINT membros_pkey PRIMARY KEY (id_membro) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.pilares ADD CONSTRAINT pilares_pkey PRIMARY KEY (id_pilar) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.pilares_ciclos ADD CONSTRAINT pilares_ciclos_pkey PRIMARY KEY (id_pilar_ciclo) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.planos ADD CONSTRAINT planos_pkey PRIMARY KEY (id_plano) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.processos ADD CONSTRAINT processos_pkey PRIMARY KEY (id_processo) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.produtos_ciclos ADD CONSTRAINT produtos_ciclos_pkey PRIMARY KEY (id_produto_ciclo) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.produtos_pilares ADD CONSTRAINT produtos_pilares_pkey PRIMARY KEY (id_produto_pilar) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.produtos_componentes ADD CONSTRAINT produtos_componentes_pkey PRIMARY KEY (id_produto_componente) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.produtos_tipos_notas ADD CONSTRAINT produtos_tipos_notas_pkey PRIMARY KEY (id_produto_tipo_nota) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.produtos_planos ADD CONSTRAINT produtos_planos_pkey PRIMARY KEY (id_plano) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.produtos_elementos ADD CONSTRAINT produtos_elementos_pkey PRIMARY KEY (id_elemento) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.produtos_itens ADD CONSTRAINT produtos_itens_pkey PRIMARY KEY (id_item) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.produtos_ciclos_historicos ADD CONSTRAINT produtos_ciclos_historicos_pkey PRIMARY KEY (id_produto_ciclo_historico) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.produtos_pilares_historicos ADD CONSTRAINT produtos_pilares_historicos_pkey PRIMARY KEY (id_produto_pilar_historico) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.produtos_componentes_historicos ADD CONSTRAINT produtos_componentes_historicos_pkey PRIMARY KEY (id_produto_componente_historico) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.produtos_elementos_historicos ADD CONSTRAINT produtos_elementos_historicos_pkey PRIMARY KEY (id_produto_elemento_historico) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.produtos_itens_historicos ADD CONSTRAINT produtos_itens_historicos_pkey PRIMARY KEY (id_produto_item_historico) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.radares ADD CONSTRAINT radares_pkey PRIMARY KEY (id_radar) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.roles ADD CONSTRAINT roles_pkey PRIMARY KEY (id_role) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.status ADD CONSTRAINT status_pkey PRIMARY KEY (id_status) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.tipos_notas ADD CONSTRAINT tipos_notas_pkey PRIMARY KEY (id_tipo_nota) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.tipos_notas_componentes ADD CONSTRAINT tipos_notas_componentes_pkey PRIMARY KEY (id_tipo_nota_componente) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.versoes ADD CONSTRAINT versoes_pkey PRIMARY KEY (id_versao) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.users ADD CONSTRAINT users_pkey PRIMARY KEY (id_user) END")
	db.Exec("IF NOT EXISTS (SELECT 1 FROM sys.objects WHERE name = " +
		" 'status_membros_fkey' AND type in (N'PK')) BEGIN " +
		" ALTER TABLE virtus.workflows ADD CONSTRAINT workflows_pkey PRIMARY KEY (id_workflow) END")
}
