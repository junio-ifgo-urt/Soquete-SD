package br.ifgoiano.urt.socket.tcp;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * RESUMO: Cliente TCP que solicita a hora atual de regiões específicas ao servidor.
 * Esta classe demonstra o fluxo de abertura de socket, envio de dados (request)
 * e recepção de resposta (response) utilizando streams com codificação UTF-8.
 * * FUNCIONAMENTO: O cliente conecta ao IP e porta configurados, envia o identificador
 * da região (ex: America/New_York) e aguarda o retorno formatado pelo servidor.
 * * @author Prof. Junio Lima
 * @since  2025-05-22
 * @version 1.0
 */
public class TCPClienteRelogio {
	private static final String SERVIDOR = "10.1.1.109";
    //private static final String SERVIDOR = "localhost"; //127.0.0.1
    private static final int PORTA = 9876;

    public static void main(String[] args) {
        System.out.println("---> Cliente iniciado <---\n");

        try (Socket socket = new Socket(SERVIDOR, PORTA);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
             Scanner scanner = new Scanner(System.in)) {
        	
        	// Obtém o endereço IP do servidor
            String serverIP = socket.getInetAddress().getHostAddress();
            System.out.println("Conectado ao servidor no endereço IP: " + serverIP + ", porta: " + PORTA);

            //System.out.print("Digite a região (ex: America/Sao_Paulo): ");
            //String region = scanner.nextLine().trim();
        	//String region = "America/Sao_Paulo".trim();
            //String region = "Europe/Paris".trim();
            String region = "America/New_York".trim();

            writer.write(region + "\n");
            writer.flush();

            String serverResponse = reader.readLine();
            System.out.println("Resposta do servidor: " + serverResponse);
            
            region = "Europe/Paris".trim();

            writer.write(region + "\n");
            writer.flush();

            serverResponse = reader.readLine();
            System.out.println("\nResposta do servidor: " + serverResponse);
            
        } catch (IOException e) {
            System.out.println("Erro: " + e.getMessage());
        } finally {
            System.out.println("\nCliente finalizado.");
        }
    }
}

