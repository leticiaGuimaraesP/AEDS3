package PatternMatching;
/* Java Program for Bad Character Heuristic of Boyer
Moore String Matching Algorithm */

class AWQ{
	
	static int NO_OF_CHARS = 256;
	
	//A utility function to get maximum of two integers
	static int max (int a, int b) { 
        if(a>b){
            return a;
        }else{
            return b;
        }
        //return (a > b)? a: b; 
    }

	
	static void badCharHeuristic(char []str, int size, int badchar[]){
        //inicializa todas as 256 posições do vetor
        for (int i = 0; i < NO_OF_CHARS; i++)
            badchar[i] = -1;

        //preenche o vetor nas posições das letras que constituem o padrão
        for (int i = 0; i < size; i++){
            badchar[(int) str[i]] = i;

        }
            
	}

	/* A pattern searching function that uses Bad
	Character Heuristic of Boyer Moore Algorithm */
    static int search(char txt[], char pat[]){
        int val=0;
        int m = pat.length; //tamanho do padrão 
        int n = txt.length; //tamanho do texto

        int badchar[] = new int[NO_OF_CHARS];

        //monta o vetor que possuirá as posições de cada elemento do padrão
        badCharHeuristic(pat, m, badchar);

        int s = 0; // s is shift of the pattern with
                    // respect to text
        //there are n-m+1 potential alignments
        while(s <= (n - m)){

            int j = m-1;

            /* Keep reducing index j of pattern while
                characters of pattern and text are
                matching at this shift s */
            while(j >= 0 && pat[j] == txt[s+j]){
                j--;
            }

            /* If the pattern is present at current
                shift, then index j will become -1 after
                the above loop */
            if (j < 0)
            {
                System.out.println("Patterns occur at shift = " + s);

                /* Shift the pattern so that the next
                    character in text aligns with the last
                    occurrence of it in pattern.
                    The condition s+m < n is necessary for
                    the case when pattern occurs at the end
                    of text */
                //txt[s+m] is character after the pattern in text
                s += (s+m < n)? m-badchar[txt[s+m]] : 1;

            }

            else
                /* Shift the pattern so that the bad character
                    in text aligns with the last occurrence of
                    it in pattern. The max function is used to
                    make sure that we get a positive shift.
                    We may get a negative shift if the last
                    occurrence of bad character in pattern
                    is on the right side of the current
                    character. */
                //j(posição onde ocorreu a falha) - valor do carctere ruim do texto no vetor 
                val = max(1, j - badchar[txt[s+j]]);
                s += val;
        }
        return val;
	}

	/* Driver program to test above function */
	public static void main(String []args) {
		
		char txt[] = "ABAAABCD".toCharArray();
		char pat[] = "ABC".toCharArray();
		search(txt, pat);
	}
}

