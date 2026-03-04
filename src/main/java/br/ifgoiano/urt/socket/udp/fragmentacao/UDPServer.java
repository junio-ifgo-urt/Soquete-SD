package br.ifgoiano.urt.socket.udp.fragmentacao;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/*
 * Exemplo que mostra como um servidor precisa fazer para reconstruir uma mensagem grande 
 * maior do que o buffer, onde o cliente enviou vários fragmentos de pacotes separadas
 * O servidor precisa saber quantos pacotes recebeu
 * @author Prof. Junio Lima
 * @since  2025-03-17
 * @version 1.0
 */
public class UDPServer {
    private static final int BUFFER_SIZE = 10;//24; // Tamanho do buffer
    private static final int SERVER_PORT = 9876;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(SERVER_PORT)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            StringBuilder messageBuilder = new StringBuilder();

            while (true) {
                // Recebe um pacote
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                // Adiciona o fragmento à mensagem
                String fragment = new String(packet.getData(), 0, packet.getLength());
                messageBuilder.append(fragment);

                // Verifica se a mensagem está completa (isso depende da lógica de fragmentação)
                if (fragment.length() < BUFFER_SIZE) {
                    break; // Supõe que o último pacote foi recebido
                }
            }

            System.out.println("Mensagem recebida: " + messageBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
