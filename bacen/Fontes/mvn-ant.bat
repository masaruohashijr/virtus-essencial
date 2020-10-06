rem Ambientes: DESENV, FABRICA, HOMOLOGA, PRODUCAO 

call mvn -U -P deployment,websphere -DAMBIENTE=PRODUCAO clean install package -Dfindbugs.skip=true -Dmaven.pmd.skip=true -DskipPmd=true -DskipTests=true -Dpmd.skip=true

@echo Empacotando scripts de execução...

cd batch
for %%f in (src\main\java\br\gov\bcb\sisaps\batch\main\*.java) do (
	call mvn package -P run -DAMBIENTE=PRODUCAO -DmainClass=%%~nf -Dmaven.test.skip=true -Dpmd.skip=true -Dcpd.skip=true
)
cd ..