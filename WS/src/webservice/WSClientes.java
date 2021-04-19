package webservice;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import model.Cliente;
import biblioteca.Resposta;
import dao.DaoCliente;

@Path("clientes")
public class WSClientes
{
    @POST
    @Path("atualizar")
    @Produces(MediaType.APPLICATION_JSON)
    public Resposta atualizar(@FormParam("cliente") String json){	
    	Cliente obj = new Gson().fromJson(json, Cliente.class);
		return new DaoCliente().atualizar(obj);
    }
    
    @GET
    @Path("pesquisar")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Cliente> pesquisar(@QueryParam("filtro") String filtro){	
	    return new DaoCliente().pesquisar(filtro);
    }
    
    @DELETE
    @Path("excluir")
    @Produces(MediaType.APPLICATION_JSON)
    public Resposta excluir(@QueryParam("id") Integer id){	
	    return new DaoCliente().excluir(id);
    }
}
