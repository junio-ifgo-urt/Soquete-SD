package br.ifgoiano.urt.socket.tcp;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

/**
 * RESUMO: Servidor TCP que fornece a hora atual com base na região (TimeZone)
 * solicitada pelo cliente. Demonstra o uso de ServerSocket, Streams de 
 * entrada/saída com UTF-8 e a API java.time.
 * * @author Prof. Junio Lima
 * @since  2025-05-22
 * @version 1.0
 */
public class TCPServidorRelogio {
    private static final int PORTA = 9876;

    public static void main(String[] args) {
        System.out.println("\n---> Iniciando o servidor ... <---\n");

        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            // Obtém o endereço IP do servidor
            InetAddress serverAddress = InetAddress.getLocalHost();
            String serverIP = serverAddress.getHostAddress();

            System.out.println("Servidor esperando conexões no endereço " + serverIP + ", na porta " + PORTA + "...");
        	
            while (true) {
                // O serverSocket.accept() é uma operação bloqueante. 
                // Ele fica aguardando até que um cliente se conecte.
                // Assim que um cliente se conecta, o método retorna um objeto Socket para 
                // comunicação com esse cliente. Assim que o atendimento termina e sai do bloco try, 
                // o Java fecha automaticamente o socket do cliente, liberando os recursos do sistema operacional.
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {

                    String clientMessage = reader.readLine();
                    System.out.println("Região solicitada pelo cliente: " + clientMessage);

                    // Processar a região e obter a hora
                    String serverResponse;
                    try {
                        ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.of(clientMessage));
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
                        serverResponse = "Hora atual na região " + clientMessage + ": " + dateTime.format(formatter);
                    } catch (Exception e) {
                        serverResponse = "Região inválida: " + clientMessage;
                    }

                    writer.write(serverResponse + "\n");
                    writer.flush();
                } catch (IOException e) {
                    System.out.println("Erro no atendimento ao cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        } finally {
            System.out.println("\n---> Servidor finalizado <--- ");
        }
    }
}

