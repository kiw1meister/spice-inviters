import javax.swing.*;

public class App {
    public static void main(String[] args) {
        int tileSize = 32; //tile size in pixels
        int rows = 16; //board rows
        int columns = 16; //board columns
        int boardWidth = tileSize * columns; //total width of game board
        int boardHeight = tileSize * rows; //total height of game board

        JFrame frame = new JFrame("Spice Inviters");
        //frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        SpiceInviters spiceInviters = new SpiceInviters();
        frame.add(spiceInviters);
        frame.pack();
        spiceInviters.requestFocus();
        frame.setVisible(true);

    }
}
