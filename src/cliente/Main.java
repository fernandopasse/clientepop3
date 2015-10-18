package cliente;

import java.io.IOException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Fernando
 */
public class Main {

    public static void acaoMensagem(int numMensagem, POP3Cliente cliente) throws IOException {
        LinkedList<Mensagem> mensagens = cliente.buscarMensagens();
        Scanner sc = new Scanner(System.in);
        laco_acao:
        while (true) {
            System.out.println("\n\n------Ações------");
            System.out.println("1- Para ler a mensagem");
            System.out.println("2- Para excluir a mensagem");
            System.out.println("3- Sair");
            System.out.print("Digite a opção desejada: ");
            String op = sc.next();
            switch (op) {
                case "1":
                    System.out.println("--- Mensagem num. " + (numMensagem) + " ---");
                    System.out.println(mensagens.get(numMensagem - 1).buscaCabecalhos().get("From"));
                    System.out.println(mensagens.get(numMensagem - 1).buscaCabecalhos().get("Subject"));
                    System.out.println(mensagens.get(numMensagem - 1).buscaCabecalhos().get("Date"));
                    System.out.println(mensagens.get(numMensagem - 1).buscaCorpo());
                    break;
                case "2":
                    cliente.deletar(numMensagem);
                    System.out.println("Mensagem excluída com sucesso!");
                    break;
                case "3":
                    break laco_acao;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    public static void listarMensagemAssunto(POP3Cliente cliente) throws IOException {
        LinkedList<Mensagem> mensagens = cliente.buscarMensagens();
        System.out.println("\n");
        for (int index = 0; index < mensagens.size(); index++) {
            System.out.println("Mensagem " + (index + 1) + " : " + mensagens.get(index).buscaCabecalhos().get("Subject"));
        }
    }

    public static void listarMensagem(POP3Cliente cliente) throws IOException {
        LinkedList<Mensagem> mensagens = cliente.buscarMensagens();
        System.out.println("\n");
        for (int index = 0; index < mensagens.size(); index++) {
            System.out.println("--- Mensagem num. " + (index + 1) + " ---");
            System.out.println(mensagens.get(index).buscaCabecalhos().get("From"));
            System.out.println(mensagens.get(index).buscaCabecalhos().get("Subject"));
            System.out.println(mensagens.get(index).buscaCabecalhos().get("Date"));
        }
    }

    public static boolean validaEmail(String email) {
        if ((email == null) || (email.trim().length() == 0)) {
            return false;
        }
        String emailPattern = "\\b(^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";
        Pattern pattern = Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void main(String args[]) throws IOException {
        HashMap<String, String> listaDominios = new HashMap<>();
        POP3Cliente cliente = new POP3Cliente();

        listaDominios.put("aol.com", "pop.aol.com");
        listaDominios.put("gmail.com", "pop.gmail.com");
        listaDominios.put("yahoo.com.br", "pop.mail.yahoo.com.br");
        listaDominios.put("globo.com", "pop3.globo.com");
        listaDominios.put("terra.com.br", "pop.terra.com.br");
      //  listaDominios.put("ufv.br", "pop.ufv.br");

        Scanner sc = new Scanner(System.in);
        String email;
        while (true) {
            while (true) {
                System.out.println("Digite seu e-mail: ");
                email = sc.next();
                if (validaEmail(email)) {
                    break;
                } else {
                    System.out.println("E-mail inválido tente novamente ou -1, para encerrar!");
                    if (email.equals("-1")) {
                        System.exit(1);
                    }
                }
            }

            if (listaDominios.get(email.substring(email.indexOf("@") + 1)) != null) {
                while (true) {
                    try {
                        System.out.println("Conectando-se ao seu servidor POP...");
                        cliente.conexao(listaDominios.get(email.substring(email.indexOf("@") + 1)));
                        break;
                    } catch (IOException e) {
                        System.out.println("Erro ao tentar se conectar o servidor POP!");
                    }
                }
            }else{
                while (true) {
                    try {
                        System.out.println("Servidor POP não foi reconhecido");
                        System.out.print("Digite manualmente a seguir ou -1, para encerrar: ");
                        String pop = sc.next();
                        if (pop.equals("-1")) {
                            System.exit(1);
                        }
                        System.out.println("Conectando-se ao seu servidor POP...");
                        cliente.conexao(pop);
                        break;
                    } catch (IOException e) {
                        System.out.println("Servidor POP não foi reconhecido, tente novamente!");
                    }
                }
            }

            System.out.println("Digite sua senha: ");
            String senha = sc.next();
            
            try {
                cliente.logar(email, senha);
                break;
            } catch (RuntimeException e) {
                System.out.println("E-mail ou senha inválida tente novamente!");
            }
        }

        System.out.println("Bem vindo!");

        System.out.println("\nExistem " + cliente.verificaNumeroNovasMensagens() + " mensagens\n");

        laco_opcao:
        while (true) {
            String nomeEmail = email.substring(email.indexOf("@") + 1);
            System.out.println("E-mail - " + nomeEmail);
            System.out.println("--- Opções do E-mail ---");
            System.out.println("1- Listar informações das mensagens");
            System.out.println("2- Listar assuntos");
            System.out.println("3- Exibir/Apagar mensagem");
            System.out.println("4- Sair do cliente");
            System.out.print("Digite a opção desejada: ");
            int opMensagem = sc.nextInt();
            laco:
            switch (opMensagem) {
                case 1:
                    listarMensagem(cliente);
                    System.out.println("\n\n");
                    break;
                case 2:
                    listarMensagemAssunto(cliente);
                    System.out.println("\n\n");
                    break;
                case 3:
                    try {
                        System.out.println("Digite o número da mensagem:");
                        int op = sc.nextInt();
                        acaoMensagem(op, cliente);
                    } catch (InputMismatchException e) {
                        System.out.println("A mensagem procurada não existe!");
                    }
                    break;
                case 4:
                    break laco_opcao;
                default:
                    System.out.println("Opção inválida!");
            }

        }
        System.out.println("Saindo...");
        cliente.sair();
        cliente.desconectar();
    }
}
