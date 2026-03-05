package br.ifgoiano.urt.socket.tcp;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * RESUMO: Simulador de carga para o servidor de relógio.
 * FUNCIONAMENTO: Dispara múltiplas Threads, onde cada uma age como um cliente
 * independente, realizando requisições simultâneas para testar o ThreadPool do servidor.
 * * @author Prof. Junio Lima
 * @since  2026-03-05
 */
public class TCPClienteRelogioMultiThread {
    private static final String SERVIDOR = "localhost";
    private static final int PORTA = 9876;
    private static final int TOTAL_CLIENTES = 15; // Mais que o MAX_THREADS do servidor para ver a fila

    public static void main(String[] args) {
        for (int i = 1; i <= TOTAL_CLIENTES; i++) {
            int idCliente = i;
            
            // Cria uma nova thread para cada cliente simulado
            new Thread(() -> {
                try (Socket socket = new Socket(SERVIDOR, PORTA);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

                    String region = "America/Sao_Paulo";
                    System.out.println("[Cliente " + idCliente + "] Solicitando: " + region);

                    writer.write(region + "\n");
                    writer.flush();

                    String response = reader.readLine();
                    System.out.println("[Cliente " + idCliente + "] Resposta: " + response);

                    // Pequena pausa para manter a conexão aberta por alguns segundos
                    // Isso ajuda a ver o ThreadPool do servidor sendo ocupado
                    Thread.sleep(3000); 

                } catch (Exception e) {
                    System.out.println("[Cliente " + idCliente + "] Erro: " + e.getMessage());
                }
            }).start();
        }
    }
}