package dao;

import http.Ambiente;
import interfaces.DaoBasico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import biblioteca.Resposta;
import model.Cliente;

public class DaoCliente implements DaoBasico<Cliente>{
	
	@Override
	public Resposta atualizar(Cliente cliente) {
		Resposta retorno = new Resposta();
		
		try {
			StringBuilder sql = new StringBuilder();
			boolean novoRegistro = (cliente.getClienteId() == null || cliente.getClienteId() <= 0);
			
			if (novoRegistro) {
			  sql
			  .append("insert into CLIENTES(")		
			  .append("  NOME,")
			  .append("  SEXO,")
			  .append("  CPF,")
			  .append("  TELEFONE,")
			  .append("  CLIENTE_ID")			  
			  .append(") values (")
			  .append("  ?,")
			  .append("  ?,")
			  .append("  ?,")
			  .append("  ?,")
			  .append("  ?")
			  .append(")");
			} else {
			  sql
			  .append("update CLIENTES set")		
			  .append("  NOME = ?,")
			  .append("  SEXO = ?,")
			  .append("  CPF = ?, ")
			  .append("  TELEFONE = ? ")
			  .append("where CLIENTE_ID = ?");
			}
			
			ResultSet rs = null;
			Connection con = null;
			PreparedStatement stmt = null;
			
			try {
				con = Ambiente.getNovaConexao();
				
				if (novoRegistro) {
				  stmt = con.prepareStatement("select SEQ_CLIENTES.nextval from dual");
				  rs = stmt.executeQuery();
				  rs.next();
				  cliente.setClienteId(rs.getInt(1));
				  stmt.close();
				}
				 
				stmt = con.prepareStatement(sql.toString());
				
				stmt.setString(1, cliente.getNome());
				stmt.setString(2, cliente.getSexo());
				stmt.setString(3, cliente.getCpf());
				stmt.setString(4, cliente.getTelefone());
				stmt.setLong(5, cliente.getClienteId());
				
				stmt.executeUpdate();
				con.commit();
			} catch (SQLException e) {
				con.rollback();
				retorno.setHouveErro(true);
				retorno.setMensagem(e.getMessage());
			} finally {
				if (rs != null) 
				  rs.close();
				
				if (stmt != null) 
				  stmt.close();
				
				if (con != null)
				  con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			retorno.setHouveErro(true);
			retorno.setMensagem(e.getMessage());
		}
	
		return retorno;
	}
	
	@Override
	public Resposta excluir(Integer id) {
		Resposta retorno = new Resposta();
		
		try {			
			Connection con = null;
			PreparedStatement stmt = null;
			
			try {
				con = Ambiente.getNovaConexao();				 
				stmt = con.prepareStatement("delete from CLIENTES where CLIENTE_ID = ?");
				
				stmt.setInt(1, id);
				stmt.execute();
				con.commit();
			} catch (SQLException e) {
				con.rollback();
				retorno.setHouveErro(true);
				retorno.setMensagem(e.getMessage());
			} finally {
				if (stmt != null)
				  stmt.close();
				
				if (con != null)
				  con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			retorno.setHouveErro(true);
			retorno.setMensagem(e.getMessage());
		}
	
		return retorno;
	}
	
	@Override
	public List<Cliente> pesquisar(String filtro){
		StringBuilder sql = new StringBuilder();
		
		sql
		.append("select")		
		.append("  CLIENTE_ID,")
		.append("  NOME,")
		.append("  SEXO,")
		.append("  CPF,")
		.append("  TELEFONE ")
		.append("from ")
		.append("  CLIENTES ")
		.append(filtro == null ? "" : filtro);
		
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement stmt = null;
		List<Cliente> clientes = new ArrayList<Cliente>();
		
		try {
			try {
				con = Ambiente.getNovaConexao();
				stmt = con.prepareStatement(sql.toString());
				rs = stmt.executeQuery();
				  
				while (rs.next()){
					Cliente cliente = new Cliente();
					
					cliente.setClienteId(rs.getInt("CLIENTE_ID"));
					cliente.setNome(rs.getString("NOME"));
					cliente.setSexo(rs.getString("SEXO"));
					cliente.setCpf(rs.getString("CPF"));
					cliente.setTelefone(rs.getString("TELEFONE"));
					
					clientes.add(cliente);
				}
			} catch (Exception e) {
				e.printStackTrace();
				clientes = new ArrayList<Cliente>();
			} finally {
				if (rs != null)
				  rs.close();
				
				if (stmt != null)
				  stmt.close();
				
				if (con != null)
				  con.close();
			}			
		} catch (Exception e) {
			e.printStackTrace();
			clientes = new ArrayList<Cliente>();
		}
				
		return clientes;
	}
}
