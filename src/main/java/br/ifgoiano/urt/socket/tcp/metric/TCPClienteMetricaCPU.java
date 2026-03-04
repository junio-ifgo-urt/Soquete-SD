package br.ifgoiano.urt.socket.tcp.metric;

import java.io.*;
import java.net.*;
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

/* 
 * TCP com 1 conexão que envia 100 mensagens distintas
 * Pega as métricas do cliente: 
*  Mede a latência (tempo de resposta)
*  Mede o uso médio da CPU antes e depois
*  
*  @Date: 17/03/25
 */

public class TCPClienteMetricaCPU {
	//private static final String SERVER_IP = "127.0.0.1";
    private static final String SERVER_IP = "10.1.1.157";
    private static final int SERVER_PORT = 9876;
    private static final int NUM_TESTES = 100;

    public static void main(String[] args) {
        try (Socket clientSocket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            double cpuInicio = osBean.getProcessCpuLoad();
            if (Double.isNaN(cpuInicio) || cpuInicio < 0 || cpuInicio > 100) {
                cpuInicio = 0;
            }
            cpuInicio *= 100;

            System.out.println("Cliente conectado. Enviando todas as 100 mensagens de uma vez...");

            long inicio = System.nanoTime();

            // Envia todas as 100 mensagens de uma vez
            for (int i = 0; i < NUM_TESTES; i++) {
                out.println("Ping " + i);
            }

            // Aguarda todas as 100 respostas do servidor
            for (int i = 0; i < NUM_TESTES; i++) {
                String resposta = in.readLine();
                System.out.println("Recebido do servidor: " + resposta);
            }

            long fim = System.nanoTime();
            long latencia = fim - inicio;

            double cpuFim = osBean.getProcessCpuLoad() * 100;
            double latenciaMs = latencia / 1e6;

            System.out.printf("Latência total: %.2f ms | Uso de CPU antes: %.2f%% | Uso de CPU depois: %.2f%%\n",
                    latenciaMs, cpuInicio, cpuFim);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
