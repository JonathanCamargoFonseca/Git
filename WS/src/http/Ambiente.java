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

      //Conex�o
      poolConexoes.setDriverClass("oracle.jdbc.OracleDriver");
      poolConexoes.setJdbcUrl("jdbc:oracle:thin:@" + servidor + ":" + porta + ":" + servico);
      poolConexoes.setPassword(senha);
      poolConexoes.setUser(usuario);

      //Limites de conex�es abertas para controle
      poolConexoes.setInitialPoolSize(3);
      poolConexoes.setMaxPoolSize(200);
      poolConexoes.setCheckoutTimeout(0);

      //Tempo que uma conex�o ser� mantida no pool sem ser usada.
      poolConexoes.setMaxIdleTime(10);

      // N�mero de segundos que conex�es acima do limite minPoolSize dever�o permanecer
      // inativas no pool antes de serem fechadas. Destinado para aplica��es que desejam
      // reduzir agressivamente o n�mero de conex�es abertas, diminuindo o pool novamente
      // para minPoolSize, se, seguindo um pico, o n�vel de load diminui e Conex�es n�o
      // s�o mais requeridas. Se maxIdleTime est� definido, maxIdleTimeExcessConnections
      // dever� ser um valor menor para que o par�metro tenho efeito. Zero significa que
      // n�o existir� nenhuma imposi��o, Conex�es em excesso n�o ser�o mais fechadas.
      poolConexoes.setMaxIdleTimeExcessConnections(30);

      // c3p0 � muito ass�ncrono. Opera��es JDBC lentas geralmente s�o executadas por
      // helper threads que n�o det�m travas de fechamento. Separar essas opera��es atrav�z
      // de m�ltiplas threads pode melhorar significativamente a performace, permitindo
      // que v�rias opera��es sejam executadas ao mesmo tempo.
      poolConexoes.setNumHelperThreads(3);

      // Se true, e se unreturnedConnectionTimeout est� definido com um valor positivo,
      // ent�o o pool capturar� a stack trace (via uma exce��o) de todos os checkouts
      // de Conex�es, e o stack trace ser� impresso quando o checkout de Conex�es der
      // timeout. Este param�tro � destinado para debug de aplica��es com leak de
      // Conex�es, isto �, aplica��es que ocasionalmente falham na libera��o/fechamento
      // de Conex�es, ocasionando o crescimento do pool, e eventualmente na sua exaust�o
      // (quando o pool atinge maxPoolSize com todas as suas conex�es em uso e perdidas).
      // Este param�tro deveria ser setado apenas para debugar a aplica��o, j� que capturar
      // o stack trace deixa mais o lento o precesso de check-out de Conex�es.
      poolConexoes.setDebugUnreturnedConnectionStackTraces(false);

      // Segundos. Se setado, quando uma aplica��o realiza o check-out e falha na realiza��o
      // do check-in [i.e. close()] de um Conex�o, dentro de per�odo de tempo especificado,
      // o pool ir�, sem cerimonias, destruir a conex�o [i.e. destroy()]. Isto permite
      // que aplica��es com ocasionais leaks de conex�o sobrevivam, ao inv�z de exaurir
      // o pool. E Isto � uma pena. Zero significa sem timeout, aplica��es deveriam fechar
      // suas pr�prias Conex�es. Obviamente, se um valor positivo � definido, este valor
      // deve ser maior que o maior valor que uma conex�o deveria permanecer em uso. Caso
      //# contr�rio, o pool ir� ocasionalmente matar conex�es ativas, o que � ruim. Isto
      // basicamente � uma p�ssima id�ia, por�m � uma funcionalidade pedida com frequ�ncia.
      // Consertem suas aplica��es para que n�o vazem Conex�es!!! Use esta funcionalidade
      // temporariamente em combina��o com  debugUnreturnedConnectionStackTraces para
      // descobrir onde as conex�es es�o vazando!
      poolConexoes.setUnreturnedConnectionTimeout(300);

    } catch (Exception ex) {
      escreverLogExcecao("N�o foi poss�vel criar uma conex�o nova C3P0 com o banco de dados", Ambiente.class.getName(), ex);
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
      escreverLogExcecao("N�o foi poss�vel obter uma nova conex�o do C3P0", Ambiente.class.getName(), e);
      return null;
    }
  }

  public static void escreverLogExcecao(String mensagem, String className, Exception e) {
    String mensagemExcecao = "";
    if ((e != null) && (!e.getMessage().isEmpty())) {
      mensagemExcecao = "Exce��o: " + e.getMessage() + " ";
    }
    mensagemExcecao += "Classe: " + className;

    //Escrevendo nos logs do servidor web (glssfish/tomcat)
    java.util.logging.Logger.getLogger(className).log(Level.SEVERE, mensagem + mensagemExcecao, e);
  }

  public static String getNomeEstacao() {
    try {
      return InetAddress.getLocalHost().getHostName().toUpperCase();
    } catch (Exception e) {
      return "N�O IDENTIFICADA";
    }
  }
      
}