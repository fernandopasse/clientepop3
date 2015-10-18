package cliente;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Fernando
 */
public class Mensagem {
    
    private final Map<String, List<String>> cabecalhos;
    private final String corpo;
    
    /**
     * Este contrutor recebe um hash contendo &lt; nome, valor &gt;, de cada cabeçalho
     * 
     * @param cabecalhos - hash contendo &lt;nome, valor&gt; de cada cabeçalho
     * @param corpo - contém o conteúdo da mensagem
     */
    protected Mensagem(HashMap<String, LinkedList<String>> cabecalhos, String corpo) {
        this.cabecalhos = Collections.unmodifiableMap(cabecalhos);
        this.corpo = corpo;
    }
    
    /**
     * Método retor Map dos cabeçalhos
     * 
     * @return - Map&lt;String, List&lt;String&gt;&gt; contendo &lt;nome, valor&gt; de cada cabeçalho
     */
    public Map<String, List<String>> buscaCabecalhos() {
        return cabecalhos;
    }
    
    /**
     * Método busca corpo das mensagens
     * 
     * @return String contendo a mensagem 
     */
    public String buscaCorpo(){
        return this.corpo;
    }
}
