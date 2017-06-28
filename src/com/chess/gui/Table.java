package com.chess.gui;


import static javax.swing.JFrame.setDefaultLookAndFeelDecorated;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import com.chess.engine.classic.board.*;
import com.chess.engine.classic.board.Move.MoveFactory;
import com.chess.engine.classic.pieces.Piece;
import com.chess.engine.classic.player.Player;
import com.chess.engine.classic.player.ai.*;
import com.google.common.collect.Lists;


public final class Table extends Observable {

    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;
    private Board chessBoard;
    private Move computerMove;
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    private String pieceIconPath =  "src/com/chess/simple/";
    private Color lightTileColor = Color.decode("#f0dab5");
    private Color darkTileColor = Color.decode("#b58763");
    private HashMap <String,BufferedImage> mapa = new HashMap <String,BufferedImage>();
    private HashMap <String,BufferedImage> mapaOffline = new HashMap <String,BufferedImage>();
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(900, 900);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(450, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

    private static final Table INSTANCE = new Table();

    private Table()  {
        //URL ua = new URL("http://4.1m.yt/CPEiZie.gif");
        //final BufferedImage image = ImageIO.read(ua.openStream());
       /* try {
            mapa.put("BQ", ImageIO.read(new URL("http://4.1m.yt/h0SMn1U.gif").openStream()));
            mapa.put("WQ", ImageIO.read(new URL("http://4.1m.yt/iBES5.gif").openStream()));
            mapa.put("BK", ImageIO.read(new URL("http://4.1m.yt/1NbZRdO.gif").openStream()));
            mapa.put("WK", ImageIO.read(new URL("http://3.1m.yt/1xhW8fc.gif").openStream()));
            mapa.put("BP", ImageIO.read(new URL("http://2.1m.yt/n-be-h.gif").openStream()));
            mapa.put("WP", ImageIO.read(new URL("http://3.1m.yt/zOpd3bL.gif").openStream()));
            mapa.put("BN", ImageIO.read(new URL("http://1.1m.yt/oclPEBk.gif").openStream()));
            mapa.put("WN", ImageIO.read(new URL("http://4.1m.yt/hQ4lnzd.gif").openStream()));
            mapa.put("WB", ImageIO.read(new URL("http://1.1m.yt/Ey7sF8n.gif").openStream()));
            mapa.put("BB", ImageIO.read(new URL("http://1.1m.yt/NdGif4.gif").openStream()));
            mapa.put("WR", ImageIO.read(new URL("http://4.1m.yt/CPEiZie.gif").openStream()));
            mapa.put("BR", ImageIO.read(new URL("http://2.1m.yt/EpMrh-i.gif").openStream()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/


        try {
            //System.out.println("OVO JE MAPA " + pieceIconPath+"BQ.gif");
            mapa.put("BQ", ImageIO.read(new File(pieceIconPath+"BQ.gif")));
            mapa.put("WQ", ImageIO.read(new File(pieceIconPath+"WQ.gif")));
            mapa.put("BK", ImageIO.read(new File(pieceIconPath+"BK.gif")));
            mapa.put("WK", ImageIO.read(new File(pieceIconPath+"WK.gif")));
            mapa.put("BP", ImageIO.read(new File(pieceIconPath+"BP.gif")));
            mapa.put("WP", ImageIO.read(new File(pieceIconPath+"WP.gif")));
            mapa.put("BN", ImageIO.read(new File(pieceIconPath+"BN.gif")));
            mapa.put("WN", ImageIO.read(new File(pieceIconPath+"WN.gif")));
            mapa.put("WB", ImageIO.read(new File(pieceIconPath+"WB.gif")));
            mapa.put("BB", ImageIO.read(new File(pieceIconPath+"BB.gif")));
            mapa.put("WR", ImageIO.read(new File(pieceIconPath+"WR.gif")));
            mapa.put("BR", ImageIO.read(new File(pieceIconPath+"BR.gif")));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



        this.gameFrame = new JFrame("CHESS");
        final JMenuBar tableMenuBar = new JMenuBar();
        populateMenuBar(tableMenuBar);
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setLayout(new BorderLayout());
        this.chessBoard = Board.createStandardBoard();
        this.boardDirection = BoardDirection.NORMAL;
       // this.pieceIconPath = "src/com/chess/simple/";
        this.gameHistoryPanel = new GameHistoryPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);


        setDefaultLookAndFeelDecorated(true);
        this.gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        center(this.gameFrame);
        this.gameFrame.setVisible(true);
    }

    public static Table get() {
        return INSTANCE;
    }

    private JFrame getGameFrame() {
        return this.gameFrame;
    }

    private Board getGameBoard() {
        return this.chessBoard;
    }

    private MoveLog getMoveLog() {
        return this.moveLog;
    }

    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    private GameHistoryPanel getGameHistoryPanel() {
        return this.gameHistoryPanel;
    }



    private GameSetup getGameSetup() {
        return this.gameSetup;
    }



    public void show() {
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    private void populateMenuBar(final JMenuBar tableMenuBar) {
        tableMenuBar.add(createFileMenu());

        tableMenuBar.add(createOptionsMenu());
    }

    private static void center(final JFrame frame) {
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        final int w = frame.getSize().width;
        final int h = frame.getSize().height;
        final int x = (dim.width - w) / 2;
        final int y = (dim.height - h) / 2;
        frame.setLocation(x, y);
    }

    private JMenu createFileMenu() {
        final JMenu filesMenu = new JMenu("File");
        filesMenu.setMnemonic(KeyEvent.VK_F);


        final JMenuItem resetMenuItem = new JMenuItem("New Game", KeyEvent.VK_P);
        resetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                undoAllMoves();
            }

        });
        filesMenu.add(resetMenuItem);

        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game", KeyEvent.VK_S);
        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });
        filesMenu.add(setupGameMenuItem);
        final JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Table.get().getGameFrame().dispose();
                System.exit(0);
            }
        });
        filesMenu.add(exitMenuItem);

        return filesMenu;
    }

    private JMenu createOptionsMenu() {

        final JMenu optionsMenu = new JMenu("Options");
        optionsMenu.setMnemonic(KeyEvent.VK_O);



        final JMenuItem evaluateBoardMenuItem = new JMenuItem("Evaluate Board", KeyEvent.VK_E);
        evaluateBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                System.out.println(StandardBoardEvaluator.get().evaluate(chessBoard, gameSetup.getSearchDepth()));

            }
        });
        optionsMenu.add(evaluateBoardMenuItem);



        final JMenuItem legalMovesMenuItem = new JMenuItem("Current State", KeyEvent.VK_L);
        legalMovesMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                System.out.println(chessBoard.getWhitePieces());
                System.out.println(chessBoard.getBlackPieces());
                System.out.println(playerInfo(chessBoard.currentPlayer()));
                System.out.println(playerInfo(chessBoard.currentPlayer().getOpponent()));
            }
        });
        optionsMenu.add(legalMovesMenuItem);


        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip board");

        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });

        optionsMenu.add(flipBoardMenuItem);

        return optionsMenu;
    }


    private static String playerInfo(final Player player) {
        return ("Player is: " +player.getAlliance() + "\nlegal moves =" + player.getLegalMoves() + "\ninCheck = " +
                player.isInCheck() + "\nisInCheckMate = " +player.isInCheckMate() +
                "\nisCastled = " +player.isCastled())+ "\n";
    }

    private void updateGameBoard(final Board board) {
        this.chessBoard = board;
    }

    private void updateComputerMove(final Move move) {
        this.computerMove = move;
    }

    private void undoAllMoves() {
        for(int i = Table.get().getMoveLog().size() - 1; i >= 0; i--) {
            final Move lastMove = Table.get().getMoveLog().removeMove(Table.get().getMoveLog().size() - 1);
            this.chessBoard = this.chessBoard.currentPlayer().unMakeMove(lastMove).getToBoard();
        }
        this.computerMove = null;
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(chessBoard);
    }


    private void moveMadeUpdate(final PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }

    private void setupUpdate(final GameSetup gameSetup) {
        setChanged();
        notifyObservers(gameSetup);
    }

    private static class TableGameAIWatcher
            implements Observer {

        public void update(final Observable o,
                           final Object arg) {
            if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
                !Table.get().getGameBoard().currentPlayer().isInCheckMate() &&
                !Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
                //System.out.println(Table.get().getGameBoard().currentPlayer() + " is set to AI, thinking....");
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }

            if (  Table.get().getGameBoard().currentPlayer().isInCheckMate()) {

                JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over: Player " + Table.get().getGameBoard().currentPlayer() + " is in checkmate!", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            if (Table.get().getGameBoard().currentPlayer().isInStaleMate()) {

                JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over: Player " + Table.get().getGameBoard().currentPlayer() + " is in stalemate!", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        }

    }

    enum PlayerType {
        HUMAN,
        COMPUTER
    }

    private static class AIThinkTank extends SwingWorker<Move, String> {

        private AIThinkTank() {
        }

        @Override
        protected Move doInBackground() throws Exception {
            final Move bestMove;
             if (Table.get().getGameSetup().getAB())
             {
                 final AlphaBeta strategy = new AlphaBeta();
                 bestMove = strategy.execute(
                         Table.get().getGameBoard(), Table.get().getGameSetup().getSearchDepth());
             }
             else {
                 final MiniMax strategy = new MiniMax();
                 bestMove = strategy.execute(
                         Table.get().getGameBoard(), Table.get().getGameSetup().getSearchDepth());
             }

            return bestMove;
        }

        @Override
        public void done() {
            try {
                final Move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getToBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());

                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class BoardPanel extends JPanel {

        final List<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setBackground(Color.decode("#8B4726"));
            validate();
        }

        public void drawBoard(final Board board) {
            removeAll();
            for (final TilePanel boardTile : boardDirection.traverse(boardTiles)) {
                boardTile.drawTile(board);
                add(boardTile);
            }
            validate();
            repaint();
        }

    }

    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();

    }

    public static class MoveLog {

        private final List<Move> moves;

        MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(final Move move) {
            this.moves.add(move);
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(int index) {
            return this.moves.remove(index);
        }

    }

    private class TilePanel extends JPanel {

        private final int tileId;

        TilePanel(final BoardPanel boardPanel,
                  final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent event) {
                    if (isRightMouseButton(event)) {
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    } else if (isLeftMouseButton(event)) {
                        if (sourceTile == null) {
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) {
                                sourceTile = null;
                            }
                        } else {
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(),
                                    destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getToBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                    }
                    invokeLater(new Runnable() {
                        public void run() {
                            gameHistoryPanel.redo(chessBoard, moveLog);
                            boardPanel.drawBoard(chessBoard);
                            if (gameSetup.isAIPlayer(chessBoard.currentPlayer())) {

                                Table.get().moveMadeUpdate(PlayerType.HUMAN);
                            }
                            //boardPanel.drawBoard(chessBoard);

                        }
                    });
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                }

                @Override
                public void mousePressed(final MouseEvent e) {
                }
            });
            validate();
        }

        public void drawTile(final Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightTileBorder(board);
            highlightAIMove();
            validate();
            repaint();
        }

        private void highlightTileBorder(final Board board) {
            if(humanMovedPiece != null &&
                    humanMovedPiece.getPieceAllegiance() == board.currentPlayer().getAlliance() &&
                    humanMovedPiece.getPiecePosition() == this.tileId) {
                setBorder(BorderFactory.createLineBorder(Color.RED));
            }
            else{
                setBorder(BorderFactory.createLineBorder(Color.darkGray));
            }
        }
        private void highlightAIMove() {
            if(computerMove != null) {
                if(this.tileId == computerMove.getCurrentCoordinate()) {
                    setBackground(Color.decode("#cdd369"));
                } else if(this.tileId == computerMove.getDestinationCoordinate()) {
                    setBackground(Color.decode("#cdd369"));
                }
            }
        }

      /* private void assignTilePieceIcon(final Board board) {
            this.removeAll();
            if(board.getTile(this.tileId).isTileOccupied()) {
               // final BufferedImage image = ImageIO.read(new File(pieceIconPath +
                     //    board.getTile(this.tileId).getPiece().getPieceAllegiance(.toString().substring(0, 1) + "" +
                    //     board.getTile(this.tileId).getPiece().toString() +
                     //    ".gif"));
                // getClass().getResource("images/mark.gif")
                String b = "images/" +
                        board.getTile(this.tileId).getPiece().getPieceAllegiance().toString().substring(0, 1) + "" +
                        board.getTile(this.tileId).getPiece().toString() + ".gif";
                System.out.println(b);


                String znj = "C:/Users/Jovana/Desktop/s" + board.getTile(this.tileId).getPiece().getPieceAllegiance().toString().substring(0, 1) + "" +
                       board.getTile(this.tileId).getPiece().toString() + ".gif";
                //final BufferedImage image = ImageIO.read(new File(b));
                // final BufferedImage image = ImageIO.read(new File(b));
                add(new JLabel(new ImageIcon(mapa.get(board.getTile(this.tileId).getPiece().getPieceAllegiance().toString().substring(0, 1) + "" +
                        board.getTile(this.tileId).getPiece().toString()))));
                //  C:\Users\Jovana\Downloads\s
            }
        } */


        private void assignTilePieceIcon(final Board board) {
            this.removeAll();
            if(board.getTile(this.tileId).isTileOccupied()) {


                    String t =
                            board.getTile(this.tileId).getPiece().getPieceAllegiance().toString().substring(0, 1) + "" +
                            board.getTile(this.tileId).getPiece().toString();


                    final BufferedImage image = mapa.get(t);
                    add(new JLabel(new ImageIcon(image)));

            }
        }


        private void assignTileColor() {
            if (BoardUtils.INSTANCE.FIRST_ROW.get(this.tileId) ||
                BoardUtils.INSTANCE.THIRD_ROW.get(this.tileId) ||
                BoardUtils.INSTANCE.FIFTH_ROW.get(this.tileId) ||
                BoardUtils.INSTANCE.SEVENTH_ROW.get(this.tileId)) {
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else if(BoardUtils.INSTANCE.SECOND_ROW.get(this.tileId) ||
                      BoardUtils.INSTANCE.FOURTH_ROW.get(this.tileId) ||
                      BoardUtils.INSTANCE.SIXTH_ROW.get(this.tileId)  ||
                      BoardUtils.INSTANCE.EIGHTH_ROW.get(this.tileId)) {
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }
    }
}

