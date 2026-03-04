package br.ifgoiano.urt.socket.udp.metric;

import java.lang.management.ManagementFactory;
//import java.lang.management.OperatingSystemMXBean;
import com.sun.management.OperatingSystemMXBean;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/* 
 * Pega as métricas do cliente: 
*  Mede a latência (tempo de resposta)
*  Mede o uso médio da CPU antes e depois
*  
*  @Date: 17/03/25
 */
public class UDPClienteMetricaCPU {
	//private static final String SERVER_IP = "127.0.0.1";
    private static final String SERVER_IP = "10.1.1.157";
    private static final int SERVER_PORT = 9876;
    private static final int NUM_TESTES = 100;

    public static void main(String[] args) {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            byte[] sendBuffer;
            byte[] receiveBuffer = new byte[1024];
            
            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            double cpuInicio = capturarUsoCPU(osBean);
            
            System.out.println("Cliente UDP iniciado. Enviando " + NUM_TESTES + " pacotes...");
            long latenciaTotal = 0;
            
            for (int i = 0; i < NUM_TESTES; i++) {
                String mensagem = "Ping " + i;
                sendBuffer = mensagem.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, SERVER_PORT);
                
                long inicio = System.nanoTime();
                clientSocket.send(sendPacket);
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                clientSocket.receive(receivePacket);
                long fim = System.nanoTime();
                
                long latencia = fim - inicio;
                latenciaTotal += latencia;
                
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Recebido: " + response + " | RTT: " + (latencia / 1e6) + " ms");
            }
            
            Thread.sleep(500);
            double cpuFim = capturarUsoCPU(osBean);
            double mediaLatencia = (latenciaTotal / NUM_TESTES) / 1e6;
            
            System.out.printf("Latência Média: %.2f ms | CPU antes: %.2f%% | CPU depois: %.2f%%\n", mediaLatencia, cpuInicio, cpuFim);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double capturarUsoCPU(OperatingSystemMXBean osBean) {
        double cpu = osBean.getProcessCpuLoad();
        if (Double.isNaN(cpu) || cpu < 0 || cpu > 100) {
            return 0;
        }
        return cpu * 100;
    }
}

