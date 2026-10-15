import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Main {

    public static void main(String[] args) {
        File pastaOrigem = new File("C:\\Users\\Rober\\Downloads\\Nova pasta (2)\\WhatsApp Unknown 2026-09-17 at 21.41.30");
        File pastaDestino = new File("C:\\Users\\Rober\\Downloads\\Nova pasta (2)");

        // Cria a pasta de destino se não existir
        if (!pastaDestino.exists()) {
            pastaDestino.mkdirs();
        }

        // Define a data a ser impressa nas fotos (formato: DD/MM/AAAA)
        String textoData =  LocalDate.of(2026,3,26).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // Filtra arquivos por extensão de imagem
        File[] fotos = pastaOrigem.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".jpg") ||
                        name.toLowerCase().endsWith(".jpeg") ||
                        name.toLowerCase().endsWith(".png")
        );

        if (fotos != null) {
            for (File foto : fotos) {
                try {
                    File arquivoSaida = new File(pastaDestino, foto.getName());
                    adicionarDataNaFoto(foto, arquivoSaida, textoData);
                    System.out.println("Processada: " + foto.getName());
                } catch (IOException e) {
                    System.err.println("Erro ao processar " + foto.getName() + ": " + e.getMessage());
                }
            }
        }
    }
    private static void adicionarDataNaFoto(File fotoOrigem, File fotoDestino, String textoData) throws IOException {
        BufferedImage imagem = ImageIO.read(fotoOrigem); //
        Graphics2D g2d = imagem.createGraphics(); //

        // Melhora a qualidade da renderização do texto
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Define a fonte (escala o tamanho com base na altura da imagem)
        int tamanhoFonte = Math.max(16, imagem.getHeight() / 30);
        Font fonte = new Font("Arial", Font.BOLD, tamanhoFonte);
        g2d.setFont(fonte); //

        // Calcula a posição do texto no canto inferior direito
        FontMetrics metrics = g2d.getFontMetrics(fonte);
        int larguraTexto = metrics.stringWidth(textoData);
        int alturaTexto = metrics.getHeight();

        int margem = 20;
        int x = imagem.getWidth() - larguraTexto - margem;
        int y = imagem.getHeight() - margem;

        // Desenha uma leve sombra preta para dar contraste em fundos claros
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(textoData, x + 2, y + 2);

        // Desenha a data em cor amarela (estilo clássico de câmera) ou branca
        g2d.setColor(new Color(255, 165, 0)); // Laranja/Amarelo clássico
        g2d.drawString(textoData, x, y); //

        g2d.dispose(); // Libera os recursos gráficos

        // Descobre a extensão do arquivo original para salvar no mesmo formato
        String nomeArquivo = fotoOrigem.getName();
        String formato = nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1);

        ImageIO.write(imagem, formato, fotoDestino); //
    }
}
