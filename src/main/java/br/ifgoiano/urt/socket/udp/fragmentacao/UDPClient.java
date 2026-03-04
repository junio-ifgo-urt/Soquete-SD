package br.ifgoiano.urt.socket.udp.fragmentacao;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/*
 * Exemplo que mostra como um cliente pode fragmentar uma mensagem grande 
 * maior do que o buffer em vários fragmentos de pacotes separadas
 * * @author Prof. Junio Lima
 * @since  2025-03-17
 * @version 1.0
 */
public class UDPClient {
    private static final int BUFFER_SIZE = 10;//24; // Tamanho do buffer
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9876;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "Uma mensagem muito grande que precisa ser fragmentada...";
            byte[] messageBytes = message.getBytes();

            // Fragmenta a mensagem em pacotes menores
            int offset = 0; // Inicializa o offset como 0
            while (offset < messageBytes.length) { // Enquanto houver bytes restantes na mensagem
                // Calcula o tamanho do fragmento atual
                int length = Math.min(BUFFER_SIZE, messageBytes.length - offset);
                
                // Cria um array para armazenar o fragmento
                byte[] fragment = new byte[length];
                
                // Copia os bytes da mensagem original para o fragmento
                System.arraycopy(messageBytes, offset, fragment, 0, length);
                
                // Exibe o fragmento (convertendo bytes para String para visualização)
                System.out.println("Pedaço: " + new String(fragment, StandardCharsets.UTF_8));
                
                // Cria um DatagramPacket com o fragmento
                DatagramPacket packet = new DatagramPacket(fragment, fragment.length, InetAddress.getByName(SERVER_IP), SERVER_PORT);
                
                // Envia o fragmento
                socket.send(packet);
                
                // Atualiza o offset para o próximo fragmento
                offset += length;
            }

            System.out.println("Mensagem enviada com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
