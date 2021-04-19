package biblioteca;

public class Resposta {
    private Boolean houveErro;
    private String mensagem;
    private Integer retornoInt;

    public Resposta(){
      this.houveErro = Boolean.FALSE;
	  this.mensagem = "";
	  this.retornoInt = 0;
    }

	public Boolean getHouveErro() {
		return houveErro;
	}

	public void setHouveErro(Boolean houveErro) {
		this.houveErro = houveErro;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Integer getRetornoInt() {
		return retornoInt;
	}

	public void setRetornoInt(Integer retornoInt) {
		this.retornoInt = retornoInt;
	}

	
}
