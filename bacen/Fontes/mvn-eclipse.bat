@ECHO OFF
call mvn -U eclipse:eclipse %*
IF %ERRORLEVEL% EQU 0 goto Fim

echo Nao foi possivel executar eclipse:eclipse. Tentando mais uma vez com install antes.
call mvn -e -U -P default,rapido install eclipse:eclipse %*

:Fim
pause
echo Fim