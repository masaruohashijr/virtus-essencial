#!/bin/bash

mainClass=$1
CLASSE_QUE_CONTEM_O_MAIN=br.gov.bcb.sisaps.batch.main.${mainClass}
cd /opt/quartz/jobs/sisapsBatch.SisapsApp/bin
export PWD=/opt/quartz/jobs/sisapsBatch.SisapsApp/bin

if [ -f $WAS_HOME/bin/setupCmdLine.sh ]; then
        $WAS_HOME/bin/setupCmdLine.sh
fi

CPATH=`./cpath.sh`

$JAVA_HOME/bin/java -cp ..:$CPATH $WAS_SSL_PROPS -Dquartz.home=$QUARTZ_HOME -Dlog4j.configuration=log4j.properties $CLASSE_QUE_CONTEM_O_MAIN $1 $2 1> stdout_${mainClass}.log 2> stderr_${mainClass}.log

RC=$?
echo Resultado=${RC}

tar -cvf log_sisapsbatch.tar *.log 
# TODO - declare no lugar de "TODO_ALIAS_BCJCIFS_PRO_DIRETORIO_LOG_DO_BATCH"  o alias BcJcifs para o compartilhamento de rede  que armazenara o log (pedir a DISIP) 
$QUARTZ_HOME/bin/jcifscopy.sh log_sisapsbatch.tar SMB:SIGAS.BATCHONLINE

exit ${RC}