package CodiceV2;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class FileBox {


    private File file; //file da tenere su disco
    private String absolutPathToFile = "C:/Users/dawid/IdeaProjects/progetto2022/data/";
    private String fileName;
    public ReadersWritersHandler handler;


    public FileBox(String fileName) throws IOException {
        this.fileName = fileName;
        this.file = new File(absolutPathToFile + "/" + fileName + ".txt");
        this.file.createNewFile();
        this.handler = new ReadersWritersHandler();

    }

    //altro costruttore per il metodo loadFiles
    public FileBox(String fileName, boolean loader) throws IOException {
        if (loader) {
            this.fileName = fileName;
            this.file = new File(absolutPathToFile + "/" + fileName + ".txt");
            this.handler = new ReadersWritersHandler();

        }
    }

    public File getFile() {
        return this.file;
    }

    public String getLastModified() {
        String fileName1 = absolutPathToFile + "/" + this.fileName + ".txt";
        String filesAttr = "";
        try {
            //formatto e stampo info ultime modifiche file
            Path file = Paths.get(fileName1);
            BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
            FileTime creationTime = attr.creationTime();
            FileTime lastAccessTime = attr.lastAccessTime();
            FileTime lastModifiedTime = attr.lastModifiedTime();
            DateFormat dfCT = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            String dfCreationTime = dfCT.format(creationTime.toMillis());
            String dfLastAccessTime = dfCT.format(lastAccessTime.toMillis());
            String dfLastModifiedTime = dfCT.format(lastModifiedTime.toMillis());

            filesAttr = " creationTime: " + dfCreationTime + " " +
                    " lastAccessTime: " + dfLastAccessTime + " " + " lastModifiedTime: " + dfLastModifiedTime;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return filesAttr;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

//aggiunge line  lavorando con RAF
    public void appendLine(String editRequest) throws IOException {
        //creo raf in mod. scrittura
        RandomAccessFile raf = new RandomAccessFile(this.getFile(), "rw");
        //lunghezza file
        long fileLength = this.getFile().length();
        //punto su ultima linea del file..
        raf.seek(fileLength);
        //e aggiungo la stringa
        raf.writeBytes(editRequest + "\n");
        raf.close();
    }
//rimuove ultima riga del file lavorando sui byte
    public String removeLastLine() throws IOException {
        String filePath = absolutPathToFile + "/" + this.fileName + ".txt";
        //raf in mod. rw
        RandomAccessFile f = new RandomAccessFile(filePath, "rw");
        //lunghezza file -1, ossia punto alla fine dell'ultima linea di testo
        long length = f.length() - 1;
        byte b;
        if (!filePath.isEmpty()) { //controlla che il file non sia vuoto poichè non si può rimuovere linea da file vuoto
            do { //ciclo fino a quando non trovo un linefeed character ossia b==10 iterando all'indietro sulla lunghezza del file
                length -= 1;
                f.seek(length); 
                b = f.readByte();
            } while (b != 10 && length > 0);//quando la trova tronca fino a quella linea
            if (length == 0) {
                f.setLength(length);
            } else {
                f.setLength(length + 1);
            }
            return "Line successfully removed";
        } else {
            return "Cannot remove line from empty file";
        }
    }
 //ritorna linea rimossa con backspace
    public String getRemovedLine() throws IOException {
        String fileName1 = absolutPathToFile + "/" + this.fileName + ".txt";
        String lineRemoved = "";
        if (this.getFile().length() == 0) {
            return lineRemoved;
        } else {
            File f = new File(fileName1);
            String nextLine = "";
            BufferedReader reader = new BufferedReader(new FileReader(f));
            while ((nextLine = reader.readLine()) != null) {
                lineRemoved = nextLine;
            }
        }
        return lineRemoved;
    }

    public void setFile(File file) {
        this.file = file;
    }

}

