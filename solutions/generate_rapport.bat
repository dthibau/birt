@echo off
setlocal

REM Vérification des paramètres
if "%~1"=="" (
    echo Usage: %~nx0 "C:\chemin\vers\rapport.rptdesign"
    exit /b 1
)

REM Définition des variables
set BIRT_RUNTIME_DIR="C:\birt-runtime-4_9_0"
set REPORT_FILE=%~1
set OUTPUT_DIR="C:\reports"
set OUTPUT_FILE=%OUTPUT_DIR%\mon_rapport_%DATE:~6,4%%DATE:~3,2%%DATE:~0,2%.pdf"
set LOG_FILE=%OUTPUT_DIR%\generation_log.txt"

REM Vérification de l'existence du fichier de rapport
if not exist "%REPORT_FILE%" (
    echo Erreur: Le fichier de rapport "%REPORT_FILE%" n'existe pas. >> %LOG_FILE%
    echo Erreur: Le fichier de rapport "%REPORT_FILE%" n'existe pas.
    exit /b 1
)

REM Vérification et création du dossier de sortie si nécessaire
if not exist %OUTPUT_DIR% mkdir %OUTPUT_DIR%

REM Exécution de BIRT pour générer le rapport
echo Génération du rapport "%REPORT_FILE%" en cours... >> %LOG_FILE%
"%BIRT_RUNTIME_DIR%\ReportEngine\genReport.bat" -f pdf -o "%OUTPUT_FILE%" -r "%REPORT_FILE%"

REM Vérification de la réussite de la génération
if exist "%OUTPUT_FILE%" (
    echo Rapport généré avec succès : %OUTPUT_FILE% >> %LOG_FILE%
    echo Rapport généré avec succès : %OUTPUT_FILE%
) else (
    echo Erreur lors de la génération du rapport. >> %LOG_FILE%
    echo Erreur lors de la génération du rapport.
    exit /b 1
)

endlocal
exit /b 0

