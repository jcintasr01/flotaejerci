import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BattleshipGUI extends JFrame {
    private board jugador = new board();
    private board rival = new board();
    private boolean turnoJugador = true;
    private JButton[][] botonesJugador = new JButton[10][10];
    private JButton[][] botonesRival = new JButton[10][10];
    private JLabel estadoLabel;
    private JButton btnNuevoJuego;
    
    private JPanel panelBarcos;
    private ArrayList<JLabel> labelsBarcosJugador;
    private ArrayList<JLabel> labelsBarcosRival;
    private boolean[] barcosJugadorHundidos = new boolean[5];
    private boolean[] barcosRivalHundidos = new boolean[5];
    
    // ← NUEVO: Estados previos para detectar cambios
    private boolean[] ultimoEstadoJugador = new boolean[5];
    private boolean[] ultimoEstadoRival = new boolean[5];

    public BattleshipGUI() {
        jugador.colocarBarcosAutomatico();
        rival.colocarBarcosAutomatico();
        
        // ← INICIALIZAR estados previos
        for (int i = 0; i < 5; i++) {
            ultimoEstadoJugador[i] = false;
            ultimoEstadoRival[i] = false;
            barcosJugadorHundidos[i] = false;
            barcosRivalHundidos[i] = false;
        }
        
        inicializarPanelBarcos();
        
        setTitle("Hundir la Flota - PVE");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel panelEstado = new JPanel();
        estadoLabel = new JLabel("¡Tu turno! Haz clic en el tablero rival.", JLabel.CENTER);
        estadoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panelEstado.add(estadoLabel);
        add(panelEstado, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new BorderLayout());
        JPanel panelTableros = new JPanel(new GridLayout(1, 2, 10, 0));
        panelTableros.add(crearPanelTablero("TUS BARCOS", botonesJugador, true));
        panelTableros.add(crearPanelTablero("RIVAL", botonesRival, false));
        
        panelCentral.add(panelTableros, BorderLayout.CENTER);
        panelCentral.add(panelBarcos, BorderLayout.EAST);
        add(panelCentral, BorderLayout.CENTER);

        JPanel panelControles = new JPanel();
        btnNuevoJuego = new JButton("Nuevo Juego");
        btnNuevoJuego.addActionListener(e -> reiniciarJuego());
        btnNuevoJuego.setEnabled(false);
        panelControles.add(btnNuevoJuego);
        add(panelControles, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void inicializarPanelBarcos() {
        panelBarcos = new JPanel();
        panelBarcos.setBorder(BorderFactory.createTitledBorder("BARCOS"));
        panelBarcos.setLayout(new BoxLayout(panelBarcos, BoxLayout.Y_AXIS));
        panelBarcos.setBackground(Color.WHITE);
        panelBarcos.setPreferredSize(new Dimension(220, 350));
        
        JLabel tituloJugador = new JLabel("TUS BARCOS:", JLabel.CENTER);
        tituloJugador.setFont(new Font("Arial", Font.BOLD, 14));
        tituloJugador.setForeground(Color.BLUE);
        panelBarcos.add(tituloJugador);
        
        labelsBarcosJugador = new ArrayList<>();
        String[] nombres = {"Portaaviones (5)", "Acorazado (4)", "Crucero (3)", "Submarino (3)", "Destructor (2)"};
        for (String nombre : nombres) {
            JLabel label = new JLabel("● " + nombre);
            label.setFont(new Font("Arial", Font.BOLD, 11));
            label.setForeground(Color.BLUE);
            labelsBarcosJugador.add(label);
            panelBarcos.add(label);
        }
        
        panelBarcos.add(Box.createVerticalStrut(20));
        
        JLabel tituloRival = new JLabel("BARCOS RIVAL:", JLabel.CENTER);
        tituloRival.setFont(new Font("Arial", Font.BOLD, 14));
        tituloRival.setForeground(Color.RED);
        panelBarcos.add(tituloRival);
        
        labelsBarcosRival = new ArrayList<>();
        for (String nombre : nombres) {
            JLabel label = new JLabel("● " + nombre);
            label.setFont(new Font("Arial", Font.BOLD, 11));
            label.setForeground(Color.RED);
            labelsBarcosRival.add(label);
            panelBarcos.add(label);
        }
    }

    private JPanel crearPanelTablero(String titulo, JButton[][] botones, boolean mostrarBarcos) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        panel.setPreferredSize(new Dimension(300, 350));
        
        JPanel grid = new JPanel(new GridLayout(11, 11));
        grid.add(new JLabel(""));
        for (int j = 0; j < 10; j++) grid.add(new JLabel(String.valueOf(j+1), JLabel.CENTER));
        
        for (int i = 0; i < 10; i++) {
            grid.add(new JLabel(String.valueOf((char)('A' + i)), JLabel.CENTER));
            for (int j = 0; j < 10; j++) {
                JButton btn = new JButton("");
                btn.setPreferredSize(new Dimension(35, 35));
                btn.setBackground(mostrarBarcos && jugador.tableroPropio[i][j] == 'B' ? 
                    Color.GRAY : Color.LIGHT_GRAY);
                btn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                btn.setFont(new Font("Arial", Font.BOLD, 12));
                
                final int fila = i, col = j;
                if (!mostrarBarcos) {
                    btn.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(MouseEvent e) {
                            if (turnoJugador && btn.getBackground() == Color.LIGHT_GRAY) {
                                // ← TU DISPARO AL RIVAL
                                String resultado = rival.disparar(fila, col);
                                actualizarBoton(botonesRival, fila, col, resultado, rival);
                                
                                if (rival.todosBarcosHundidos()) {
                                    estadoLabel.setText("¡GANASTE! Nueva partida:");
                                    btnNuevoJuego.setEnabled(true);
                                    return;
                                }
                                
                                if (resultado.contains("TOCADO")) {
                                    estadoLabel.setText("¡TOCADO! Turno máquina...");
                                } else {
                                    estadoLabel.setText("¡AGUA! Turno máquina...");
                                    // ← VERIFICAR RIVAL SOLO después de AGUA (siguiente turno)
                                    verificarCambiosBarcos(rival.getEstadoBarcos(), ultimoEstadoRival, 
                                                         labelsBarcosRival, barcosRivalHundidos);
                                    ultimoEstadoRival = rival.getEstadoBarcos();
                                }
                                turnoJugador = false;
                                
                                // ← MÁQUINA DISPARA
                                Timer timerMaquina = new Timer();
                                timerMaquina.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        int[] disparo = rival.disparoMaquina(jugador);
                                        String res = jugador.disparar(disparo[0], disparo[1]);
                                        actualizarBoton(botonesJugador, disparo[0], disparo[1], res, jugador);
                                        
                                        if (jugador.todosBarcosHundidos()) {
                                            estadoLabel.setText("¡PERDISTE! Nueva partida:");
                                            btnNuevoJuego.setEnabled(true);
                                            return;
                                        }
                                        
                                        if (res.contains("TOCADO")) {
                                            estadoLabel.setText("¡La máquina tocó! Tu turno.");
                                        } else {
                                            estadoLabel.setText("¡Tu turno!");
                                            // ← VERIFICAR JUGADOR SOLO después de AGUA máquina
                                            verificarCambiosBarcos(jugador.getEstadoBarcos(), ultimoEstadoJugador, 
                                                                 labelsBarcosJugador, barcosJugadorHundidos);
                                            ultimoEstadoJugador = jugador.getEstadoBarcos();
                                        }
                                        turnoJugador = true;
                                    }
                                }, 1500);
                            }
                        }
                    });
                }
                botones[i][j] = btn;
                grid.add(btn);
            }
        }
        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private void actualizarBoton(JButton[][] botones, int fila, int col, String resultado, board tablero) {
        if (resultado.contains("TOCADO")) {
            botones[fila][col].setBackground(Color.RED);
            botones[fila][col].setForeground(Color.WHITE);
            botones[fila][col].setText("X");
        } else if (resultado.contains("AGUA") || resultado.equals("Fuera de rango")) {
            botones[fila][col].setBackground(Color.CYAN);
            botones[fila][col].setForeground(Color.BLUE);
            botones[fila][col].setText("O");
        }
        botones[fila][col].setEnabled(false);
    }

    // ← NUEVA FUNCIÓN: Verifica SOLO cambios de estado (hundido ←→ no hundido)
    private void verificarCambiosBarcos(boolean[] estadoActual, boolean[] estadoAnterior, 
                                      ArrayList<JLabel> labels, boolean[] hundidos) {
        String[] nombres = {"Portaaviones (5)", "Acorazado (4)", "Crucero (3)", "Submarino (3)", "Destructor (2)"};
        for (int i = 0; i < 5; i++) {
            // ← SOLO tacha si AHORA está hundido Y ANTES NO lo estaba
            if (estadoActual[i] && !estadoAnterior[i] && !hundidos[i]) {
                labels.get(i).setForeground(Color.GRAY);
                labels.get(i).setText("✗ " + nombres[i]);
                hundidos[i] = true;
            }
        }
    }

    private void reiniciarJuego() {
        jugador = new board();
        rival = new board();
        jugador.colocarBarcosAutomatico();
        rival.colocarBarcosAutomatico();
        turnoJugador = true;
        
        // ← RESET estados
        for (int i = 0; i < 5; i++) {
            barcosJugadorHundidos[i] = false;
            barcosRivalHundidos[i] = false;
            ultimoEstadoJugador[i] = false;
            ultimoEstadoRival[i] = false;
        }
        
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                botonesJugador[i][j].setBackground(jugador.tableroPropio[i][j] == 'B' ? 
                    Color.GRAY : Color.LIGHT_GRAY);
                botonesJugador[i][j].setText("");
                botonesJugador[i][j].setForeground(Color.BLACK);
                botonesJugador[i][j].setEnabled(true);
                
                botonesRival[i][j].setBackground(Color.LIGHT_GRAY);
                botonesRival[i][j].setText("");
                botonesRival[i][j].setForeground(Color.BLACK);
                botonesRival[i][j].setEnabled(true);
            }
        }
        
        String[] nombres = {"Portaaviones (5)", "Acorazado (4)", "Crucero (3)", "Submarino (3)", "Destructor (2)"};
        for (int i = 0; i < 5; i++) {
            labelsBarcosJugador.get(i).setForeground(Color.BLUE);
            labelsBarcosJugador.get(i).setText("● " + nombres[i]);
            labelsBarcosRival.get(i).setForeground(Color.RED);
            labelsBarcosRival.get(i).setText("● " + nombres[i]);
        }
        
        estadoLabel.setText("¡Nuevo juego! Tu turno.");
        btnNuevoJuego.setEnabled(false);
    }
}
