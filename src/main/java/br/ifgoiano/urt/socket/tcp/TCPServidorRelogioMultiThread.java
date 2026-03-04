package br.ifgoiano.urt.socket.tcp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServidorRelogioMultiThread {
    private static final int PORTA = 9876;
    private static final int MAX_THREADS = 10; // Limita o número de threads ativas

    public static void main(String[] args) {
        System.out.println("\n---> Servidor iniciado na porta " + PORTA + " <---\n");

        // Cria um pool de threads para atender múltiplos clientes sem consumir muitos recursos
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Aceita conexões sem bloquear o servidor
                pool.execute(new ClienteHandler(clientSocket)); // Processa o cliente em uma nova thread
            }
        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        } finally {
            pool.shutdown();
            System.out.println("\n---> Servidor finalizado <--- ");
        }
    }

    // Classe interna que processa a requisição de cada cliente
    private static class ClienteHandler implements Runnable {
        private final Socket clientSocket;

        public ClienteHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {

                String clientMessage;
                while ((clientMessage = reader.readLine()) != null) { // Mantém a conexão aberta
                    System.out.println("Região solicitada: " + clientMessage);

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
                }
            } catch (IOException e) {
                System.out.println("Erro ao processar cliente: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                    System.out.println("Cliente desconectado.");
                } catch (IOException e) {
                    System.out.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        }
    }
}

