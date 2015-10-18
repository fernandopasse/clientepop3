/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author Fernando
 */
public class POP3ClienteSSL extends POP3Cliente {

    private SSLSocket clienteSocket;

    /**
     * Médodo conexao, realiza conexão o servidor
     *
     * @param host - endereço do servidor
     * @param porta - porta do servidor
     * @throws IOException - Lança exceção caso ocorra um erro na conexão
     */
    @Override
    public void conexao(String host, int porta) throws IOException {
        //Realiza conexão com o servidor  
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        this.clienteSocket = (SSLSocket) factory.createSocket(host, porta);
        //Cria Buffer para entrada de dados
        this.entrada = new BufferedReader(new InputStreamReader(this.clienteSocket.getInputStream()));
        //Cria Buffer para saida de dados
        this.saida = new BufferedWriter(new OutputStreamWriter(this.clienteSocket.getOutputStream()));
        System.out.println(this.lerResposta());
    }

}
