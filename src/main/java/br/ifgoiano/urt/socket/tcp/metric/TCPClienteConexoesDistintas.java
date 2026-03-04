package br.ifgoiano.urt.socket.tcp.metric;

import java.io.*;
import java.net.*;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

/* 
 * TCP com 100 conexões distintas, onde cada conexão envia e rebece uma msg
 * Pega as métricas do cliente: 
*  Mede a latência (tempo de resposta)
*  Mede o uso médio da CPU antes e depois
*  
*  @Date: 17/03/25
 */
class TCPClienteConexoesDistintas {
	//private static final String SERVER_IP = "127.0.0.1";
    private static final String SERVER_IP = "10.1.1.157";
    private static final int SERVER_PORT = 9876;
    private static final int NUM_TESTES = 100;

    public static void main(String[] args) {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpuInicio = capturarUsoCPU(osBean);

        System.out.println("Cliente TCP iniciado. Enviando " + NUM_TESTES + " mensagens com conexões distintas...");
        long latenciaTotal = 0;

        for (int i = 0; i < NUM_TESTES; i++) {
            long inicio = System.nanoTime();
            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                
                String mensagem = "Ping " + i;
                out.println(mensagem);
                String resposta = in.readLine();
                
                long fim = System.nanoTime();
                long latencia = fim - inicio;
                latenciaTotal += latencia;
                
                System.out.println("Recebido: " + resposta + " | RTT: " + (latencia / 1e6) + " ms");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}
        double cpuFim = capturarUsoCPU(osBean);
        double mediaLatencia = (latenciaTotal / NUM_TESTES) / 1e6;
        
        System.out.printf("Latência Média: %.2f ms | CPU antes: %.2f%% | CPU depois: %.2f%%\n", mediaLatencia, cpuInicio, cpuFim);
    }

    private static double capturarUsoCPU(OperatingSystemMXBean osBean) {
        double cpu = osBean.getProcessCpuLoad();
        if (Double.isNaN(cpu) || cpu < 0 || cpu > 100) {
            return 0;
        }
        return cpu * 100;
    }
}
