package DataStructure;
import java.io.IOException;
import java.io.RandomAccessFile;

import ArchieveManipulation.Crud;
import Entities.Movies;

public class Sort {
    public static void distribuicao(int qtd) throws IOException{ //primeira etapa da ordenação, onde os registros serão separados em blocos de tamanho 500 e ordenados em memória principal
        RandomAccessFile arq = new RandomAccessFile("arquivo.txt", "rw");

        //DISTRIBUIÇAO
        arq.seek(0); //posiciona o ponteiro no inicio do arquivo = cabecalho
        int cabecalho = arq.readInt(); //descobre qual e o ultimo registro do arquivo 
        int pos = 4; //posicao do primeiro registro
        int tam = 0;
        Movies[] array = new Movies[qtd];
        boolean acabou = false;

        int contArq = 0; //define o arquivo que receberá o array de registros
        int contArray = 0; //contabiliza os elementos do array

        do{
            //Primeira parte - preenche o array
            if(contArray<qtd && !acabou){
                arq.seek(pos); //posiciona o ponteiro no inicio do proximo registro
                boolean lapide = arq.readBoolean(); //leitura da lapide
                tam = arq.readInt(); //leitura do tamanho do registro
                if(lapide==true){ //verifica se o registro e valido
                    array[contArray] = new Movies();
                    byte[] arrayByte = new byte[tam]; 
                    arq.read(arrayByte); //leitura do array de bytes
                    array[contArray].fromByteArray(arrayByte); //transforma o array de bytes em um objeto Movie  
                    
                    if(array[contArray].getId()==cabecalho){ //testa se o arquivo de leitura chegou no fim
                        acabou = true;
                    }
                    contArray++; //acrescenta 1 para acompanhar a proxima posiçao
                }
                pos += tam + 4 + 1; //adiciona a quantidade de bytes para chegar no inicio do proximo registro
            

            //Segunda parte - Array ja esta preenchido, então será transferido para o proximo arquivo
            }else{
                array = quicksort(array, 0, contArray-1); //ordenação em memória primária
                if(contArq%2==0){ //se o contador de arquivo for par, o vetor sera inserido no primeiro arquivo
                    for(int i=0; i<contArray; i++){
                        Crud.writeArq(array[i], "arquivo1.txt");
                    } 
                }else{ //caso contrario, o vetor sera inserido no segundo arquivo 
                    for(int i=0; i<contArray; i++){
                        Crud.writeArq(array[i], "arquivo2.txt");
                    }   
                }
                //essa parte so é importante quando o vetor não possui 500 elementos, isso é, quando o arquivo lido chego no fim
                if(contArray==qtd){ 
                    contArq++; //troca o arquivo que ira receber o proximo array
                    contArray=0; //para o array ser preenchido novamente, é necessario zerar o contador
                }else{ //se o vetor possuir menos que 500 elementos, significa que a distribuiçao chegou ao fim
                    contArray = -1;
                }
                
            }
        }while(!acabou || contArray>=0); 
        
        arq.close();
    }

    public static void intercalacaoBalanceadaComum() throws IOException{ //método da ordenação comum, contendo todas as estapas
        RandomAccessFile arq1 = new RandomAccessFile("arquivo1.txt", "rw");
        RandomAccessFile arq2 = new RandomAccessFile("arquivo2.txt", "rw");
        RandomAccessFile arq3 = new RandomAccessFile("arquivo3.txt", "rw");
        RandomAccessFile arq4 = new RandomAccessFile("arquivo4.txt", "rw");
        
        //reiniciar todos os arquivos temporários antes de iniciar a ordenação para evitar sobrescritas de ordenações anteriores
        Crud.reiniciar("arquivo1.txt");
        Crud.reiniciar("arquivo2.txt");
        Crud.reiniciar("arquivo3.txt");
        Crud.reiniciar("arquivo4.txt");

        distribuicao(500);    
        
        //INTERCALAÇAO
        int tam = 1000; //500*2
        int pos1 = 4, pos2 = 4;
        Movies movie1 = new Movies();
        Movies movie2 = new Movies();

        int qtdRegistro = qtdeRegistros(); //4000
        int qtdBlocos;// quantidade de blocos = qtd total de registros/(1000*tam)
        int cont=1;
        int contArqLeitura = 0; //define os arquivos que serao lidos
        int contArq = 0; //define em qual arquivo escrever
        String ultimoArq = "";

        float result = qtdRegistro/(tam);
        qtdBlocos = (int)Math.ceil(result);
        if(qtdRegistro%2==1){
            qtdBlocos += 1;
        }

        do{
            if(contArqLeitura%2==0){ //quando os registros serão lidos dos arquivos 1 e 2
                System.out.println("Lendo os arquivos 1 e 2 - " + qtdBlocos);
                do{
                    System.out.println("Bloco: "+cont);
                    for(int i=0; i<tam; i++){
                        movie1.setId(-1);
                        movie2.setId(-1);
                        //se o ponteiro chegar no final de algum arquivo lido, é necessario interromper a leitura deste!!!!
                        boolean acabou1=false, acabou2=false;
                        
                        if(!isFim("arquivo1.txt", pos1)){ //se o arquivo chegou ao final
                            movie1 = Crud.readPos(pos1, "arquivo1.txt");
                        }else{
                            acabou1 = true;
                        }
                        if(!isFim("arquivo2.txt", pos2)){ //se o arquivo chegou ao final
                            movie2 = Crud.readPos(pos2, "arquivo2.txt");
                        }else{
                            acabou2 = true;
                        }
                        if(isFim("arquivo1.txt", pos2)&&isFim("arquivo2.txt", pos2)){ //se ambos os arquivos chegaram ao final, quebra o loop
                            break;
                        }
                        if(movie1.getId() == -1 && movie2.getId() == -1){
                            break;
                        }
                        if(contArq%2==0){ //determina qual arquivo receberá os registros lidos
                            //bloco condicional comparando os ids para determinar qual objeto deve ser escrito
                            if(movie1.getId() == -1){
                                Crud.writeArq(movie2, "arquivo3.txt");
                                byte[] arrayB = movie2.toByteArray();
                                pos2 += 4  + 1 + arrayB.length;
                            }else if(movie2.getId() == -1){
                                Crud.writeArq(movie1, "arquivo3.txt");
                                byte[] arrayB = movie1.toByteArray();
                                pos1 += 4 + 1 +arrayB.length;
                            }else if(acabou2 || movie1.getId()>movie2.getId()){
                                Crud.writeArq(movie2, "arquivo3.txt");
                                byte[] arrayB = movie2.toByteArray();
                                pos2 += 4  + 1 + arrayB.length;
                            }else if(acabou1 || movie1.getId()<movie2.getId()){
                                Crud.writeArq(movie1, "arquivo3.txt");
                                byte[] arrayB = movie1.toByteArray();
                                pos1 += 4 + 1 +arrayB.length;
                            }
                        }else{
                            //bloco condicional comparando os ids para determinar qual objeto deve ser escrito
                            if(movie1.getId() == -1){
                                Crud.writeArq(movie2, "arquivo4.txt");
                                byte[] arrayB = movie2.toByteArray();
                                pos2 += 4  + 1 + arrayB.length;
                            }else if(movie2.getId() == -1){
                                Crud.writeArq(movie1, "arquivo4.txt");
                                byte[] arrayB = movie1.toByteArray();
                                pos1 += 4 + 1 +arrayB.length;
                            }else if(acabou1 || movie1.getId()>movie2.getId()){
                                Crud.writeArq(movie2, "arquivo4.txt");
                                byte[] arrayB = movie2.toByteArray();
                                pos2 += 4 + 1 + arrayB.length;
                            }else if(acabou2 || movie1.getId()<movie2.getId()){
                                Crud.writeArq(movie1, "arquivo4.txt");
                                byte[] arrayB = movie1.toByteArray();
                                pos1 += 4 + 1 + arrayB.length;
                            }
                        }
                    }
                    contArq++; //define em qual registro escrever
                    cont++; //aux para saber se chegou ao fim dos arquivos lidos
                }while(cont<=qtdBlocos);
                cont=1;
                //reiniciar os dois arquivos utilizados anteriormente antes que eles sejam necessários novamente
                Crud.reiniciar("arquivo1.txt"); 
                Crud.reiniciar("arquivo2.txt");
            }else{
                System.out.println("Lendo os arquivos 3 e 4 - "+ qtdBlocos);
                do{
                    System.out.println("Bloco: "+cont);
                    for(int i=0; i<tam; i++){ //quando os registros serão lidos dos arquivos 3 e 4
                        boolean acabou1=false, acabou2=false;
                        movie1.setId(-1);
                        movie2.setId(-1);

                        if(!isFim("arquivo3.txt", pos1)){
                            movie1 = Crud.readPos(pos1, "arquivo3.txt");
                        }else{
                            acabou1 = true;
                        }
                        if(!isFim("arquivo4.txt", pos2)){ //se o arquivo chegou ao final
                            movie2 = Crud.readPos(pos2, "arquivo4.txt");
                        }else{
                            acabou2 = true;
                        }
                        if(isFim("arquivo3.txt", pos2)&&isFim("arquivo4.txt", pos2)){ //se o arquivo chegou ao final
                            break;
                        }
                        if(movie1.getId() == -1 && movie2.getId() == -1){
                            break;
                        }
                        if(cont%2==1){ //determina qual arquivo receberá os registros lidos
                            //bloco condicional comparando os ids para determinar qual objeto deve ser escrito
                            if(movie1.getId() == -1){
                                Crud.writeArq(movie2, "arquivo1.txt");
                                byte[] arrayB = movie2.toByteArray();
                                pos2 += 4  + 1 + arrayB.length;
                            }else if(movie2.getId() == -1){
                                Crud.writeArq(movie1, "arquivo1.txt");
                                byte[] arrayB = movie1.toByteArray();
                                pos1 += 4 + 1 +arrayB.length;
                            }else if(acabou2 || movie1.getId()>movie2.getId()){
                                Crud.writeArq(movie2, "arquivo1.txt");
                                byte[] arrayB = movie2.toByteArray();
                                pos2 += 4  + 1 + arrayB.length;
                            }else if(acabou1 || movie1.getId()<movie2.getId()){
                                Crud.writeArq(movie1, "arquivo1.txt");
                                byte[] arrayB = movie1.toByteArray();
                                pos1 += 4 + 1 +arrayB.length;
                            }
                        }else{
                            //bloco condicional comparando os ids para determinar qual objeto deve ser escrito
                            if(movie1.getId() == -1){
                                Crud.writeArq(movie2, "arquivo2.txt");
                                byte[] arrayB = movie2.toByteArray();
                                pos2 += 4  + 1 + arrayB.length;
                            }else if(movie2.getId() == -1){
                                Crud.writeArq(movie1, "arquivo2.txt");
                                byte[] arrayB = movie1.toByteArray();
                                pos1 += 4 + 1 +arrayB.length;
                            }else if(acabou1 || movie1.getId()>movie2.getId()){
                                Crud.writeArq(movie2, "arquivo2.txt");
                                byte[] arrayB = movie2.toByteArray();
                                pos2 += 4 + 1 + arrayB.length;
                            }else if(acabou2 || movie1.getId()<movie2.getId()){
                                Crud.writeArq(movie1, "arquivo2.txt");
                                byte[] arrayB = movie1.toByteArray();
                                pos1 += 4 + 1 + arrayB.length;
                            }
                        }
                    }
                    contArq++; //define em qual arquivo escrever
                    cont++; //aux para saber se chegou ao fim
                }while(cont<=qtdBlocos);
                cont=1;
                //reiniciar os dois arquivos utilizados anteriormente antes que eles sejam necessários novamente
                Crud.reiniciar("arquivo3.txt");
                Crud.reiniciar("arquivo4.txt");
            }
            tam *= 2;
            contArq = 0;
            result = (float) qtdBlocos/2;
            if(result==0.5){ //não permite loop infinito, para quando a quantidade de blocos existentes for 1
                if(contArqLeitura%2==0){
                    ultimoArq += "arquivo3.txt";
                }else{
                    ultimoArq += "arquivo1.txt";
                }
                break;
            }else{
                qtdBlocos = (int)Math.ceil(result);
            }
            contArqLeitura++; //altera os arquivos que serão lidos
            pos1 = pos2 = 4;
        }while(qtdBlocos>=1);
        
        arq1.close();
        arq2.close();
        arq3.close();
        arq4.close();
        Crud.writeArqOrdenado(ultimoArq); //escrever os registros ordenados no arquivo principal
    }

    public static void intercalacaoBalanceadaVariavel(int qtd) throws IOException{ //método de ordenação variável, contendo todas as etapas
        RandomAccessFile arq1 = new RandomAccessFile("arquivo1.txt", "rw");
        RandomAccessFile arq2 = new RandomAccessFile("arquivo2.txt", "rw");
        RandomAccessFile arq3 = new RandomAccessFile("arquivo3.txt", "rw");
        RandomAccessFile arq4 = new RandomAccessFile("arquivo4.txt", "rw");
        
        //reiniciar todos os arquivos temporários antes de iniciar a ordenação para evitar sobrescritas de ordenações anteriores
        Crud.reiniciar("arquivo1.txt");
        Crud.reiniciar("arquivo2.txt");
        Crud.reiniciar("arquivo3.txt");
        Crud.reiniciar("arquivo4.txt");
        
        distribuicao(qtd);    
        
        //INTERCALAÇAO
        int tam = qtd*2; //1000*2 ou 4*2
        int pos1 = 4, pos2 = 4;
        Movies movie1 = new Movies();
        Movies movie2 = new Movies();

        int qtdRegistro = qtdeRegistros(); 
        int qtdBlocos;// quantidade de blocos = qtd total de registros/(1000*tam)
        int cont=1;
        int contArqLeitura = 0; //define os arquivos que serao lidos
        int contArq = 0; //define qual arquivo escrever
        String ultimoArq = "";

        float result = qtdRegistro/(tam);
        qtdBlocos = (int)Math.ceil(result);
        if(qtdRegistro%2==1){
            qtdBlocos += 1;
        }

        do{
            if(contArqLeitura%2==0){ //quando os registros serão lidos dos arquivos 1 e 2
                System.out.println("Lendo os arquivo 1 e 2 - " + qtdBlocos);
                int cont1=0, cont2=0;
                do{
                    System.out.println("Bloco: "+cont);
                    int tam1 = defineTam("arquivo1.txt", pos1); //organiza o tamanho de cada bloco do primeiro arq
                    int tam2 = defineTam("arquivo2.txt", pos2); //organiza o tamanho de cada bloco do segundo arq
                    tam = (tam1+tam2)*500; //calcula a quantidade de registros que serão comparados do arquivo 1 e 2

                    for(int i=0; i<tam; i++){ //loop que percorre os n registros do bloco
                        movie1.setId(-1);
                        movie2.setId(-1);
                        //se o ponteiro chegar no final de algum arquivo lido, é necessario interromper a leitura deste!!!!
                        boolean acabou1=false, acabou2=false;

                        if(!isFim("arquivo1.txt", pos1) || cont1<tam1*500){ //testa se chegou no final do bloco
                            movie1 = Crud.readPos(pos1, "arquivo1.txt");
                        }else{
                            acabou1 = true;
                        }
                        if(!isFim("arquivo2.txt", pos2) || cont2<tam2*500){ //testa se chegou no final do bloco
                            movie2 = Crud.readPos(pos2, "arquivo2.txt");
                        }else{
                            acabou2 = true;
                        }
                        if((isFim("arquivo1.txt", pos2)&&isFim("arquivo2.txt", pos2)) || (cont1>=tam1*500 && cont2>=tam2*500)){
                            break;
                        }
                        if(movie1.getId() == -1 && movie2.getId() == -1){
                            break;
                        }

                        if(contArq%2==0){ //determina em qual arquivo recebera os registros lidos
                            //bloco condicional comparando os ids para determinar qual objeto deve ser escrito
                            if(movie1.getId() == -1){
                                Crud.writeArq(movie2, "arquivo3.txt");
                                byte[] arrayB = movie2.toByteArray();
                                pos2 += 4  + 1 + arrayB.length;
                                cont2++;
                            }else if(movie2.getId() == -1){
                                Crud.writeArq(movie1, "arquivo3.txt");
                                byte[] arrayB = movie1.toByteArray();
                                pos1 += 4 + 1 +arrayB.length;
                                cont1++;
                            }else if(acabou1 || movie1.getId()>movie2.getId()){
                                Crud.writeArq(movie2, "arquivo3.txt");
                                byte[] arrayB = movie2.toByteArray();
                                pos2 += 4 + 1 + arrayB.length;
                                cont2++;
                            }else if(acabou2 || movie1.getId()<movie2.getId()){
                                Crud.writeArq(movie1, "arquivo3.txt");
                                byte[] arrayB = movie1.toByteArray();
                                pos1 += 4 + 1 + arrayB.length;
                                cont1++;
                            }
                        }else{
                            //bloco condicional comparando os ids para determinar qual objeto deve ser escrito
                            if(movie1.getId() == -1){
                                Crud.writeArq(movie2, "arquivo4.txt");
                                byte[] arrayB = movie2.toByteArray();
                                pos2 += 4  + 1 + arrayB.length;
                                cont2++;
                            }else if(movie2.getId() == -1){
                                Crud.writeArq(movie1, "arquivo4.txt");
                                byte[] arrayB = movie1.toByteArray();
                                pos1 += 4 + 1 +arrayB.length;
                                cont1++;
                            }else if(acabou1 || movie1.getId()>movie2.getId()){
                                Crud.writeArq(movie2, "arquivo4.txt");
                                byte[] arrayB = movie2.toByteArray();
                                pos2 += 4 + 1 + arrayB.length;
                                cont2++;
                            }else if(acabou2 || movie1.getId()<movie2.getId()){
                                Crud.writeArq(movie1, "arquivo4.txt");
                                byte[] arrayB = movie1.toByteArray();
                                pos1 += 4 + 1 + arrayB.length;
                                cont1++;
                            }
                        }
                    }
                    cont += (tam/qtd*2) - 1;
                    contArq++; //define em qual registro escrever
                }while(cont<=qtdBlocos);
                cont=1;
                //reiniciar os dois arquivos utilizados anteriormente antes que eles sejam necessários
                Crud.reiniciar("arquivo1.txt");
                Crud.reiniciar("arquivo2.txt");
                
                //testa se não será necessário usar mais intercalções entre os blocos - se chegou ao fim da ordenação
                if(isFim("arquivo4.txt", 4)){
                    ultimoArq = "arquivo3.txt";
                    break;
                }
            }else{
                System.out.println("Lendo os arquivos 3 e 4 - " + qtdBlocos);
                int cont1=0, cont2=0;
                do{
                    System.out.println("Bloco: "+cont);
                    int tam1 = defineTam("arquivo3.txt", pos1);
                    int tam2 = defineTam("arquivo4.txt", pos2);
                    tam = (tam1+tam2)*500;

                    for(int i=0; i<tam; i++){
                        movie1.setId(-1);
                        movie2.setId(-1);
                        //se o ponteiro chegar no final de algum arquivo lido, é necessario interromper a leitura deste!!!!
                        boolean acabou1=false, acabou2=false;

                        if(!isFim("arquivo3.txt", pos1) || cont1<tam1*500){ //testa se chegou no final do bloco
                            movie1 = Crud.readPos(pos1, "arquivo3.txt");
                        }else{
                            acabou1 = true;
                        }
                        if(!isFim("arquivo4.txt", pos2) || cont2<tam2*500){ //testa se chegou no final do bloco
                            movie2 = Crud.readPos(pos2, "arquivo4.txt");
                        }else{
                            acabou2 = true;
                        }
                        if((isFim("arquivo3.txt", pos2)&&isFim("arquivo4.txt", pos2)) || (cont1>=tam1*500 && cont2>=tam2*500)){
                            break;
                        }
                        if(movie1.getId() == -1 && movie2.getId() == -1){
                            break;
                        }

                        if(contArq%2==0){ //determina em qual arquivo recebera os registros lidos
                            //bloco condicional comparando os ids para determinar qual objeto deve ser escrito
                            if(movie1.getId() == -1){
                                Crud.writeArq(movie2, "arquivo1.txt");
                                byte[] arrayB = movie2.toByteArray();
                                pos2 += 4  + 1 + arrayB.length;
                                cont2++;
                            }else if(movie2.getId() == -1){
                                Crud.writeArq(movie1, "arquivo1.txt");
                                byte[] arrayB = movie1.toByteArray();
                                pos1 += 4 + 1 +arrayB.length;
                                cont1++;
                            }else if(acabou1 || movie1.getId()>movie2.getId()){
                                Crud.writeArq(movie2, "arquivo1.txt");
                                byte[] arrayB = movie2.toByteArray();
                                pos2 += 4 + 1 + arrayB.length;
                                cont2++;
                            }else if(acabou2 || movie1.getId()<movie2.getId()){
                                Crud.writeArq(movie1, "arquivo1.txt");
                                byte[] arrayB = movie1.toByteArray();
                                pos1 += 4 + 1 + arrayB.length;
                                cont1++;
                            }
                        }else{
                            //bloco condicional comparando os ids para determinar qual objeto deve ser escrito
                            if(movie1.getId() == -1){
                                Crud.writeArq(movie2, "arquivo2.txt");
                                byte[] arrayB = movie2.toByteArray();
                                pos2 += 4  + 1 + arrayB.length;
                                cont2++;
                            }else if(movie2.getId() == -1){
                                Crud.writeArq(movie1, "arquivo2.txt");
                                byte[] arrayB = movie1.toByteArray();
                                pos1 += 4 + 1 +arrayB.length;
                                cont1++;
                            }else if(acabou1 || movie1.getId()>movie2.getId()){
                                Crud.writeArq(movie2, "arquivo2.txt");
                                byte[] arrayB = movie2.toByteArray();
                                pos2 += 4 + 1 + arrayB.length;
                                cont2++;
                            }else if(acabou2 || movie1.getId()<movie2.getId()){
                                Crud.writeArq(movie1, "arquivo2.txt");
                                byte[] arrayB = movie1.toByteArray();
                                pos1 += 4 + 1 + arrayB.length;
                                cont1++;
                            }
                        }
                    }
                    cont += (tam/qtd*2) - 1;
                    contArq++; //define em qual registro escrever
                }while(cont<=qtdBlocos);
                cont=1;
                //reiniciar os dois arquivos utilizados anteriormente antes que eles sejam necessários
                Crud.reiniciar("arquivo3.txt");
                Crud.reiniciar("arquivo4.txt");

                //testa se não será necessário usar mais intercalções entre os blocos - se chegou ao fim da ordenação
                if(isFim("arquivo2.txt", 4)){
                    ultimoArq = "arquivo1.txt";
                    break;
                }
            }
            contArq = 0;
            result = (float) qtdBlocos/2;
            if(result<=0.5){ //não permite loop infitino, para o loop quando a quantidade de blocos existentes for 1
                if(contArqLeitura%2==0){
                    ultimoArq += "arquivo3.txt";
                }else{
                    ultimoArq += "arquivo1.txt";
                }
                break;
            }else{
                qtdBlocos = (int)Math.ceil(result);
            }
            contArqLeitura++; //altera os arquivos que serão lidos
            pos1 = pos2 = 4;
        }while(qtdBlocos>=1);
        
        arq1.close();
        arq2.close();
        arq3.close();
        arq4.close();
        Crud.writeArqOrdenado(ultimoArq); //escrever todos os registros ordenados no arquivo principal
    }

    public static int defineTam(String arquivo, int pos) throws IOException{  //metodo que define quais registros pertecem ao bloco na segunda ordenação externa
        RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");
        int tam = 0;
        int cont=0;
        Movies aux1 = new Movies();
        Movies aux2 = new Movies();
        do{
            arq.seek(pos); //posiciona o ponteiro no inicio do proximo registro
            boolean lapide = arq.readBoolean(); //leitura da lapide
            int tamanho = arq.readInt(); //leitura do tamanho do registro
            if(lapide==true){ //verifica se o registro e valido
                cont++;
            } 
            if(cont==499){
                aux1 = Crud.readPos(pos, arquivo);
                pos += tamanho + 4 + 1; //adiciona a quantidade de bytes para chegar no inicio do proximo registro
                aux2 = Crud.readPos(pos, arquivo);
                if(aux1.getId()<aux2.getId()){
                    tam++;
                    cont=1;
                }else{
                    break;
                }
            }else{
                pos += tamanho + 4 + 1; //adiciona a quantidade de bytes para chegar no inicio do proximo registro
            }
        }while(pos<arq.length()); 

        arq.close();
        return tam;
    }

    public static boolean isFim(String arquivo, int pos) throws IOException{ //método que retorna se o arquivo chegou ao fim
        RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");
        if(pos<arq.length()){
            arq.close();
            return false;
        }else{
            arq.close();
            return true;
        }
    }

    public static int qtdeRegistros() throws IOException{ //método que retoena a quantidade de registros existem no total
        RandomAccessFile arq = new RandomAccessFile("arquivo.txt", "rw");
        
        int result = 0;
        int pos = 4; //posicao do primeiro registro
        int tam = 0;

        do{
            arq.seek(pos); //posiciona o ponteiro no inicio do proximo registro
            boolean lapide = arq.readBoolean(); //leitura da lapide
            tam = arq.readInt(); //leitura do tamanho do registro
            if(lapide==true){ //verifica se o registro e valido
                result++;
            } 
            pos += tam + 4 + 1; //adiciona a quantidade de bytes para chegar no inicio do proximo registro
        }while(pos<arq.length()); 

        arq.close();
        return result;
    }

    public static Movies[] quicksort(Movies[] array, int esq, int dir){ //método de ordenação que é utilizado somente em memória primária
        int i = esq, j = dir;
        Movies pivo = array[(dir+esq)/2]; //define um marco central no array
        while (i <= j) {
            while (array[i].getId() < pivo.getId()) i++;
            while (array[j].getId() > pivo.getId()) j--;
            if (i <= j) { //bloco para realizar a troca dos objetos de acordo com o resultado das comparações acima
                Movies temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                i++;
                j--;
            }
        }
        if (esq < j)  quicksort(array, esq, j); //chamada recursiva passando novas posições padrões
        if (i < dir)  quicksort(array, i, dir); //chamada recursiva passando novas posições padrões

        return array;
    }
}
