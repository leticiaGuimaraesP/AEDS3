package DataCompression;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

public class LZWDecompression {
	public static int bitsz1;
	public static String bttost[] = new String[256];
	public static String big1;

	public static void descompactacao(String arquivo) throws IOException {
		big1 = "";
		bitsz1 = 0;
		binarioParaString();
		descompactacaoLZW(arquivo);
	}

	public static void descompactacaoLZW(String arquivo) throws IOException {
		String arquivoC = arquivo.substring(0, arquivo.length() - 4);
		int tamDicionario = 256;
		int k, mpsz = 256;
		String ts;
		Map<Integer, String> dicionario = new HashMap<Integer, String>();
		for (int i = 0; i < 256; i++){
			dicionario.put(i, "" + (char) i);
		}
		String fileF = arquivoC;

		File file = null, fileAux = null;
		file = new File(arquivo);
		fileAux = new File(fileF);
		try {
			FileInputStream fileInput = new FileInputStream(file);
			DataInputStream dataIn = new DataInputStream(fileInput);
			FileOutputStream fileOutput = new FileOutputStream(fileAux);
			DataOutputStream dataOut = new DataOutputStream(fileOutput);
			Byte c;
			bitsz1 = dataIn.readInt();

			while (true) {
				try {
					c = dataIn.readByte();
					big1 += bttost[byteParaInteiro(c)];
					if (big1.length() >= bitsz1)
						break;
				} catch (EOFException eof) {
					System.out.println("End of File");
					break;
				}
			}
			if (big1.length() >= bitsz1) {
				k = stringParaInt(big1.substring(0, bitsz1));
				big1 = big1.substring(bitsz1, big1.length());
			} else {
				dataIn.close();
				dataOut.close();
				return;
			}
			String w = "" + (char) k;
			dataOut.writeBytes(w);
			// System.out.println(w);
			while (true) {
				try {
					while (big1.length() < bitsz1) {
						c = dataIn.readByte();
						big1 += bttost[byteParaInteiro(c)];
					}
					k = stringParaInt(big1.substring(0, bitsz1));
					big1 = big1.substring(bitsz1, big1.length());

					String entry = "";
					if (dicionario.containsKey(k)) {
						entry = dicionario.get(k);
					} else if (k == tamDicionario) {
						entry = w + w.charAt(0);

					}
					dataOut.writeBytes(entry);

					if (mpsz < 100000) {
						ts = w + entry.charAt(0);
						dicionario.put(tamDicionario++, ts);
						mpsz += ts.length();
					}
					w = entry;
				} catch (EOFException eof) {
					System.out.println("End of File");
					break;
				}
			}
			dataIn.close();
			dataOut.close();
			fileInput.close();
			fileOutput.close();
		} catch (IOException e) {
			System.out.println("IO exception = " + e);
		}

		calculaTam(arquivo, arquivoC);
		
		file = null;
		fileAux = null;
	}

	//conversao dos binarios para string (8 bits)
	public static void binarioParaString() {
		int i, j;
		String r1;
		bttost[0] = "0";
		for (i = 0; i < 256; i++) {
			r1 = "";
			j = i;
			if (i != 0)
				bttost[i] = "";
			while (j != 0) {
				if ((j % 2) == 1){
					bttost[i] += "1";
				}else{
					bttost[i] += "0";
				}
				j /= 2;
			}
			for (j = bttost[i].length() - 1; j >= 0; j--) {
				r1 += bttost[i].charAt(j);
			}
			while (r1.length() < 8) {
				r1 = "0" + r1;
			}
			bttost[i] = r1;
		}
	}
	
	//byte para inteiro
	public static int byteParaInteiro(Byte bt) {
		int ret = bt;
		if (ret < 0){
			ret += 256;
		}
		return ret;

	}

	//String para int
	public static int stringParaInt(String s) {
		int ret = 0, i;
		for (i = 0; i < s.length(); i++) {
			ret *= 2;
			if (s.charAt(i) == '1'){
				ret++;
			}
		}
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
