package br.ifgoiano.urt.socket.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * RESUMO: Servidor de tempo utilizando o protocolo UDP (User Datagram Protocol).
 * Esta classe demonstra a comunicação não orientada à conexão, onde cada
 * requisição é tratada como um datagrama independente.
 * * FUNCIONAMENTO: 
 * 1. O servidor aguarda a chegada de um DatagramPacket contendo o ID de uma região.
 * 2. Extrai o endereço e a porta do remetente diretamente do pacote recebido.
 * 3. Envia uma resposta de volta para a origem sem estabelecer uma sessão persistente.
 * * @author Prof. Junio Lima
 * @since  2025-05-22
 * @version 1.0
 */
public class UDPServidorRelogio {
	private static final int PORTA = 9876;
	
    public static void main(String[] args) {
    	DatagramSocket serverSocket = null;
    	try {
            serverSocket = new DatagramSocket(PORTA);
            byte[] receiveBuffer = new byte[1024];
            byte[] sendBuffer;
            
            // Obtém o endereço IP do servidor
            InetAddress serverAddress = InetAddress.getLocalHost();
            String serverIP = serverAddress.getHostAddress();

            System.out.println("\n---> Iniciando o servidor ... <---\n");
            System.out.println("Servidor esperando conexões no endereço "+ serverIP + 
            						" na porta " + PORTA + "...\n");
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(receivePacket);

                String clientMessage = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
                System.out.println("\nRegião solicitada pelo cliente: " + clientMessage);
                System.out.println("Endereço do cliente: " + receivePacket.getAddress());

                // Processar a região solicitada e obter a hora atual
                String serverResponse;
                try {
                    ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.of(clientMessage));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
                    serverResponse = "Hora atual na regiao " + clientMessage + ": " + dateTime.format(formatter);
                } catch (Exception e) {
                    serverResponse = "Regiao invalida: " + clientMessage;
                }

                sendBuffer = serverResponse.getBytes();
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);
            }// end while
    	} // end try
        catch (SocketException e){
        	// tentar criar um socket em uma endereco/porta já em uso
        	System.out.println("Socket: " + e.getMessage());
	   }catch (IOException e) {
		   System.out.println("IO: " + e.getMessage());
	   }finally {
		   if(serverSocket != null) {
			   serverSocket.close();
		   	   System.out.println("\n---> Servidor finalizado <--- ");
		   }
	   }

    }// end main
}

