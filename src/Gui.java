import javax.swing.JFrame;

public class Gui {
    public static void main(String[] args) {
        int rows = 21;
        int cols = 19;
        int tileSize = 32;
        int boardWidth = tileSize * cols;
        int boardHeight = tileSize * rows;

        JFrame frame = new JFrame("PacMan");
        frame.setSize(boardWidth, boardHeight); //setSize sempre antes do setLocation
        frame.setLocationRelativeTo(null); // se não a borda da janela fica no meio, não ela toda
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacMan game = new PacMan();
        frame.add(game);
        frame.pack(); // certifica o mesmo tamanho das 2 janelas
        frame.setVisible(true); // deixa visivel depois de tudo estar pronto
    }
}
