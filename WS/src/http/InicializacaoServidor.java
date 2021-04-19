package http;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServlet;

public class InicializacaoServidor extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void init() {
		Connection conexao = Ambiente.getNovaConexao();
		
	    if (conexao == null)
	      System.out.print("Não foi possível criar o pool de conexões ao iniciar a aplicação!");
	    else {
	       try {
			  conexao.close();
		   } catch (SQLException e) {
			   System.out.print("Não foi possível fechar o pool de conexões ao iniciar a aplicação!");
		   }
	       conexao = null;
	    }
	}
}