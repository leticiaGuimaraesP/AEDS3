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

public class LZWCompression {
	public static String maior;
	public static int btsz;

	public static void compactacao(String arquivo) throws IOException {
		btsz = 0;
		maior = "";
		calcularTam(arquivo);
		compactacaoLZW(arquivo);
		btsz = 0;
		maior = "";
		calcularFormulas(arquivo, arquivo+".LZW");
	}

	public static void compactacaoLZW(String arquivo) {
		Map<String, Integer> dicionario = new HashMap<String, Integer>();
		int tamanhoDicionario = 256;
		maior = "";
		for (int i = 0; i < 256; i++){
			dicionario.put("" + (char) i, i);
		}
	
		int mpsz = 256;
		String w = "";
		String fileos = arquivo + ".LZW";
		File file, fileAux;
		file = new File(arquivo);
		fileAux = new File(fileos);

		try {
			FileInputStream fileInput = new FileInputStream(file);
			DataInputStream dataIn = new DataInputStream(fileInput);
			FileOutputStream fileOutput = new FileOutputStream(fileAux);
			DataOutputStream dataOut = new DataOutputStream(fileOutput);
			dataOut.writeInt(btsz);
			Byte c;
			int ch;
			while (true) {
				try {
					c = dataIn.readByte();
					ch = byteParaInt(c);
					String wc = w + (char) ch;
					if (dicionario.containsKey(wc)){
						w = wc;
					}else {
						maior += intParaBinario(dicionario.get(w));
						while (maior.length() >= 8) {
							dataOut.write(stringParaByte(maior.substring(0, 8)));
							maior = maior.substring(8, maior.length());
						}
						if (mpsz < 100000) {
							dicionario.put(wc, tamanhoDicionario++);
							mpsz += wc.length();
						}
						w = "" + (char) ch;
					}
				} catch (EOFException eof) {
					//System.out.println("End of File");
					break;
				}
			}
			if (!w.equals("")) {
				maior += intParaBinario(dicionario.get(w));
				while (maior.length() >= 8) {
					dataOut.write(stringParaByte(maior.substring(0, 8)));
					maior = maior.substring(8, maior.length());
				}
				if (maior.length() >= 1) {
					dataOut.write(stringParaByte(maior));
				}
			}
			dataIn.close();
			dataOut.close();
			fileInput.close();
			fileOutput.close();
		} catch (IOException e) {
			System.out.println("IO exception = " + e);
		}
		file = null;
		fileAux = null;
	}

	public static void calcularTam(String arquivo) {
		Map<String, Integer> dicionario = new HashMap<String, Integer>();
		int tamanhoDicionario = 256;
		for (int i = 0; i < 256; i++){
			dicionario.put("" + (char) i, i);
		}

		int mpsz = 256;
		String w = "";
		File file = null;
		file = new File(arquivo);

		try {
			FileInputStream fileInput = new FileInputStream(file);
			DataInputStream dataIn = new DataInputStream(fileInput);
			Byte c;
			int ch;
			while (true) {
				try {
					c = dataIn.readByte();
					ch = byteParaInt(c);
					String wc = w + (char) ch;
					if (dicionario.containsKey(wc)){
						w = wc;
					} else {
						if (mpsz < 100000) {
							dicionario.put(wc, tamanhoDicionario++);
							mpsz += wc.length();
						}
						w = "" + (char) ch;
					}
				} catch (EOFException eof) {
					//System.out.println("End of File");
					break;
				}
			}
			fileInput.close();
			dataIn.close();
		} catch (IOException e) {
			System.out.println("IO exception = " + e);
		}
		if (tamanhoDicionario <= 1) { //se o arquivo estiver cheio
			btsz = 1;
		} else {
			btsz = 0;
			long i = 1;
			while (i < tamanhoDicionario) {
				i *= 2;
				btsz++;
			}
		}
		file = null;
	}

	public static int byteParaInt(Byte var) {
		int ret = var;
		if (ret < 0) {
			ret += 256;
		}
		return ret;
	}

	//transforma inteiro em binario
	public static String intParaBinario(int inp) {
		String ret = "", r1 = "";
		if (inp == 0)
			ret = "0";
		int i;
		while (inp != 0) {
			if ((inp % 2) == 1){
				ret += "1";
			}else{
				ret += "0";
			}
			inp /= 2;
		}
		for (i = ret.length() - 1; i >= 0; i--) {
			r1 += ret.charAt(i);
		}
		while (r1.length() != btsz) {
			r1 = "0" + r1;
		}
		return r1;
	}
	
	//String inteiro em byte
	public static Byte stringParaByte(String in) {
		int i, n = in.length();
		byte ret = 0;
		for (i = 0; i < n; i++) {
			ret *= 2.;
			if (in.charAt(i) == '1'){
				ret++;
			}		
		}
		for (; n < 8; n++){
			ret *= 2.;
		}
		Byte r = ret;
		return r;
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
