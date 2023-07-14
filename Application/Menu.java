package Application;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Scanner;

import ArchieveManipulation.Crud;
import DataCompression.HuffmanCompression;
import DataCompression.HuffmanDecompression;
import DataCompression.LZWCompression;
import DataCompression.LZWDecompression;
import DataStructure.BTree;
import DataStructure.Elemento;
import DataStructure.Hashing;
import DataStructure.InvertedList;
import DataStructure.Sort;
import Entities.Movies;
import PatternMatching.BoyerMoore;

public class Menu {
    static SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    public static BTree arvore;
    public static Hashing hash;
    public static void main(String[] args) throws Exception{
        menu();
    }

    public static void menu() throws Exception{
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);
        int op;
        int ord;
        //Interação com o usuário fornecendo as opções do CRUD + ordenação
        System.out.println("\nEscolha uma das opções abaixo:");
        System.out.println("0 - Encerrar");
        System.out.println("1 - Carregar a base de dados");
        System.out.println("2 - Ler o arquivo");
        System.out.println("3 - Cadastrar novo registro");
        System.out.println("4 - Ler registro");
        System.out.println("5 - Atualizar registro");
        System.out.println("6 - Deletar registro");
        System.out.println("7 - Ordenar o arquivo");
        System.out.println("8 - Construir Árvore B");
        System.out.println("9 - Pesquisar no indice (Árvore B)");
        System.out.println("10 - Construir Hash");
        System.out.println("11 - Pesquisar no indice (Hash)");
        System.out.println("12 - Pesquisar na Lista Invertida");
        System.out.println("13 - Realizar compressão dos dados");
        System.out.println("14 - Realizar descompressão dos dados");
        System.out.println("15 - Procurar padrão - Boyer Moore");
        System.out.print("Opção: ");
        op = Integer.parseInt(sc.nextLine());

        if(op == 1){
            Crud.reiniciar("arquivo.txt");
            Crud.read(); //carregar a base de dados CSV + escrever no arquivo em bytes
        }else if(op == 2){
            Crud.readArq("arquivo.txt"); //percorre o arquivo;
        }else if(op == 3){
            Movies novo = new Movies(); //novo objeto é criado
            
            System.out.println("Campos a serem preenchidos: ");
            boolean existe = true;
            do{ //validaçao do id escolhido pelo usuario
                System.out.println("Id: ");
                int id = Integer.parseInt(sc.nextLine());
                existe = Crud.readId(id);
                if(existe){
                    System.out.println("Id já utilizado! Tente novamente.");
                }else{
                    System.out.println("Id validado!");
                    novo.setId(id);
                }
            }while(existe);

            //caso o id seja validado, os outros dados a serem inseridos são solicitados 
            System.out.println("Data: (yyyy-MM-dd)");
            String data1 = sc.nextLine();
            novo.setDate(data1); 
            System.out.println("Language: ");
            String lg = sc.nextLine();
            novo.setLanguage(lg);
            System.out.println("Title: ");
            String tt = sc.nextLine();
            novo.setTitle(tt);
            System.out.println("Average: ");
            double av = Double.parseDouble(sc.nextLine());
            novo.setAverage(av);
            System.out.println("Gender: ");
            String gd = sc.nextLine();
            novo.setGender(gd);

            int address = Crud.writeArq(novo, "arquivo.txt"); //metodo que passa o objeto para o arquivo 

            hash.addchaves(novo.getId(), address);
            hash = Crud.constroiHash("arquivo.txt");

            Elemento elemento = new Elemento(novo.getId(), address);
            arvore.Insert(elemento);
            arvore = Crud.constroiArvore("arquivo.txt");

        }else if(op == 4){
            System.out.println("Digite o id: ");
            int id = sc.nextInt();
            Crud.readId(id); //passa o id inserido para um método que irá buscar o registro específico, caso exista
        }else if(op == 5){
            Movies novo = new Movies(); //cria-se o novo objeto

            //solicita todos os dados atualizados a serem inseridos
            System.out.println("Digite o id do registro que você deseja alterar: ");
            int id = Integer.parseInt(sc.nextLine());
            novo.setId(id);
            System.out.println("Campos a serem alterados: ");
            System.out.println("Data: (yyyy-MM-dd)");
            String data1 = sc.nextLine();
            novo.setDate(data1); 
            System.out.println("Language: ");
            String lg = sc.nextLine();
            novo.setLanguage(lg);
            System.out.println("Title: ");
            String tt = sc.nextLine();
            novo.setTitle(tt);
            System.out.println("Average: ");
            double av = Double.parseDouble(sc.nextLine());
            novo.setAverage(av);
            System.out.println("Gender: ");
            String gd = sc.nextLine();
            novo.setGender(gd);
            Crud.update(novo);//chama o método que faz a atualização, passando o objeto preenchido
        }else if(op == 6){
            System.out.println("Digite o id: ");
            int id = sc.nextInt();
            Crud.delete(id); //chama o método que fará a exclusão do registro pelo id informado, caso esse registro exista
            
            arvore = Crud.constroiArvore("arquivo.txt");
            hash = Crud.constroiHash("arquivo.txt");
            
        }else if(op==7){
            //novo menu para informar as opções de ordenação ao usuário
            System.out.println("Qual ordenação deve ser realizada?");
            System.out.println("1 - Intercalação Balanceada Comum");
            System.out.println("2 - Intercalação Balanceada Variável");
            System.out.print("Ordenação: ");
            ord = Integer.parseInt(sc.nextLine());

            if(ord == 1){
                Sort.intercalacaoBalanceadaComum(); //chama a ordenação comum, sem parametros
                System.out.println("Ordenação realizada com sucesso!");
            }
            else if(ord == 2){
                System.out.println("Digite a quantidade de registro por bloco: ");
                int qtd = sc.nextInt();
                Sort.intercalacaoBalanceadaVariavel(qtd); //chama a ordenação variavel, com parametros
                System.out.println("Ordenação realizada com sucesso!");
            }
            else{
                menu();
            }
        }else if(op==8){
            Crud.reiniciar("arquivoB.txt");
            arvore = Crud.constroiArvore("arquivo.txt");
        }else if(op==9){
            System.out.println("Digite um ID: ");
            int id = sc.nextInt();
            Movies movie = new Movies();
            int address = arvore.SearchArq(id);
            if(address==-1){
                System.out.println("Não foi possível encontrar nenhum registro com esse ID!");
            }else{
                movie = Crud.readPos(address, "arquivo.txt");
                System.out.println(movie.getId()+" "+movie.getTitle());
            }
        }else if(op==10){
            Crud.reiniciar("arquivoHash.txt");
            hash = Crud.constroiHash("arquivo.txt");
        }else if(op==11){
            System.out.println("Digite um ID: ");
            int id = sc.nextInt();
            Movies movie = new Movies();
            int address = hash.search(id);
            if(address==-1){
                System.out.println("Não foi possível encontrar nenhum registro com esse ID!");
            }else{
                movie = Crud.readPos(address, "arquivo.txt");
                System.out.println(movie.getId()+" "+movie.getTitle());
            }
            
        }else if(op==12){
            System.out.println("Pesquisar através da nota, do gênero ou utilizando ambos?");
            String option = sc.nextLine();
            if(option.equals("Nota")){ 
                Crud.reiniciar("AverageInvList.txt"); //Reiniciar o arquivo
                InvertedList list = new InvertedList(0.0);
                list.buildAverageList(); //Criar a lista invertida por notas
            
                System.out.println("Informe uma nota, entre 0.0 e 10.0, para busca: ");
                double value = sc.nextDouble();

                Crud.invertedListSearch(value); //Pesquisar, pelo arquivo, filmes com a nota passada
            }
            else if(option.equals("Genero")){
                Crud.reiniciar("GenderInvList.txt"); //Reiniciar o arquivo
                InvertedList list = new InvertedList();
                list.buildGenderList(); //Criar a lista invertida por gêneros

                System.out.println("Informe o gênero desejado para busca: ");
                String value = sc.nextLine();

                Crud.invertedListSearch(value, list.getGenders()); //Pesquisar, pelo arquivo, filmes com o gênero passado
            }
            else{
                Crud.reiniciar("AverageInvList.txt"); //Reiniciar o arquivo
                Crud.reiniciar("GenderInvList.txt"); //Reiniciar o arquivo
                InvertedList avList = new InvertedList(0.0);
                InvertedList genderList = new InvertedList();

                avList.buildAverageList(); //Criar a lista invertida por notas
                genderList.buildGenderList(); //Criar a lista invertida por gêneros

                System.out.println("Informe a nota: ");
                double avKey = sc.nextDouble();
                sc.nextLine();
                System.out.println("Informe o gênero: ");
                String genKey = sc.nextLine();

                Crud.bothInvertedListSearch(avKey, genKey, genderList.getGenders()); //Pesquisar, pelos dois arquivos, filmes com o gênero e a nota informados
            }
            
        }else if(op==13){
            System.out.println("Opções: ");
            System.out.println("1 - Huffman");
            System.out.println("2 - LZW");
            int code = sc.nextInt();

            if(code==1){
                HuffmanCompression.compactacao("arquivo.txt");
            }
            else{
                LZWCompression.compactacao("arquivo.txt");
            }
        }else if(op==14){
            System.out.println("Opções: ");
            System.out.println("1 - Huffman");
            System.out.println("2 - LZW");
            int code = sc.nextInt();

            if(code==1){
                HuffmanDecompression.descompactacao("arquivo.txt.HUFF");
            }
            else{
                LZWDecompression.descompactacao("arquivo.txt.LZW");
            }
        }else if(op==15){
            BoyerMoore bm = new BoyerMoore();
            System.out.println("Qual padrão você deseja procurar no arquivo?");
            String padrao = sc.nextLine();
            bm.readArq("arquivo.txt", padrao);
        }
        
        if(op>0){ 
            //sempre retorna ao menu inicial quando for uma opção válida
            menu();
        }
        sc.close();
    }
}