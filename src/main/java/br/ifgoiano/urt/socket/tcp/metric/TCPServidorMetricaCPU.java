package br.ifgoiano.urt.socket.tcp.metric;

import java.io.*;
import java.net.*;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

import java.io.*;
import java.net.*;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

/* 
 * TCP com 1 conexão que recebe 100 mensagens distintas
 * Pega as métricas do cliente: 
*  Mede a latência (tempo de resposta)
*  Mede o uso médio da CPU antes e depois
*  
*  @Date: 17/03/25
 */
public class TCPServidorMetricaCPU {
    private static final int SERVER_PORT = 9876;
    private static final int NUM_TESTES = 100;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
        	InetAddress serverAddress = InetAddress.getLocalHost();
            String serverIP = serverAddress.getHostAddress();

            System.out.println("\n---> Iniciando o servidor TCP... <---\n");
            System.out.println("Servidor esperando conexões no endereço " + serverIP + " na porta " + SERVER_PORT + "...\n");


            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    InetAddress clientAddress = clientSocket.getInetAddress();
                    System.out.println("Cliente conectado: " + clientAddress);

                    // Medição de CPU antes do processamento
                    OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                    double cpuInicio = osBean.getProcessCpuLoad() * 100;

                    long tempoInicio = System.nanoTime();
                    int mensagensAtendidas = 0;

                    // Lê todas as mensagens de uma vez
                    while (mensagensAtendidas < NUM_TESTES && in.ready()) {
                        String mensagem = in.readLine();
                        if (mensagem == null) break;
                        mensagensAtendidas++;
                    }

                    long tempoFim = System.nanoTime();
                    double segundos = (tempoFim - tempoInicio) / 1e9;
                    double usoCPU = osBean.getProcessCpuLoad() * 100;

                    // Envia resposta única ao cliente
                    out.println("ACK: Recebidas " + mensagensAtendidas + " mensagens");

                    System.out.printf("Mensagens processadas: %d | Tempo: %.2f s | Taxa: %.2f pacotes/s | CPU: %.2f%%\n",
                            mensagensAtendidas, segundos, mensagensAtendidas / segundos, usoCPU);

                } catch (IOException e) {
                    System.err.println("Erro ao processar cliente: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


