package br.ifgoiano.urt.socket.tcp.metric;

import java.io.*;
import java.net.*;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

/* 
 * TCP com 100 conexões distintas, onde cada conexão rebece uma msg
 * Pega as métricas do cliente: 
*  Mede a latência (tempo de resposta)
*  Mede o uso médio da CPU antes e depois
*  
*  @Date: 17/03/25
 */
public class TCPServidorConexoesDistintas {
    private static final int SERVER_PORT = 9876;
    private static final int NUM_TESTES = 100;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            InetAddress serverAddress = InetAddress.getLocalHost();
            String serverIP = serverAddress.getHostAddress();

            System.out.println("\n---> Iniciando o servidor TCP... <---\n");
            System.out.println("Servidor esperando conexões no endereço " + serverIP + " na porta " + SERVER_PORT + "...\n");

            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

            int conexoesAtendidas = 0;
            long tempoTotal = 0;
            double usoCpuTotal = 0;

            while (conexoesAtendidas < NUM_TESTES) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    InetAddress clientAddress = clientSocket.getInetAddress();
                    System.out.println("Cliente conectado: " + clientAddress + " (Conexão " + (conexoesAtendidas + 1) + ")");

                    long inicioTempo = System.nanoTime();
                    try { Thread.sleep(200); } catch (InterruptedException ignored) {} // Tempo para estabilizar CPU
                    double cpuInicio = osBean.getProcessCpuLoad() * 100;

                    String mensagem = in.readLine();
                    if (mensagem != null) {
                        System.out.println("Recebido: " + mensagem);
                        out.println("ACK: " + mensagem);
                    }

                    long fimTempo = System.nanoTime();
                    double cpuFim = osBean.getProcessCpuLoad() * 100;
                    if (Double.isNaN(cpuFim)) cpuFim = 0; // Se a leitura falhar, assume 0

                    conexoesAtendidas++;
                    tempoTotal += (fimTempo - inicioTempo);
                    usoCpuTotal += cpuFim;
                }
            }

            // Cálculo das médias
            double tempoMedioSegundos = (tempoTotal / (double) NUM_TESTES) / 1e9;
            double usoCpuMedio = usoCpuTotal / NUM_TESTES;

            System.out.printf("TCP Finalizado -> Conexões: %d | Tempo médio: %.4f s | Taxa: %.2f conexões/s | CPU média: %.2f%%\n",
                    conexoesAtendidas, tempoMedioSegundos, NUM_TESTES / tempoMedioSegundos, usoCpuMedio);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

