package DataCompression;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.PriorityQueue;

public class HuffmanCompression {
    
    static PriorityQueue<arvore> pq = new PriorityQueue<arvore>();
	static int[] freq = new int[300];
	static String[] ss = new String[300];
	static int exbits;
	static byte bt;
	static int cnt; //contador de caracteres diferentes
	static arvore raiz;
	
	//classe da árvore
	static class arvore implements Comparable<arvore> {
		arvore filhoEsq;
		arvore filhoDir;
		public String deb;
		public int Bite;
		public int Freqnc;

		public int compareTo(arvore arvore) {
			int aux=0;
			if (this.Freqnc < arvore.Freqnc){
				aux=-1;
				return aux;
			}
			if (this.Freqnc > arvore.Freqnc){
				aux=1;
				return aux;
			}
			return 0;
		}
	}

	public static void compactacao(String arquivo) throws IOException {
		inicioCompactacao();
		CalFreq(arquivo); //calcula a frequencia de cada símbolo
		formataNo();
		if (cnt > 1){
			dfs(raiz, "");
		}
		fakezip(arquivo); 
		realzip("arqAux.txt", arquivo + ".HUFF"); 
		inicioCompactacao();
		String arquivoC = arquivo + ".HUFF";
		calcularFormulas(arquivo, arquivoC); 
	}
	
	public static void inicioCompactacao() {
		int i;
		cnt = 0;
		if (raiz != null){
			fredfs(raiz);
		}
		for (i = 0; i < 300; i++){
			freq[i] = 0;
		}
		for (i = 0; i < 300; i++){
			ss[i] = "";
		}	
		pq.clear();
	}
	
	//calcula a frequencia do arquivo passado por parâmetro
	public static void CalFreq(String arquivo) {
		File file = null;
		file = new File(arquivo);
		try {
			FileInputStream file_input = new FileInputStream(file);
			DataInputStream data_in = new DataInputStream(file_input);
			while (true) {
				try {

					bt = data_in.readByte();
					freq[binario(bt)]++;
				} catch (EOFException eof) {
					//System.out.println("End of File");
					break;
				}
			}
			file_input.close();
			data_in.close();
		} catch (IOException e) {
			System.out.println("IO Exception =: " + e);
		}
		file = null;
	}

	//método de conversão de byte para binário
	public static int binario(Byte var) {
		int ret = var;
		if (ret < 0) {
			ret = ~var;
			ret = ret + 1;
			ret = ret ^ 255;
			ret += 1;
		}
		return ret;
	}

	public static void fredfs(arvore atual) {
		if (atual.filhoEsq == null && atual.filhoDir == null) {
			atual = null;
			return;
		}
		if (atual.filhoEsq != null){
			fredfs(atual.filhoEsq);
		}
		if (atual.filhoDir != null){
			fredfs(atual.filhoDir);
		}
	}
	
	public static void dfs(arvore atual, String st) {
		atual.deb = st;
		if ((atual.filhoEsq == null) && (atual.filhoDir == null)) {
			ss[atual.Bite] = st;
			return;
		}
		if (atual.filhoEsq != null){
			dfs(atual.filhoEsq, st + "0");
		}
		if (atual.filhoDir != null){
			dfs(atual.filhoDir, st + "1");
		}
	}

	//formando os nós
	public static void formataNo() {
		int i;
		pq.clear();
		for (i = 0; i < 300; i++) {
			if (freq[i] != 0) {
				arvore arvoreTemp = new arvore();
				arvoreTemp.Bite = i;
				arvoreTemp.Freqnc = freq[i];
				arvoreTemp.filhoEsq = null;
				arvoreTemp.filhoDir = null;
				pq.add(arvoreTemp);
				cnt++;
			}
		}
		arvore arvoreTemp1, arvoreTemp2;
		if (cnt == 0) {
			return;
		} else if (cnt == 1) {
			for (i = 0; i < 300; i++)
				if (freq[i] != 0) {
					ss[i] = "0";
					break;
				}
			return;
		}
		//o arquivo não pode estar vazio
		while (pq.size() != 1) {
			arvore arvoreTemp = new arvore();
			arvoreTemp1 = pq.poll();
			arvoreTemp2 = pq.poll();
			arvoreTemp.filhoEsq = arvoreTemp1;
			arvoreTemp.filhoDir = arvoreTemp2;
			arvoreTemp.Freqnc = arvoreTemp1.Freqnc + arvoreTemp2.Freqnc;
			pq.add(arvoreTemp);
		}
		raiz = pq.poll();
	}

	//cria um arquivo auxiliar para colocar os símbolos binários
	public static void fakezip(String arquivo) {
		File file, fileAux;
		file = new File(arquivo);
		fileAux = new File("arqAux.txt");

		try {
			FileInputStream file_input = new FileInputStream(file);
			DataInputStream data_in = new DataInputStream(file_input);
			PrintStream ps = new PrintStream(fileAux);
			while (true) {
				try {
					bt = data_in.readByte();
					ps.print(ss[binario(bt)]);
				} catch (EOFException eof) {
					//System.out.println("End of File");
					break;
				}
			}
			file_input.close();
			data_in.close();
			ps.close();

		} catch (IOException e) {
			System.out.println("IO Exception =: " + e);
		}
		file = null;
		fileAux = null;

	}

	//cria o arquivo compactado
	public static void realzip(String arquivo, String arquivoAux) {
		File file, fileAux;
		file = new File(arquivo);
		fileAux = new File(arquivoAux);

		int i;
		Byte varByte;
		
		try {
			FileInputStream file_input = new FileInputStream(file);
			DataInputStream data_in = new DataInputStream(file_input);
			FileOutputStream file_output = new FileOutputStream(fileAux);
			DataOutputStream data_out = new DataOutputStream(file_output);
			data_out.writeInt(cnt);
			for (i = 0; i < 256; i++) {
				if (freq[i] != 0) {
					varByte = (byte) i;
					data_out.write(varByte);
					data_out.writeInt(freq[i]);
				}
			}
			long textoBits;
			textoBits = file.length() % 8;
			textoBits = (8 - textoBits) % 8;
			exbits = (int) textoBits;
			data_out.writeInt(exbits);
			while (true) {
				try {
					bt = 0;
					byte ch;
					for (exbits = 0; exbits < 8; exbits++) {
						ch = data_in.readByte();
						bt *= 2;
						if (ch == '1'){
							bt++;
						}	
					}
					data_out.write(bt);
				} catch (EOFException eof) {
					int x;
					if (exbits != 0) {
						for (x = exbits; x < 8; x++) {
							bt *= 2;
						}
						data_out.write(bt);
					}
					exbits = (int) textoBits;
					//System.out.println("extrabits: " + exbits);
					//System.out.println("End of File");
					break;
				}
			}
			data_in.close();
			data_out.close();
			file_input.close();
			file_output.close();
			//System.out.println("output file's size: " + fileo.length());
		} catch (IOException e) {
			System.out.println("IO exception = " + e);
		}
		file.delete();
		file = null;
		fileAux = null;
	}

	public static void calcularFormulas(String arquivo, String arquivoC) throws IOException{
		RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");
		RandomAccessFile arqC = new RandomAccessFile(arquivoC, "rw");

		double tam = arq.length();
		double tamC = arqC.length();
		System.out.println("Tamnho dos arquivo original: "+ tam);
		System.out.println("Tamnho dos arquivo compactado: "+ tamC);

		double  div = tamC/tam;
		double pr = 100 * (1 - div);
		System.out.println("A taxa de compressão foi: "+div);
		System.out.println("O fator de compressão foi: "+pr);

		div = (double) tam/tamC;
		double gc = 100 * log(div);
		System.out.println("O ganho de compressão foi: "+gc);

		arq.close();
		arqC.close();
	}

	public static double log(double valor) {
        return Math.log(valor) / Math.log(2.71828182846);
    }
}
