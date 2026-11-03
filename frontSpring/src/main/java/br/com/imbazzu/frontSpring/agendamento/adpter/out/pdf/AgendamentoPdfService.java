package br.com.imbazzu.frontSpring.agendamento.adpter.out.pdf;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
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
            var baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            var tituloFont = new Font(baseFont, 16, Font.BOLD);
            var secaoFont = new Font(baseFont, 12, Font.BOLD);
            var textoFont = new Font(baseFont, 8);
            documento.add(new Paragraph(titulo, tituloFont));
            documento.add(new Paragraph("Data: " + data, textoFont));
            documento.add(new Paragraph(" "));
            if (veiculos.isEmpty()) {
                documento.add(new Paragraph("Nenhum veículo incluído neste relatório.", textoFont));
            }
            for (var veiculo : veiculos) {
                documento.add(new Paragraph(
                        veiculo.nomeTipoVeiculo()
                                + (veiculo.identificacao() == null ? "" : " — " + veiculo.identificacao()),
                        secaoFont));
                documento.add(new Paragraph(
                        "Ocupação: %d/%d lugares · Saída: %s".formatted(
                                veiculo.assentosOcupados(),
                                veiculo.capacidade(),
                                veiculo.horarioSaida() == null ? "a definir" : veiculo.horarioSaida()),
                        textoFont));
                documento.add(new Paragraph(" "));
                documento.add(tabelaPassageiros(veiculo.passageiros(), detalhes, textoFont));
                documento.add(new Paragraph(" "));
            }
            if (!passageirosSemVeiculo.isEmpty()) {
                documento.add(new Paragraph("Passageiros aguardando atribuição", secaoFont));
                documento.add(new Paragraph(" "));
                documento.add(tabelaPassageiros(passageirosSemVeiculo, detalhes, textoFont));
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

    private static PdfPTable tabelaPassageiros(
            List<AgendamentoResponseDto> passageiros,
            Map<UUID, PassageiroDetalheView> detalhes,
            Font fonte) throws DocumentException {
        var tabela = new PdfPTable(new float[] {2.4f, 1.5f, 1.4f, 2.4f, 1.2f, 1.1f, 1.1f});
        tabela.setWidthPercentage(100);
        tabela.setHeaderRows(1);
        for (var cabecalho : List.of(
                "Passageiro / telefone", "Busca", "Cidade / destino", "Endereço de busca",
                "Chegada", "Acompanhantes", "Saída necessária")) {
            adicionarCelula(tabela, cabecalho, fonte, true);
        }
        for (var passageiro : passageiros) {
            var detalhe = detalhes.get(passageiro.idPessoa());
            adicionarCelula(
                    tabela, detalhe.nome() + "\n" + detalhe.telefone(), fonte, false);
            adicionarCelula(tabela, passageiro.buscarNaResidencia() ? "Residência" : "Não", fonte, false);
            adicionarCelula(tabela, detalhe.cidade() + "\n" + detalhe.destino(), fonte, false);
            adicionarCelula(
                    tabela,
                    passageiro.buscarNaResidencia()
                            ? (detalhe.endereco() == null || detalhe.endereco().isBlank()
                                    ? "Endereço não informado"
                                    : detalhe.endereco())
                            : "—",
                    fonte,
                    false);
            adicionarCelula(tabela, passageiro.horarioChegada().toString(), fonte, false);
            adicionarCelula(tabela, Integer.toString(passageiro.acompanhantes()), fonte, false);
            adicionarCelula(tabela, passageiro.horarioSaidaNecessario().toString(), fonte, false);
        }
        return tabela;
    }

    private static void adicionarCelula(PdfPTable tabela, String texto, Font fonte, boolean cabecalho) {
        var celula = new PdfPCell(new Phrase(texto == null ? "" : texto, fonte));
        celula.setPadding(5);
        if (cabecalho) {
            celula.setBackgroundColor(new java.awt.Color(232, 237, 243));
        }
        tabela.addCell(celula);
    }
}
