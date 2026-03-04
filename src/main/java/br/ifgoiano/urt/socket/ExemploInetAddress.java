package br.ifgoiano.urt.socket;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * RESUMO: Demonstração do uso das classes InetAddress e InetSocketAddress
 * para resolução de nomes (DNS) e manipulação de endereços de rede em Java.
 * * FUNCIONAMENTO:
 * 1. InetAddress: Utilizado para obter o IP a partir de um nome de host (Hostname).
 * 2. InetSocketAddress: Utilizado para representar um par Endereço IP + Número de Porta,
 * essencial para a criação de Sockets TCP/UDP.
 * * @author Prof. Junio (IFG - IFMaker)
 * @since  2025-05-22
 * @version 1.0
 */
public class ExemploInetAddress {
    public static void main(String[] args) {
    	System.out.println(" ----> Usando InetAddress <---- ");
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            System.out.println("Endereço IP: " + address.getHostAddress());
            System.out.println("Nome do host: " + address.getHostName());
        } catch (UnknownHostException e) {
            System.out.println("Host não encontrado.");
        }
        System.out.println("\n ----> Usando InetSocketAddress <---- ");
        try {
            InetSocketAddress address2 = new InetSocketAddress("www.ifgoiano.edu.br", 8080);
            System.out.println("Endereço IP: " + address2.getAddress().getHostAddress());
            System.out.println("Porta: " + address2.getPort());
            System.out.println("Nome do host: " + address2.getHostName());
           
        } catch (Exception e) {
            System.out.println("Host não encontrado.");
        }
    }
}