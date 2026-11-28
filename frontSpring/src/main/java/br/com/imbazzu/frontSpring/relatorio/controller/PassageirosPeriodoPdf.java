package br.com.imbazzu.frontSpring.relatorio.controller;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import br.com.imbazzu.frontSpring.agendamento.adpter.out.pdf.AgendamentoPdfService;

final class PassageirosPeriodoPdf {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final Color VERDE = new Color(0, 92, 63);
    private static final Color VERDE_CLARO = new Color(234, 245, 238);
    private static final Color BORDA = new Color(218, 226, 232);
    private static final Color TEXTO = new Color(38, 48, 56);
    private static final Font SECAO = new Font(Font.HELVETICA, 10.5f, Font.BOLD, VERDE);
    private static final Font ROTULO = new Font(Font.HELVETICA, 8, Font.BOLD, new Color(55, 70, 78));
    private static final Font NORMAL = new Font(Font.HELVETICA, 8.5f, Font.NORMAL, TEXTO);
    private static final Font CABECALHO = new Font(Font.HELVETICA, 8, Font.BOLD, Color.WHITE);

    private PassageirosPeriodoPdf() {}

    static byte[] gerar(LocalDate inicio, LocalDate fim, List<String> filtros, List<PassageiroPeriodo> viagens) {
        var buffer = new ByteArrayOutputStream();
        var documento = new Document(PageSize.A4.rotate(), 28, 28, 28, 38);
        try {
            var writer = PdfWriter.getInstance(documento, buffer);
            AgendamentoPdfService.adicionarRodapeCompartilhado(writer, "Passageiros por período");
            documento.open();
            AgendamentoPdfService.adicionarCabecalhoCompartilhado(documento, "Passageiros por período",
                    "SECRETARIA MUNICIPAL  |  TRANSPORTE DE PASSAGEIROS");

            var periodo = new Paragraph("PERÍODO: " + DATA.format(inicio) + " a " + DATA.format(fim), SECAO);
            periodo.setSpacingBefore(7);
            periodo.setSpacingAfter(5);
            documento.add(periodo);
            var aviso = new Paragraph("Agendamentos não representam confirmação de viagem realizada. "
                    + "A saída e o motorista são os atualmente atribuídos ao veículo na agenda.", NORMAL);
            aviso.setSpacingAfter(7);
            documento.add(aviso);

            var filtrosTabela = new PdfPTable(2);
            filtrosTabela.setWidthPercentage(100);
            filtrosTabela.setSpacingAfter(10);
            for (String filtro : filtros) {
                var celula = celula(filtro, NORMAL, new Color(247, 249, 248));
                celula.setPadding(6);
                filtrosTabela.addCell(celula);
            }
            documento.add(filtrosTabela);

            var porPessoa = viagens.stream().collect(Collectors.groupingBy(
                    PassageiroPeriodo::idPessoa, LinkedHashMap::new, Collectors.toList()));
            var resumo = new PdfPTable(2);
            resumo.setWidthPercentage(100);
            resumo.setSpacingAfter(12);
            resumo.addCell(celula("PASSAGEIROS: " + porPessoa.size(), SECAO, VERDE_CLARO));
            resumo.addCell(celula("AGENDAMENTOS: " + viagens.size(), SECAO, VERDE_CLARO));
            documento.add(resumo);

            if (viagens.isEmpty()) {
                documento.add(new Paragraph("Nenhum agendamento encontrado.", NORMAL));
            }
            for (var grupo : porPessoa.values()) {
                adicionarPassageiro(documento, grupo);
            }
            documento.close();
            return buffer.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new IllegalStateException("Não foi possível gerar o PDF de passageiros.", e);
        } finally {
            if (documento.isOpen()) documento.close();
        }
    }

    private static void adicionarPassageiro(Document documento, List<PassageiroPeriodo> grupo)
            throws DocumentException {
        var pessoa = grupo.getFirst();
        var tabela = new PdfPTable(new float[] {1.0f, 1.2f, 1.1f, 1.3f, 1.1f, 1.3f, 0.8f, 1.4f, 0.7f});
        tabela.setWidthPercentage(100);
        tabela.setSpacingBefore(4);
        tabela.setSpacingAfter(14);
        tabela.setHeaderRows(3);
        tabela.setSplitLate(false);

        var nome = celula(texto(pessoa.nome()), SECAO, VERDE_CLARO);
        nome.setColspan(9);
        nome.setPadding(9);
        tabela.addCell(nome);

        var detalhes = new Paragraph();
        detalhes.setLeading(12);
        detalhes.add(new Phrase("CPF: ", ROTULO));
        detalhes.add(new Phrase(texto(pessoa.cpf()) + "    ", NORMAL));
        detalhes.add(new Phrase("Telefone: ", ROTULO));
        detalhes.add(new Phrase(texto(pessoa.telefone()) + "    ", NORMAL));
        detalhes.add(new Phrase("Especificação: ", ROTULO));
        detalhes.add(new Phrase(texto(pessoa.especificacao()), NORMAL));
        var dados = new PdfPCell(detalhes);
        dados.setColspan(9);
        dados.setBorderColor(BORDA);
        dados.setPadding(9);
        dados.setPaddingBottom(11);
        tabela.addCell(dados);

        for (String titulo : List.of("Data", "Lista", "Cidade", "Destino", "Veículo", "Motorista", "Chegada", "Saída programada", "Acomp.")) {
            var cabecalho = celula(titulo, CABECALHO, VERDE);
            cabecalho.setBorderColor(VERDE);
            tabela.addCell(cabecalho);
        }
        int indice = 0;
        for (var viagem : grupo) {
            var fundo = indice++ % 2 == 0 ? Color.WHITE : new Color(247, 250, 248);
            String saida = viagem.horarioSaida() == null ? "Não informado" : HORA.format(viagem.horarioSaida());
            for (String valor : List.of(DATA.format(viagem.data()), texto(viagem.lista()), texto(viagem.cidade()),
                    texto(viagem.destino()), texto(viagem.tipoVeiculo()),
                    viagem.idVeiculo() == null ? "Veículo pendente" : motorista(viagem.motorista()),
                    HORA.format(viagem.horarioChegada()),
                    saida, Integer.toString(viagem.acompanhantes()))) {
                tabela.addCell(celula(valor, NORMAL, fundo));
            }
        }
        documento.add(tabela);
    }

    private static PdfPCell celula(String texto, Font fonte, Color fundo) {
        var celula = new PdfPCell(new Phrase(texto, fonte));
        celula.setBackgroundColor(fundo);
        celula.setBorderColor(BORDA);
        celula.setPadding(7);
        celula.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return celula;
    }

    private static String texto(String valor) {
        return valor == null || valor.isBlank() ? "Não informado" : valor;
    }

    private static String motorista(String valor) {
        return valor == null || valor.isBlank() ? "Não atribuído" : valor;
    }
}
