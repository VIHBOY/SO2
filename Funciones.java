
//clase de las funciones, para guardar despues de la lectura del archivo
public class Funciones{
	private String x;
	private String name;
	private String contenido;
	public Funciones( String n, String v, String c){
		x =v; //en funcion de que esta (x, y etc)
		name = n; //nombre de la funcion
		contenido = c;//cuerpo
	}
	public String getX(){
		return this.x;
	}
	public String getName(){
		return this.name;
	}
	public String getContenido(){
		return this.contenido;
	}



}
