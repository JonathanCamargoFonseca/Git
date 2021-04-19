package http;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServlet;

public class InicializacaoServidor extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void init() {
		Connection conexao = Ambiente.getNovaConexao();
		
	    if (conexao == null)
	      System.out.print("N�o foi poss�vel criar o pool de conex�es ao iniciar a aplica��o!");
	    else {
	       try {
			  conexao.close();
		   } catch (SQLException e) {
			   System.out.print("N�o foi poss�vel fechar o pool de conex�es ao iniciar a aplica��o!");
		   }
	       conexao = null;
	    }
	}
}