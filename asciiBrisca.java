import java.util.Scanner;

import cartas.*;

public class asciiBrisca {
    final static int hor = 70, ver = 24, ch = 7, cv = 6;
    static Scanner sc = new Scanner(System.in);
    final static boolean wins[] = {false,false};

    public static void main(String[] args) throws InterruptedException {
        final int pausa = 700;
        Pantalla pan = new Pantalla(hor, ver);
        Carta[] centro = { Baraja.getNoCarta(), Baraja.getNoCarta() }; // cartas en juego
        // creamos la baraja, mezclada por el msmo metodo
        Baraja baraja = new Baraja();
        // la ultima carta define el palo del triunfo
        Carta ultima = baraja.ultimaCarta();
        char triunfo = ultima.getPalo();
        JuegoBrisca brisca = new JuegoBrisca(triunfo);
        Jugador robot = new Jugador(3);
        Jugador humano = new Jugador(3);
        muestraTapete(pan, ultima, triunfo, baraja, robot, humano, centro, false); // muestra tapete en vacio
        boolean salehumano = true;

        // reparte las cartas iniciales
        Thread.sleep(pausa * 2);
        for (int i = 0; i < 3; i++) {
            robot.recibeCarta(baraja.repartirCarta());
            humano.recibeCarta(baraja.repartirCarta());
            muestraTapete(pan, ultima, triunfo, baraja, robot, humano, centro, false);
            Thread.sleep(pausa);
        }
        while (humano.conCartas()) {
            int jh, jr; // jugada-humano, jugada-robot
            if (salehumano) {
                jh = muestraTapete(pan, ultima, triunfo, baraja, robot, humano, centro, true);
                centro[1] = humano.juegaCarta(jh);
                muestraTapete(pan, ultima, triunfo, baraja, robot, humano, centro, false);
                Thread.sleep(pausa);
                brisca.setPalo(centro[1].getPalo());
                //estrategia robot: pruebo con todas la que mas puntos gane y restan los valores de jugar
                int[] mivalor= new int[3];
                for (int i = 0; i < mivalor.length; i++) {
                    mivalor[i]=brisca.ganaMano(robot.mano[i],centro[1])*10-robot.mano[i].getValor();
                    if(robot.mano[i].getPalo()=='n') mivalor[i]=-100;
                }               
                jr=mejor(mivalor);
                centro[0] = robot.juegaCarta(jr);
                salehumano = gana(pausa, pan, centro, baraja, ultima, triunfo, brisca, robot, humano, salehumano);
            } else { //sale el robot elige lo que menos valga de lo que tiene
                int[] mivalor= new int[3];
                for (int i = 0; i < mivalor.length; i++) {    
                    mivalor[i]=(robot.mano[i].getValor()*-1)-((robot.mano[i].getPalo()==triunfo)?100:0);                    
                    if(robot.mano[i].getValor()=='n') mivalor[i]=-10000;
                } 
                jr=mejor(mivalor); // cargado en negativo para salga lo peor
                centro[0] =robot.juegaCarta(jr);
                brisca.setPalo(centro[0].getPalo());
                jh = muestraTapete(pan, ultima, triunfo, baraja, robot, humano, centro, true);
                centro[1] = humano.juegaCarta(jh);
                salehumano = gana(pausa, pan, centro, baraja, ultima, triunfo, brisca, robot, humano, salehumano);
            }
        }

    }
    private static boolean gana(final int pausa, Pantalla pan, Carta[] centro, Baraja baraja, Carta ultima,
            char triunfo, JuegoBrisca brisca, Jugador robot, Jugador humano, boolean salehumano)
            throws InterruptedException {
        int tantos;
        muestraTapete(pan, ultima, triunfo, baraja, robot, humano, centro, false);
        Thread.sleep(pausa);
        tantos=brisca.ganaMano(centro[0],centro[1]);
        if (tantos>0){
            wins[0]=true;
            robot.suma(tantos);
            salehumano=false;
        } else {
            wins[1]=true;
            humano.suma(tantos*-1);
            salehumano=true;
        }
        centro[0]=Baraja.getNoCarta();
        centro[1]=Baraja.getNoCarta();
        muestraTapete(pan, ultima, triunfo, baraja, robot, humano, centro, false);
        Thread.sleep(pausa);
        robot.recibeCarta(baraja.repartirCarta());
        muestraTapete(pan, ultima, triunfo, baraja, robot, humano, centro, false);
        Thread.sleep(pausa/2);
        humano.recibeCarta(baraja.repartirCarta());
        muestraTapete(pan, ultima, triunfo, baraja, robot, humano, centro, false);
        Thread.sleep(pausa);
        return salehumano;
    }
    
    private static int mejor(int[] array) {
        if (array[0] > array[1] && array[0] > array[2]){ 
            return 0;
        } else if (array[1] > array[0] && array[1] > array[2]) {
            return 1;
        } else {
            return 2;
        }
    }

    private static int muestraTapete(Pantalla pan, Carta ultima, char triunfo, Baraja baraja,
            Jugador robot, Jugador humano, Carta[] centro, boolean pregunta) throws InterruptedException {
        String temp;
        final int ch = 7, cv = 6;
        int hor = pan.getHor(), ver = pan.getVer();
        int v = 5;
        char col = 'n';
        pan.borra();
        if (baraja.vacia()) {
            pan.situa(hor - 1 - (1 * ch), v, baraja.getGr().get("hueco"), triunfo);
        } else {
            pan.situa(hor - 1 - (1 * ch), v, ultima.toString(), triunfo);
        }
        // pan.situa(hor+2-(1*ch),v+1,graficos.get("trasera"),'w');
        if (baraja.quedan() > 1) {
            pan.situa(hor - (1 * ch) - 3, v + 4, baraja.getGr().get("traserah"), 'w');
        }
        pan.situa("     " + baraja.quedan(), 'n');
        pan.marc();
        pan.marc(0, 0, hor - 7 - 1, v, 'l', 'n');
        pan.marc(0, 0, hor - 14 - 2, v, 'l', 'n');
        pan.situa(hor - 7 - 1, 1, "PERSONA", 'w');
        String stp = Integer.toString(humano.getPuntos());
        pan.situa(hor - 7 - 1 + ((7 - stp.length()) / 2), 3, stp, 'w');
        stp = Integer.toString(robot.getPuntos());
        pan.situa(hor - 14 - 2, 1, "MÁQUINA", 'w');
        pan.situa(hor - 14 - 2 + ((7 - stp.length()) / 2), 3, stp, 'w');
        pan.marc(0, 0, hor, v, 'd', 'l');
        pan.situa(2, 1, baraja.getGr().get("titulo"), 'w');
        pan.situa(hor / 2 - ch, v * 2 + 1, centro[0].toString(), centro[0].getPalo());
        pan.situa(hor / 2, v * 2 + 1, centro[1].toString(), centro[1].getPalo());
        temp = (robot.mano[0].getValor() == 0) ? baraja.getGr().get("hueco") : baraja.getGr().get("trasera");
        col = (robot.mano[0].getValor() == 0) ? 'n' : 'w';
        pan.situa(1, v, temp, col);
        temp = (robot.mano[1].getValor() == 0) ? baraja.getGr().get("hueco") : baraja.getGr().get("trasera");
        col = (robot.mano[1].getValor() == 0) ? 'n' : 'w';
        pan.situa(1 + ch, v, temp, col);
        temp = (robot.mano[2].getValor() == 0) ? baraja.getGr().get("hueco") : baraja.getGr().get("trasera");
        col = (robot.mano[2].getValor() == 0) ? 'n' : 'w';
        pan.situa(1 + 2 * ch, v, temp, col);
        pan.situa(hor - 1 - (3 * ch), ver - cv - 2, humano.mano[0].toString(), humano.mano[0].getPalo());
        pan.situa(hor + 1 - (3 * ch), ver - 2, "(1)", 'n');
        pan.situa(hor - 1 - (2 * ch), ver - cv - 2, humano.mano[1].toString(), humano.mano[1].getPalo());
        pan.situa(hor + 1 - (2 * ch), ver - 2, "(2)", 'n');
        pan.situa(hor - 1 - (1 * ch), ver - cv - 2, humano.mano[2].toString(), humano.mano[2].getPalo());
        pan.situa(hor + 1 - (1 * ch), ver - 2, "(3)", 'n');
        if(wins[0]){
            pan.situa(1, ver/2+1, baraja.getGr().get("win"), 'g');
        }
        if(wins[1]){
            pan.situa(hor-23, ver/2+1, baraja.getGr().get("win"), 'g');
        }
        wins[0]=wins[1]=false;

        int rs=9;
        
        do {

            pan.mostra();
            if (pregunta) {

                for (int i = 0; i < hor - (2 * ch) + 2; i++) {
                    System.out.print(" ");
                }
                String res = sc.nextLine();
                int idx = res.charAt(0) - 49;
                System.out.println("-------" + idx);
                if (idx >= 0 && idx <= 2) {
                    if (humano.mano[idx].getValor() > 0) {
                        rs = idx;
                        pregunta = false;
                    }
                }

            }
        } while (pregunta);

        return rs;

    }

}
