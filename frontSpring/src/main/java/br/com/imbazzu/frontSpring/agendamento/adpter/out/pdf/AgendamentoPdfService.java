package br.com.imbazzu.frontSpring.agendamento.adpter.out.pdf;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.HashMap;
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
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.PassageiroDetalheView;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.PassageiroRelatorioView;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.TipoVeiculoUtil;
public final class AgendamentoPdfService {
    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String ORIGEM_PADRAO = "CORONEL JOÃO SÁ";
    private static final int VAGAS_PADRAO_CARRO = 4;
    // Até oito carros por página, em A4 vertical: duas colunas por quatro linhas.
    private static final int MAX_CARROS_POR_PAGINA = 8;
    private static final float[] COLUNAS_CARD_VEICULO = {2.4f, 1.8f, 2.2f, 1.0f};
    private enum FaixaHorario {
        ANTES_0530("ANTES DAS 05:30"),
        A_PARTIR_0530("A PARTIR DAS 05:30");
        private final String rotulo;
        FaixaHorario(String rotulo) {
            this.rotulo = rotulo;
        }
        public static FaixaHorario de(VeiculoAbertoResponseDto veiculo) {
            if ("ANTES_0530".equals(veiculo.faixaHorarioSaida())) {
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

    public static byte[] gerarComprovante(
            AgendamentoResponseDto agendamento, PassageiroDetalheView pessoa, VeiculoAbertoResponseDto veiculo) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4, 42, 42, 40, 40);
        try {
            var estilos = new EstilosPdf();
            PdfWriter.getInstance(documento, buffer);
            documento.open();
            adicionarCabecalho(documento, "Comprovante de agendamento",
                    "SECRETARIA MUNICIPAL  |  TRANSPORTE DE PASSAGEIROS", estilos);
            documento.add(criarEspacamento(12));

            var tabela = new PdfPTable(1);
            tabela.setWidthPercentage(100);
            PdfPCell passageiroDestaque = new PdfPCell();
            passageiroDestaque.setBackgroundColor(new java.awt.Color(229, 244, 235));
            passageiroDestaque.setBorderColor(new java.awt.Color(192, 220, 202));
            passageiroDestaque.setPadding(16);
            passageiroDestaque.addElement(new Paragraph("PASSAGEIRO", estilos.getTituloBloco()));
            passageiroDestaque.addElement(new Paragraph(valorRelatorio(pessoa.nome()),
                    new Font(estilos.getBaseFont(), 18, Font.BOLD, new java.awt.Color(31, 92, 63))));
            tabela.addCell(passageiroDestaque);
            adicionarTituloBloco(tabela, "INFORMAÇÕES DA VIAGEM", estilos);
            adicionarCampoComprovante(tabela, "Data da viagem", agendamento.data().format(DATA_FORMATTER), estilos);
            adicionarCampoComprovante(tabela, "Cidade", pessoa.cidade(), estilos);
            adicionarCampoComprovante(tabela, "Destino", pessoa.destino(), estilos);
            adicionarCampoComprovante(tabela, "Chegada prevista",
                    formatarHoraRelatorio(agendamento.horarioChegada()), estilos);
            adicionarCampoComprovante(tabela, "Acompanhantes", Integer.toString(agendamento.acompanhantes()), estilos);
            adicionarCampoComprovante(tabela, "Busca na residência",
                    agendamento.buscarNaResidencia() ? "Sim" : "Não", estilos);
            if (agendamento.buscarNaResidencia()) {
                adicionarCampoComprovante(tabela, "Endereço de busca", pessoa.endereco(), estilos);
            }
            if (veiculo != null) {
                adicionarCampoComprovante(tabela, "Veículo", veiculo.nomeTipoVeiculo()
                        + (veiculo.identificacao() == null || veiculo.identificacao().isBlank()
                                ? "" : " — " + veiculo.identificacao()), estilos);
                if (veiculo.horarioSaida() != null) {
                    adicionarCampoComprovante(tabela, "Saída do veículo",
                            formatarHoraRelatorio(veiculo.horarioSaida()), estilos);
                }
            }
            documento.add(tabela);
            documento.add(criarEspacamento(12));
            var aviso = new Paragraph(
                    "Guarde este comprovante. Os dados do transporte podem ser atualizados pela secretaria.",
                    new Font(estilos.getBaseFont(), 10f, Font.NORMAL, new java.awt.Color(55, 70, 78)));
            aviso.setLeading(15);
            PdfPTable quadroAviso = new PdfPTable(1);
            quadroAviso.setWidthPercentage(100);
            PdfPCell celulaAviso = new PdfPCell(aviso);
            celulaAviso.setBackgroundColor(new java.awt.Color(242, 246, 244));
            celulaAviso.setBorderColor(new java.awt.Color(218, 226, 232));
            celulaAviso.setPadding(12);
            quadroAviso.addCell(celulaAviso);
            documento.add(quadroAviso);
            documento.add(criarEspacamento(10));
            documento.add(new Paragraph("Código do agendamento: " + agendamento.id(), estilos.getTextoNormal()));
            documento.close();
            return buffer.toByteArray();
        } catch (DocumentException | IOException e) {
            fecharDocumentoSeguro(documento);
            throw new IllegalStateException("Não foi possível gerar o comprovante de agendamento.", e);
        } catch (RuntimeException e) {
            fecharDocumentoSeguro(documento);
            throw e;
        }
    }

    private static void adicionarCampoComprovante(PdfPTable tabela, String rotulo, String valor, EstilosPdf estilos) {
        var campo = new Paragraph();
        campo.setLeading(16);
        campo.add(new Phrase(rotulo + ": ",
                new Font(estilos.getBaseFont(), 10, Font.BOLD, new java.awt.Color(91, 112, 101))));
        campo.add(new Phrase(valorRelatorio(valor),
                new Font(estilos.getBaseFont(), 11, Font.NORMAL, new java.awt.Color(33, 48, 41))));
        var celula = new PdfPCell(campo);
        celula.setBorderColor(new java.awt.Color(218, 226, 232));
        celula.setPadding(9);
        tabela.addCell(celula);
    }

    public static byte[] gerarPassageiros(LocalDate data, List<PassageiroRelatorioView> passageiros) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4, 28, 28, 28, 34);
        try {
            EstilosPdf estilos = new EstilosPdf();
            PdfWriter writer = PdfWriter.getInstance(documento, buffer);
            writer.setPageEvent(new RodapePaginas(estilos.getBaseFont(), "Passageiros cadastrados"));
            documento.open();
            adicionarCabecalho(documento, "Passageiros cadastrados",
                    "SECRETARIA MUNICIPAL  |  TRANSPORTE DE PASSAGEIROS", estilos);
            adicionarLinhaDataOrigem(documento, data, estilos);
            PdfPTable resumo = new PdfPTable(2);
            resumo.setWidthPercentage(100);
            resumo.setWidths(new float[] {3f, 1f});
            resumo.setSpacingAfter(10);
            PdfPCell rotuloTotal = new PdfPCell(new Phrase("TOTAL DE PASSAGEIROS", estilos.getSubtitulo()));
            rotuloTotal.setBackgroundColor(new java.awt.Color(242, 246, 244));
            rotuloTotal.setBorderColor(new java.awt.Color(218, 226, 232));
            rotuloTotal.setPadding(7);
            resumo.addCell(rotuloTotal);
            PdfPCell quantidadeTotal = new PdfPCell(new Phrase(Integer.toString(passageiros.size()), estilos.getTotalDestaque()));
            quantidadeTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
            quantidadeTotal.setBackgroundColor(new java.awt.Color(230, 244, 236));
            quantidadeTotal.setBorderColor(new java.awt.Color(192, 220, 202));
            quantidadeTotal.setPadding(7);
            resumo.addCell(quantidadeTotal);
            documento.add(resumo);
            if (passageiros.isEmpty()) {
                documento.add(new Paragraph("Nenhum passageiro encontrado na seleção.", estilos.getTextoNormal()));
            }
            for (PassageiroRelatorioView linha : passageiros) {
                var pessoa = linha.pessoa();
                var agendamento = linha.agendamento();
                var veiculo = linha.veiculo();
                PdfPTable card = new PdfPTable(2);
                card.setWidthPercentage(100);
                card.setWidths(new float[] {1f, 1f});
                card.setKeepTogether(true);
                card.setSpacingAfter(8);
                PdfPCell titulo = new PdfPCell(new Phrase(valorRelatorio(pessoa.nome()), estilos.getSecao()));
                titulo.setColspan(2);
                titulo.setBackgroundColor(new java.awt.Color(234, 245, 238));
                titulo.setBorderColor(new java.awt.Color(192, 220, 202));
                titulo.setPadding(7);
                card.addCell(titulo);
                PdfPTable dadosPessoais = new PdfPTable(1);
                dadosPessoais.setWidthPercentage(100);
                adicionarTituloBloco(dadosPessoais, "DADOS DO PASSAGEIRO", estilos);
                adicionarCampoRelatorio(dadosPessoais, "CPF", pessoa.cpf(), estilos);
                adicionarCampoRelatorio(dadosPessoais, "Telefone", pessoa.telefone(), estilos);
                adicionarCampoRelatorio(dadosPessoais, "Especificação", pessoa.especificacao(), estilos);
                adicionarCampoRelatorio(dadosPessoais, "Acompanhantes",
                        Integer.toString(agendamento.acompanhantes()), estilos);
                adicionarCampoRelatorio(dadosPessoais, "Busca na residência",
                        agendamento.buscarNaResidencia() ? "Sim" : "Não", estilos);
                if (agendamento.buscarNaResidencia()) {
                    adicionarCampoRelatorio(dadosPessoais, "Endereço", pessoa.endereco(), estilos);
                }
                if (pessoa.observacao() != null && !pessoa.observacao().isBlank()) {
                    adicionarCampoRelatorio(dadosPessoais, "Observação", pessoa.observacao(), estilos);
                }
                adicionarBlocoAoCartao(card, dadosPessoais);

                PdfPTable dadosViagem = new PdfPTable(1);
                dadosViagem.setWidthPercentage(100);
                adicionarTituloBloco(dadosViagem, "DADOS DA VIAGEM", estilos);
                adicionarCampoRelatorio(dadosViagem, "Lista", linha.lista(), estilos);
                adicionarCampoRelatorio(dadosViagem, "Cidade", pessoa.cidade(), estilos);
                adicionarCampoRelatorio(dadosViagem, "Destino", pessoa.destino(), estilos);
                adicionarCampoRelatorio(dadosViagem, "Horário de chegada",
                        formatarHoraRelatorio(agendamento.horarioChegada()), estilos);
                adicionarBlocoAoCartao(card, dadosViagem);

                PdfPCell veiculoDestaque = new PdfPCell(construirDestaqueVeiculo(veiculo, estilos));
                veiculoDestaque.setColspan(2);
                veiculoDestaque.setPadding(0);
                veiculoDestaque.setBorderColor(new java.awt.Color(218, 226, 232));
                card.addCell(veiculoDestaque);
                documento.add(card);
            }
            documento.close();
            return buffer.toByteArray();
        } catch (DocumentException | IOException e) {
            fecharDocumentoSeguro(documento);
            throw new IllegalStateException("Não foi possível gerar o PDF de passageiros.", e);
        } catch (RuntimeException e) {
            fecharDocumentoSeguro(documento);
            throw e;
        }
    }

    private static String valorRelatorio(String valor) {
        return valor == null || valor.isBlank() ? "Não informado" : valor;
    }

    private static String formatarHoraRelatorio(LocalTime hora) {
        return hora == null ? "Não informado" : HORA_FORMATTER.format(hora);
    }

    private static void adicionarTituloBloco(PdfPTable tabela, String titulo, EstilosPdf estilos) {
        PdfPCell celula = new PdfPCell(new Phrase(titulo, estilos.getTituloBloco()));
        celula.setBackgroundColor(new java.awt.Color(242, 246, 244));
        celula.setBorderColor(new java.awt.Color(218, 226, 232));
        celula.setPadding(5);
        tabela.addCell(celula);
    }

    private static void adicionarCampoRelatorio(
            PdfPTable tabela, String rotulo, String valor, EstilosPdf estilos) {
        Paragraph campo = new Paragraph();
        campo.setLeading(12f);
        campo.add(new Phrase(rotulo + ": ", estilos.getRotuloCampo()));
        campo.add(new Phrase(valorRelatorio(valor), estilos.getTextoNormal()));
        PdfPCell celula = new PdfPCell(campo);
        celula.setBorder(PdfPCell.NO_BORDER);
        celula.setPaddingTop(3);
        celula.setPaddingBottom(3);
        celula.setPaddingLeft(6);
        celula.setPaddingRight(6);
        tabela.addCell(celula);
    }

    private static void adicionarBlocoAoCartao(PdfPTable cartao, PdfPTable bloco) {
        PdfPCell celula = new PdfPCell(bloco);
        celula.setVerticalAlignment(Element.ALIGN_TOP);
        celula.setPadding(5);
        celula.setBorderColor(new java.awt.Color(218, 226, 232));
        cartao.addCell(celula);
    }

    private static PdfPTable construirDestaqueVeiculo(
            VeiculoAbertoResponseDto veiculo, EstilosPdf estilos) {
        boolean pendente = veiculo == null;
        PdfPTable destaque = new PdfPTable(1);
        destaque.setWidthPercentage(100);
        java.awt.Color fundo = pendente
                ? new java.awt.Color(255, 247, 224)
                : new java.awt.Color(230, 244, 236);
        java.awt.Color borda = pendente
                ? new java.awt.Color(235, 205, 126)
                : new java.awt.Color(192, 220, 202);

        PdfPCell rotulo = new PdfPCell(new Phrase(
                pendente ? "VEÍCULO PENDENTE" : "VEÍCULO ATRIBUÍDO", estilos.getRotuloVeiculo()));
        rotulo.setBackgroundColor(fundo);
        rotulo.setBorderColor(borda);
        rotulo.setBorderWidth(0);
        rotulo.setPaddingLeft(8);
        rotulo.setPaddingTop(6);
        rotulo.setPaddingBottom(2);
        destaque.addCell(rotulo);

        String tipo = pendente ? "Pendente" : valorRelatorio(veiculo.nomeTipoVeiculo());
        PdfPCell nomeVeiculo = new PdfPCell(new Phrase(tipo, estilos.getVeiculoDestaque()));
        nomeVeiculo.setBackgroundColor(fundo);
        nomeVeiculo.setBorderColor(borda);
        nomeVeiculo.setBorderWidth(0);
        nomeVeiculo.setPaddingLeft(8);
        nomeVeiculo.setPaddingBottom(6);
        destaque.addCell(nomeVeiculo);

        if (!pendente) {
            StringBuilder informacoes = new StringBuilder("Saída: ")
                    .append(formatarHoraRelatorio(veiculo.horarioSaida()));
            if (veiculo.identificacao() != null && !veiculo.identificacao().isBlank()) {
                informacoes.append("  |  Identificação: ").append(veiculo.identificacao());
            }
            PdfPCell detalheVeiculo = new PdfPCell(new Phrase(informacoes.toString(), estilos.getRotuloCampo()));
            detalheVeiculo.setBackgroundColor(fundo);
            detalheVeiculo.setBorderColor(borda);
            detalheVeiculo.setBorderWidth(0);
            detalheVeiculo.setPaddingLeft(8);
            detalheVeiculo.setPaddingBottom(6);
            destaque.addCell(detalheVeiculo);
        }
        return destaque;
    }
    // ------------------------------------------------------------------
    // RELATÓRIO GERAL (A4 vertical, até oito carros por página)
    // ------------------------------------------------------------------
    public static byte[] gerar(
            LocalDate data,
            String titulo,
            List<VeiculoAbertoResponseDto> veiculos,
            List<AgendamentoResponseDto> passageirosSemVeiculo,
            Map<UUID, PassageiroDetalheView> detalhes) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4, 22, 22, 22, 34);
        try {
            EstilosPdf estilos = new EstilosPdf();
            PdfWriter writer = PdfWriter.getInstance(documento, buffer);
            writer.setPageEvent(new RodapePaginas(estilos.getBaseFont(), "Relatório geral de carros"));
            documento.open();
            Map<UUID, Integer> numerosVeiculos = new HashMap<>();
            for (int i = 0; i < veiculos.size(); i++) {
                numerosVeiculos.put(veiculos.get(i).id(), i + 1);
            }
            Map<FaixaHorario, List<VeiculoAbertoResponseDto>> veiculosPorFaixa = veiculos.stream()
                    .collect(Collectors.groupingBy(
                            FaixaHorario::de,
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
                            "SECRETARIA MUNICIPAL  |  TRANSPORTE DE PASSAGEIROS — PÁGINA " + writer.getPageNumber() + " (" + faixa.getRotulo() + ")",
                            estilos
                    );
                    adicionarLinhaDataOrigem(documento, data, estilos);
                    PdfPTable gridVeiculos = new PdfPTable(2);
                    gridVeiculos.setWidthPercentage(100);
                    gridVeiculos.setSpacingAfter(8);
                    gridVeiculos.setWidths(new float[] {1f, 1f});
                    for (int i = 0; i < MAX_CARROS_POR_PAGINA && indiceVeiculo < totalVeiculosNestaFaixa; i += 2) {
                        VeiculoAbertoResponseDto veiculoEsq = veiculosDaFaixa.get(indiceVeiculo++);
                        int linhasEsq = calcularLinhasNecessarias(veiculoEsq);
                        PdfPTable tabelaEsq = construirCardRelatorio(veiculoEsq, detalhes, estilos, linhasEsq,
                                numerosVeiculos.get(veiculoEsq.id()));
                        PdfPCell celulaEsq = new PdfPCell(tabelaEsq);
                        // Contorno externo do carro: escuro e mais espesso para impressão P&B.
                        celulaEsq.setBorder(PdfPCell.BOX);
                        celulaEsq.setBorderColor(new java.awt.Color(35, 35, 35));
                        celulaEsq.setBorderWidth(1.6f);
                        celulaEsq.setPadding(3f);
                        celulaEsq.setPaddingBottom(4f);
                        gridVeiculos.addCell(celulaEsq);

                        if (indiceVeiculo < totalVeiculosNestaFaixa && i + 1 < MAX_CARROS_POR_PAGINA) {
                            VeiculoAbertoResponseDto veiculoDir = veiculosDaFaixa.get(indiceVeiculo++);
                            int linhasDir = calcularLinhasNecessarias(veiculoDir);
                            PdfPTable tabelaDir = construirCardRelatorio(veiculoDir, detalhes, estilos, linhasDir,
                                    numerosVeiculos.get(veiculoDir.id()));
                            PdfPCell celulaDir = new PdfPCell(tabelaDir);
                            // Mesmo contorno aplicado ao cartão da coluna esquerda.
                            celulaDir.setBorder(PdfPCell.BOX);
                            celulaDir.setBorderColor(new java.awt.Color(35, 35, 35));
                            celulaDir.setBorderWidth(1.6f);
                            celulaDir.setPadding(3f);
                            celulaDir.setPaddingBottom(4f);
                            gridVeiculos.addCell(celulaDir);
                        } else {
                            // Completa apenas a última linha ímpar; não cria cartões vazios.
                            PdfPCell celulaVazia = new PdfPCell(new Phrase(" "));
                            celulaVazia.setBorder(PdfPCell.NO_BORDER);
                            gridVeiculos.addCell(celulaVazia);
                        }
                    }
                    documento.add(gridVeiculos);
                }

                // A folha de endereços vem imediatamente depois de todos os carros desta faixa.
                PdfPTable tabelaBuscas = construirTabelaBuscasResidencia(
                        veiculosDaFaixa, detalhes, estilos, numerosVeiculos);
                if (tabelaBuscas != null) {
                    documento.newPage();

                    Paragraph tituloBuscas = new Paragraph(
                            "BUSCAR NA RESIDÊNCIA — " + faixa.getRotulo(),
                            estilos.getSecao());
                    tituloBuscas.setSpacingAfter(8f);
                    documento.add(tituloBuscas);
                    documento.add(tabelaBuscas);
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

    private static PdfPTable construirCardRelatorio(
            VeiculoAbertoResponseDto veiculo, Map<UUID, PassageiroDetalheView> detalhes,
            EstilosPdf estilos, int linhas, int numero) throws DocumentException {
        if (TipoVeiculoUtil.ehCarro(veiculo.nomeTipoVeiculo())) {
            return construirTabelaVeiculoCard(veiculo, detalhes, estilos, linhas, numero);
        }
        PdfPTable tabela = new PdfPTable(1);
        tabela.setWidthPercentage(100);
        tabela.setKeepTogether(true);
        tabela.addCell(criarCelulaTitulo(nomeVeiculo(veiculo, numero).toUpperCase(), estilos, Element.ALIGN_LEFT));
        adicionarCampoResumoVeiculo(tabela, "SAÍDA", formatarHoraRelatorio(veiculo.horarioSaida()), estilos);
        adicionarCampoResumoVeiculo(tabela, "LUGARES", veiculo.assentosOcupados() + " ocupados de "
                + veiculo.capacidade() + " — " + Math.max(0, veiculo.capacidade() - veiculo.assentosOcupados())
                + " disponíveis", estilos);
        var buscas = veiculo.passageiros() == null ? List.<AgendamentoResponseDto>of()
                : veiculo.passageiros().stream().filter(AgendamentoResponseDto::buscarNaResidencia).toList();
        int acompanhantes = buscas.stream().mapToInt(p -> Math.max(0, p.acompanhantes())).sum();
        adicionarCampoResumoVeiculo(tabela, "BUSCA NA RESIDÊNCIA",
                (buscas.size() + acompanhantes) + " pessoas (" + buscas.size()
                        + (buscas.size() == 1 ? " passageiro e " : " passageiros e ")
                        + acompanhantes + (acompanhantes == 1 ? " acompanhante)" : " acompanhantes)"), estilos);
        String cidades = veiculo.passageiros() == null ? "" : veiculo.passageiros().stream()
                .map(p -> detalheRelatorio(detalhes, p))
                .map(PassageiroDetalheView::cidade)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.joining(", "));
        adicionarCampoResumoVeiculo(tabela, "CIDADES", cidades.isBlank() ? "Não informado" : cidades, estilos);
        return tabela;
    }

    private static void adicionarCampoResumoVeiculo(PdfPTable tabela, String rotulo, String valor, EstilosPdf estilos) {
        Paragraph campo = new Paragraph();
        campo.setLeading(12);
        campo.add(new Phrase(rotulo + "\n", new Font(estilos.getBaseFont(), 8, Font.BOLD,
                new java.awt.Color(0, 75, 50))));
        campo.add(new Phrase(valor, new Font(estilos.getBaseFont(), 9, Font.NORMAL, new java.awt.Color(20, 32, 27))));
        PdfPCell celula = new PdfPCell(campo);
        celula.setBackgroundColor(new java.awt.Color(228, 238, 232));
        celula.setBorderColor(new java.awt.Color(115, 145, 127));
        celula.setPadding(5);
        tabela.addCell(celula);
    }

    private static String nomeVeiculo(VeiculoAbertoResponseDto veiculo, int numero) {
        return valorRelatorio(veiculo.nomeTipoVeiculo())
                + " " + numero
                + (veiculo.identificacao() == null || veiculo.identificacao().isBlank()
                        ? "" : " — " + veiculo.identificacao());
    }

    private static PassageiroDetalheView detalheRelatorio(
            Map<UUID, PassageiroDetalheView> detalhes, AgendamentoResponseDto passageiro) {
        return java.util.Objects.requireNonNull(detalhes.get(passageiro.id()),
                "Detalhes não encontrados para o agendamento " + passageiro.id());
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
            int totalLinhasAlvo,
            int numero) throws DocumentException {
        PdfPTable tabela = new PdfPTable(COLUNAS_CARD_VEICULO);
        tabela.setWidthPercentage(100);
        tabela.setSpacingAfter(2);
        tabela.setHeaderRows(2);
        tabela.setKeepTogether(true);
        PdfPCell tituloVeiculo = criarCelulaTitulo(nomeVeiculo(veiculo, numero), estilos, Element.ALIGN_LEFT);
        tituloVeiculo.setColspan(4);
        tabela.addCell(tituloVeiculo);
        // 1. Cabeçalhos das colunas de passageiros
        for (String cabecalho : List.of("PASSAGEIRO", "DESTINO", "BUSCA", "HORÁRIO")) {
            adicionarCelulaFormatada(tabela, cabecalho, estilos.getTextoCabecalho(), true, 3f);
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
        cellFrete.setPaddingBottom(1f);
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
        celulaContainerRodape.setPadding(3f);
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
        for (String cabecalho : List.of("PASSAGEIRO", "DESTINO", "BUSCA", "HORÁRIO")) {
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
    /**
     * Lista exclusivamente pacientes que precisam ser buscados em casa.
     * A ordem acompanha a sequência dos veículos da respectiva faixa horária.
     */
    private static PdfPTable construirTabelaBuscasResidencia(
            List<VeiculoAbertoResponseDto> veiculos,
            Map<UUID, PassageiroDetalheView> detalhes,
            EstilosPdf estilos,
            Map<UUID, Integer> numerosVeiculos) throws DocumentException {

        PdfPTable tabela = new PdfPTable(new float[] {2f, 3f, 1f, 1.7f});
        tabela.setWidthPercentage(100);
        tabela.setHeaderRows(1);
        tabela.setSplitRows(true);

        adicionarCelulaFormatada(tabela, "PACIENTE", estilos.getTextoCabecalho(), true, 7f);
        adicionarCelulaFormatada(tabela, "ENDEREÇO PARA BUSCAR", estilos.getTextoCabecalho(), true, 7f);
        adicionarCelulaFormatada(tabela, "SAÍDA", estilos.getTextoCabecalho(), true, 7f);
        adicionarCelulaFormatada(tabela, "VEÍCULO", estilos.getTextoCabecalho(), true, 7f);

        int quantidade = 0;

        for (VeiculoAbertoResponseDto veiculo : veiculos) {
            if (veiculo.passageiros() == null || veiculo.passageiros().isEmpty()) {
                continue;
            }

            for (AgendamentoResponseDto passageiro : veiculo.passageiros()) {
                if (!passageiro.buscarNaResidencia()) {
                    continue;
                }

                PassageiroDetalheView detalhe = detalheRelatorio(detalhes, passageiro);

                String nome = detalhe.nome() == null ? "" : detalhe.nome().toUpperCase();
                String endereco = detalhe.endereco() == null || detalhe.endereco().isBlank()
                        ? "ENDEREÇO NÃO INFORMADO"
                        : detalhe.endereco();

                adicionarCelulaFormatada(tabela, nome, estilos.getPassageiroNomeDestaque(), false, 8f);
                adicionarCelulaFormatada(tabela, endereco, estilos.getTextoNormal(), false, 8f);
                adicionarCelulaFormatada(tabela, formatarHoraRelatorio(veiculo.horarioSaida()),
                        estilos.getTextoNormal(), false, 8f);
                adicionarCelulaFormatada(tabela, nomeVeiculo(veiculo, numerosVeiculos.get(veiculo.id())),
                        estilos.getTextoNormal(), false, 8f);
                quantidade++;
            }
        }

        return quantidade == 0 ? null : tabela;
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
        for (String cabecalho : List.of("PASSAGEIRO", "DESTINO", "BUSCA", "HORÁRIO")) {
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
            PassageiroDetalheView d = detalheRelatorio(detalhes, p);
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
            tabela.addCell(criarCelulaConteudoPersonalizada(pPassageiro, estilos, 2.5f));
            Paragraph pDestino = new Paragraph();
            pDestino.setLeading(13.5f);
            pDestino.add(new Phrase(d.cidade().toUpperCase() + "\n", estilos.getCidadeDestaque()));
            pDestino.add(new Phrase(d.destino(), estilos.getTextoNormal()));
            tabela.addCell(criarCelulaConteudoPersonalizada(pDestino, estilos, 2.5f));
            String busca = p.buscarNaResidencia() ? "BUSCAR NA RESIDÊNCIA" : "Não busca";
            adicionarCelulaFormatada(tabela, busca, estilos.getTextoNormal(), false, 2.5f);
            String horarioChegadaStr = p.horarioChegada() != null ? p.horarioChegada().format(HORA_FORMATTER) : "-";
            adicionarCelulaFormatada(tabela, horarioChegadaStr, estilos.getTextoNormal(), false, 2.5f);
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

    public static void adicionarCabecalhoCompartilhado(Document documento, String titulo, String subtitulo)
            throws DocumentException, IOException {
        adicionarCabecalho(documento, titulo, subtitulo, new EstilosPdf());
    }

    public static void adicionarRodapeCompartilhado(PdfWriter writer, String titulo) {
        writer.setPageEvent(new RodapePaginas(new EstilosPdf().getBaseFont(), titulo));
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
        private final Font tituloBloco = new Font(baseFont, 7.5f, Font.BOLD, new java.awt.Color(80, 95, 105));
        private final Font rotuloCampo = new Font(baseFont, 8.2f, Font.BOLD, new java.awt.Color(55, 70, 78));
        private final Font totalDestaque = new Font(baseFont, 11f, Font.BOLD, new java.awt.Color(0, 92, 63));
        private final Font rotuloVeiculo = new Font(baseFont, 7.5f, Font.BOLD, new java.awt.Color(0, 92, 63));
        private final Font veiculoDestaque = new Font(baseFont, 12f, Font.BOLD, new java.awt.Color(0, 70, 45));
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
        public Font getTituloBloco() { return tituloBloco; }
        public Font getRotuloCampo() { return rotuloCampo; }
        public Font getTotalDestaque() { return totalDestaque; }
        public Font getRotuloVeiculo() { return rotuloVeiculo; }
        public Font getVeiculoDestaque() { return veiculoDestaque; }
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
        private final String tituloRelatorio;
        private PdfTemplate totalPaginasTemplate;
        private RodapePaginas(BaseFont baseFont, String tituloRelatorio) {
            this.baseFont = baseFont;
            this.tituloRelatorio = tituloRelatorio;
        }
        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            totalPaginasTemplate = writer.getDirectContent().createTemplate(30, 12);
        }
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            String textoRodape = tituloRelatorio + " — Transporte de Passageiros   Página "
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