package br.com.imbazzu.frontSpring.agendamento.adpter.out.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.AgendamentoController.PassageiroDetalheView;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;

public final class AgendamentoPdfService {

    private static final LocalTime CORTE_MANHA = LocalTime.of(5, 30);
    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String ORIGEM_PADRAO = "CORONEL JOÃO SÁ";
    private static final int VAGAS_PADRAO_CARRO = 4;
    
    // Limite exato de 4 carros por página em paisagem (2 linhas x 2 colunas)
    private static final int MAX_CARROS_POR_PAGINA = 4; 
    
    private static final float[] COLUNAS_CARD_VEICULO = {2.4f, 1.8f, 2.2f, 1.0f};

    private enum FaixaHorario {
        ANTES_0530("ANTES DAS 05:30"),
        A_PARTIR_0530("A PARTIR DAS 05:30");

        private final String rotulo;

        FaixaHorario(String rotulo) {
            this.rotulo = rotulo;
        }

        public static FaixaHorario de(LocalTime saida) {
            if (saida != null && saida.isBefore(CORTE_MANHA)) {
                return ANTES_0530;
            }
            return A_PARTIR_0530;
        }

        public String getRotulo() {
            return rotulo;
        }
    }

    private AgendamentoPdfService() {
        throw new UnsupportedOperationException("Classe utilitária não deve ser instanciada");
    }

    // ------------------------------------------------------------------
    // RELATÓRIO GERAL (Agrupado por Faixa de Horário - 2 carros por linha, max 4 por folha)
    // ------------------------------------------------------------------

    public static byte[] gerar(
            LocalDate data,
            String titulo,
            List<VeiculoAbertoResponseDto> veiculos,
            List<AgendamentoResponseDto> passageirosSemVeiculo,
            Map<UUID, PassageiroDetalheView> detalhes) {
        
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4.rotate(), 22, 22, 22, 34);

        try {
            EstilosPdf estilos = new EstilosPdf();
            PdfWriter writer = PdfWriter.getInstance(documento, buffer);
            writer.setPageEvent(new RodapePaginas(estilos.getBaseFont()));
            documento.open();

            Map<FaixaHorario, List<VeiculoAbertoResponseDto>> veiculosPorFaixa = veiculos.stream()
                    .sorted(Comparator.comparing(
                            VeiculoAbertoResponseDto::horarioSaida,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.groupingBy(
                            v -> FaixaHorario.de(v.horarioSaida()),
                            () -> new EnumMap<>(FaixaHorario.class),
                            Collectors.toList()));

            int paginaAtual = 0;
            for (FaixaHorario faixa : FaixaHorario.values()) {
                List<VeiculoAbertoResponseDto> veiculosDaFaixa = veiculosPorFaixa.get(faixa);
                if (veiculosDaFaixa == null || veiculosDaFaixa.isEmpty()) {
                    continue;
                }

                int totalVeiculosNestaFaixa = veiculosDaFaixa.size();
                int indiceVeiculo = 0;

                while (indiceVeiculo < totalVeiculosNestaFaixa) {
                    if (paginaAtual++ > 0) {
                        documento.newPage();
                    }

                    adicionarCabecalho(
                            documento, 
                            titulo,
                            "SECRETARIA MUNICIPAL  |  TRANSPORTE DE PASSAGEIROS — PÁGINA " + paginaAtual + " (" + faixa.getRotulo() + ")", 
                            estilos
                    );
                    adicionarLinhaDataOrigem(documento, data, estilos);

                    PdfPTable gridVeiculos = new PdfPTable(2);
                    gridVeiculos.setWidthPercentage(100);
                    gridVeiculos.setSpacingAfter(8);
                    gridVeiculos.setWidths(new float[] {1f, 1f});

                    for (int i = 0; i < MAX_CARROS_POR_PAGINA; i += 2) {
                        if (indiceVeiculo >= totalVeiculosNestaFaixa) {
                            // Se acabaram os carros reais, preenche o resto da página com moldes em branco
                            PdfPTable tabelaVaziaEsq = construirTabelaCardVazio(estilos);
                            PdfPCell celulaEsq = new PdfPCell(tabelaVaziaEsq);
                            celulaEsq.setBorder(PdfPCell.NO_BORDER);
                            celulaEsq.setPadding(3);
                            gridVeiculos.addCell(celulaEsq);

                            PdfPTable tabelaVaziaDir = construirTabelaCardVazio(estilos);
                            PdfPCell celulaDir = new PdfPCell(tabelaVaziaDir);
                            celulaDir.setBorder(PdfPCell.NO_BORDER);
                            celulaDir.setPadding(3);
                            gridVeiculos.addCell(celulaDir);
                            
                            continue;
                        }

                        VeiculoAbertoResponseDto veiculoEsq = veiculosDaFaixa.get(indiceVeiculo++);
                        VeiculoAbertoResponseDto veiculoDir = (indiceVeiculo < totalVeiculosNestaFaixa) ? veiculosDaFaixa.get(indiceVeiculo++) : null;

                        int linhasEsq = calcularLinhasNecessarias(veiculoEsq);
                        PdfPTable tabelaEsq = construirTabelaVeiculoCard(veiculoEsq, detalhes, estilos, linhasEsq);
                        PdfPCell celulaEsq = new PdfPCell(tabelaEsq);
                        celulaEsq.setBorder(PdfPCell.NO_BORDER);
                        celulaEsq.setPadding(3);
                        gridVeiculos.addCell(celulaEsq);

                        if (veiculoDir != null) {
                            int linhasDir = calcularLinhasNecessarias(veiculoDir);
                            PdfPTable tabelaDir = construirTabelaVeiculoCard(veiculoDir, detalhes, estilos, linhasDir);
                            PdfPCell celulaDir = new PdfPCell(tabelaDir);
                            celulaDir.setBorder(PdfPCell.NO_BORDER);
                            celulaDir.setPadding(3);
                            gridVeiculos.addCell(celulaDir);
                        } else {
                            PdfPTable tabelaVaziaDir = construirTabelaCardVazio(estilos);
                            PdfPCell celulaDir = new PdfPCell(tabelaVaziaDir);
                            celulaDir.setBorder(PdfPCell.NO_BORDER);
                            celulaDir.setPadding(3);
                            gridVeiculos.addCell(celulaDir);
                        }
                    }

                    documento.add(gridVeiculos);
                }
            }

            if (passageirosSemVeiculo != null && !passageirosSemVeiculo.isEmpty()) {
                if (paginaAtual > 0) {
                    documento.newPage();
                }
                adicionarCabecalho(documento, titulo, "SECRETARIA MUNICIPAL  |  TRANSPORTE DE PASSAGEIROS", estilos);
                adicionarLinhaDataOrigem(documento, data, estilos);
                documento.add(construirTabelaSemVeiculo(passageirosSemVeiculo, detalhes, estilos));
            } else if (paginaAtual == 0) {
                documento.add(new Paragraph("Nenhum veículo incluído neste relatório.", estilos.getTextoNormal()));
            }

            documento.close();
            return buffer.toByteArray();

        } catch (DocumentException | IOException e) {
            fecharDocumentoSeguro(documento);
            throw new IllegalStateException("Não foi possível gerar o PDF da agenda.", e);
        } catch (RuntimeException e) {
            fecharDocumentoSeguro(documento);
            throw e;
        }
    }

    private static int calcularLinhasNecessarias(VeiculoAbertoResponseDto veiculo) {
        int qtdePassageiros = (veiculo.passageiros() != null) ? veiculo.passageiros().size() : 0;
        if (qtdePassageiros == 0) {
            return VAGAS_PADRAO_CARRO;
        }
        
        int totalOcupantes = 0;
        for (AgendamentoResponseDto p : veiculo.passageiros()) {
            int acomp = Math.max(0, p.acompanhantes());
            totalOcupantes += (1 + acomp);
        }
        
        if (totalOcupantes >= VAGAS_PADRAO_CARRO) {
            return qtdePassageiros;
        }
        
        int vagasRestantes = VAGAS_PADRAO_CARRO - totalOcupantes;
        return qtdePassageiros + vagasRestantes;
    }

    // ------------------------------------------------------------------
    // RELATÓRIO INDIVIDUAL (Duas Vias)
    // ------------------------------------------------------------------

    public static byte[] gerarIndividual(
            LocalDate data,
            VeiculoAbertoResponseDto veiculo,
            Map<UUID, PassageiroDetalheView> detalhes) {
        
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4, 36, 36, 30, 30);

        try {
            PdfWriter.getInstance(documento, buffer);
            documento.open();
            EstilosPdf estilos = new EstilosPdf();
            
            List<String> vias = List.of("VIA DO MOTORISTA", "VIA DA SECRETARIA");

            for (int i = 0; i < vias.size(); i++) {
                if (i > 0) {
                    documento.newPage();
                }

                String identificacaoStr = (veiculo.identificacao() == null || veiculo.identificacao().isBlank()) 
                        ? "" : " — " + veiculo.identificacao();
                
                adicionarCabecalho(
                        documento,
                        veiculo.nomeTipoVeiculo() + identificacaoStr,
                        data,
                        estilos
                );

                Paragraph viaTexto = new Paragraph(vias.get(i), estilos.getVia());
                viaTexto.setAlignment(Element.ALIGN_RIGHT);
                documento.add(viaTexto);
                documento.add(criarEspacamento(6));

                documento.add(construirTabelaDetalhesVeiculo(veiculo, data, estilos));
                
                Paragraph tituloSecao = new Paragraph("Relação de passageiros", estilos.getSecao());
                tituloSecao.setSpacingBefore(10);
                tituloSecao.setSpacingAfter(4);
                documento.add(tituloSecao);

                documento.add(construirTabelaPassageirosIndividual(veiculo.passageiros(), detalhes, estilos));
                documento.add(criarEspacamento(10));

                Paragraph assinaturaMotorista = new Paragraph(
                        "Assinatura do motorista: ______________________________________________",
                        estilos.getAssinatura());
                assinaturaMotorista.setSpacingBefore(8);
                documento.add(assinaturaMotorista);

                Paragraph conferenciaSecretaria = new Paragraph(
                        "Conferência da secretaria: ______________________________________________",
                        estilos.getAssinatura());
                conferenciaSecretaria.setSpacingBefore(4);
                documento.add(conferenciaSecretaria);
            }

            documento.close();
            return buffer.toByteArray();

        } catch (DocumentException | IOException e) {
            fecharDocumentoSeguro(documento);
            throw new IllegalStateException("Não foi possível gerar o PDF do veículo.", e);
        } catch (RuntimeException e) {
            fecharDocumentoSeguro(documento);
            throw e;
        }
    }

    // ------------------------------------------------------------------
    // CONSTRUTORES DE COMPONENTES E TABELAS
    // ------------------------------------------------------------------

    private static PdfPTable construirTabelaVeiculoCard(
            VeiculoAbertoResponseDto veiculo,
            Map<UUID, PassageiroDetalheView> detalhes,
            EstilosPdf estilos,
            int totalLinhasAlvo) throws DocumentException {
        
        PdfPTable tabela = new PdfPTable(COLUNAS_CARD_VEICULO);
        tabela.setWidthPercentage(100);
        tabela.setSpacingAfter(8);
        tabela.setHeaderRows(1);
        tabela.setKeepTogether(true);

        // 1. Cabeçalhos das colunas de passageiros
        for (String cabecalho : List.of("PASSAGEIRO", "DESTINO", "ENDEREÇO / BUSCA", "HORÁRIO")) {
            adicionarCelulaFormatada(tabela, cabecalho, estilos.getTextoCabecalho(), true, 6f);
        }

        // 2. Adiciona os passageiros reais e completa as linhas em branco
        int linhasAdicionadas = adicionarLinhasPassageirosGeral(tabela, veiculo.passageiros(), detalhes, estilos);
        
        int linhasFaltantes = Math.max(0, totalLinhasAlvo - linhasAdicionadas);
        for (int i = 0; i < linhasFaltantes; i++) {
            adicionarCelulaFormatada(tabela, " ", estilos.getTextoNormal(), false, 9.5f);
            adicionarCelulaFormatada(tabela, " ", estilos.getTextoNormal(), false, 9.5f);
            adicionarCelulaFormatada(tabela, " ", estilos.getTextoNormal(), false, 9.5f);
            adicionarCelulaFormatada(tabela, " ", estilos.getTextoNormal(), false, 9.5f);
        }

        // 3. Rodapé refinado do Card
        String horarioStr = veiculo.horarioSaida() == null ? "A definir" : veiculo.horarioSaida().format(HORA_FORMATTER);
        
        PdfPTable rodapeCard = new PdfPTable(2);
        rodapeCard.setWidthPercentage(100);
        rodapeCard.setWidths(new float[] { 2.4f, 1.6f });

        PdfPTable blocoEsquerdo = new PdfPTable(1);
        blocoEsquerdo.setWidthPercentage(100);
        
        PdfPCell cellFrete = new PdfPCell(new Phrase("Frete: Sim (    )    Não (    )", estilos.getInfoDestaque()));
        cellFrete.setBorder(PdfPCell.NO_BORDER);
        cellFrete.setPaddingBottom(4f);
        blocoEsquerdo.addCell(cellFrete);

        PdfPCell cellMotorista = new PdfPCell(new Phrase("Motorista: _______________________________", estilos.getInfoDestaque()));
        cellMotorista.setBorder(PdfPCell.NO_BORDER);
        blocoEsquerdo.addCell(cellMotorista);

        PdfPCell celulaEsquerdaContainer = new PdfPCell(blocoEsquerdo);
        celulaEsquerdaContainer.setBorder(PdfPCell.NO_BORDER);
        celulaEsquerdaContainer.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell celulaDireita = new PdfPCell(new Phrase("Saída: " + horarioStr, estilos.getInfoDestaque()));
        celulaDireita.setBorder(PdfPCell.NO_BORDER);
        celulaDireita.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celulaDireita.setVerticalAlignment(Element.ALIGN_MIDDLE);

        rodapeCard.addCell(celulaEsquerdaContainer);
        rodapeCard.addCell(celulaDireita);

        PdfPCell celulaContainerRodape = new PdfPCell(rodapeCard);
        celulaContainerRodape.setColspan(4);
        celulaContainerRodape.setBorderColor(new java.awt.Color(215, 223, 229));
        celulaContainerRodape.setBackgroundColor(new java.awt.Color(242, 246, 244));
        celulaContainerRodape.setPadding(6f);
        tabela.addCell(celulaContainerRodape);

        return tabela;
    }

    private static PdfPTable construirTabelaCardVazio(EstilosPdf estilos) throws DocumentException {
        PdfPTable tabela = new PdfPTable(COLUNAS_CARD_VEICULO);
        tabela.setWidthPercentage(100);
        tabela.setSpacingAfter(8);
        tabela.setHeaderRows(1);
        tabela.setKeepTogether(true);

        // Cabeçalhos padrão
        for (String cabecalho : List.of("PASSAGEIRO", "DESTINO", "ENDEREÇO / BUSCA", "HORÁRIO")) {
            adicionarCelulaFormatada(tabela, cabecalho, estilos.getTextoCabecalho(), true, 6f);
        }

        // Insere 4 linhas em branco completas para o molde manual
        for (int i = 0; i < VAGAS_PADRAO_CARRO; i++) {
            adicionarCelulaFormatada(tabela, " ", estilos.getTextoNormal(), false, 9.5f);
            adicionarCelulaFormatada(tabela, " ", estilos.getTextoNormal(), false, 9.5f);
            adicionarCelulaFormatada(tabela, " ", estilos.getTextoNormal(), false, 9.5f);
            adicionarCelulaFormatada(tabela, " ", estilos.getTextoNormal(), false, 9.5f);
        }

        // Rodapé em branco para preenchimento manual
        PdfPTable rodapeCard = new PdfPTable(2);
        rodapeCard.setWidthPercentage(100);
        rodapeCard.setWidths(new float[] { 2.4f, 1.6f });

        PdfPTable blocoEsquerdo = new PdfPTable(1);
        blocoEsquerdo.setWidthPercentage(100);
        
        PdfPCell cellFrete = new PdfPCell(new Phrase("Frete: Sim (    )    Não (    )", estilos.getInfoDestaque()));
        cellFrete.setBorder(PdfPCell.NO_BORDER);
        cellFrete.setPaddingBottom(4f);
        blocoEsquerdo.addCell(cellFrete);

        PdfPCell cellMotorista = new PdfPCell(new Phrase("Motorista: _______________________________", estilos.getInfoDestaque()));
        cellMotorista.setBorder(PdfPCell.NO_BORDER);
        blocoEsquerdo.addCell(cellMotorista);

        PdfPCell celulaEsquerdaContainer = new PdfPCell(blocoEsquerdo);
        celulaEsquerdaContainer.setBorder(PdfPCell.NO_BORDER);
        celulaEsquerdaContainer.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell celulaDireita = new PdfPCell(new Phrase("Saída: ________", estilos.getInfoDestaque()));
        celulaDireita.setBorder(PdfPCell.NO_BORDER);
        celulaDireita.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celulaDireita.setVerticalAlignment(Element.ALIGN_MIDDLE);

        rodapeCard.addCell(celulaEsquerdaContainer);
        rodapeCard.addCell(celulaDireita);

        PdfPCell celulaContainerRodape = new PdfPCell(rodapeCard);
        celulaContainerRodape.setColspan(4);
        celulaContainerRodape.setBorderColor(new java.awt.Color(215, 223, 229));
        celulaContainerRodape.setBackgroundColor(new java.awt.Color(242, 246, 244));
        celulaContainerRodape.setPadding(6f);
        tabela.addCell(celulaContainerRodape);

        return tabela;
    }

    private static PdfPTable construirTabelaSemVeiculo(
            List<AgendamentoResponseDto> passageiros,
            Map<UUID, PassageiroDetalheView> detalhes,
            EstilosPdf estilos) throws DocumentException {
        
        PdfPTable tabela = new PdfPTable(COLUNAS_CARD_VEICULO);
        tabela.setWidthPercentage(100);
        tabela.setHeaderRows(2);

        PdfPCell tituloCell = criarCelulaTitulo("PASSAGEIROS AGUARDANDO ATRIBUIÇÃO", estilos, Element.ALIGN_LEFT);
        tituloCell.setColspan(4);
        tabela.addCell(tituloCell);

        for (String cabecalho : List.of("PASSAGEIRO", "DESTINO", "ENDEREÇO / BUSCA", "HORÁRIO")) {
            adicionarCelulaFormatada(tabela, cabecalho, estilos.getTextoCabecalho(), true, 6f);
        }

        adicionarLinhasPassageirosGeral(tabela, passageiros, detalhes, estilos);
        return tabela;
    }

    private static int adicionarLinhasPassageirosGeral(
            PdfPTable tabela,
            List<AgendamentoResponseDto> passageiros,
            Map<UUID, PassageiroDetalheView> detalhes,
            EstilosPdf estilos) {
        
        if (passageiros == null || passageiros.isEmpty()) return 0;

        int contador = 0;
        for (AgendamentoResponseDto p : passageiros) {
            PassageiroDetalheView d = detalhes.get(p.idPessoa());
            if (d == null) continue;

            StringBuilder acompBuilder = new StringBuilder();
            int qtdAcomp = Math.max(0, p.acompanhantes());
            for (int i = 0; i < qtdAcomp; i++) {
                acompBuilder.append("\nAcomp");
            }
            
            Paragraph pPassageiro = new Paragraph();
            pPassageiro.setLeading(13.5f);
            pPassageiro.add(new Phrase(d.nome().toUpperCase() + "\n", estilos.getPassageiroNomeDestaque()));
            pPassageiro.add(new Phrase("Tel: " + d.telefone(), estilos.getPassageiroTelefoneDestaque()));
            if (acompBuilder.length() > 0) {
                pPassageiro.add(new Phrase(acompBuilder.toString(), estilos.getAcompDestaque()));
            }
            tabela.addCell(criarCelulaConteudoPersonalizada(pPassageiro, estilos, 6.5f));

            Paragraph pDestino = new Paragraph();
            pDestino.setLeading(13.5f);
            pDestino.add(new Phrase(d.cidade().toUpperCase() + "\n", estilos.getCidadeDestaque()));
            pDestino.add(new Phrase(d.destino(), estilos.getTextoNormal()));
            tabela.addCell(criarCelulaConteudoPersonalizada(pDestino, estilos, 6.5f));

            String endereco = !p.buscarNaResidencia()
                    ? "Não busca"
                    : (d.endereco() == null || d.endereco().isBlank() ? "Endereço não informado" : d.endereco());
            adicionarCelulaFormatada(tabela, endereco, estilos.getTextoNormal(), false, 6.5f);

            String horarioChegadaStr = p.horarioChegada() != null ? p.horarioChegada().format(HORA_FORMATTER) : "-";
            adicionarCelulaFormatada(tabela, horarioChegadaStr, estilos.getTextoNormal(), false, 6.5f);
            
            contador++;
        }
        return contador;
    }

    private static PdfPTable construirTabelaDetalhesVeiculo(
            VeiculoAbertoResponseDto veiculo, 
            LocalDate data, 
            EstilosPdf estilos) throws DocumentException {
        
        PdfPTable dadosVeiculo = new PdfPTable(new float[] {1.1f, 1.3f, 1.1f, 1.5f});
        dadosVeiculo.setWidthPercentage(100);
        dadosVeiculo.setSpacingAfter(6);

        adicionarCelulaFormatada(dadosVeiculo, "Tipo", estilos.getTextoCabecalho(), true, 5.5f);
        adicionarCelulaFormatada(dadosVeiculo, veiculo.nomeTipoVeiculo(), estilos.getTextoNormal(), false, 5.5f);
        
        adicionarCelulaFormatada(dadosVeiculo, "Data", estilos.getTextoCabecalho(), true, 5.5f);
        adicionarCelulaFormatada(dadosVeiculo, data.format(DATA_FORMATTER), estilos.getTextoNormal(), false, 5.5f);

        adicionarCelulaFormatada(dadosVeiculo, "Identificação", estilos.getTextoCabecalho(), true, 5.5f);
        adicionarCelulaFormatada(dadosVeiculo, veiculo.identificacao() != null ? veiculo.identificacao() : "-", estilos.getTextoNormal(), false, 5.5f);
        
        adicionarCelulaFormatada(dadosVeiculo, "Placa", estilos.getTextoCabecalho(), true, 5.5f);
        adicionarCelulaFormatada(dadosVeiculo, "____________________", estilos.getTextoNormal(), false, 5.5f);

        adicionarCelulaFormatada(dadosVeiculo, "Ocupação", estilos.getTextoCabecalho(), true, 5.5f);
        adicionarCelulaFormatada(dadosVeiculo, String.format("%d de %d lugares", veiculo.assentosOcupados(), veiculo.capacidade()), estilos.getTextoNormal(), false, 5.5f);
        
        adicionarCelulaFormatada(dadosVeiculo, "Saída", estilos.getTextoCabecalho(), true, 5.5f);
        String saidaStr = veiculo.horarioSaida() == null ? "A definir" : veiculo.horarioSaida().format(HORA_FORMATTER);
        adicionarCelulaFormatada(dadosVeiculo, saidaStr, estilos.getTextoNormal(), false, 5.5f);

        return dadosVeiculo;
    }

    private static PdfPTable construirTabelaPassageirosIndividual(
            List<AgendamentoResponseDto> passageiros,
            Map<UUID, PassageiroDetalheView> detalhes,
            EstilosPdf estilos) throws DocumentException {
        
        PdfPTable tabela = new PdfPTable(new float[] {2.1f, 2.2f, 2.3f, 0.8f});
        tabela.setWidthPercentage(100);
        tabela.setHeaderRows(1);
        tabela.setSplitRows(true);

        for (String cabecalho : List.of("Passageiro / telefone", "Cidade / destino / chegada", "Busca / endereço", "Acomp.")) {
            adicionarCelulaFormatada(tabela, cabecalho, estilos.getTextoCabecalho(), true, 6f);
        }

        int totalOcupantes = 0;
        if (passageiros != null && !passageiros.isEmpty()) {
            for (AgendamentoResponseDto passageiro : passageiros) {
                int acomp = Math.max(0, passageiro.acompanhantes());
                totalOcupantes += (1 + acomp);
            }
        }

        int linhasAdicionadas = 0;
        if (passageiros != null && !passageiros.isEmpty()) {
            for (AgendamentoResponseDto passageiro : passageiros) {
                PassageiroDetalheView detalhe = detalhes.get(passageiro.idPessoa());
                if (detalhe == null) continue;

                Paragraph pPassageiro = new Paragraph();
                pPassageiro.setLeading(13.5f);
                pPassageiro.add(new Phrase(detalhe.nome().toUpperCase() + "\n", estilos.getPassageiroNomeDestaque()));
                pPassageiro.add(new Phrase("Tel: " + detalhe.telefone(), estilos.getPassageiroTelefoneDestaque()));
                tabela.addCell(criarCelulaConteudoPersonalizada(pPassageiro, estilos, 6.5f));
                
                String chegadaStr = passageiro.horarioChegada() != null ? passageiro.horarioChegada().format(HORA_FORMATTER) : "-";
                
                Paragraph pCidade = new Paragraph();
                pCidade.setLeading(13.5f);
                pCidade.add(new Phrase(detalhe.cidade().toUpperCase() + "\n", estilos.getCidadeDestaque()));
                pCidade.add(new Phrase(detalhe.destino() + "\nChegada: " + chegadaStr, estilos.getTextoNormal()));
                tabela.addCell(criarCelulaConteudoPersonalizada(pCidade, estilos, 6.5f));

                String enderecoInfo = passageiro.buscarNaResidencia()
                        ? "Residência\n" + (detalhe.endereco() == null || detalhe.endereco().isBlank() ? "Endereço não informado" : detalhe.endereco())
                        : "Sem busca";
                
                adicionarCelulaFormatada(tabela, enderecoInfo, estilos.getTextoNormal(), false, 6.5f);
                adicionarCelulaFormatada(tabela, Integer.toString(passageiro.acompanhantes()), estilos.getTextoNormal(), false, 6.5f);
                linhasAdicionadas++;
            }
        }

        int linhasFaltantes = 0;
        if (totalOcupantes < VAGAS_PADRAO_CARRO) {
            linhasFaltantes = VAGAS_PADRAO_CARRO - totalOcupantes;
        }

        for (int i = 0; i < linhasFaltantes; i++) {
            adicionarCelulaFormatada(tabela, " ", estilos.getTextoNormal(), false, 9.5f);
            adicionarCelulaFormatada(tabela, " ", estilos.getTextoNormal(), false, 9.5f);
            adicionarCelulaFormatada(tabela, " ", estilos.getTextoNormal(), false, 9.5f);
            adicionarCelulaFormatada(tabela, " ", estilos.getTextoNormal(), false, 9.5f);
        }

        return tabela;
    }

    // ------------------------------------------------------------------
    // MÉTODOS AUXILIARES DE SUPORTE AO LAYOUT
    // ------------------------------------------------------------------

    private static void adicionarCabecalho(Document documento, String titulo, LocalDate data, EstilosPdf estilos)
            throws DocumentException, IOException {
        adicionarCabecalho(documento, titulo, "SECRETARIA MUNICIPAL  |  TRANSPORTE DE PASSAGEIROS  |  " + data.format(DATA_FORMATTER), estilos);
    }

    private static void adicionarCabecalho(
            Document documento, String titulo, String linhaSubtitulo, EstilosPdf estilos)
            throws DocumentException, IOException {
        
        PdfPTable cabecalho = new PdfPTable(new float[] {1.1f, 5.2f});
        cabecalho.setWidthPercentage(100);
        cabecalho.setSpacingAfter(4);

        Image logo = Image.getInstance(AgendamentoPdfService.class.getResource("/static/images/logo.png"));
        logo.scaleToFit(85, 34);
        
        PdfPCell celulaLogo = new PdfPCell(logo, true);
        celulaLogo.setBorder(PdfPCell.NO_BORDER);
        celulaLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cabecalho.addCell(celulaLogo);

        PdfPTable textoTabela = new PdfPTable(1);
        textoTabela.setWidthPercentage(100);

        PdfPCell tituloCell = new PdfPCell(new Phrase(titulo, estilos.getTituloPrincipal()));
        tituloCell.setBorder(PdfPCell.NO_BORDER);
        tituloCell.setPaddingBottom(1);
        textoTabela.addCell(tituloCell);

        PdfPCell subtituloCell = new PdfPCell(new Phrase(linhaSubtitulo, estilos.getSubtitulo()));
        subtituloCell.setBorder(PdfPCell.NO_BORDER);
        textoTabela.addCell(subtituloCell);

        PdfPCell textoCelula = new PdfPCell(textoTabela);
        textoCelula.setBorder(PdfPCell.NO_BORDER);
        textoCelula.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cabecalho.addCell(textoCelula);
        
        documento.add(cabecalho);

        PdfPTable faixa = new PdfPTable(1);
        faixa.setWidthPercentage(100);
        PdfPCell faixaCelula = new PdfPCell(new Phrase(" "));
        faixaCelula.setFixedHeight(2f);
        faixaCelula.setBorder(PdfPCell.NO_BORDER);
        faixaCelula.setBackgroundColor(new java.awt.Color(0, 130, 75));
        faixa.addCell(faixaCelula);
        faixa.setSpacingAfter(4);
        documento.add(faixa);
    }

    private static void adicionarLinhaDataOrigem(Document documento, LocalDate data, EstilosPdf estilos)
            throws DocumentException {
        PdfPTable linha = new PdfPTable(2);
        linha.setWidthPercentage(100);
        linha.setSpacingAfter(4);

        PdfPCell esquerda = new PdfPCell(new Phrase("DATA: " + data.format(DATA_FORMATTER), estilos.getVia()));
        esquerda.setBorder(PdfPCell.NO_BORDER);

        PdfPCell direita = new PdfPCell(new Phrase(ORIGEM_PADRAO, estilos.getVia()));
        direita.setBorder(PdfPCell.NO_BORDER);
        direita.setHorizontalAlignment(Element.ALIGN_RIGHT);

        linha.addCell(esquerda);
        linha.addCell(direita);
        documento.add(linha);
    }

    private static PdfPCell criarCelulaTitulo(String texto, EstilosPdf estilos, int alinhamento) {
        PdfPCell celula = new PdfPCell(new Phrase(texto, estilos.getTituloVeiculo()));
        celula.setBackgroundColor(new java.awt.Color(0, 92, 63));
        celula.setBorderColor(new java.awt.Color(0, 92, 63));
        celula.setHorizontalAlignment(alinhamento);
        celula.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celula.setPadding(4);
        return celula;
    }

    private static void adicionarCelulaFormatada(PdfPTable tabela, String texto, Font fonte, boolean cabecalho, float paddingVertical) {
        PdfPCell celula = new PdfPCell(new Phrase(texto == null ? "" : texto, fonte));
        celula.setPaddingTop(paddingVertical);
        celula.setPaddingBottom(paddingVertical);
        celula.setPaddingLeft(5f);
        celula.setPaddingRight(5f);
        celula.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celula.setBorderColor(new java.awt.Color(218, 226, 232));
        if (cabecalho) {
            celula.setBackgroundColor(new java.awt.Color(0, 105, 75));
            celula.setBorderColor(new java.awt.Color(0, 105, 75));
        }
        tabela.addCell(celula);
    }

    private static void adicionarCelulaFormatada(PdfPTable tabela, String texto, Font fonte, boolean cabecalho) {
        adicionarCelulaFormatada(tabela, texto, fonte, cabecalho, 6f);
    }

    private static PdfPCell criarCelulaConteudoPersonalizada(Paragraph paragraph, EstilosPdf estilos, float paddingVertical) {
        PdfPCell celula = new PdfPCell(paragraph);
        celula.setPaddingTop(paddingVertical);
        celula.setPaddingBottom(paddingVertical);
        celula.setPaddingLeft(5f);
        celula.setPaddingRight(5f);
        celula.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celula.setBorderColor(new java.awt.Color(218, 226, 232));
        return celula;
    }

    private static Paragraph criarEspacamento(float tamanho) {
        Paragraph p = new Paragraph(" ");
        p.setLeading(tamanho);
        return p;
    }

    private static void fecharDocumentoSeguro(Document documento) {
        try {
            if (documento != null && documento.isOpen()) {
                documento.close();
            }
        } catch (Exception ignored) {
        }
    }

    // ------------------------------------------------------------------
    // GERENCIAMENTO DE ESTILOS E FONTES
    // ------------------------------------------------------------------

    private static final class EstilosPdf {
        private final BaseFont baseFont = criarBaseFont();
        private final Font tituloPrincipal = new Font(baseFont, 14.5f, Font.BOLD, new java.awt.Color(0, 92, 63));
        private final Font subtitulo = new Font(baseFont, 8.5f, Font.BOLD, new java.awt.Color(80, 95, 105));
        private final Font secao = new Font(baseFont, 11f, Font.BOLD, new java.awt.Color(0, 92, 63));
        private final Font textoNormal = new Font(baseFont, 8.5f, Font.NORMAL, new java.awt.Color(38, 48, 56));
        private final Font textoCabecalho = new Font(baseFont, 8.5f, Font.BOLD, java.awt.Color.WHITE);
        private final Font via = new Font(baseFont, 9.5f, Font.BOLD, new java.awt.Color(0, 105, 75));
        private final Font assinatura = new Font(baseFont, 9f, Font.NORMAL, new java.awt.Color(38, 48, 56));
        private final Font tituloVeiculo = new Font(baseFont, 9.5f, Font.BOLD, java.awt.Color.WHITE);
        private final Font infoDestaque = new Font(baseFont, 8.2f, Font.BOLD, new java.awt.Color(0, 92, 63));

        private final Font passageiroNomeDestaque = new Font(baseFont, 8.8f, Font.BOLD, new java.awt.Color(0, 70, 45));
        private final Font passageiroTelefoneDestaque = new Font(baseFont, 8.2f, Font.BOLD, new java.awt.Color(0, 105, 75));
        private final Font cidadeDestaque = new Font(baseFont, 8.8f, Font.BOLD, new java.awt.Color(0, 92, 63));
        private final Font acompDestaque = new Font(baseFont, 7.8f, Font.BOLD, new java.awt.Color(130, 45, 45));

        private static BaseFont criarBaseFont() {
            try {
                return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            } catch (IOException | DocumentException e) {
                throw new IllegalStateException("Não foi possível carregar a fonte padrão do PDF.", e);
            }
        }

        public BaseFont getBaseFont() { return baseFont; }
        public Font getTituloPrincipal() { return tituloPrincipal; }
        public Font getSubtitulo() { return subtitulo; }
        public Font getSecao() { return secao; }
        public Font getTextoNormal() { return textoNormal; }
        public Font getTextoCabecalho() { return textoCabecalho; }
        public Font getVia() { return via; }
        public Font getAssinatura() { return assinatura; }
        public Font getTituloVeiculo() { return tituloVeiculo; }
        public Font getInfoDestaque() { return infoDestaque; }
        public Font getPassageiroNomeDestaque() { return passageiroNomeDestaque; }
        public Font getPassageiroTelefoneDestaque() { return passageiroTelefoneDestaque; }
        public Font getCidadeDestaque() { return cidadeDestaque; }
        public Font getAcompDestaque() { return acompDestaque; }
    }

    // ------------------------------------------------------------------
    // EVENTO DE RODAPÉ (Numeração de Páginas Dinâmica)
    // ------------------------------------------------------------------

    private static final class RodapePaginas extends PdfPageEventHelper {
        private static final float TAMANHO_FONTE = 8f;
        private final BaseFont baseFont;
        private PdfTemplate totalPaginasTemplate;

        private RodapePaginas(BaseFont baseFont) {
            this.baseFont = baseFont;
        }

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            totalPaginasTemplate = writer.getDirectContent().createTemplate(30, 12);
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            String textoRodape = "Relatório Geral de Carros — Transporte de Passageiros   Página "
                    + writer.getPageNumber() + " de ";
            
            float larguraTexto = baseFont.getWidthPoint(textoRodape, TAMANHO_FONTE);
            float posX = (document.left() + document.right() - larguraTexto - 12) / 2;
            float posY = document.bottom() - 12;

            cb.beginText();
            cb.setFontAndSize(baseFont, TAMANHO_FONTE);
            cb.setTextMatrix(posX, posY);
            cb.showText(textoRodape);
            cb.endText();
            
            cb.addTemplate(totalPaginasTemplate, posX + larguraTexto, posY);
        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            totalPaginasTemplate.beginText();
            totalPaginasTemplate.setFontAndSize(baseFont, TAMANHO_FONTE);
            totalPaginasTemplate.setTextMatrix(0, 0);
            totalPaginasTemplate.showText(String.valueOf(writer.getPageNumber() - 1));
            totalPaginasTemplate.endText();
        }
    }
}