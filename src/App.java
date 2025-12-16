import java.util.Random;
import java.util.Scanner;

public class App {
    private static Scanner sc = new Scanner(System.in);
    private static Random rand = new Random();
    private static board player1 = new board();
    private static board player2 = new board();
    private static boolean turnoJugador1 = true;

    public static void main(String[] args) {
        menuJuego();
    }

    public static void menuJuego() {
        System.out.println("=== HUNDIR LA FLOTA ===");
        System.out.println("1) PVP\n2) PVE\n0) Salir");
        int opcion = sc.nextInt();
        sc.nextLine();

        switch (opcion) {
            case 1: jugarPVP(); break;
            case 2: jugarPVE(); break;
            case 0: System.exit(0);
        }
    }

    public static void jugarPVP() {
        player1.colocarBarcosAutomatico();
        player2.colocarBarcosAutomatico();
        System.out.println("¡Barcos colocados! Jugarán por turnos.");
        jugar();
    }

    public static void jugarPVE() {
        player1.colocarBarcosAutomatico();
        player2.colocarBarcosAutomatico();
        System.out.println("¡Tu flota lista! La máquina ya colocó la suya.");
        jugar();
    }

    public static void jugar() {
        while (!player1.todosBarcosHundidos() && !player2.todosBarcosHundidos()) {
            if (turnoJugador1) {
                mostrarJugador1();
                System.out.print("Tu disparo (fila col, ej: 5 3): ");
                int fila = sc.nextInt();
                int col = sc.nextInt();
                String resultado = player2.disparar(fila, col);
                System.out.println("¡" + resultado + "!");
                turnoJugador1 = false;
            } else {
                mostrarJugador2();
                // Máquina dispara
                int[] disparo = player2.disparoMaquina(player1);
                String resultado = player1.disparar(disparo[0], disparo[1]);
                System.out.println("Máquina: " + resultado + " en " + (disparo[0]+1) + "," + (disparo[1]+1));
                turnoJugador1 = true;
            }
        }
        mostrarGanador();
    }

    public static void mostrarJugador1() {
        System.out.println("\nTUS BARCOS:");
        player1.mostrarTableroPropio();
        System.out.println("TABLEROS RIVAL:");
        player2.mostrarTableroRival();
    }

    public static void mostrarJugador2() {
        System.out.println("\nTurno máquina...");
    }

    public static void mostrarGanador() {
        if (player2.todosBarcosHundidos()) {
            System.out.println("¡GANASTE! Hundiste toda la flota enemiga.");
        } else {
            System.out.println("¡PERDISTE! La máquina te hundió toda la flota.");
        }
        player1.mostrarEstadisticas();
    }
}
