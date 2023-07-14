package DataStructure;
import java.io.IOException;
import java.io.RandomAccessFile;

import ArchieveManipulation.Crud;
import Entities.Movies;

public class InvertedList{
    private int size = Crud.moviesArchSize(); //Tamahho do arquivo de dados (quantidade de registros válidos)
    private Movies movies[] = new Movies[size]; //Array de filmes com todos os filmes armazenados
    private double averages[][] = new double[101][size]; //Matriz da lista de notas
    private String genders[] = new String[17]; //Array contendo todos os gêneros
    private int ids[][] = new int[17][size]; //Matriz da lista de gêneros

    public String[] getGenders(){ //Método para retornar o array contendo os gêneros
        return this.genders;
    }

    public InvertedList() throws IOException{ //Construtor para realizar a busca através de um gênero
        this.movies = Crud.readRegisters("arquivo.txt", this.size); //Preencher o array de filmes com todos os registros no arquivo de dados

        //Preencher o array de gêneros com todos os gêneros
        this.genders[0] = "Science Fiction";
        this.genders[1] = "Action";
        this.genders[2] = "Adventure";
        this.genders[3] = "Fantasy";
        this.genders[4] = "Crime";
        this.genders[5] = "Thriller";
        this.genders[6] = "Comedy";
        this.genders[7] = "Horror";
        this.genders[8] = "Animation";
        this.genders[9] = "Family";
        this.genders[10] = "Mystery";
        this.genders[11] = "Drama";
        this.genders[12] = "Romance";
        this.genders[13] = "Western";
        this.genders[14] = "Music";
        this.genders[15] = "War";
        this.genders[16] = "Documentary";

        //Preencher a primeira coluna da matriz de gênero com a numeração de cada gênero
        for(int ln = 0; ln < 17; ln++){
            this.ids[ln][0] = ln;
        }
    }

    public InvertedList(double notas) throws IOException{ //Construtor para realizar a busca através de uma nota
        this.movies = Crud.readRegisters("arquivo.txt", this.size); //Preencher o array de filmes com todos os registros no arquivo de dados

        //Preencher a primeira coluna da matriz de notas com todas as notas de 0.0 até 10.0
        for(int i = 0; i < 101; i++){
            averages[i][0] =  Math.round(notas * 10.0) / 10.0;
            notas += 0.1;
        }
    }

    public void buildAverageList() throws IOException{ //Construir a lista de NOTAS
        for(Movies i : this.movies){ //Percorrer todos os filmes
            for(int ln = 0; ln < 101; ln++){ //Percorrer todas as linhas na primeira coluna da matriz para pegar todas as notas
                if(i.getAverage() == this.averages[ln][0]){ //Se a nota do filme for igual a nota encontrada na matriz
                    for(int col = 1; col < this.size; col++){ //Percorrer as colunas daquela nota na matriz para inserir os id's dos filmes
                        if(this.averages[ln][col] == 0.0){ //Se estiver em uma posição vazia na matriz
                            this.averages[ln][col] = (int) i.getId(); //Armazenar o id do filme na matriz
                            break;
                        }
                    }
                    break;
                }
            }
        }

        buildAverageArchieve();
    }

    public void buildGenderList() throws IOException{ //Construir a lista de GÊNEROS
        for(Movies i : this.movies){ //Percorrer todos os filmes
            for(int ln = 0; ln < 17; ln++){ //Percorrer todas as linhas na primeira coluna da matriz para pegar todas os gêneros
                if(i.getGender().contains(this.genders[ln])){ //Se o gênero do filme for igual ao gênero encontrado na matriz
                    for(int col = 1; col < this.size; col++){ //Percorrer as colunas daquele gênero na matriz para inserir os id's dos filmes
                        if(this.ids[ln][col] == 0){ //Se estiver em uma posição vazia na matriz
                            this.ids[ln][col] = i.getId(); //Armazenar o id do filme na matriz
                            break;
                        }
                    }
                }
            }
        }

        buildGenderArchieve();
    }

    public void buildAverageArchieve() throws IOException{ //Construir o arquivo contendo a lista invertida pelas NOTAS
        RandomAccessFile arq = new RandomAccessFile("AverageInvList.txt", "rw");

        for(int ln = 0; ln < 101; ln++){ //Percorrer todas a linhas da matriz
            arq.writeDouble(this.averages[ln][0]); //Escrever a nota no arquivo
            int cont = 0; //Contador para contabilizar a quantidade de filmes que possuem uma mesma nota
            int value[] = new int[this.size]; //Array para armazenar os id's que serão escritos no arquivo
            for(int col = 1, i = 0; col < this.size; col++){ //Percorrer todas as colunas da matriz
                if(averages[ln][col] != 0.0){ //Se a posição da matriz armazenar algum id
                    cont++; //Incrementa o contador
                    value[i] = (int) averages[ln][col]; //Armazena o id no array de id's
                    i++; //Incrementa a variável de controle do array de id's
                }
            }
            arq.writeInt(cont); //Escreve o contador contendo a quantidade de filmes de uma mesma nota no arquivo
            for(int i = 0; i < cont; i++){ //Escreve todos os id's que possuem a mesma nota
                arq.writeInt(value[i]);
            }
        }

        arq.close();
    }

    public void buildGenderArchieve() throws IOException{ //Construir o arquivo contendo a lista invertida pelos GÊNEROS
        RandomAccessFile arq = new RandomAccessFile("GenderInvList.txt", "rw");

        for(int ln = 0; ln < 17; ln++){ //Percorrer todas a linhas da matriz
            byte[] arrayByte = Movies.invListToByteArray(this.genders[ln]); //Transformar o gênero em um array de bytes
            arq.writeInt(arrayByte.length); //Escrever o tamanho do array de bytes no arquivo
            arq.write(arrayByte); //Escrever o array de bytes contendo o gênero no arquivo
            int cont = 0; //Contador para contabilizar a quantidade de filmes que possuem um mesmo gênero
            int value[] = new int[this.size]; //Array para armazenar os id's que serão escritos no arquivo
            for(int col = 1, i = 0; col < this.size; col++){ //Percorrer todas as colunas da matriz
                if(ids[ln][col] != 0){ //Se a posição da matriz armazenar algum id
                    cont++; //Incrementa o contador
                    value[i] = ids[ln][col]; //Armazena o id no array de id's
                    i++; //Incrementa a variável de controle do array de id's
                }
            } 
            arq.writeInt(cont); //Escreve o contador contendo a quantidade de filmes de um mesmo gênero no arquivo
            for(int i = 0; i < cont; i++){ //Escreve todos os id's que possuem o mesmo gênero
                arq.writeInt(value[i]);
            }
        }
        
        arq.close();
    }
}