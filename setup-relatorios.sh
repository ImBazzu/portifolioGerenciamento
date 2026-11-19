#!/bin/bash
# Script de Compilação e Teste - Sistema de Relatórios

echo "=========================================="
echo "   COMPILAÇÃO E TESTE - RELATÓRIOS"
echo "=========================================="
echo ""

# Cores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}📦 Etapa 1: Compilar Backend${NC}"
cd backend
mvn clean compile -DskipTests -q
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Backend compilado com sucesso${NC}"
else
    echo -e "${YELLOW}❌ Erro na compilação${NC}"
    exit 1
fi
cd ..

echo ""
echo -e "${BLUE}🧪 Etapa 2: Executar Testes de API${NC}"
echo "Execute em outro terminal:"
echo "  cd backend"
echo "  mvn spring-boot:run"
echo ""
echo "Depois faça os testes (em outro terminal):"
echo "  curl http://localhost:8080/api/relatorios/agendamentos"
echo "  curl http://localhost:8080/api/relatorios/pessoas"
echo "  curl http://localhost:8080/api/relatorios/veiculos"
echo "  curl http://localhost:8080/api/relatorios/operacional"
echo ""

echo -e "${BLUE}🎨 Etapa 3: Acessar Frontend${NC}"
echo "URL: http://localhost:8080/relatorios"
echo ""

echo -e "${GREEN}=========================================="
echo "✅ INSTALAÇÃO COMPLETA!"
echo "==========================================${NC}"
echo ""
echo "Próximos passos:"
echo "1. Compilar backend: mvn clean install"
echo "2. Iniciar aplicação: mvn spring-boot:run"
echo "3. Abrir navegador: http://localhost:8080/relatorios"
echo ""
