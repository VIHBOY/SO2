import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class P1 {
    public static void main(String[] args) {
	  	String fileName = "funciones.txt";
	  	String regex = "(\\w+)\\(([a-z])\\) *= *(.*)";
	  	Pattern patron_linea = Pattern.compile(regex);
	  	Matcher match;
	  	String string_num;		//lectura de la primera linea
	  	int num_funciones;		//string_num pasado a int
	  	String linea;			//linea del .txt
	  	Funciones[] funciones;	//array de "Funcion"
	  	int i;
	  	//-------------------------------------------------------------
	  	//LECTURA DEL ARCHIVO
	  	try{
		  	FileReader fileReader = new FileReader(fileName);
		  	BufferedReader bufferedReader = new BufferedReader(fileReader);
		  	string_num = bufferedReader.readLine();
		  	i = 0;
		  	num_funciones = Integer.parseInt(string_num);
		  	funciones = new Funciones[num_funciones];

		  	//-------------------------------------------------------
		  	//LECTURA LINEA POR LINEA
		  	while( i<num_funciones ){

		  		linea = bufferedReader.readLine();
				match = patron_linea.matcher(linea.trim());
				if ( match.find() ){

					funciones[i] = new Funciones(match.group(1).trim(),match.group(2).trim(), match.group(3).trim());
				}
				else{
					System.out.println("NO MATCH");
				}
		  		i++;
		  	}
		 	System.out.println("Funciones procesadas");


		 	Boolean funcionando = true;

		 	while(funcionando){

		 		System.out.print(">Ingrese operacion: ");
				Scanner scan = new Scanner(System.in);
	  			String input = scan.nextLine();
	  			if(input.equals("")){
	  				System.exit(0);
	  			}
				funcionHebra resultado = new funcionHebra(input.trim(),funciones,num_funciones);
				resultado.start();
				Boolean flag = true;
				while (flag){
					flag = resultado.isParsing();
				}
				System.out.println(">Resultado: "+resultado.getRes());
		 		}

		}
		catch(FileNotFoundException ex){
			System.out.println("No existe ese archivo\n");
		}
		catch(IOException ex){
		 	System.out.println("Error de lectura\n");
		}
	}

}
