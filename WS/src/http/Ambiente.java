package http;

import java.net.InetAddress;
import java.sql.Connection;
import java.util.logging.Level;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class Ambiente {

  private static Ambiente instanciaUnica;
  private static ComboPooledDataSource poolConexoes;
  
  public Ambiente() {
    try {
      poolConexoes = new ComboPooledDataSource();
      
      String usuario = "AMCOM";
      String senha = "DADOS";
      String servidor = "127.0.0.1";
      String porta = "1521";
      String servico = "ORCL";

      //Conexão
      poolConexoes.setDriverClass("oracle.jdbc.OracleDriver");
      poolConexoes.setJdbcUrl("jdbc:oracle:thin:@" + servidor + ":" + porta + ":" + servico);
      poolConexoes.setPassword(senha);
      poolConexoes.setUser(usuario);

      //Limites de conexões abertas para controle
      poolConexoes.setInitialPoolSize(3);
      poolConexoes.setMaxPoolSize(200);
      poolConexoes.setCheckoutTimeout(0);

      //Tempo que uma conexão será mantida no pool sem ser usada.
      poolConexoes.setMaxIdleTime(10);

      // Número de segundos que conexões acima do limite minPoolSize deverão permanecer
      // inativas no pool antes de serem fechadas. Destinado para aplicações que desejam
      // reduzir agressivamente o número de conexões abertas, diminuindo o pool novamente
      // para minPoolSize, se, seguindo um pico, o nível de load diminui e Conexões não
      // são mais requeridas. Se maxIdleTime está definido, maxIdleTimeExcessConnections
      // deverá ser um valor menor para que o parâmetro tenho efeito. Zero significa que
      // não existirá nenhuma imposição, Conexões em excesso não serão mais fechadas.
      poolConexoes.setMaxIdleTimeExcessConnections(30);

      // c3p0 é muito assíncrono. Operações JDBC lentas geralmente são executadas por
      // helper threads que não detém travas de fechamento. Separar essas operações atravéz
      // de múltiplas threads pode melhorar significativamente a performace, permitindo
      // que várias operações sejam executadas ao mesmo tempo.
      poolConexoes.setNumHelperThreads(3);

      // Se true, e se unreturnedConnectionTimeout está definido com um valor positivo,
      // então o pool capturará a stack trace (via uma exceção) de todos os checkouts
      // de Conexões, e o stack trace será impresso quando o checkout de Conexões der
      // timeout. Este paramêtro é destinado para debug de aplicações com leak de
      // Conexões, isto é, aplicações que ocasionalmente falham na liberação/fechamento
      // de Conexões, ocasionando o crescimento do pool, e eventualmente na sua exaustão
      // (quando o pool atinge maxPoolSize com todas as suas conexões em uso e perdidas).
      // Este paramêtro deveria ser setado apenas para debugar a aplicação, já que capturar
      // o stack trace deixa mais o lento o precesso de check-out de Conexões.
      poolConexoes.setDebugUnreturnedConnectionStackTraces(false);

      // Segundos. Se setado, quando uma aplicação realiza o check-out e falha na realização
      // do check-in [i.e. close()] de um Conexão, dentro de período de tempo especificado,
      // o pool irá, sem cerimonias, destruir a conexão [i.e. destroy()]. Isto permite
      // que aplicações com ocasionais leaks de conexão sobrevivam, ao invéz de exaurir
      // o pool. E Isto é uma pena. Zero significa sem timeout, aplicações deveriam fechar
      // suas próprias Conexões. Obviamente, se um valor positivo é definido, este valor
      // deve ser maior que o maior valor que uma conexão deveria permanecer em uso. Caso
      //# contrário, o pool irá ocasionalmente matar conexões ativas, o que é ruim. Isto
      // basicamente é uma péssima idéia, porém é uma funcionalidade pedida com frequência.
      // Consertem suas aplicações para que não vazem Conexões!!! Use esta funcionalidade
      // temporariamente em combinação com  debugUnreturnedConnectionStackTraces para
      // descobrir onde as conexões esão vazando!
      poolConexoes.setUnreturnedConnectionTimeout(300);

    } catch (Exception ex) {
      escreverLogExcecao("Não foi possível criar uma conexão nova C3P0 com o banco de dados", Ambiente.class.getName(), ex);
    }
  }

  public static synchronized Ambiente getInstance() {
    if (instanciaUnica == null) {
      instanciaUnica = new Ambiente();
    }

    return instanciaUnica;
  }

  public static Connection getNovaConexao() {
    try {
      Ambiente.getInstance();

      Connection conexao = Ambiente.poolConexoes.getConnection();
      conexao.setAutoCommit(false);

      return conexao;
    } catch (Exception e) {
      escreverLogExcecao("Não foi possível obter uma nova conexão do C3P0", Ambiente.class.getName(), e);
      return null;
    }
  }

  public static void escreverLogExcecao(String mensagem, String className, Exception e) {
    String mensagemExcecao = "";
    if ((e != null) && (!e.getMessage().isEmpty())) {
      mensagemExcecao = "Exceção: " + e.getMessage() + " ";
    }
    mensagemExcecao += "Classe: " + className;

    //Escrevendo nos logs do servidor web (glssfish/tomcat)
    java.util.logging.Logger.getLogger(className).log(Level.SEVERE, mensagem + mensagemExcecao, e);
  }

  public static String getNomeEstacao() {
    try {
      return InetAddress.getLocalHost().getHostName().toUpperCase();
    } catch (Exception e) {
      return "NÃO IDENTIFICADA";
    }
  }
      
}