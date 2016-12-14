package Federico;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javazoom.jl.player.Player;


import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class MP3 {
    private String filename;
    private Player player;
    private String artist;
    private String album;
    private String title;
    private File file;

    // constructor that takes the name of an MP3 file
    public MP3(String filename, File file) {
        this.file = file;
        this.filename = filename;
        getSongData();
    }




    /* method to extract the metadata from the MP3 file.
    Code derived from example at - https://www.youtube.com/watch?v=OrwO_Q4QJNc
     */
    public void getSongData() {

        try (InputStream input = new FileInputStream(file))
        {
            DefaultHandler handler = new DefaultHandler();
            Metadata metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseCon = new ParseContext();
            parser.parse(input, handler, metadata, parseCon);

            title = metadata.get("title");
            artist = metadata.get("xmpDM:artist");
            album = metadata.get("xmpDM:album");


        }

        catch (IOException | SAXException | TikaException e) {
            e.printStackTrace();
        }}


    //simple closing method
    public void close() { if (player != null) player.close(); }

    // play the MP3 file to the sound card
    public void play() {
        try {
            FileInputStream fis     = new FileInputStream(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new Player(bis);
        }
        catch (Exception e) {
            System.out.println("Problem playing file " + filename);
            System.out.println(e);
        }

        // run in new thread to play in background
        new Thread() {
            public void run() {
                try { player.play(); }
                catch (Exception e) { System.out.println(e); }
            }
        }.start();





    }

    public String getFilename() {
        return filename;
    }

    public File getFile() {
        return file;
    }

    public Player getPlayer (){
        return player;}

    public String getsongname() {
        return title;
    }

    public String getartistname() {
        return artist;
    }

    public String getalbumname (){return album;}

    @Override
    public String toString() {
        return title + " by " + artist;
    }
}
