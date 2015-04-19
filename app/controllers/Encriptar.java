package controllers;

/**
 * Created by scvalencia on 4/17/15.
 */
import java.util.HashMap;

public class Encriptar {
    public static char[]q1={'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9'};
    //public static char[]q3={'4','b','P','Q','R','S','T','U','V','W','X','Y','Z','a','c','d','e','f','A','B','C','D','E','g','h','m','n','r','o','p','q','O','s','t','u','v','y','z','0','3','i','j','k','l','5','6','w','x','7','8','9','F','G','H','1','2','I','J','K','L','M','N'};

    public static String word="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static char[]q2=word.toCharArray();
    public static HashMap<Character,Character> letras= new HashMap<Character, Character>();
    public static HashMap<Character,Character> letrasReversa= new HashMap<Character, Character>();
    public Encriptar(){
        for(int i=0; i<q1.length;i++)
        {
            letras.put(q1[i], q2[i]);
            letrasReversa.put(q2[i], q1[i]);
        }
    }
    // retrieving


    //encriptar
    //var word="HOla"
    //resultado1="Uczn"
    //resultado2="cznU"

    public static String cambiarLetras(String w)
    {
        String wr="";
        char[] arr=w.toCharArray();
        char[] arrresp=new char[arr.length];
        if(w!=""){
            for(int i= 0; i<arr.length;i++)
            {
                char a=letras.get(arr[i]);
                arrresp[i]=a;
            }

            for(int i= 0; i<arrresp.length;i++)
            {
                wr+=arrresp[i];
            }

        }
        return wr;
    }

    public static String cambiarLetrasDesencriptar(String w)
    {
        String wr="";
        char[] arr=w.toCharArray();
        char[] arrresp=new char[arr.length];
        if(w!=""){
            for(int i= 0; i<arr.length;i++)
            {
                char a=letrasReversa.get(arr[i]);
                arrresp[i]=a;
            }

            for(int i= 0; i<arrresp.length;i++)
            {
                wr+=arrresp[i];
            }

        }
        return wr;
    }

    public static String moduloPalabra(String w)
    {
        char[] r=w.toCharArray();
        char[] respuesta=new char[w.length()];
        String resp="";
        if(w!=""){

            for(int i= 0; i<w.length()-1;i++)
            {
                respuesta[i]=r[i+1];
            }
            respuesta[w.length()-1]=r[0];
            for(int i= 0; i<respuesta.length;i++)
            {
                resp+=respuesta[i];
            }

        }
        return resp;
    }




    public static String moduloPalabraDesencriptar(String w)
    {
        char[] r=w.toCharArray();
        char[] respuesta=new char[w.length()];
        String resp="";
        if(w!=""){
            for(int i= 0; i<w.length()-1;i++)
            {
                respuesta[i+1]=r[i];
            }
            respuesta[0]=r[w.length()-1];
            for(int i= 0; i<respuesta.length;i++)
            {
                resp+=respuesta[i];
            }

        }
        return resp;
    }

    public static String encriptando(String a){
        String xxx=moduloPalabra(a);
        return cambiarLetras(xxx);
    }

    public static String desencriptando(String a){
        String xxx=moduloPalabraDesencriptar(a);
        return cambiarLetrasDesencriptar(xxx);
    }
}
