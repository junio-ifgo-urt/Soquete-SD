package br.ifgoiano.urt.socket.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * RESUMO: Cliente UDP que solicita a hora atual de uma região específica.
 * Demonstra a comunicação baseada em datagramas (não orientada à conexão).
 * * FUNCIONAMENTO:
 * 1. O cliente cria um DatagramSocket sem especificar porta local (porta efêmera).
 * 2. Encapsula a mensagem (região) em um DatagramPacket com o IP e porta do servidor.
 * 3. Utiliza o método send() para disparar o pacote (operação não bloqueante de rede).
 * 4. Utiliza o método receive() para aguardar a resposta (operação bloqueante).
 * 5. Implementa um timeout (5 s) para evitar bloqueio indefinido, tratando a falha de omissão.
 * 6. Caso o servidor não responda, captura a SocketTimeoutException.
 * Garante a integridade dos caracteres (UTF-8).
 * * @author Prof. Junio Lima
 * @since  2025-05-22
 * @version 1.0
 */
public class UDPClienteRelogio {
    private static final String SERVIDOR_IP = "localhost";
    private static final int PORTA = 9876;
    private static final int TIMEOUT_MS = 5000; // 5 segundos

    public static void main(String[] args) {
        System.out.println("---> Cliente UDP iniciado <---");

        // Uso do try-with-resources para garantir o fechamento do socket
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            
            // Define o tempo máximo de espera pelo receive()
            clientSocket.setSoTimeout(TIMEOUT_MS);

            InetAddress serverAddress = InetAddress.getByName(SERVIDOR_IP);
            
            // Preparação dos dados
            //String region = "America/Sao_Paulo".trim();
            //String region = "America/New_York".trim();
            String region = "Europe/Paris".trim();
            byte[] sendBuffer = region.getBytes(StandardCharsets.UTF_8);
            
            // Envio do Datagrama
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, PORTA);
            System.out.println("Enviando requisição para: " + region);
            clientSocket.send(sendPacket);

            // Preparação para receber a resposta
            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            try {
                // O programa ficará bloqueado aqui por no máximo 5 segundos
                clientSocket.receive(receivePacket);

                String serverResponse = new String(
                    receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8
                );
                System.out.println("Resposta do servidor: " + serverResponse);

            } catch (SocketTimeoutException e) {
                // Tratamento de falha de omissão (servidor não respondeu dentro do tempo limite)
                System.err.println("[ERRO] O servidor não respondeu dentro de " + (TIMEOUT_MS/1000) + " segundos.");
                System.err.println("Possíveis causas: Servidor offline ou congestionamento na rede.");
            }

        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("---> Cliente finalizado <---");
    }
}