import java.io.PrintStream;
import java.util.Hashtable;
import java.lang.String;
import java.util.ArrayList;

class TokenAsignaciones
{
	  //Variable para validar asignaciones a caracteres(ichr)
	  public static int segunda = 0;
	  //Tabla que almacenara los tokens declarados
	  private static Hashtable tabla = new Hashtable();
	  
	  //Listas que guardaran los tipos compatibles de las variables
	  private static ArrayList<Integer> intComp = new ArrayList();
	  private static ArrayList<Integer> decComp = new ArrayList();
	  private static ArrayList<Integer> strComp = new ArrayList();
	  private static ArrayList<Integer> chrComp = new ArrayList();

	  private static int ErrorSE=0;
	  
												//variable		//tipoDato
	public static void InsertarSimbolo(Token identificador, int tipo)
	{
		//En este metodo se agrega a la tabla de tokens el identificador que esta siendo declarado junto con su tipo de dato
		tabla.put(identificador.image, tipo);
	}

	public static void AcumErr(){
		ErrorSE++;
	}

	public static int getErr(){
		return ErrorSE;
	}

	public static void DesacumErr(){
		ErrorSE--;
	}
	  
	public static void SetTables()
	{
		/*En este metodo se inicializan las tablas, las cuales almacenaran los tipo de datos compatibles con:		
		 entero = intComp
		 decimal = decComp
		 cadena = strComp
		 caracter = chrComp
		*/
		intComp.add(44); //int
		intComp.add(48); //numero
		intComp.add(6); //incremento
		intComp.add(7); //decremento
		
		decComp.add(44);//int
		decComp.add(45);//float
		decComp.add(48);//numero
		decComp.add(50);//decimal
		
		chrComp.add(46);
		chrComp.add(52);
		
		strComp.add(47);//String
		strComp.add(51);//Cadena
	}
 
	public static String checkAsing(Token TokenIzq, Token TokenAsig)
	{
		//variables en las cuales se almacenara el tipo de dato del identificador y de las asignaciones (ejemplo: n1(tipoIdent1) = 2(tipoIdent2) + 3(tipoIdent2))
		int tipoIdent1;
		int tipoIdent2;		
							/* De la tabla obtenemos el tipo de dato del identificador  
								asi como, si el token enviado es diferente a algun tipo que no se declara como los numeros(48), los decimales(50),
								caracteres(52) y cadenas(51)
								entonces tipoIdent1 = tipo_de_dato, ya que TokenAsig es un dato*/
		if(TokenIzq.kind != 48 && TokenIzq.kind != 50)		
		{
			try 
			{
				//Si el TokenIzq.image existe dentro de la tabla de tokens, entonces tipoIdent1 toma el tipo de dato con el que TokenIzq.image fue declarado
				tipoIdent1 = (Integer)tabla.get(TokenIzq.image);	
			}
			catch(Exception e)
			{
				AcumErr();
				//Si TokenIzq.image no se encuentra en la tabla en la cual se agregan los tokens, el token no ha sido declarado, y se manda un error
				return getErr() + ". Error semántico en línea " + TokenIzq.beginLine + ". El identificador " + TokenIzq.image + " No ha sido declarado.\r\n";
			}
		}
		else 		
			tipoIdent1 = 0;
			
		//TokenAsig.kind != 48 && TokenAsig.kind != 50 && TokenAsig.kind != 51 && TokenAsig.kind != 52
		if(TokenAsig.kind == 49)	
		{
			/*Si el tipo de dato que se esta asignando, es algun identificador(kind == 49) 
			se obtiene su tipo de la tabla de tokens para poder hacer las comparaciones*/
			try
			{
				tipoIdent2 = (Integer)tabla.get(TokenAsig.image);
			}
			catch(Exception e)
			{
				AcumErr();
				//si el identificador no existe manda el error
				return getErr() + ". Error semántico en línea " + TokenIzq.beginLine + ". El identificador " + TokenAsig.image + " No ha sido declarado.\r\n";
			}
		}
				//Si el dato es entero(48) o decimal(50) o caracter(51) o cadena(52)
				//tipoIdent2 = tipo_del_dato
		else if(TokenAsig.kind == 48 || TokenAsig.kind == 50 || TokenAsig.kind == 51 || TokenAsig.kind == 52 || TokenAsig.kind == 6 || TokenAsig.kind == 7)
			tipoIdent2 = TokenAsig.kind;
		else //Si no, se inicializa en algun valor "sin significado(con respecto a los tokens)", para que la variable este inicializada y no marque error al comparar
			tipoIdent2 = 0; 

			
	  
		
		if(tipoIdent1 == 44) //Int
		{
			//Si la lista de enteros(intComp) contiene el valor de tipoIdent2, entonces es compatible y se puede hacer la asignacion
			if(intComp.contains(tipoIdent2))
				return " ";
			else //Si el tipo de dato no es compatible manda el error
				AcumErr();
				return getErr() + ". Error semántico en línea " + TokenIzq.beginLine + ". No se puede convertir " + TokenAsig.image + " a Entero.\r\n";
		}
		else if(tipoIdent1 == 45) //float
		{
			if(decComp.contains(tipoIdent2))
				return " ";
			else
				if(tipoIdent2==6 || tipoIdent2==7){
					AcumErr();
					return getErr() + ". Error semántico en línea " + TokenIzq.beginLine + ". No se puede usar " + TokenAsig.image + " con un Decimal.\r\n";
				}
				else{
					AcumErr();
					return getErr() + ". Error semántico en línea " + TokenIzq.beginLine + ". No se puede convertir " + TokenAsig.image + " a Decimal.\r\n";

				}
		}
		else if(tipoIdent1 == 46) //char
		{
			/*variable segunda: cuenta cuantos datos se van a asignar al caracter: 
				si a el caracter se le asigna mas de un dato (ej: 'a' + 'b') marca error 
				NOTA: no se utiliza un booleano ya que entraria en asignaciones pares o impares*/
			segunda++;
			if(segunda < 2)
			{
				if(chrComp.contains(tipoIdent2))
					return " ";				
				else
					AcumErr();
					return getErr() + ". Error semántico en línea " + TokenIzq.beginLine + ". No se puede convertir " + TokenAsig.image + " a Caracter.\r\n";
			}
			else //Si se esta asignando mas de un caracter manda el error
				AcumErr();
				return getErr() + ". Error semántico en línea " + TokenIzq.beginLine + ". No se puede asignar mas de un valor a un caracter.\r\n";
			
		}
		else if(tipoIdent1 == 47) //string
		{
			if(strComp.contains(tipoIdent2))
				return " ";
			else
				if(tipoIdent2==6 || tipoIdent2==7){
					AcumErr();
					return getErr() + ". Error semántico en línea " + TokenIzq.beginLine + ". No se puede usar " + TokenAsig.image + " con una Cadena.\r\n";
				}
				else {
					AcumErr();
					return getErr() + ". Error semántico en línea " + TokenIzq.beginLine + ". No se puede convertir " + TokenAsig.image + " a Cadena.\r\n";
				}
		}
		else
		{
			AcumErr();
			return getErr() + ". Error semántico en línea " + TokenIzq.beginLine + ". El Identificador " + TokenIzq.image + " no ha sido declarado.\n";
		}

	}	  
	
	
	/*Metodo que verifica si un identificador ha sido declarado, 
		ej cuando se declaran las asignaciones: i++, i--)*/ 
	public static String checkVariable(Token checkTok)
	{
		if(checkTok.kind == 49){
			try
			{
				//Intenta obtener el token a verificar(checkTok) de la tabla de los tokens
				int tipoIdent1 = (Integer)tabla.get(checkTok.image);
				return " ";
			}
			catch(Exception e)
			{
				//Si no lo puede obtener, manda el error
				AcumErr();
				return getErr() + ". Error semántico en línea " + checkTok.beginLine + ". El identificador " + checkTok.image + " No ha sido declarado.\r\n";
			}
		}
		else{
			return " ";
		}
	}

    public static String CheckFunction(Token TokenNFunc){
        int tipoIdent1;

        try
        {
            //Si el TokenIzq.image existe dentro de la tabla de tokens, entonces tipoIdent1 toma el tipo de dato con el que TokenIzq.image fue declarado
            tipoIdent1 = (Integer)tabla.get(TokenNFunc.image);
            return " ";
        }
        catch(Exception e)
        {
            AcumErr();
            //Si TokenIzq.image no se encuentra en la tabla en la cual se agregan los tokens, el token no ha sido declarado, y se manda un error
            return getErr() + ". Error semántico en línea " + TokenNFunc.beginLine + ". La función " + TokenNFunc.image + " No ha sido declarada.\r\n";
        }

    }

    public static String CheckScanString(Token checkTok){
		int tipoIdent1 = (Integer)tabla.get(checkTok.image);
		if(tipoIdent1 == 47){
			return " ";
		}
		else{
			AcumErr();
			return "Error";
		}
	}

	public static String CheckScanInt(Token checkTok){
		int tipoIdent1 = (Integer)tabla.get(checkTok.image);
		if(tipoIdent1 == 44){
			return " ";
		}
		else{
			AcumErr();
			return "Error";
		}
	}

 }
  
  
  
  
  
  
  