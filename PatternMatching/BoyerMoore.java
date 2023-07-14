package PatternMatching;

import java.io.IOException;
import java.io.RandomAccessFile;

import Entities.Movies;

public class BoyerMoore {

    static int NUM_CHARS = 256;
	
    public static int search(char txt[], char pat[]){
        int cont=0;
        boolean achou = false;
        int val=0;
        int s = 0, j;
        int m = pat.length; //tamanho do padrão 
        int n = txt.length; //tamanho do texto
        
        //vetores de deslocamento
        int badchar[] = new int[NUM_CHARS];
        int []bpos = new int[m + 1];
        int []shift = new int[m + 1];
        
        //montagem do vetor Bad Character - que possuirá as posições de cada elemento do padrão
        badCharHeuristic(pat, m, badchar);
        s = 0; 

        //initialize all occurrence of shift to 0
        for(int i = 0; i < m + 1; i++){
            shift[i] = 0;
        }
            
        //montagem do vetor Good Suffix
        /*
        1- O sufixo formado aparece anteriormente precedido por algum caractere diferente?
        2- O sufixo formado é prefixo?
        3- Reduz o sufixo e questiona se o novo sufixo formado é prefixo.
         */
        caso1(shift, bpos, pat, m); //Primeiro Caso
        caso2(shift, bpos, pat, m); //Segundo e Terceiro Caso

        while(s <= (n - m)){
            j = m-1;

            /* Keep reducing index j of pattern while
                characters of pattern and text are
                matching at this shift s */
            while(j >= 0 && pat[j] == txt[s+j]){
                j--;
            }

            /* If the pattern is present at current
                shift, then index j will become -1 after
                the above loop */
            if (j < 0){
                //System.out.println("Patterns occur at shift = " + s);
                s += (s+m < n)? m-badchar[txt[s+m]] : 1;
                achou = true;;
                cont++;
            }else{
                //j(posição onde ocorreu a falha) - valor do carctere ruim do texto no vetor 
                //System.out.println("posi: "+(int)(txt[s+j]));
                int pos = txt[s+j];
                int val1 = 1;
                if(pos<=256){
                    val1 = max(1, j - badchar[txt[s+j]]);
                }
                
                int val2 = shift[j + 1];

                if(val1>val2){
                    s += val1;
                }else{
                    s += val2;
                }
                
            }   
        }
        return cont;
    }

	//Compara dois inteiros e retorna o maior
	static int max (int a, int b) {  //os parametros sao as duas opções possiveis
        if(a>b){
            return a;
        }else{
            return b;
        }
    }
	
	static void badCharHeuristic(char []str, int tam, int badchar[]){
        //inicializa todas as 256 posições do vetor
        for (int i = 0; i < NUM_CHARS; i++)
            badchar[i] = -1;

        //preenche o vetor nas posições das letras que constituem o padrão
        for (int i = 0; i < tam; i++){
            badchar[(int) str[i]] = i;
        }   
	}

     // preprocessing for strong good suffix rule
    static void caso1(int []shift, int []bpos, char []pat, int m){
        // m is the length of pattern
        int i = m, j = m + 1;
        bpos[i] = j;

        while(i > 0){
            /*if character at position i-1 is not
            equivalent to character at j-1, then
            continue searching to right of the
            pattern for border */
            while(j <= m && pat[i - 1] != pat[j - 1]){
                /* the character preceding the occurrence of t
                in pattern P is different than the mismatching
                character in P, we stop skipping the occurrences
                and shift the pattern from i to j */
                if (shift[j] == 0){
                    shift[j] = j - i;
                }
                //Update the position of next border
                j = bpos[j];
            }
            /* p[i-1] matched with p[j-1], border is found.
            store the beginning position of border */
            i--; j--;
            bpos[i] = j;
        }
    }

    //Preprocessing for case 2
    static void caso2(int []shift, int []bpos, char []padrao, int m){
        int i, j;
        j = bpos[0];
        for(i = 0; i <= m; i++){
            /* set the border position of the first character
            of the pattern to all indices in array shift
            having shift[i] = 0 */
            if(shift[i] == 0){
                shift[i] = j;
            }
            
            /* suffix becomes shorter than bpos[0],
            use the position of next widest border
            as value of j */
            if (i == j){
                j = bpos[j];
            }  
        }
    }

    public void readArq(String arquivo, String padrao) throws IOException{ //leitura do arquivo inteiro - todos os registros validos
        RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");
        
        arq.seek(0); //posiciona o ponteiro no inicio do arquivo = cabecalho
        int cabecalho = arq.readInt(); //descobre qual e o ultimo registro do arquivo 
        int pos = 4; //posicao do primeiro registro
        int tam = 0;
        Movies temp = new Movies();
        boolean acabou = false;
        int aux=0;
        do{
            arq.seek(pos); //posiciona o ponteiro no inicio do proximo registro
            boolean lapide = arq.readBoolean(); //leitura da lapide
            tam = arq.readInt(); //leitura do tamanho do registro
            if(lapide==true){ //verifica se o registro e valido
                byte[] arrayByte = new byte[tam]; 
                arq.read(arrayByte); //leitura do array de bytes
                temp.fromByteArray(arrayByte); //transforma o array de bytes em um objeto Movie
                String texto = temp.toString();
                //System.out.println(texto);
                int cont = search(texto.toCharArray(), padrao.toCharArray()); //chamada do metodo para procurar o padrao no texto
                System.out.println(aux++); //Imprime o texto
                if(cont>0){ //testa se foi possivel achar o padrão no texto
                    System.out.println("O padão foi encontado no filme " + cont + " vez/vezes: ");
                    System.out.println(texto);
                }
                if(temp.getId()==cabecalho){ //testa se o arquivo chegou no fim
                    acabou = true;
                }
            } 
            pos += tam + 4 + 1; //adiciona a quantidade de bytes para chegar no inicio do proximo registro
        }while(!acabou);    
        arq.close();
    }
       
}


