package ArchieveManipulation;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Scanner;

import Application.Menu;
import DataStructure.BTree;
import DataStructure.Elemento;
import DataStructure.Hashing;
import Entities.Movies;

public class Crud {
    static SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    static Scanner leia = new Scanner(System.in);
    
    public static void read() throws Exception{ //leitura da base de dados
        try {
            // Leitura do arquivo CSV
            String basefile = "./Top_10000_Movies.csv";

            FileInputStream fstream = new FileInputStream(basefile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            // Leitura das linhas do arquivo CSV de filmes
            String line;
            while((line = br.readLine()) != null) {
                Movies movie = new Movies();
                movie.assignment(line); //passa cada linha do CSV por vez ao método de atribuição
                writeArq(movie, "arquivo.txt"); //escreve um objeto de cada vez no arquvio de bytes
            }
            System.out.println("Base carregada com sucesso!");

            // Fechar a leitura CSV
            fstream.close();
        }
        catch(IOException e) { e.printStackTrace(); }
    }

    public static void readArq(String arquivo) throws IOException{ //leitura do arquivo inteiro - todos os registros validos
        RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");
        
        arq.seek(0); //posiciona o ponteiro no inicio do arquivo = cabecalho
        int cabecalho = arq.readInt(); //descobre qual e o ultimo registro do arquivo 
        int pos = 4; //posicao do primeiro registro
        int tam = 0;
        Movies temp = new Movies();
        boolean acabou = false;

        do{
            arq.seek(pos); //posiciona o ponteiro no inicio do proximo registro
            boolean lapide = arq.readBoolean(); //leitura da lapide
            tam = arq.readInt(); //leitura do tamanho do registro
            if(lapide==true){ //verifica se o registro e valido
                byte[] arrayByte = new byte[tam]; 
                arq.read(arrayByte); //leitura do array de bytes
                temp.fromByteArray(arrayByte); //transforma o array de bytes em um objeto Movie
                String dataFormatada = df.format(temp.getDate());
                System.out.println(lapide + " " + temp.getId() + " " + dataFormatada + " " + temp.getLanguage()+ " " + temp.getTitle() + " " + temp.getAverage() + " " + temp.getGender()); 
                if(temp.getId()==cabecalho){
                    acabou = true;
                }
            } 
            pos += tam + 4 + 1; //adiciona a quantidade de bytes para chegar no inicio do proximo registro
        }while(!acabou);    
        arq.close();
    }

    public static boolean readId(int id) throws IOException{ //leitura de um unico registro de acordo com o id informado
        RandomAccessFile arq = new RandomAccessFile("arquivo.txt", "rw");
        
        boolean achou = false;
        arq.seek(0); //posiciona o ponteiro no inicio do arquivo = cabecalho
        int cabecalho = arq.readInt();
        int pos = 4; //posicao do primeiro registro
        int tam = 0;
        Movies temp = new Movies();

        do{
            arq.seek(pos); //posiciona o ponteiro no inicio do proximo registro
            boolean lapide = arq.readBoolean(); //leitura da lapide
            tam = arq.readInt(); //leitura do tamanho do registro
            if(lapide==true){ //verifica se o registro e valido
                byte[] arrayByte = new byte[tam]; 
                arq.read(arrayByte); //leitura do array de bytes
                temp.fromByteArray(arrayByte); //transforma o array de bytes em um objeto Movite
                if(temp.getId()==id){ //testa se e o registro procurado
                    achou = true;
                    String dataFormatada = df.format(temp.getDate());
                    System.out.println();
                    System.out.println("Registro encontrado! Dados do registro: ");
                    System.out.println(temp.getId() +" "+ dataFormatada + " " + temp.getLanguage()+ " " + temp.getTitle() + " " + temp.getAverage() + " " + temp.getGender()); 
                }
            }
            pos += tam + 4 + 1; //adiciona a quantidade de bytes para chegar no inicio do proximo registro
        }while(temp.getId()!=cabecalho && !achou);  
        if(!achou){
            System.out.println("Registro não encontrado!");
        }

        arq.close();
        return achou;
    }

    public static Movies readPos(int pos, String arquivo) throws IOException{ //leitura de um unico registro de acordo com a posição informada - usada por outros métodos
        RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");

        Movies temp = new Movies();
        arq.seek(pos); //posiciona o ponteiro no inicio do proximo registro
        boolean lapide = arq.readBoolean(); //leitura da lapide
        int tam = arq.readInt(); //leitura do tamanho do registro
        if(lapide==true){ //verifica se o registro e valido
            byte[] arrayByte = new byte[tam]; 
            arq.read(arrayByte); //leitura do array de bytes
            temp.fromByteArray(arrayByte); //transforma o array de bytes em um objeto Movite
        }

        arq.close();
        return temp;
    }

    public static int writeArq(Movies movie, String arquivo){ //escreve no fim do arquvio 
        int pos=0;
        try {
            RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");
            pos=(int) arq.length();
            //antes de escrever o novo registro no arquvio, tem que alterar o cabecalho
            arq.seek(0);
            arq.writeInt(movie.getId());
            
            arq.seek(arq.length());
            
            //primeiro transforma o objeto Movie em um array de bytes para depois escrever esse array no arquivo
            byte[] arrayByte = movie.toByteArray();
            boolean lapide=true;

            arq.writeBoolean(lapide);
            arq.writeInt(arrayByte.length); //Tamanho do registro em bytes
            arq.write(arrayByte);
            arq.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return pos;
    }

    public static void writeArqPos(Movies movie, int pos){ //escreve um registro no arquivo na posicao desejada, esse metodo
        //é utilizado apenas quando o usuario deseja alterar algum registro e o tamanho continua o mesmo
        try {
            RandomAccessFile arq = new RandomAccessFile("arquivo.txt", "rw");
            arq.seek(pos);

            //primeiro transforma o objeto Movie em um array de bytes para depois escrever esse array no arquivo
            byte[] arrayByte = movie.toByteArray();
            boolean lapide=true;

            arq.writeBoolean(lapide);
            arq.writeInt(arrayByte.length); //Tamanho do registro em bytes
            arq.write(arrayByte);
            arq.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void writeArqOrdenado(String arquivo) throws IOException{ //escreve todos os registros no arquivo principal após realizar a ordenação
        RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");
        reiniciar("arquivo.txt"); //reinicia o arquivo principal antes de voltar a escrever nele

        Movies aux = new Movies();
        int pos = 4;
        do{
            aux = Crud.readPos(pos, arquivo); //leitura dos registros no arquivo passado por parametro
            Crud.writeArq(aux, "arquivo.txt"); //escrita no arquivo principal do registro lido 
            byte[] arrayB = aux.toByteArray(); //obter o tamanho do registro para pular corretamente para o próximo registro
            pos += 4 + 1 + arrayB.length;
        }while(pos < arq.length());

        arq.close();
    }

    public static boolean delete(int id) throws IOException{ //deletar um registro atraves do Id passado pelo usuario
        RandomAccessFile arq = new RandomAccessFile("arquivo.txt", "rw");
        
        boolean apagou = false;
        arq.seek(0); //posiciona o ponteiro no inicio do arquivo = cabecalho
        int cabecalho = arq.readInt(); //descobre qual e o ultimo registro do arquivo 
        int pos = 4; //posicao do primeiro registro
        int tam = 0;
        int aux = 0;
        Movies temp = new Movies();

        do{
            arq.seek(pos); //posiciona o ponteiro no inicio do proximo registro
            boolean lapide = arq.readBoolean(); //leitura da lapide
            tam = arq.readInt(); //leitura do tamanho do registro
            if(lapide==true){ //verifica se o registro e valido
                byte[] arrayByte = new byte[tam]; 
                arq.read(arrayByte); //leitura do array de bytes
                temp.fromByteArray(arrayByte); //transforma o array de bytes em um objeto Movie
                if(temp.getId()==id){ //testa se e o registro procurado
                    apagou=true;
                    arq.seek(pos);
                    lapide = false;
                    arq.writeBoolean(lapide);
                    if(cabecalho==id){
                        arq.seek(0);
                        arq.writeInt(aux);
                    }
                    System.out.println("Registro apagado com sucesso!");
                }else{
                    aux = temp.getId();
                }
            }
            pos += tam + 4 + 1; //adiciona a quantidade de bytes para chegar no inicio do proximo registro
        }while(temp.getId()!=cabecalho && !apagou);  
        if(!apagou){
            System.out.println("Registro não encontrado!");
        }
   
        arq.close();
        return apagou;
    }

    public static boolean update(Movies novo) throws IOException{ //atualizar um registro que sera identificado atraves do Id passado pelo usuario
        RandomAccessFile arq = new RandomAccessFile("arquivo.txt", "rw");
        
        boolean achou = false;
        arq.seek(0); //posiciona o ponteiro no inicio do arquivo = cabecalho 
        int pos = 4; //posicao do primeiro registro
        int tam = 0;
        Movies temp = new Movies();
        int aux = 0;

        do{
            arq.seek(pos); //posiciona o ponteiro no inicio do proximo registro
            boolean lapide = arq.readBoolean(); //leitura da lapide
            tam = arq.readInt(); //leitura do tamanho do registro
            aux = arq.readInt();
            if(aux == novo.getId() && lapide == true){ //verifica se o registro e valido
                arq.seek(pos+1+4);
                byte[] arrayByte = new byte[tam]; 
                arq.read(arrayByte); //leitura do array de bytes
                temp.fromByteArray(arrayByte); //transforma o array de bytes em um objeto Movie
                achou=true;
                String dataFormatada = df.format(temp.getDate());
                System.out.println("Registro encontrado! Dados atuais do registro: ");
                System.out.println(temp.getId() + " " + dataFormatada + " " + temp.getLanguage()+ " " + temp.getTitle() + " " + temp.getAverage() + " " + temp.getGender());
                dataFormatada = df.format(novo.getDate());
                System.out.println("Novos dados do registro: ");
                System.out.println(novo.getId() + " " + dataFormatada + " " + novo.getLanguage()+ " " + novo.getTitle() + " " + novo.getAverage() + " " + novo.getGender()); 
                    
                byte[] arrayByteNovo = novo.toByteArray(); //cria-se o array de bytes com os novos campos
                //if(arrayByteNovo.length==tam || arrayByteNovo.length<tam){
                if(arrayByteNovo.length==tam){ //se os dois objetos possuirem o mesmo tamanho
                    writeArqPos(novo, pos); //escreve por cima do antigo resgistro 
                }else{ //se possuirem tamanhos diferentes
                    arq.seek(pos);
                    lapide = false;
                    arq.writeBoolean(lapide); //apaga o registro antigo

                    //int tamArq = (int) (arq.length());
                    int address = writeArq(novo, "arquivo.txt");
                    Menu.hash.updateAddress(novo.getId(),address);
                    Menu.arvore.updateAddress(novo.getId(),address);

                    writeArq(novo, "arquivo.txt"); //cria-se um novo registro no final do arquvio com o mesmo id
                }
                System.out.println("Registro alterado com sucesso!");
            }
            pos += tam + 4 + 1; //adiciona a quantidade de bytes para chegar no inicio do proximo registro
        }while(!achou);  
        if(!achou){
            System.out.println("ID informado inválido!");
        }

        arq.close();
        return achou;
    }
    
    public static void reiniciar(String arquivo) throws IOException{ //método para reiniciar um arquivo antes que o programa volte a escrever nele
        try {
            RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");
            arq.setLength(0); //zerar o tamanho do arquivo 
            arq.close();
        } catch (IOException e) {
            System.err.println("Erro ao abrir o arquivo!");
        }
    }

    public static BTree constroiArvore(String arquivo) throws IOException{
        reiniciar("arquivoB.txt");
        RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");
        BTree arvore = new BTree(2);

        arq.seek(0); //posiciona o ponteiro no inicio do arquivo = cabecalho
        int cabecalho = arq.readInt(); //descobre qual e o ultimo registro do arquivo 
        int pos = 4; //posicao do primeiro registro
        int tam = 0;
        Movies temp = new Movies();
        boolean acabou = false;

        do{
            arq.seek(pos); //posiciona o ponteiro no inicio do proximo registro
            boolean lapide = arq.readBoolean(); //leitura da lapide
            tam = arq.readInt(); //leitura do tamanho do registro
            if(lapide==true){ //verifica se o registro e valido
                byte[] arrayByte = new byte[tam]; 
                arq.read(arrayByte); //leitura do array de bytes
                temp.fromByteArray(arrayByte); //transforma o array de bytes em um objeto Movie

                int id = temp.getId();
                Elemento aux = new Elemento(id, pos);
                arvore.Insert(aux);

                if(temp.getId()==cabecalho){
                    acabou = true;
                }
            } 
            pos += tam + 4 + 1; //adiciona a quantidade de bytes para chegar no inicio do proximo registro
        }while(!acabou);    
        arq.close();
        arvore.writeArqIndice();
        //arvore.readArqB();
        //Busca no indice
        /*System.out.println("Digite o id do filme: ");
        int id = leia.nextInt();
        Movies movie = arvore.SearchArq(id);
        System.out.println(movie.getId());
        System.out.println(movie.getTitle());*/
        return arvore;
    }

    public static Hashing constroiHash(String arquivo) throws IOException{
        reiniciar("arquivoHash.txt");
        RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");
        Hashing hash = new Hashing();
 
        arq.seek(0); //posiciona o ponteiro no inicio do arquivo = cabecalho
        int cabecalho = arq.readInt(); //descobre qual e o ultimo registro do arquivo 
        int pos = 4; //posicao do primeiro registro
        int tam = 0;
        Movies temp = new Movies();
        boolean acabou = false;

        do{
            arq.seek(pos); //posiciona o ponteiro no inicio do proximo registro
            boolean lapide = arq.readBoolean(); //leitura da lapide
            tam = arq.readInt(); //leitura do tamanho do registro
            if(lapide==true){ //verifica se o registro e valido
                byte[] arrayByte = new byte[tam]; 
                arq.read(arrayByte); //leitura do array de bytes
                temp.fromByteArray(arrayByte); //transforma o array de bytes em um objeto Movie
                hash.addchaves(temp.getId(), pos);

                if(temp.getId()==cabecalho){
                    acabou = true;
                }
            } 
            pos += tam + 4 + 1; //adiciona a quantidade de bytes para chegar no inicio do proximo registro
        }while(!acabou);  
        //hash.readHash();  
        
        //Busca no indice
        /*System.out.println("Digite o id fo filme: ");
        int id = leia.nextInt();
        pos = hash.search(id);
        if(pos!=-1){
            Movies movie = new Movies();
            movie = Crud.readPos(pos, "arquivo.txt");
            System.out.println(movie.getId()+" "+movie.getTitle());
        }else{
            System.out.println("Não foi possível localizar nenhum filme com esse ID!");
        }*/
        
        arq.close();
        return hash;
    }

    public static int moviesArchSize() throws IOException{ //Métodos para retornar o tamanho do arquivo de dados contabilizando as lápides = true
        RandomAccessFile arq = new RandomAccessFile("arquivo.txt", "rw");
        int size = 0; //Contador de lápides válidas

        arq.seek(0); //posiciona o ponteiro no inicio do arquivo = cabecalho
        int cabecalho = arq.readInt(); //descobre qual e o ultimo registro do arquivo 
        int pos = 4; //posicao do primeiro registro
        int tam = 0;
        Movies temp = new Movies();
        boolean acabou = false;

        do{
            arq.seek(pos); //posiciona o ponteiro no inicio do proximo registro
            boolean lapide = arq.readBoolean(); //leitura da lapide
            tam = arq.readInt(); //leitura do tamanho do registro
            if(lapide==true){ //verifica se o registro e valido
                byte[] arrayByte = new byte[tam]; 
                arq.read(arrayByte); //leitura do array de bytes
                temp.fromByteArray(arrayByte); //transforma o array de bytes em um objeto Movie
                if(temp.getId()==cabecalho){
                    acabou = true;
                }
                size++; //Incrementar o contador caso a lápide = true
            } 
            pos += tam + 4 + 1; //adiciona a quantidade de bytes para chegar no inicio do proximo registro
        }while(!acabou);    
        arq.close();

        return size; //Retornar o contador das lápides
    }

    public static Movies[] readRegisters(String arquivo, int size) throws IOException{ //leitura do arquivo inteiro - todos os registros validos
        RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");
        Movies movies[] = new Movies[size];

        for(int i = 0; i < size; i++){
            movies[i] = new Movies();
        }
        
        arq.seek(0); //posiciona o ponteiro no inicio do arquivo = cabecalho
        int cabecalho = arq.readInt(); //descobre qual e o ultimo registro do arquivo 
        int pos = 4; //posicao do primeiro registro
        int tam = 0;
        boolean acabou = false;
        int i = 0;

        do{
            arq.seek(pos); //posiciona o ponteiro no inicio do proximo registro
            boolean lapide = arq.readBoolean(); //leitura da lapide
            tam = arq.readInt(); //leitura do tamanho do registro
            if(lapide==true){ //verifica se o registro e valido
                byte[] arrayByte = new byte[tam]; 
                arq.read(arrayByte); //leitura do array de bytes
                movies[i].fromByteArray(arrayByte);
                if(movies[i].getId()==cabecalho){
                    acabou = true;
                }
                i++;
            } 
            pos += tam + 4 + 1; //adiciona a quantidade de bytes para chegar no inicio do proximo registro
        }while(!acabou);    
        arq.close();

        return movies;
    }

    public static void invertedListSearch(double key) throws IOException{ //Método para realizar a pesquisa da lista invertida através das NOTAS
        RandomAccessFile arq = new RandomAccessFile("AverageInvList.txt", "rw");

        int id;
        double average;
        int size;
        int pos = 0; //Iniciar a leitura do arquivo no começo do arquivo
        boolean find;
        boolean end = false;

        do{
            find = false;
            arq.seek(pos); //Posicionar a leitura do arquivo
            average = arq.readDouble(); //Leitura da nota
            size = arq.readInt(); //Leitura da quantidade de filmes que possuem aquela nota
            if(average == key){ //Se a nota lida no arquivo for igual a nota passada por parâmetro no método
                for(int i = 0; i < size; i++){ //Percorrer a quantidade de id's de filmes que possuem a nota
                    id = arq.readInt(); //Leitura do id do filme
                    readId(id); //Procurar pelo registro passando o id lido no arquivo
                }
                end = true; //Nota foi achada
            }
            if(size != 0) find = true; //Verificar se existem filmes com a nota passada para pesquisa
            pos += 8 + 4 + (size * 4); //Posicionar o ponteiro para a próxima nota
        }while(!end);

        if(!find) System.out.println("Não existem filmes com essa nota!");

        arq.close();
    }

    public static void invertedListSearch(String key, String[] genders) throws IOException{ //Método para realizar a pesquisa da lista invertida através dos GÊNEROS
        RandomAccessFile arq = new RandomAccessFile("GenderInvList.txt", "rw");

        int id; 
        String gender; 
        int size;
        int pos = 0; //Iniciar a leitura do arquivo no começo do arquivo
        boolean end = false;
        boolean find = false;
        int tam;

        for(int j = 0; j < genders.length; j++){ //Verificar se foi passado um gênero válido
            if(genders[j].equals(key)) find = true;
        }
        if(find){ //Se o gênero passado como chave de busca for válido
            do{
                arq.seek(pos); //Posicionar a leitura do arquivo 
                tam = arq.readInt(); //Leitura do tamanho do gênero
                gender = arq.readUTF(); //Leitura do gênero
                size = arq.readInt(); //Leitura da quantidade de filmes que possuem aquele gênero
                if(gender.equals(key)){ //Se o gênero lido do arquivo for igual ao gênero passado por parâmetro no método
                    for(int i = 0; i < size; i++){ //Percorrer a quantidade de id's de filmes que possuem o gênero
                        id = arq.readInt(); //Leitura do id do filme
                        readId(id); //Procurar pelo registro passando o id lido no arquivo
                    }
                    end = true; //Gênero foi achado
                }
                pos += 4 + tam + 4 + (size * 4); //Posicionar o ponteiro para o próximo gênero
            }while(!end);
        }
        else System.out.println("Não existem filmes com esse Gênero!");

        arq.close();
    }

    public static void bothInvertedListSearch(double avKey, String genKey, String[] genders) throws IOException{ //Método para realizar a pesquisa da lista invertida através dos GÊNEROS E DAS NOTAS
        RandomAccessFile avArq = new RandomAccessFile("AverageInvList.txt", "rw");
        RandomAccessFile genArq = new RandomAccessFile("GenderInvList.txt", "rw");

        String gender; 
        int size;
        int pos = 0; //Iniciar a leitura do arquivo no começo do arquivo
        boolean end = false;
        boolean find = false;
        int tam;
        double average;
        int[] genIds = new int[] {};
        int[] avIds = new int[] {};

        //LEITURA DO ARQUIVO DE LISTA INVERTIDA DOS GÊNEROS --------------------------------------------------------------------------------------------------------------------------------------------------
        for(int j = 0; j < genders.length; j++){ //Verificar se foi passado um gênero válido
            if(genders[j].equals(genKey)) find = true;
        }
        if(find){ //Se o gênero passado como chave de busca for válido
            do{
                genArq.seek(pos); //Posicionar a leitura do arquivo 
                tam = genArq.readInt(); //Leitura do tamanho do gênero
                gender = genArq.readUTF(); //Leitura do gênero
                size = genArq.readInt(); //Leitura da quantidade de filmes que possuem aquele gênero
                if(gender.equals(genKey)){ //Se o gênero lido do arquivo for igual ao gênero passado por parâmetro no método
                    genIds = new int[size]; //Inicializar o array de id's com a quantidade de filmes salvos no arquivo que possuem o gênero desejado, informados pela variável "size"
                    for(int i = 0; i < size; i++){ //Percorrer a quantidade de id's de filmes que possuem o gênero
                        genIds[i] = genArq.readInt(); //Salvar todos os id's de filmes que possuem o gênero desejado em um array para futura comparação
                    }
                    end = true; //Gênero foi achado
                }
                pos += 4 + tam + 4 + (size * 4); //Posicionar o ponteiro para o próximo gênero
            }while(!end);
        }
        else System.out.println("Não existem filmes com esse Gênero!");

        //LEITURA DO ARQUIVO DE LISTA INVERTIDA DAS NOTAS -----------------------------------------------------------------------------------------------------------------------------------------------------
        end = false;
        pos = 0;
        do{
            find = false;
            avArq.seek(pos); //Posicionar a leitura do arquivo
            average = avArq.readDouble(); //Leitura da nota
            size = avArq.readInt(); //Leitura da quantidade de filmes que possuem aquela nota
            if(average == avKey){ //Se a nota lida no arquivo for igual a nota passada por parâmetro no método
                avIds = new int[size]; //Inicializar o array de id's com a quantidade de filmes salvos no arquivo que possuem a nota desejada, informados pela variável "size"
                for(int i = 0; i < size; i++){ //Percorrer a quantidade de id's de filmes que possuem a nota
                    avIds[i] = avArq.readInt(); //Salvar todos os id's de filmes que possuem a nota desejada em um array para futura comparação
                }
                end = true; //Nota foi achada
            }
            if(size != 0) find = true; //Verificar se existem filmes com a nota passada para pesquisa
            pos += 8 + 4 + (size * 4); //Posicionar o ponteiro para a próxima nota
        }while(!end);
        if(!find) System.out.println("Não existem filmes com essa nota!");

        //DESCOBRIR QUAIS FILMES POSSUEM AMBAS CARACTERÍSTICAS DESEJADAS ----------------------------------------------------------------------------------------------------------------------------------------
        find = false;
        for(int i = 0; i < genIds.length; i++){ //Percorrer o array contendo os id's salvos do arquivo de gêneros
            for(int j = 0; j < avIds.length; j++){ //Percorrer o array contendo os id's salvos do arquivo de notas
                if(genIds[i] == avIds[j]){ //Se os id's forem iguais, é um filme que possui tanto a nota desejada quanto o gêneto desejado
                    readId(avIds[j]); //Realizar a leitura dos registros passando o id
                    find = true; //Informar que existe pelo menos registro um com as características desejadas
                }
            }
        }
        if(!find) System.out.println("Não existem filmes com a nota e o gênero desejados!");

        avArq.close();
        genArq.close();
    }
}
