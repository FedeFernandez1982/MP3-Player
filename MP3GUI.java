package Federico;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.sql.*;

/**
 * Created by Federico on 12/6/2016.
 */
public class MP3GUI extends JFrame  {
    private JButton openFileButton;
    private JButton playButton;
    private JButton stopButton;
    private JPanel rootPanel;
    private JList <MP3> Playlist;
    private JButton QuitButton;
    private JTextField NowPlayingField;
    private JButton nextButton;
    private JButton PreviousButton;
    private JButton SavePlaylist;
    private JButton DisplayPlaylist;

    MP3 Song;
    DefaultListModel <MP3> PlaylistModel;

    int initindex = 0;

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";        //Configure the driver needed
    static final String DB_CONNECTION_URL = "jdbc:mysql://localhost:3306/playlistdb";
    static final String USER = "root";
    static final String PASSWORD = "88@Data99";



    public MP3GUI() {
        super("BASIC MP3 PLAYER");

        setContentPane(rootPanel);

        pack();
        Song = null;
        ListModel listSize = Playlist.getModel();


        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        PlaylistModel = new DefaultListModel<>();
        Playlist.setModel(PlaylistModel);




        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFile();
                Song.getSongData();

            }
        });
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Song != null) {
                        Song.close();
                        Song = PlaylistModel.getElementAt(initindex);
                        NowPlayingField.setText(Song.toString());
                        Song.play();


                }
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Song != null) {
                    Song.close();


                }
            }
        });
        QuitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        Playlist.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                initindex = Playlist.getSelectedIndex();
                if (initindex != -1){
                    Song.close();
                    Song = PlaylistModel.getElementAt(initindex);

                }
            }
        });


        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    Song.close();
                    Song = PlaylistModel.getElementAt(initindex + 1);
                    Song.play();
                    NowPlayingField.setText(Song.toString());
                    initindex++;


                }
                catch(Exception endl) {
                    endl.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error!!! You can't do that.");

                }

                }


        });
        PreviousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Song.close();
                    Song = PlaylistModel.getElementAt(initindex - 1);
                    Song.play();
                    NowPlayingField.setText(Song.toString());
                    initindex--;

                }
                catch(Exception endl){
                    endl.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error!!! You can't do that.");

                }

            }
        });
        SavePlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

        //Code for the database derived from several examples of code from the labs
                try {

                    Class.forName(JDBC_DRIVER);

                } catch (ClassNotFoundException cnfe) {
                    System.out.println("Can't instantiate driver class; check you have drives and classpath configured correctly?");
                    cnfe.printStackTrace();
                    System.exit(-1);  //No driver? Need to fix before anything else will work. So quit the program
                }


        // Create a table, and insert

                try (Connection conn = DriverManager.getConnection(DB_CONNECTION_URL, USER, PASSWORD);


                    Statement statement = conn.createStatement()) {


                    String createTableSQL = "CREATE TABLE IF NOT EXISTS Records (Song varchar(50), Album varchar(50), Artist varchar (50))";
                    statement.executeUpdate(createTableSQL);




                        Song = PlaylistModel.getElementAt(Playlist.getSelectedIndex());

                        String prepStatInsert = "INSERT INTO Records VALUES (? , ?, ?)";

                        PreparedStatement psInsert = conn.prepareStatement(prepStatInsert);


                        psInsert.setString(1, Song.getsongname());
                        psInsert.setString(2, Song.getalbumname());
                        psInsert.setString(3, Song.getartistname());

                        System.out.println("Added row to database");


                        psInsert.executeUpdate();


                            statement.close();
                            conn.close();




                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                    System.exit(-1);
                }

            }
        });
        DisplayPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection conn = DriverManager.getConnection(DB_CONNECTION_URL, USER, PASSWORD);
                     Statement statement = conn.createStatement()) {

            /* Fetch all the data and display it. */

                    String fetchAllDataSQL = "SELECT * FROM Records";
                    ResultSet rs = statement.executeQuery(fetchAllDataSQL);

                    while (rs.next()) {

                        String name = rs.getString("Song");
                        String record = rs.getString("Album");
                        String singer = rs.getString("Artist");
                        System.out.println("Song : " + name + " Album : " + record + " Artist : " + singer);

                    }


                    System.out.println();
                    System.out.println("Retrieved and displayed database");
                    System.out.println();

                    rs.close();  //and close result set when done
                    statement.close();  //and statement
                    conn.close();   //and connection

                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                    System.exit(-1);
                }

            }
        });
    }

    //method to use a Jchooser when selecting audio files.
    public void selectFile() {
        JFileChooser chooser = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("MPEG3 songs", "mp3");
        chooser.setFileFilter(filter);

        chooser.setCurrentDirectory(new java.io.File("C:/Users/Federico/Desktop"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            File f = chooser.getSelectedFile();
            Song = new MP3(f.getAbsolutePath(),f);
            PlaylistModel.addElement(Song);

        }

    }
}
