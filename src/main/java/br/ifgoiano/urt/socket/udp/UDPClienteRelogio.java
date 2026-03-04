package br.ifgoiano.urt.socket.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * RESUMO: Cliente UDP que solicita a hora atual de uma região específica.
 * Demonstra a comunicação baseada em datagramas (não orientada à conexão).
 * * FUNCIONAMENTO:
 * 1. O cliente cria um DatagramSocket sem especificar porta local (porta efêmera).
 * 2. Encapsula a mensagem (região) em um DatagramPacket com o IP e porta do servidor.
 * 3. Utiliza o método send() para disparar o pacote (operação não bloqueante de rede).
 * 4. Utiliza o método receive() para aguardar a resposta (operação bloqueante).
 * * @author Prof. Junio Lima
 * @since  2025-05-22
 * @version 1.0
 */
public class UDPClienteRelogio {
	private static final int PORTA = 9876;
	
    public static void main(String[] args) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("localhost"); //127.0.0.1
            //InetAddress serverAddress = InetAddress.getByName("172.217.30.228");
            //byte[] sendBuffer = "America/New_York".getBytes(); // Exemplo de região solicitada
            //byte[] sendBuffer = "Europe/Paris".getBytes(); // Exemplo de região solicitada
            byte[] sendBuffer = "America/Sao_Paulo".getBytes(); // Exemplo de região solicitada
            byte[] receiveBuffer = new byte[1024];

            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, 
            		                                       serverAddress, PORTA);
            clientSocket.send(sendPacket);

            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            clientSocket.receive(receivePacket);

            String serverResponse = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Resposta do servidor: " + serverResponse);

            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

