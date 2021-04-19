package interfaces;

import java.util.List;
import biblioteca.Resposta;

public interface DaoBasico<T> {
	public Resposta atualizar(T objeto);
	public Resposta excluir(Integer id);
	public List<T> pesquisar(String filtro);
}
