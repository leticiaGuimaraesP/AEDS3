package DataStructure;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BTree{
    private int T;
    private int qtdNos=0;
    private boolean achou=false;
    
    // Node creation
    public class Node {
        int n;
        Elemento chaves[] = new Elemento[2 * T - 1];
        Node filhos[] = new Node[2 * T];
        boolean folha = true;

        public int Find(int k) {
            for (int i = 0; i < this.n; i++) {
                if (this.chaves[i].id == k) {
                    return i;
                }
            }
            return -1;
        }
    }

    public BTree(int t) {
        T = t;
        raiz = new Node();
        raiz.n = 0;
        raiz.folha = true;
    }

    private Node raiz;

    private void Split(Node x, int pos, Node y) {
        Node z = new Node();
        z.folha = y.folha;
        z.n = T - 1;
        for (int j = 0; j < T - 1; j++) {
            z.chaves[j] = y.chaves[j + T];
        }
        if (!y.folha) {
            for (int j = 0; j < T; j++) {
            z.filhos[j] = y.filhos[j + T];
            }
        }
        y.n = T - 1;
        for (int j = x.n; j >= pos + 1; j--) {
            x.filhos[j + 1] = x.filhos[j];
        }
        x.filhos[pos + 1] = z;

        for (int j = x.n - 1; j >= pos; j--) {
            x.chaves[j + 1] = x.chaves[j];
        }
        x.chaves[pos] = y.chaves[T - 1];
        x.n = x.n + 1;
    }

    public void Insert(final Elemento chaves) {
        Node r = raiz; //armazena a atual raiz
        if (r.n == 2 * T - 1) { //se a raiz estiver cheia 
            Node s = new Node(); //cria novo no, que será a nova raiz
            raiz = s; 
            s.folha = false; //ja que é raiz, então não é folha
            s.n = 0; //qtd de elementos 
            s.filhos[0] = r; //inicializa os filhos com os elementos da antiga raiz
            Split(s, 0, r); 
            insertValue(s, chaves);
        } else {//raiz "vazia"
            insertValue(r, chaves);
        }
    }

    final private void insertValue(Node x, Elemento k) {
        if (x.folha) { //se o no for folha
            int i = 0;
            for (i = x.n - 1; i >= 0 && k.id < x.chaves[i].id; i--) {//se a chave for menor que os elementos da folha
                x.chaves[i + 1] = x.chaves[i];
            }
            x.chaves[i + 1] = k;//insere na posição certa
            x.n = x.n + 1; //atualiza a qtd de elementos 
        } else { //se não for folha
            int i = 0;
            for (i = x.n - 1; i >= 0 && k.id < x.chaves[i].id; i--) {
                //acha a posicao do novo elemento que sera inserido
            }
            i++; //posição o ponteiro que levara para o proximo no filho
            Node tmp = x.filhos[i]; //no filho
            if (tmp.n == 2 * T - 1) { //se o no filho estiver cheio, realiza-se o split
            Split(x, i, tmp);
            if (k .id> x.chaves[i].id) {
                i++;
            }
            }
            insertValue(x.filhos[i], k); //recursividade ate achar uma folha
        }
    }

    public void Show() throws IOException {
        Show(raiz);
    }

   private void Show(Node x) { 
        //Le na ordem das paginas, de cima pra baixo - da esquerda para direita
        assert (x == null); 
        for (int i = 0; i < x.n; i++) { //percorre todos os elementos da pagina
            System.out.print(x.chaves[i].id + " ");
        }
        System.out.print("\n");
        if (!x.folha) { //se a pagina não for folha, continua o processo de percorrer a árvore 
            for (int i = 0; i < x.n + 1; i++) {
                Show(x.filhos[i]); //rescurividade ate chegar nas folhas
            }
        }
    }

    public void writeArqIndice() throws IOException {
        writeArqIndice(raiz);
    }
    public void writeArqIndice(Node x) throws IOException {  //tam fixo por pagina = 8*7 + 8*4 + 4 = 92 bytes
        RandomAccessFile arq = new RandomAccessFile("arquivoB.txt", "rw");
        int contPonteiro = 0;

        if(contPonteiro==0){
            arq.writeInt(4);
            contPonteiro = 4;
        }
        int cont=0;
        if(x == raiz){
            arq.seek(contPonteiro);
            arq.writeInt(x.n); //qtd de elementos na folha
            contPonteiro += 92;
            for (int i = 0; i < x.n; i++) {
                arq.writeInt(contPonteiro); 
                arq.writeInt(x.chaves[i].id);
                arq.writeInt(x.chaves[i].address); 
                contPonteiro = writeRec(x.filhos[cont++], contPonteiro) + 92;
                qtdNos=0;
            }
            contPonteiro = writeRec(x.filhos[cont++], contPonteiro);
            contPonteiro = contPonteiro - (92*(qtdNos-1));
            arq.writeInt(contPonteiro); //System.out.println(contPonteiro);
        }
        //readArqB();
        /*imprime a pagina e todos os filhos ate chegar na folha, depois sobe para o proximo elemento da raiz
        assert (x == null);
        for (int i = 0; i < x.n; i++) {
            System.out.print(x.chaves[i].id + " ");
        }
        if (!x.folha) {
            for (int i = 0; i < x.n + 1; i++) {
                Show(x.filhos[i]);
            }
        } */

        arq.close();
    }
    public int writeRec(Node x, int contPonteiro) throws IOException{
        RandomAccessFile arq = new RandomAccessFile("arquivoB.txt", "rw");
        int pontfolha = -1;
        int cont=0;
        if(x!=null){
            qtdNos++;
            if(!x.folha){ //NÃO FOLHA
                arq.seek(contPonteiro);
                arq.writeInt(x.n); //qtd de elementos na folha
                contPonteiro += 92;
                for (int i = 0; i < x.n; i++) {
                    arq.writeInt(contPonteiro);
                    arq.writeInt(x.chaves[i].id);
                    arq.writeInt(x.chaves[i].address);
                    contPonteiro = writeRec(x.filhos[cont++], contPonteiro)+92;
                }    
                arq.writeInt(contPonteiro);            
                contPonteiro = writeRec(x.filhos[cont++], contPonteiro);
                               
            }else{ //FOLHA
                arq.seek(contPonteiro); //-1
                arq.writeInt(x.n); //qtd de elementos na folha
                for (int i = 0; i < x.n; i++) {
                    arq.writeInt(pontfolha);
                    arq.writeInt(x.chaves[i].id);
                    arq.writeInt(x.chaves[i].address);
                }
                arq.writeInt(pontfolha); //-1
            }
        }

        arq.close();
        return contPonteiro;
    }
    
    public void readArqB() throws IOException{
        RandomAccessFile arq = new RandomAccessFile("arquivoB.txt", "rw");
        int pos=4;
        do{
            arq.seek(pos);
            int qtd = arq.readInt();
            System.out.println("Qtd: "+qtd);
            for(int i=0; i<qtd; i++){
                System.out.println("pont: "+arq.readInt());
                System.out.println("elemento: "+arq.readInt());
                System.out.println("address: "+arq.readInt());
            }
            System.out.println("pont: "+arq.readInt());
            pos+=92;
        }while(pos<=arq.length());   

        arq.close();
    }

    public int SearchArq(int id) throws IOException{
        achou=false;
        int address = SearchArq(0, id);
        return address;
    }

    private int SearchArq(int pos, int id) throws IOException{
        RandomAccessFile arq = new RandomAccessFile("arquivoB.txt", "rw");
        int pontAnt=0, pontPos;
        int elemento;
        int endereco=-1;

        if(pos==0){
            arq.seek(pos);
            pos = arq.readInt();
        }
        
        arq.seek(pos);
        int qtd = arq.readInt();
        for(int i=0; i<qtd; i++){
            pontAnt = arq.readInt();
            elemento = arq.readInt(); 
            endereco = arq.readInt(); 
            if(elemento==id){
                //System.out.println("ENDEREÇO ==== "+endereco);
                achou=true;
                arq.close();
                return endereco;
            }else if(id<elemento && pontAnt!=-1){
                endereco = SearchArq(pontAnt, id);
            }else{
                endereco=0;
            }
            if(achou){
                break;
            }
        }
        
        if(endereco==0 && pontAnt!=-1){
            pontPos = arq.readInt();  
            endereco = SearchArq(pontPos, id); 
        }
        if(achou==false){
            endereco=-1;
        }
        //System.out.println("ENDEREÇO 2 ==== "+endereco);

        arq.close();
        return endereco;
    }

    public void updateAddress(int id, int address) throws IOException{
        //tam fixo por pagina = 8*7 + 8*4 + 4 = 92 bytes
        RandomAccessFile arq = new RandomAccessFile("arquivoB.txt", "rw");
        int pos=4, pont;
        do{
            arq.seek(pos);
            int qtd = arq.readInt();
            for(int i=0; i<qtd; i++){
                pont = arq.readInt();
                int elemento = arq.readInt();
                if(elemento == id){
                    arq.writeInt(address);
                    break;
                }else{
                    pont = arq.readInt();
                }
            }
            pont = arq.readInt();
            pos+=92;
        }while(pos<=arq.length());   

        arq.close();
    }
}