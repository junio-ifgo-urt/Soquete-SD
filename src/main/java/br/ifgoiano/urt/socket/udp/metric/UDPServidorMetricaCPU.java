package br.ifgoiano.urt.socket.udp.metric;

import java.io.IOException;

import java.lang.management.ManagementFactory;
//import java.lang.management.OperatingSystemMXBean;
import com.sun.management.OperatingSystemMXBean;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/* 
 * Pega as métricas do servidor: 
*  Mede a latência (tempo de resposta)
*  Conta pacotes recebidos
*  Mede a taxa de pacotes por segundo
*  Mede o uso médio da CPU durante a execução
*  
*  @Date: 17/03/25
 */

public class UDPServidorMetricaCPU {
    private static final int PORTA = 9876;
    private static final AtomicInteger totalPacotes = new AtomicInteger(0);

    public static void main(String[] args) {
        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(PORTA);
            serverSocket.setReceiveBufferSize(1024 * 1024); // Aumenta buffer para evitar perdas

            byte[] receiveBuffer = new byte[1024];
            InetAddress serverAddress = InetAddress.getLocalHost();
            String serverIP = serverAddress.getHostAddress();

            System.out.println("\n---> Iniciando o servidor UDP... <---\n");
            System.out.println("Servidor esperando pacotes no endereço " + serverIP + " na porta " + PORTA + "...\n");

            long inicio = System.nanoTime();
            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(receivePacket);

                totalPacotes.incrementAndGet();
                String clientMessage = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
                System.out.println("Recebido: " + clientMessage + " de " + receivePacket.getAddress());

                // Responder ao cliente
                String response = "ACK: " + Instant.now();
                byte[] sendBuffer = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
                        receivePacket.getAddress(), receivePacket.getPort());
                serverSocket.send(sendPacket);

                // Estatísticas ao atingir 100 pacotes
                if (totalPacotes.get() == 100) {
                    long tempoTotal = System.nanoTime() - inicio;
                    double tempoMedio = (tempoTotal / 100.0) / 1e9;
                    double usoCPU = osBean.getProcessCpuLoad() * 100; 

                    System.out.printf("UDP Finalizado -> Tempo médio: %.4f s | Taxa: %.2f pacotes/s | CPU média: %.2f%%\n",
                            tempoMedio, 100 / tempoMedio, usoCPU);
                    break; // Encerra o servidor após 100 pacotes
                }
            }
        } catch (IOException e) {
            System.out.println("Erro de E/S: " + e.getMessage());
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
                System.out.println("Servidor UDP finalizado.");
            }
        }
    }
}

