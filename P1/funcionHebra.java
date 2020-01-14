
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public  class funcionHebra extends Thread {
		private String operacion;
		private Funciones[] funciones;
		private int nFunciones;
		private volatile String resultado;
		private volatile Boolean isParsing = true;
		private Pattern regex_multi = Pattern.compile( "(\\d+\\.?\\d*)\\*(\\d+\\.?\\d*)" );
		private Pattern regex_div = Pattern.compile( "(\\d+\\.?\\d*)/(\\d+\\.?\\d*)" );
		private Pattern regex_suma = Pattern.compile( "(\\d+\\.?\\d*)\\+(\\d+\\.?\\d*)");
		private Pattern regex_resta = Pattern.compile( "(\\d+\\.?\\d*)-(\\d+\\.?\\d*)" );
		private Pattern regex_letras = Pattern.compile("[A-Za-z]+");
		private Pattern regex_numero = Pattern.compile("^-?\\d+\\.?\\d*");
		public funcionHebra(String operacion, Funciones[] funciones, int nFunciones){
			//llamado desde el main
			this.operacion = operacion;
			this.funciones = funciones;
			this.nFunciones = nFunciones;
		}
		public funcionHebra(String nombre, String parametro, Funciones[] funciones, int nFunciones){
			//llamado desde otros threads
			int i;
			for (i = 0; i<nFunciones; i++){
				if  ( ((funciones[i]).getName()).equals(nombre)  ){
					Funciones temp = funciones[i];
					this.operacion = (temp.getContenido()).replace(temp.getX(), parametro);}}
			this.nFunciones = nFunciones;
			this.funciones = funciones;}
		public String getRes(){
			return this.resultado;}
		public Boolean isParsing(){
			return this.isParsing;}
		public String interior_parentesis(String op, int inicio){
			int contador = 1;
			int i;
			for ( i= inicio; contador>0;i++ ){
				if (op.charAt(i) == '('){
						contador++;}
				else if (op.charAt(i) == ')'){
					contador--;}}
			String temp = op.subSequence(inicio,(i-1)).toString();
			return temp;}
		public String parse(String op ){
			op = op.trim();
			Matcher match;
			if (( match = regex_letras.matcher(op) ).find()){ //funciones
				match.reset();
				funcionHebra[] arrayThreads;
				int anidadas = 0;
				int marca =0 ;
 				while (match.find(marca)){//aqui solo se cuenta cuantos hay
 					anidadas++;
 					String name= match.group();
 					int inicio = op.indexOf(name,marca)+name.length()+1;
 					String cont =  interior_parentesis(op,inicio);
 					marca= marca+name.length()+cont.length()+2;
 					match.reset();}
 				arrayThreads = new funcionHebra[anidadas];
 				match.reset();
 				int contador = 0;
 				marca = 0;
 				while (match.find(marca)){//aqui se inicializan los threads.
 					String name = match.group();
 					int inicio = op.indexOf(name,marca)+name.length()+1;
 					String cont =  interior_parentesis(op,inicio);
 					marca= marca+name.length()+cont.length()+2;
 					arrayThreads[contador] = new funcionHebra( name, parse(cont), this.funciones, this.nFunciones);
 					(arrayThreads[contador]).start();
 					contador++;}
 				Boolean flag = true;
 				while (flag){// se revisan que terminen
 					flag = false;
 					for ( int i = 0; i<anidadas;i++) {
 						if ((arrayThreads[i]).isParsing() == true){
							flag = true;}}}
 				match.reset();
 				contador = 0;
 				while(match.find()&&(contador<anidadas)){
 					String name = match.group();
 					int inicio = op.indexOf(name)+name.length()+1;
 					String cont =  interior_parentesis(op,inicio);
 					op = op.replace( name+"("+cont+")", (arrayThreads[contador]).getRes()) ;
 					match.reset(op);
 					contador++;}
 				return parse(op);}
			else if (  op.indexOf("(")!= -1 ){ //Parentesis
				int inicio = op.indexOf("(")+1;
				String temp,trozo;
				if ( (inicio>0)&&(Character.isLetter(op.charAt(inicio-1)) ==false )){
					trozo =(interior_parentesis(op,inicio));
					temp = parse( trozo );
					temp = op.replace( "("+trozo+")" ,temp ); //recolocado en la operacion inicial
					return ( parse( temp ) );}}
			else if ( (match = regex_div.matcher(op.trim() )).find() ){ //division
				match.reset();
				match.find();
				float a = Float.parseFloat(match.group(1));
				float b = Float.parseFloat(match.group(2));
				String temp = Float.toString(a/b);
				op = op.replace(match.group(), temp);
				return ( parse(op) );}
			else if ( (match = regex_multi.matcher(op.trim() )).find() ){ //multiplicacion
				match.reset();
				match.find();
				float a = Float.parseFloat(match.group(1));
				float b = Float.parseFloat(match.group(2));
				String temp = Float.toString(a*b);
				op = op.replace(match.group(), temp);
				return ( parse(op) );}
			else if ( (match = regex_resta.matcher(op.trim() )).find() ){//resta
				match.reset();
				match.find();
				float a = Float.parseFloat(match.group(1));
				float b = Float.parseFloat(match.group(2));
				String temp = Float.toString(a-b);
				op = op.replace(match.group(), temp);
				return ( parse(op) );}
			else if ( (match = regex_suma.matcher(op.trim() )).find() ){//suma
				match.reset();
				match.find();
				float a = Float.parseFloat(match.group(1));
				float b = Float.parseFloat(match.group(2));
				String temp = Float.toString(a+b);
				op = op.replace(match.group(), temp);
				return ( parse(op) );}
			else if ( (match = regex_numero.matcher(op.trim() )).find() ){//numero solo.
				return match.group();}
			return "error";}
	    public void run(){
	    	this.isParsing = true;
	    	this.resultado = parse(this.operacion);
	    	this.isParsing = false;}
}
