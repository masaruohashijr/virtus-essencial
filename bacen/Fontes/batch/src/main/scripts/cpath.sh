#!/bin/bash

CPATH=$CPATH:$DB2_JDBC

# TODO - inclua abaixo as dependencias das bibliotecas existentes no W:/lib e q nao estarao dentro do zip gerado

CPATH=$CPATH:$BC_JAVA_LIB_ROOT/lib/bc_quartz/atual/quartz.jar
CPATH=$CPATH:$BC_JAVA_LIB_ROOT/lib/bc_excecoes/atual/bc_excecoes.jar
CPATH=$CPATH:$BC_JAVA_LIB_ROOT/lib/bc_autran/atual/bc_autran.jar
CPATH=$CPATH:$BC_JAVA_LIB_ROOT/lib/bc_logging/atual/bc_logging.jar
CPATH=$CPATH:$BC_JAVA_LIB_ROOT/lib/bc_bto/atual/bc_bto.jar

# Aqui abaixo estao as referencias para todos as dependencias (diretas e transitivas)  geradas pelo maven
# o mais facil eh gerar o zip, verificar os jars que foram pra pasta "lib" do zip gerado, e declarar um a um aqui
# TODO   - coloque aqui referencia para todos os jars existentes dentro da pasta "lib" existente no zip do batch (exemplo comentado abaixo)
for JAR in ../lib/*.jar; do
	CPATH=$CPATH:$JAR
done

# WAS CLIENT LIBS
CPATH=$CPATH:$WAS_CLASSPATH
CPATH=$CPATH:$QUARTZ_HOME/properties

echo $CPATH