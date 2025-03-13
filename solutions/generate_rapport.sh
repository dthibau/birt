#!/bin/bash

# Vérification du nombre d'arguments
if [ $# -ne 1 ]; then
    echo "Usage: $0 <chemin_du_fichier_rptdesign>"
    exit 1
fi

# Récupération du paramètre
REPORT_FILE="$1"

# Définition des autres variables
BIRT_RUNTIME_DIR="/home/dthibau/Formations/Birt/mywork/birt-runtime-4.19.0-202503120947"
OUTPUT_DIR="/home/dthibau/Formations/Birt/rapports_pdf_genere"
LOG_FILE="$OUTPUT_DIR/generation_log.txt"

# Vérification que le fichier du rapport existe
if [ ! -f "$REPORT_FILE" ]; then
    echo "$(date) - Erreur : fichier $REPORT_FILE introuvable !" >> "$LOG_FILE"
    exit 1
fi

# Génération d'un nom de fichier avec la date du jour
DATE=$(date +"%Y%m%d")
REPORT_NAME=$(basename "$REPORT_FILE" .rptdesign)
OUTPUT_FILE="$OUTPUT_DIR/${REPORT_NAME}_$DATE.pdf"

# Vérification que le dossier de sortie existe
mkdir -p "$OUTPUT_DIR"

# Exécution de BIRT pour générer le rapport
echo "$(date) - Génération du rapport : $REPORT_FILE" >> "$LOG_FILE"
"$BIRT_RUNTIME_DIR/ReportEngine/genReport.sh" -f pdf -o "$OUTPUT_FILE" -r "$REPORT_FILE"

# Vérification du succès
if [ $? -eq 0 ]; then
    echo "$(date) - Rapport généré avec succès : $OUTPUT_FILE" >> "$LOG_FILE"
else
    echo "$(date) - Échec de la génération du rapport" >> "$LOG_FILE"
fi

