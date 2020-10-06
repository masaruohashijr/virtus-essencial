#!/usr/bin/bash
set +x

# Indica os nomes do folder e do workflow a ser executado.
FOLDER_NAME=TR_G17_APS
WORKFLOW_NAME=WKF_ATIVIDADES_SIGAS_ATUALIZACAO_INCREMENTAL

# Define os arquivos de log da sessao e do workflow
# No caso dos logs de sessao podem ser definidos mais de um.
ListaLogsSessao="/pcenter/SessLogs/$FOLDER_NAME/s_MPP_ATUALIZA_3_ATIVIDADES_SIGAS.log"

wlog=/pcenter/WorkflowLogs/$FOLDER_NAME/$WORKFLOW_NAME.log

# O parametro abaixo indica se todos os arquivos de log de
# sessao devem ser examinados ou somente se eles existirem.
EXAM_ALL_LOGS=1

# O parametro abaixo ativa o log do contexto do erro.
LOGA_CONTEXTO_ERRO=1

# Executa a parte generica do script.
. /pcenter/controlm/StartWorkflow_inc.sh
exit $returncode