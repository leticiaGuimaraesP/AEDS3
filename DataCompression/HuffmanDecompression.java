package DataCompression;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.PriorityQueue;

public class HuffmanDecompression {
	static String maior; 
	static String temp; 
	static int exbits1; 
	static int putit; 
	static int cntu; 
	static PriorityQueue<arvore> pq1 = new PriorityQueue<arvore>();
	static int[] freq1 = new int[300];
	static String[] ss1 = new String[300]; 
	static String[] btost = new String[300]; 
	static arvore raiz;

	static class arvore implements Comparable<arvore> {
		arvore filhoEsq;
		arvore filhoDir;
		public String deb;
		public int Bite;
		public int freq1nc;
		public int compareTo(arvore T) {
			if (this.freq1nc < T.freq1nc)
				return -1;
			if (this.freq1nc > T.freq1nc)
				return 1;
			return 0;
		}
	}

	public static void descompactacao(String arquivo) throws IOException {
		inicioDescompactacao();
		readfreq1(arquivo);
		criacaoBinario();
		int n = arquivo.length();
		String arquivo2 = arquivo.substring(0, n - 5);
		leituraBinario(arquivo, arquivo2);
		inicioDescompactacao();
		calculaTam(arquivo, arquivo2);
	}

	public static void inicioDescompactacao() {
		int i;
		if (raiz != null)
			fredfs1(raiz);
		for (i = 0; i < 300; i++)
			freq1[i] = 0;
		for (i = 0; i < 300; i++)
			ss1[i] = "";
		pq1.clear();
		maior = ""; 
		temp = ""; 
		exbits1 = 0; 
		putit = cntu = 0;
		
	}

	public static void leituraBinario(String arquivo1, String arquivo2) {
		File file1 = null, file2 = null;
		file1 = new File(arquivo1);
		file2 = new File(arquivo2);
		int ok, bt;
		Byte b;
		int j, i;
		maior = "";
		
		try {
			FileOutputStream fileOutput = new FileOutputStream(file2);
			DataOutputStream dataOut = new DataOutputStream(fileOutput);
			FileInputStream fileIn = new FileInputStream(file1);
			DataInputStream dataInput = new DataInputStream(fileIn);
			try {
				cntu = dataInput.readInt();
				//System.out.println(cntu);
				for (i = 0; i < cntu; i++) {
					b = dataInput.readByte();
					j = dataInput.readInt();
					// System.out.println(ss[to(b)]);
				}
				exbits1 = dataInput.readInt();
				//System.out.println(exbits1);
			} catch (EOFException eof) {
				//System.out.println("End of File");
			}
			while (true) {
				try {
					b = dataInput.readByte();
					bt = simbolo(b);
					maior += formataStringOito(btost[bt]);
					// System.out.println(maior);
					while (true) {
						ok = 1;
						temp = "";
						for (i = 0; i < maior.length() - exbits1; i++) {
							temp += maior.charAt(i);
							// System.out.println(temp);
							if (got() == 1) {
								dataOut.write(putit);
								ok = 0;
								String s = "";
								for (j = temp.length(); j < maior.length(); j++) {
									s += maior.charAt(j);
								}
								maior = s;
								break;
							}
						}
						if (ok == 1){
							break;
						}
					}
				} catch (EOFException eof) {
					//System.out.println("End of File");
					break;
				}
			}
			fileOutput.close();
			dataOut.close();
			fileIn.close();
			dataInput.close();
		} catch (IOException e) {
			System.out.println("IO Exception =: " + e);
		}
		file1 = null;
		file2 = null;
	}
	
	public static void fredfs1(arvore atual) {
		if (atual.filhoEsq == null && atual.filhoDir == null) {
			atual = null;
			return;
		}
		if (atual.filhoEsq != null){
			fredfs1(atual.filhoEsq);
		}
		if (atual.filhoDir != null){
			fredfs1(atual.filhoDir);
		}
	}

	public static void dfs1(arvore atual, String str) {
		atual.deb = str;
		if ((atual.filhoEsq == null) && (atual.filhoDir == null)) {
			ss1[atual.Bite] = str;
			return;
		}
		if (atual.filhoEsq != null){
			dfs1(atual.filhoEsq, str + "0");
		}	
		if (atual.filhoDir != null){
			dfs1(atual.filhoDir, str + "1");
		}	
	}

	public static void MakeNode1() {
		int i;
		cntu = 0;
		for (i = 0; i < 300; i++) {
			if (freq1[i] != 0) {
				arvore Temp = new arvore();
				Temp.Bite = i;
				Temp.freq1nc = freq1[i];
				Temp.filhoEsq = null;
				Temp.filhoDir = null;
				pq1.add(Temp);
				cntu++;
			}
		}
		arvore temp1, temp2;
		if (cntu == 0) {
			return;
		} else if (cntu == 1) {
			for (i = 0; i < 300; i++){
				if (freq1[i] != 0) {
					ss1[i] = "0";
					break;
				}
			}
		}
		while (pq1.size() != 1) {
			arvore Temp = new arvore();
			temp1 = pq1.poll();
			temp2 = pq1.poll();
			Temp.filhoEsq = temp1;
			Temp.filhoDir = temp2;
			Temp.freq1nc = temp1.freq1nc + temp2.freq1nc;
			pq1.add(Temp);
		}
		raiz = pq1.poll();
	}

	public static void readfreq1(String arquivo) {
		File file = new File(arquivo);
		int fey, i;
		Byte baital;
		try {
			FileInputStream fileIn = new FileInputStream(file);
			DataInputStream dataInput = new DataInputStream(fileIn);
			cntu = dataInput.readInt();
			for (i=0; i<cntu; i++) {
				baital = dataInput.readByte();
				fey = dataInput.readInt();
				freq1[simbolo(baital)] = fey;
			}
			dataInput.close();
			fileIn.close();
		} catch (IOException e) {
			System.out.println("IO exception = " + e);
		}
		MakeNode1(); 
		if (cntu>1)
			dfs1(raiz, ""); 

		for (i=0; i<256; i++) {
			if (ss1[i] == null){
				ss1[i] = "";
			}	
		}
		file = null;
	}

	public static void criacaoBinario() {
		int i, j;
		String t;
		for (i = 0; i < 256; i++) {
			btost[i] = "";
			j = i;
			while (j!=0) {
				if (j % 2 == 1){
					btost[i] += "1";
				}else{
					btost[i] += "0";
				}
				j /= 2;
			}
			t = "";
			for (j = btost[i].length() - 1; j >= 0; j--) {
				t += btost[i].charAt(j);
			}
			btost[i] = t;
			// System.out.println(btost[i]);
		}
		btost[0] = "0";
	}

	public static int got() {
		int i;
		for (i = 0; i < 256; i++) {
			if (ss1[i].compareTo(temp) == 0) {
				putit = i;
				return 1;
			}
		}
		return 0;
	}

	public static int simbolo(Byte b) {
		int ret = b;
		if (ret < 0) {
			ret = ~b;
			ret = ret + 1;
			ret = ret ^ 255;
			ret += 1;
		}
		return ret;
	}

	public static String formataStringOito(String b) {
		String ret = "";
		int i;
		int len = b.length();
		for (i = 0; i < (8 - len); i++){
			ret += "0";
		}
		ret += b;
		return ret;
	}

	public static void calculaTam(String arquivo, String arquivoC) throws IOException {
		RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");
		RandomAccessFile arqC = new RandomAccessFile(arquivoC, "rw");

		double tam = arqC.length();
		double tamC = arq.length();
		System.out.println("Tamnho dos arquivo descompactado: "+ tam);
		System.out.println("Tamnho dos arquivo compactado: "+ tamC);
	}
}
