package Entities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Movies{
    static SimpleDateFormat default_dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    static SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH); //Foi necessario usar outro modelo para escrever em byte
    //pq usando o formato com a - nao estava dando certo

    private int id;
    private String language;
    private String title;
    private Date release_date;
    private double average;
    private String gender;

    //construtor sem parametros
    public Movies(){
        id = -1;
        language = "";
        title = "";
        average = 0.0;
        gender = "";
    }

    //construtor com parametors
    Movies(int id, String language, String title, Date releaseDate, double average, String gender){
        this.id = id;
        this.language = language;
        this.title = title;
        this.release_date = releaseDate;
        this.average = average;
        this.gender = gender;
    }

    //get's e set's de todos os atributos
    public int getId(){
        return this.id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getLanguage(){
        return this.language;
    }
    public void setLanguage(String language){
        this.language = language;
    }
    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public Date getDate(){
        return this.release_date;
    }
    public void setDate(String data){
        Date aux = null;
        try{
            aux = default_dateFormat.parse(data);
        } catch (java.text.ParseException e) { e.printStackTrace(); };
        this.release_date = aux;
    }
    public double getAverage(){
        return this.average;
    }
    public void setAverage(double average){
        this.average = average;
    }
    public String getGender(){
        return this.gender;
    }
    public void setGender(String gender){
        this.gender = gender;
    }

    //método que transforma um objeto Movie em um array de bytes para que seja escrito nos arquivos
    public byte[] toByteArray() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(id);
        String lg = "";
        lg += language.charAt(0); //definindo como um campo fixo com 2 caracteres
        lg += language.charAt(1);
        dos.writeUTF(lg);
        dos.writeUTF(title);
        long miliseconds = release_date.getTime();
        dos.writeLong(miliseconds);
        dos.writeDouble(average);
        dos.writeUTF(gender);
        return baos.toByteArray();
    }

    //método que transforma um array de bytes em um objeto Movie 
    public void fromByteArray(byte ba[]) throws IOException{
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.language = dis.readUTF();
        this.title = dis.readUTF();
        long miliseconds  = dis.readLong();
        Date aux = new Date(miliseconds);
        this.release_date = aux;
        this.average = dis.readDouble();
        this.gender = dis.readUTF();
    }

    //Transforma o gênero passado por parâmetro em um array de bytes
    public static byte[] invListToByteArray(String key) throws IOException{ 
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeUTF(key);

        return baos.toByteArray();
    }

    //método para realizar a atribuição dos campos da base de dados nos atributos da classe Movie 
    public void assignment(String line) throws ParseException{
        int index = 0, i = 1;

        //Leitura do atributo "id"
        while(true){
            if(line.charAt(i) == ','){
                this.id = Integer.parseInt(line.substring(index, i));
                index = ++i;
                break;
            }
            i++;
        }     

        //Deslocar o index e o i para o próximo campo a ser lido
        while(true){
            if(line.charAt(i) != ','){
                index++;
                i++;
            }
            else{
                i++;
                index++;
                break;
            }
        }

        //Leitura do atributo "language"
        while(true){
            if(line.charAt(i) == ','){
                language = line.substring(index, i);
                index = ++i;
                break;
            }
            i++;
        }

        //Leitura do atributo "title"
        while(true){
            if(line.charAt(i) == '"'){
                index++;
                i++;
                while(true){
                    if(line.charAt(i) == '"' && line.charAt(i + 1) == ','){
                        title = line.substring(index, i);
                        index = i + 2;
                        i += 2;
                        break;
                    }
                    i++;
                }
                break;
            }
            else if(line.charAt(i) == ','){
                title = line.substring(index, i);
                index = ++i;
                break;
            }
            i++;
        }

        //Deslocar o index e o i para o próximo campo a ser lido
        while(true){
            if(line.charAt(i) != ','){
                index++;
                i++;
            }
            else{
                i++;
                index ++;
                break;
            }
        }

        //Leitura do atributo "release-_date"
        while(true){
            if(line.charAt(i) == ','){
                try{
                    release_date = default_dateFormat.parse(line.substring(index, i));
                } catch (java.text.ParseException e) { e.printStackTrace(); };
                index = ++i;
                break;
            }
            i++;
        }
        
        //Leitura do atributo "average"
        while(true){
            if(line.charAt(i) == ','){
                average = Double.parseDouble(line.substring(index, i));
                index = ++i;
                break;
            }
            i++;
        }

        //Deslocar o index e o i para o próximo campo a ser lido
        while(true){
            if(line.charAt(i) != ','){
                index++;
                i++;
            }
            else{
                i ++;
                index += 2;
                break;
            }
        }

        //Leitura do atributo "gender"
        while(true){
            if(line.charAt(i) == '['){
                index = ++i;
                i++;
                while(true){
                    if(line.charAt(i) == ']'){
                        gender = line.substring(index, i);
                        break;
                    }
                    i++;
                }
                break;
            }
            i++;
        }
    }

    public String toString() {  
        String dataFormatada = df.format(release_date);
        return title + " " + language + " " + dataFormatada + " " + average + " " + gender;         
    }
}