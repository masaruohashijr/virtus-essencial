@echo off

if NOT EXIST ear\src\main\application\META-INF\ibm-application-bnd.xml (
  echo ERRO:
  echo   Os arquivos descritores do Websphere nao estao presentes. Para empacotar a aplicacao pela primeira vez, eh necessario cria-los.
  echo   Leia sobre isso em http://wiki.bc/wiki_desenv/index.php/Cria%%C3%%A7%%C3%%A3o_de_descritores_Websphere#Gerando_os_descritores
  goto FIM
)

call mvn -Dfindbugs.skip=true -Dmaven.pmd.skip=true -DskipPmd=true -DskipTests=true -Dpmd.skip=true -P deployment,websphere clean package %*

@echo Empacotando scripts de execução...

cd batch
for %%f in (src\main\java\br\gov\bcb\sisaps\batch\main\*.java) do (
	call mvn package -P run -DmainClass=%%~nf -Dmaven.test.skip=true -Dpmd.skip=true -Dcpd.skip=true
)
cd ..

:FIM