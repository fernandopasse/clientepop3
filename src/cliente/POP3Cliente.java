package cliente;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Fernando
 */
public class POP3Cliente {

    private Socket clienteSocket;
    protected BufferedReader entrada;
    protected BufferedWriter saida;
    private final int PORTA = 110;

    /**
     * Médodo conexao, realiza conexão o servidor
     *
     * @param host - endereço do servidor
     * @param porta - porta do servidor
     * @throws IOException - Lança exceção caso ocorra um erro na conexão
     */
    public void conexao(String host, int porta) throws IOException {
        //Realiza conexão com o servidor
        this.clienteSocket = new Socket(host, porta);
        //Cria Buffer para entrada de dados
        this.entrada = new BufferedReader(new InputStreamReader(this.clienteSocket.getInputStream()));
        //Cria Buffer para saida de dados
        this.saida = new BufferedWriter(new OutputStreamWriter(this.clienteSocket.getOutputStream()));
        this.lerResposta();
    }

    /**
     * Método realiza a conexão em uma porta padrão, previamente configurada
     *
     * @param host - endereco do servidor
     * @throws IOException - Lança exceção caso ocorra um erro na conexão
     */
    public void conexao(String host) throws IOException {
        //Tenta realizar conexao com a porta configurada na constante PORTA
        this.conexao(host, PORTA);
    }

    /**
     * Verifica se o servidor está conectado
     *
     * @return - true se sim ou false se não
     */
    public boolean verificaConexao() {
        return this.clienteSocket != null && this.clienteSocket.isConnected();
    }

    /**
     * Método para ler respota do servidor após enviar um comando
     *
     * @return - Resposta retornada pelo servidor
     * @throws IOException - Exceção de conexão ou erro no servidor
     */
    public String lerResposta() throws IOException {
        String resposta = entrada.readLine();
        //Verifica se a mensagem retorna pelo servidor começa com -ERR, se sim lança uma exceção
        if (resposta.startsWith("-ERR")) {
            throw new RuntimeException("O servidor retornou uma resposta inesperada: " + resposta.replaceFirst("-ERR ", ""));
        }
        return resposta;
    }

    /**
     * Método para enviar comandos para o servido
     *
     * @param comando - Comando a ser enviado
     * @return - resposta do servidor
     * @throws IOException - Exceção de conexão ou erro no servidor
     */
    public String enviarComando(String comando) throws IOException {
        //Envia comando para servidor
        saida.write(comando + "\r\n");
        //Limpa o buffer
        saida.flush();
        return lerResposta();
    }

    /**
     * Método para realizar login no servidor
     *
     * @param usuario - usuário para conexão no servidor
     * @param senha - senha para utilizar no sevidor
     * @throws IOException - Exceção de conexão ou erro no servidor
     */
    public void logar(String usuario, String senha) throws IOException {
        enviarComando("USER " + usuario);
        enviarComando("PASS " + senha);
    }

    /**
     * Método para deslogar com o servidor
     *
     * @throws IOException - Exceção de conexão ou erro no servidor
     */
    public void sair() throws IOException {
        enviarComando("QUIT");
    }

    /**
     * Método para desconectar com o servidor
     *
     * @throws IOException - Exceção de conexão ou erro no servidor
     */
    public void desconectar() throws IOException {
        if (!this.verificaConexao()) {
            throw new IllegalStateException("Você não está conectado com o host!");
        }
        this.clienteSocket.close();
        this.entrada = null;
        this.saida = null;
    }

    /**
     * Método para verifica o numero de mensagens
     *
     * @return - Numero de mensagens no servidor
     * @throws IOException - Exceção de conexão ou erro no servidor
     */
    public int verificaNumeroNovasMensagens() throws IOException {
        String resposta = this.enviarComando("STAT");
        String valor[] = resposta.split(" ");
        return Integer.parseInt(valor[1]);
    }
    
    /**
     * Método para deletar mensagens no servidor
     * 
     * @param numeroMensagem - Numero da mensagem a ser deletada
     * @throws IOException - Exceção de conexão ou erro no servidor
     */
    public void deletar(int numeroMensagem) throws IOException{
        enviarComando("DELE " + numeroMensagem);
    }

    /**
     * Método para leitura de mensagens
     *
     * @param numeroMensagem - Numero da mensagem a ser lida
     * @return - Objeto Mensagem, contendo cabeçalhos e corpo
     * @throws IOException
     */
    public Mensagem buscarMensagens(int numeroMensagem) throws IOException {
        //Busca mensagem no servidor
        enviarComando("RETR " + numeroMensagem);
        HashMap<String, LinkedList<String>> cabecalhos = new HashMap<>();
        String resposta;
        String nomeCabecalho;
        // processando cabeçalhos
        while ((resposta = lerResposta()).length() != 0) {
            if (resposta.startsWith("\t") || resposta.startsWith(" ")) {
                continue; //não processar cabeçalho com várias linhas
            }
            int doisPontos = resposta.indexOf(":");

            nomeCabecalho = resposta.substring(0, doisPontos);

            String valorCabecalho;

            if (resposta.length() > nomeCabecalho.length() + 2) {
                valorCabecalho = resposta.substring(doisPontos + 2);
            } else {
                valorCabecalho = "";
            }

            LinkedList<String> cabecalhoValor = cabecalhos.get(nomeCabecalho);
            if (cabecalhoValor == null) {
                cabecalhoValor = new LinkedList<>();
                cabecalhos.put(nomeCabecalho, cabecalhoValor);
            }
            cabecalhoValor.add(valorCabecalho);
        }
        // processando corpo da mensagem
        StringBuilder bodyBuilder = new StringBuilder();
        while (!(resposta = lerResposta()).equals(".")) {
            bodyBuilder.append(resposta).append("\n");
        }

        return new Mensagem(cabecalhos, bodyBuilder.toString());

    }

    /**
     * Método para buscar mensagens no servidor
     *
     * @return - Lista contendo todas as mensagens disponiveis
     * @throws IOException
     */
    public LinkedList<Mensagem> buscarMensagens() throws IOException {
        int numeroMensagens = this.verificaNumeroNovasMensagens();
        LinkedList<Mensagem> listaMensagens = new LinkedList<>();
        for (int i = 1; i <= numeroMensagens; i++) {
            listaMensagens.add(this.buscarMensagens(i));
        }
        return listaMensagens;
    }

}
