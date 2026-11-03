package br.com.imbazzu.frontSpring.agendamento.adpter.out.pdf;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.AgendamentoController.PassageiroDetalheView;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;

public final class AgendamentoPdfService {

    private AgendamentoPdfService() {
    }

    public static byte[] gerar(
            LocalDate data,
            String titulo,
            List<VeiculoAbertoResponseDto> veiculos,
            List<AgendamentoResponseDto> passageirosSemVeiculo,
            Map<UUID, PassageiroDetalheView> detalhes) {
        var buffer = new ByteArrayOutputStream();
        var documento = new Document(PageSize.A4.rotate(), 28, 28, 28, 28);
        try {
            PdfWriter.getInstance(documento, buffer);
            documento.open();
            var estilos = new EstilosPdf();
            adicionarCabecalho(documento, titulo, data, estilos);
            documento.add(new Paragraph(" "));
            if (veiculos.isEmpty()) {
                documento.add(new Paragraph("Nenhum veículo incluído neste relatório.", estilos.texto()));
            }
            for (var veiculo : veiculos) {
                documento.add(new Paragraph(
                        veiculo.nomeTipoVeiculo()
                                + (veiculo.identificacao() == null ? "" : " — " + veiculo.identificacao()),
                        estilos.secao()));
                documento.add(new Paragraph(
                        "Ocupação: %d/%d lugares    |    Saída: %s".formatted(
                                veiculo.assentosOcupados(),
                                veiculo.capacidade(),
                                veiculo.horarioSaida() == null ? "a definir" : veiculo.horarioSaida()),
                        estilos.texto()));
                documento.add(new Paragraph(" "));
                documento.add(tabelaPassageiros(veiculo.passageiros(), detalhes, estilos));
                documento.add(new Paragraph(" "));
            }
            if (!passageirosSemVeiculo.isEmpty()) {
                documento.add(new Paragraph("Passageiros aguardando atribuição", estilos.secao()));
                documento.add(new Paragraph(" "));
                documento.add(tabelaPassageiros(passageirosSemVeiculo, detalhes, estilos));
            }
            documento.close();
            return buffer.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            if (documento.isOpen()) {
                documento.close();
            }
            throw new IllegalStateException("Não foi possível gerar o PDF da agenda.", e);
        } catch (RuntimeException e) {
            if (documento.isOpen()) {
                documento.close();
            }
            throw e;
        }
    }

    public static byte[] gerarIndividual(
            LocalDate data,
            VeiculoAbertoResponseDto veiculo,
            Map<UUID, PassageiroDetalheView> detalhes) {
        var buffer = new ByteArrayOutputStream();
        var documento = new Document(PageSize.A4, 36, 36, 30, 30);
        try {
            PdfWriter.getInstance(documento, buffer);
            documento.open();
            var estilos = new EstilosPdf();
            var vias = List.of("VIA DO MOTORISTA", "VIA DA SECRETARIA");
            for (int indice = 0; indice < vias.size(); indice++) {
                if (indice > 0) {
                    documento.newPage();
                }
                var via = vias.get(indice);
                adicionarCabecalho(
                        documento,
                        veiculo.nomeTipoVeiculo()
                                + (veiculo.identificacao() == null ? "" : " — " + veiculo.identificacao()),
                        data,
                        estilos);
                var viaTexto = new Paragraph(via, estilos.via());
                viaTexto.setAlignment(Element.ALIGN_RIGHT);
                documento.add(viaTexto);
                documento.add(new Paragraph(" "));

                var dadosVeiculo = new PdfPTable(new float[] {1.1f, 1.3f, 1.1f, 1.5f});
                dadosVeiculo.setWidthPercentage(100);
                dadosVeiculo.setSpacingAfter(10);
                adicionarCelula(dadosVeiculo, "Tipo", estilos.textoCabecalho(), true);
                adicionarCelula(dadosVeiculo, veiculo.nomeTipoVeiculo(), estilos.texto(), false);
                adicionarCelula(dadosVeiculo, "Data", estilos.textoCabecalho(), true);
                adicionarCelula(dadosVeiculo, data.toString(), estilos.texto(), false);
                adicionarCelula(dadosVeiculo, "Identificação", estilos.textoCabecalho(), true);
                adicionarCelula(dadosVeiculo, veiculo.identificacao(), estilos.texto(), false);
                adicionarCelula(dadosVeiculo, "Placa", estilos.textoCabecalho(), true);
                adicionarCelula(dadosVeiculo, "____________________", estilos.texto(), false);
                adicionarCelula(dadosVeiculo, "Ocupação", estilos.textoCabecalho(), true);
                adicionarCelula(
                        dadosVeiculo,
                        "%d de %d lugares".formatted(veiculo.assentosOcupados(), veiculo.capacidade()),
                        estilos.texto(),
                        false);
                adicionarCelula(dadosVeiculo, "Saída", estilos.textoCabecalho(), true);
                adicionarCelula(
                        dadosVeiculo,
                        veiculo.horarioSaida() == null ? "A definir" : veiculo.horarioSaida().toString(),
                        estilos.texto(),
                        false);
                documento.add(dadosVeiculo);
                documento.add(new Paragraph("Relação de passageiros", estilos.secao()));
                documento.add(new Paragraph(" "));
                documento.add(tabelaPassageirosIndividual(veiculo.passageiros(), detalhes, estilos));
                documento.add(new Paragraph(" "));

                var assinatura = new Paragraph(
                        "Assinatura do motorista: ______________________________________________",
                        estilos.assinatura());
                assinatura.setSpacingBefore(14);
                documento.add(assinatura);
                documento.add(new Paragraph(
                        "Conferência da secretaria: ______________________________________________",
                        estilos.assinatura()));
            }
            documento.close();
            return buffer.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            if (documento.isOpen()) {
                documento.close();
            }
            throw new IllegalStateException("Não foi possível gerar o PDF do veículo.", e);
        } catch (RuntimeException e) {
            if (documento.isOpen()) {
                documento.close();
            }
            throw e;
        }
    }

    private static PdfPTable tabelaPassageiros(
            List<AgendamentoResponseDto> passageiros,
            Map<UUID, PassageiroDetalheView> detalhes,
            EstilosPdf estilos) throws DocumentException {
        var tabela = new PdfPTable(new float[] {2.4f, 1.5f, 1.4f, 2.4f, 1.2f, 1.1f});
        tabela.setWidthPercentage(100);
        tabela.setHeaderRows(1);
        for (var cabecalho : List.of(
                "Passageiro / telefone", "Busca", "Cidade / destino", "Endereço de busca",
                "Chegada", "Qtd. acompanhantes")) {
            adicionarCelula(tabela, cabecalho, estilos.textoCabecalho(), true);
        }
        for (var passageiro : passageiros) {
            var detalhe = detalhes.get(passageiro.idPessoa());
            adicionarCelula(
                    tabela, detalhe.nome() + "\n" + detalhe.telefone(), estilos.texto(), false);
            adicionarCelula(tabela, passageiro.buscarNaResidencia() ? "Residência" : "Não", estilos.texto(), false);
            adicionarCelula(tabela, detalhe.cidade() + "\n" + detalhe.destino(), estilos.texto(), false);
            adicionarCelula(
                    tabela,
                    passageiro.buscarNaResidencia()
                            ? (detalhe.endereco() == null || detalhe.endereco().isBlank()
                                    ? "Endereço não informado"
                                    : detalhe.endereco())
                            : "—",
                    estilos.texto(),
                    false);
            adicionarCelula(tabela, passageiro.horarioChegada().toString(), estilos.texto(), false);
            adicionarCelula(tabela, Integer.toString(passageiro.acompanhantes()), estilos.texto(), false);
        }
        return tabela;
    }

    private static PdfPTable tabelaPassageirosIndividual(
            List<AgendamentoResponseDto> passageiros,
            Map<UUID, PassageiroDetalheView> detalhes,
            EstilosPdf estilos) throws DocumentException {
        var tabela = new PdfPTable(new float[] {2.1f, 2.2f, 2.3f, 0.8f});
        tabela.setWidthPercentage(100);
        tabela.setHeaderRows(1);
        tabela.setSplitRows(true);
        for (var cabecalho : List.of(
                "Passageiro / telefone", "Cidade / destino / chegada", "Busca / endereço", "Acomp.")) {
            adicionarCelula(tabela, cabecalho, estilos.textoCabecalho(), true);
        }
        for (var passageiro : passageiros) {
            var detalhe = detalhes.get(passageiro.idPessoa());
            adicionarCelula(tabela, detalhe.nome() + "\n" + detalhe.telefone(), estilos.texto(), false);
            adicionarCelula(
                    tabela,
                    detalhe.cidade() + "\n" + detalhe.destino() + "\nChegada: " + passageiro.horarioChegada(),
                    estilos.texto(),
                    false);
            adicionarCelula(
                    tabela,
                    passageiro.buscarNaResidencia()
                            ? "Residência\n" + (detalhe.endereco() == null || detalhe.endereco().isBlank()
                                    ? "Endereço não informado"
                                    : detalhe.endereco())
                            : "Sem busca",
                    estilos.texto(),
                    false);
            adicionarCelula(tabela, Integer.toString(passageiro.acompanhantes()), estilos.texto(), false);
        }
        if (passageiros.isEmpty()) {
            var celula = new PdfPCell(new Phrase("Nenhum passageiro atribuído.", estilos.texto()));
            celula.setColspan(4);
            celula.setPadding(8);
            tabela.addCell(celula);
        }
        return tabela;
    }

    private static void adicionarCabecalho(Document documento, String titulo, LocalDate data, EstilosPdf estilos)
            throws DocumentException, java.io.IOException {
        var cabecalho = new PdfPTable(new float[] {1.2f, 5f});
        cabecalho.setWidthPercentage(100);
        cabecalho.setSpacingAfter(12);
        var logo = Image.getInstance(
                AgendamentoPdfService.class.getResource("/static/images/logo.png"));
        logo.scaleToFit(100, 44);
        var celulaLogo = new PdfPCell(logo, true);
        celulaLogo.setBorder(PdfPCell.NO_BORDER);
        celulaLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cabecalho.addCell(celulaLogo);

        var texto = new PdfPTable(1);
        texto.setWidthPercentage(100);
        var tituloCell = new PdfPCell(new Phrase(titulo, estilos.titulo()));
        tituloCell.setBorder(PdfPCell.NO_BORDER);
        tituloCell.setPaddingBottom(4);
        texto.addCell(tituloCell);
        var subtitulo = new PdfPCell(new Phrase(
                "SECRETARIA MUNICIPAL  |  TRANSPORTE DE PASSAGEIROS  |  " + data,
                estilos.subtitulo()));
        subtitulo.setBorder(PdfPCell.NO_BORDER);
        texto.addCell(subtitulo);
        var textoCelula = new PdfPCell(texto);
        textoCelula.setBorder(PdfPCell.NO_BORDER);
        textoCelula.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cabecalho.addCell(textoCelula);
        documento.add(cabecalho);

        var faixa = new PdfPTable(1);
        faixa.setWidthPercentage(100);
        var faixaCelula = new PdfPCell(new Phrase(" "));
        faixaCelula.setFixedHeight(4);
        faixaCelula.setBorder(PdfPCell.NO_BORDER);
        faixaCelula.setBackgroundColor(new java.awt.Color(0, 130, 75));
        faixa.addCell(faixaCelula);
        faixa.setSpacingAfter(10);
        documento.add(faixa);
    }

    private static void adicionarCelula(PdfPTable tabela, String texto, Font fonte, boolean cabecalho) {
        var celula = new PdfPCell(new Phrase(texto == null ? "" : texto, fonte));
        celula.setPadding(6);
        celula.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celula.setBorderColor(new java.awt.Color(215, 223, 229));
        if (cabecalho) {
            celula.setBackgroundColor(new java.awt.Color(0, 105, 75));
            celula.setBorderColor(new java.awt.Color(0, 105, 75));
        }
        tabela.addCell(celula);
    }

    private static final class EstilosPdf {
        private final BaseFont base = criarBaseFont();
        private final Font titulo = new Font(base, 17, Font.BOLD, new java.awt.Color(0, 92, 63));
        private final Font subtitulo = new Font(base, 8, Font.BOLD, new java.awt.Color(80, 95, 105));
        private final Font secao = new Font(base, 12, Font.BOLD, new java.awt.Color(0, 92, 63));
        private final Font texto = new Font(base, 8, Font.NORMAL, new java.awt.Color(38, 48, 56));
        private final Font textoCabecalho = new Font(base, 8, Font.BOLD, java.awt.Color.WHITE);
        private final Font via = new Font(base, 9, Font.BOLD, new java.awt.Color(0, 105, 75));
        private final Font assinatura = new Font(base, 9, Font.NORMAL, new java.awt.Color(38, 48, 56));

        private static BaseFont criarBaseFont() {
            try {
                return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            } catch (java.io.IOException | DocumentException e) {
                throw new IllegalStateException("Não foi possível carregar a fonte do PDF.", e);
            }
        }

        Font titulo() { return titulo; }
        Font subtitulo() { return subtitulo; }
        Font secao() { return secao; }
        Font texto() { return texto; }
        Font textoCabecalho() { return textoCabecalho; }
        Font via() { return via; }
        Font assinatura() { return assinatura; }
    }
}
