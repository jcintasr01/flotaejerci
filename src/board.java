import java.util.ArrayList;
import java.util.Random;

public class board {
    char[][] tableroPropio = new char[10][10];
    private char[][] tableroRival = new char[10][10];
    private ArrayList<ArrayList<int[]>> barcosPorTipo = new ArrayList<>();
    private Random rand = new Random();
    private boolean[] barcosColocados = new boolean[5];

    public board() {
        inicializarTableros();
        for (int i = 0; i < 5; i++) {
            barcosPorTipo.add(new ArrayList<int[]>());
            barcosColocados[i] = false;
        }
    }

    private void inicializarTableros() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                tableroPropio[i][j] = '~';
                tableroRival[i][j] = '~';
            }
        }
    }

    public void colocarBarcosAutomatico() {
        // ← ORDEN EXACTO: cada tipo UNA VEZ SOLO
        int[][] barcos = {{5, 0}, {4, 1}, {3, 2}, {3, 3}, {2, 4}}; // {tamaño, tipo}
        
        for (int[] barco : barcos) {
            int tam = barco[0];
            int tipo = barco[1];
            boolean colocado = false;
            while (!colocado) {
                int fila = rand.nextInt(10);
                int col = rand.nextInt(10);
                String dir = rand.nextBoolean() ? "H" : "V";
                if (colocarBarco(fila, col, tam, dir, tipo)) {
                    colocado = true;
                }
            }
        }
    }

    private boolean colocarBarco(int fila, int col, int tam, String dir, int tipoBarco) {
        if (barcosColocados[tipoBarco]) return false; // ← UNO POR TIPO
        
        if (dir.equals("H") && col + tam > 10) return false;
        if (dir.equals("V") && fila + tam > 10) return false;

        // Verificar espacio libre
        for (int i = 0; i < tam; i++) {
            int f = dir.equals("H") ? fila : fila + i;
            int c = dir.equals("V") ? col : col + i;
            if (tableroPropio[f][c] != '~') return false;
        }

        // ← REGISTRO EXACTO por tipo fijo
        ArrayList<int[]> posicionesBarco = new ArrayList<>();
        for (int i = 0; i < tam; i++) {
            int f = dir.equals("H") ? fila : fila + i;
            int c = dir.equals("V") ? col : col + i;
            tableroPropio[f][c] = 'B';
            posicionesBarco.add(new int[]{f, c});
        }
        
        barcosPorTipo.get(tipoBarco).addAll(posicionesBarco);
        barcosColocados[tipoBarco] = true;
        
        return true;
    }

    public String disparar(int fila, int col) {
        if (fila < 0 || fila >= 10 || col < 0 || col >= 10 || tableroRival[fila][col] != '~') {
            return "Fuera de rango";
        }

        if (tableroPropio[fila][col] == 'B') {
            tableroRival[fila][col] = 'X';
            tableroPropio[fila][col] = 'X';
            return "¡TOCADO!";
        } else {
            tableroRival[fila][col] = 'O';
            return "¡AGUA!";
        }
    }

    public int[] disparoMaquina(board rival) {
        int fila, col;
        do {
            fila = rand.nextInt(10);
            col = rand.nextInt(10);
        } while (rival.tableroRival[fila][col] != '~');
        return new int[]{fila, col};
    }

    public boolean[] getEstadoBarcos() {
        boolean[] hundidos = new boolean[5];
        for (int tipo = 0; tipo < 5; tipo++) {
            if (!barcosColocados[tipo]) {
                hundidos[tipo] = false;
                continue;
            }
            
            ArrayList<int[]> barco = barcosPorTipo.get(tipo);
            boolean estaHundido = !barco.isEmpty(); // ← Si tiene posiciones
            for (int[] pos : barco) {
                if (tableroPropio[pos[0]][pos[1]] != 'X') {
                    estaHundido = false;
                    break;
                }
            }
            hundidos[tipo] = estaHundido;
        }
        return hundidos;
    }

    public boolean todosBarcosHundidos() {
        boolean[] estado = getEstadoBarcos();
        for (boolean hundido : estado) {
            if (!hundido) return false;
        }
        return true;
    }

    public void mostrarTableroPropio() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < 10; i++) {
            System.out.print((char)('A' + i) + " ");
            for (int j = 0; j < 10; j++) {
                System.out.print(tableroPropio[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void mostrarTableroRival() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < 10; i++) {
            System.out.print((char)('A' + i) + " ");
            for (int j = 0; j < 10; j++) {
                System.out.print(tableroRival[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void mostrarEstadisticas() {
        int tocados = 0, agua = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (tableroRival[i][j] == 'X') tocados++;
                else if (tableroRival[i][j] == 'O') agua++;
            }
        }
        System.out.println("Estadísticas: " + tocados + " tocados, " + agua + " agua.");
    }
}
