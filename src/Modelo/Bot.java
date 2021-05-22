package Modelo;

import Vista.MateHaciaNegras;

public class Bot {

    String[][] tablero;
    Movimientos movimientos = new Movimientos();
    boolean primerMovimiento = true;
    int[][] puntuacionesPosiciones = new int[8][8];

    //Te retorna las posicion antigua[0] y la posicion nueva[1]
    public String[] movimientoBot(String[][] tablero) {
        this.tablero = tablero;
        examinarLasCasillasAtacadasAlInicio();
        return arregloFinal();
    }

    private String[] arregloFinal() {
        int puntuacionMaxima = -600;
        int yInicial = 1;
        int xInicial = 4;
        int yFinal = 3;
        int xFinal = 4;
        boolean jaqueMate = true;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (comprobarSiLaFichaEsnegra(tablero, i, j)) {
                    String posicionInicial = "" + i + "" + j;
                    String[] movimientosPosibles = movimientos.movimientosFichas(tablero, posicionInicial);

                    String ficha = tablero[i][j];

                    for (int k = 0; k < movimientosPosibles.length; k++) {
                        String posicionFinal = movimientosPosibles[k];
                        try {
                            char ejeY = posicionFinal.charAt(0);
                            char ejeX = posicionFinal.charAt(1);
                            int intEjeY = Integer.parseInt(String.valueOf(ejeY));
                            int intEjeX = Integer.parseInt(String.valueOf(ejeX));
                            System.out.println(ficha + " : " + posicionFinal);
                            int puntuacionPosicionEspecifica = punutacionMovimiento(i, j, intEjeY, intEjeX);
                            System.out.println(puntuacionPosicionEspecifica);
                            if (puntuacionMaxima <= puntuacionPosicionEspecifica) {
                                yInicial = i;
                                xInicial = j;
                                yFinal = intEjeY;
                                xFinal = intEjeX;
                                puntuacionMaxima = puntuacionPosicionEspecifica;
                            }

                            if (puntuacionPosicionEspecifica > -600) {
                                jaqueMate = false;
                            }

                        } catch (Exception ex) {
                        }
                    }
                }
            }
        }

        String posicionAntigua = "" + yInicial + "" + xInicial;
        String posicionNueva = "" + yFinal + "" + xFinal;

        if (primerMovimiento) {
            posicionAntigua = "14";
            posicionNueva = "34";
            primerMovimiento = false;
        }
        System.out.println("Fin turno");

        if (jaqueMate == true) {
            MateHaciaNegras ventana = new MateHaciaNegras(null, true);
            ventana.setVisible(true);
        }
        String arrayPosiciones[] = {posicionAntigua, posicionNueva};
        return arrayPosiciones;

    }

    private int punutacionMovimiento(int yInicial, int xInicial, int yFinal, int xFinal) {
        int puntuacion = 0;
        puntuacion += puntuacionEnCasoDeFichaAtaque(yInicial, xInicial, yFinal, xFinal);
        puntuacion += puntuacionesPosiciones[yInicial][xInicial];
        puntuacion += puntuacionSiEstaDefendiendo(yInicial, xInicial);
        puntuacion += puntuacionCasillaSegura(yFinal, xFinal);
        puntuacion += puntuacionMovimientosEnFuturo(yInicial, xInicial, yFinal, xFinal);
        puntuacion += puntuacionParaDefenderComiendo(tablero, yInicial, xInicial, yFinal, xFinal);
        return puntuacion;
    }

    private int puntuacionMovimientosEnFuturo(int yInicial, int xInicial, int yFinal, int xFinal) {
        int puntuacion = 0;
        String tableroFuturo[][] = new String[8][8];
        copiarTablero(tableroFuturo);
        tableroFuturo[yFinal][xFinal] = tableroFuturo[yInicial][xInicial];
        tableroFuturo[yInicial][xInicial] = "";

        if (miReyEnJaque(tableroFuturo)) {
            return -2000;
        } else if (jaqueMateHaciaEl(tableroFuturo)) {
            return 1000;
        } else if (podriaHacerJaqueMate(tableroFuturo)) {
            return -2000;
        } else {
            puntuacion += fichaQuePuedeDefender(tableroFuturo, yFinal, xFinal);
            puntuacion += fichaQuePuedeAtacar(tableroFuturo, yFinal, xFinal);
            return puntuacion;
        }
    }

    private int fichaQuePuedeDefender(String[][] tableroM, int y, int x) {
        String[] ataquesEsaFicha = movimientos.movimientoAtaqueFichaB(tableroM, y, x);
        int valor = 0;
        for (int i = 0; i < ataquesEsaFicha.length; ++i) {
            int yAtaque = Integer.parseInt(String.valueOf(ataquesEsaFicha[i].charAt(0)));
            int xAtaque = Integer.parseInt(String.valueOf(ataquesEsaFicha[i].charAt(1)));

            if (comprobarSiLaFichaEsnegra(tableroM, yAtaque, xAtaque)) {
                valor += 2;
            }

        }
        return valor;
    }

    private int fichaQuePuedeAtacar(String[][] tableroM, int y, int x) {
        String[] ataquesEsaFicha = movimientos.movimientoAtaqueFichaB(tableroM, y, x);
        int valor = 0;
        for (int i = 0; i < ataquesEsaFicha.length; ++i) {
            int yAtaque = Integer.parseInt(String.valueOf(ataquesEsaFicha[i].charAt(0)));
            int xAtaque = Integer.parseInt(String.valueOf(ataquesEsaFicha[i].charAt(1)));

            if (comprobarSiLaFichaEsBlanca(tableroM, yAtaque, xAtaque)) {
                valor += 2;
            }

        }
        return valor;
    }

    private boolean podriaHacerJaqueMate(String[][] tableroFuturo) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (comprobarSiLaFichaEsBlanca(tablero, i, j)) {
                    String posicion = "" + i + "" + j;
                    String[] movimientosBlanca = movimientos.movimientosAmodificados(tableroFuturo, posicion);
                    if (!movimientosBlanca[0].equals("")) {
                        for (int k = 0; k < movimientosBlanca.length; k++) {

                            int iF = Integer.parseInt(String.valueOf(movimientosBlanca[k].charAt(0)));
                            int jF = Integer.parseInt(String.valueOf(movimientosBlanca[k].charAt(1)));

                            String[][] tableroFichaCambiada = new String[8][8];
                            copiarTablero(tableroFuturo, tableroFichaCambiada);

                            tableroFichaCambiada[iF][jF] = tableroFichaCambiada[i][j];
                            tableroFichaCambiada[i][j] = "";
                            if (miReyEnJaqueMate(tableroFichaCambiada)) {
                                return true;
                            }
                        }
                    }

                }
            }
        }
        return false;
    }

    private boolean miReyEnJaqueMate(String[][] tableroM) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (comprobarSiLaFichaEsnegra(tableroM, i, j)) {
                    String posicion = "" + i + "" + j;
                    String[] movimientosNegra = movimientos.movimientosBmodificados(tableroM, posicion);
                    if (movimientosNegra != null) {
                        if (!movimientosNegra[0].equals("")) {
                            return false;
                        }
                    }

                }
            }
        }
        return true;
    }

    private int puntuacionParaDefenderComiendo(String[][] tableroM, int yInicial, int xInicial, int yFinal, int xFinal) {
        String posicionFichaQueAtacaValiosa = posicionFichaQueAtacaAlaMasValiosa(tableroM);
        if (!posicionFichaQueAtacaValiosa.equals("ninguna")) {
            if (comprobarSiLaPosicionDeAtaqueCoincideConLaQueAtaca(yFinal, xFinal, posicionFichaQueAtacaValiosa)) {
                int valorFichaMasValiosa = valorFichaMasValiosaAtacadaEnElTablero(tableroM);
                if (estaLaFichaDefendidaPorA(tableroM, posicionFichaQueAtacaValiosa) == false) {
                    return valorFichaMasValiosa * 3;
                } else {
                    if (fichaQueAtacaValiosaEsMayorOigualQueLaQueLaAtaca(tableroM, yInicial, xInicial, yFinal, xFinal)) {
                        return valorFichaMasValiosa * 2;
                    }
                }
            }
        }
        return 0;
    }

    private boolean fichaQueAtacaValiosaEsMayorOigualQueLaQueLaAtaca(String[][] tableroM, int yInicial, int xInicial, int yFinal, int xFinal) {
        String fichaQueAtacaMasValiosa = tableroM[yFinal][xFinal];
        String fichaQueLaAtaca = tableroM[yInicial][xInicial];

        int valorAtacaMasValiosa = valorDeLaFicha(fichaQueAtacaMasValiosa);
        int valorQueLaAtaca = valorDeLaFicha(fichaQueLaAtaca);

        return (valorAtacaMasValiosa >= valorQueLaAtaca) ? true : false;

    }

    private String buscarFichaQueLaDefiende(String[][] tableroM, String posicion) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (comprobarSiLaFichaEsBlanca(tableroM, i, j)) {
                    String[] movimientosAtaque = movimientos.movimientoAtaqueFichaA(tableroM, i, j);
                    for (int k = 0; k < movimientosAtaque.length; k++) {
                        if (movimientosAtaque[k].equals(posicion)) {
                            return tableroM[i][j];
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean comprobarSiLaPosicionDeAtaqueCoincideConLaQueAtaca(int yFinal, int xFinal, String posAtaque) {
        String posicionAtaca = "" + yFinal + "" + xFinal;
        return (posicionAtaca.equals(posAtaque)) ? true : false;
    }

    private int valorFichaMasValiosaAtacadaEnElTablero(String[][] tableroM) {
        String[] movimientosFichasA = movimientos.todosLosMovimientosFichasA(tableroM);
        int valorMayor = 0;
        if (!movimientosFichasA[0].equals("")) {
            for (int i = 0; i < movimientosFichasA.length; i++) {
                int y = Integer.parseInt(String.valueOf(movimientosFichasA[i].charAt(0)));
                int x = Integer.parseInt(String.valueOf(movimientosFichasA[i].charAt(1)));
                if (comprobarSiLaFichaEsnegra(tableroM, y, x)) {
                    String ficha = tableroM[y][x];
                    int valorTemporal = valorDeLaFicha(ficha);
                    if (valorTemporal > valorMayor) {
                        valorMayor = valorTemporal;
                    }

                }

            }
        }
        return valorMayor;
    }

    private String posicionFichaQueAtacaAlaMasValiosa(String[][] tableroM) {
        String posicionAtacanteMasValiosa = "ninguna";
        int valorMayor = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (comprobarSiLaFichaEsBlanca(tableroM, i, j)) {
                    String[] movimientosFichaBlanca = movimientos.movimientoAtaqueFichaA(tableroM, i, j);
                    if (!movimientosFichaBlanca[0].equals("")) {
                        for (int k = 0; k < movimientosFichaBlanca.length; k++) {
                            int y = Integer.parseInt(String.valueOf(movimientosFichaBlanca[k].charAt(0)));
                            int x = Integer.parseInt(String.valueOf(movimientosFichaBlanca[k].charAt(1)));
                            if (comprobarSiLaFichaEsnegra(tableroM, y, x)) {
                                String ficha = tableroM[y][x];
                                int valorTemporal = valorDeLaFicha(ficha);
                                if (valorTemporal > valorMayor) {
                                    posicionAtacanteMasValiosa = "" + i + "" + j;
                                    valorMayor = valorTemporal;
                                }

                            }
                        }
                    }

                }
            }
        }
        return posicionAtacanteMasValiosa;
    }

    private boolean jaqueMateHaciaEl(String[][] tableroM) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String posicion = "" + i + "" + j;
                if (comprobarSiLaFichaEsBlanca(tableroM, i, j)) {
                    String[] movimientosBlancos = movimientos.movimientosAmodificados(tableroM, posicion);
                    if (!movimientosBlancos[0].equals("")) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean miReyEnJaque(String[][] tableroM) {
        //Buscamos primero al rey y luego vemos todas los movimientos posibles de A
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (tableroM[i][j].equals("B_rey")) {
                    String posicionRey = "" + i + "" + j;
                    String[] movimientosBlancas = movimientos.todosLosMovimientosFichasA(tableroM);
                    for (int k = 0; k < movimientosBlancas.length; k++) {
                        if (posicionRey.equals(movimientosBlancas[k])) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        }
        return false;
    }

    private void copiarTablero(String[][] tableroM) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tableroM[i][j] = tablero[i][j];
            }
        }
    }

    private void copiarTablero(String[][] tableroOriginal, String[][] tableroCopia) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tableroCopia[i][j] = tableroOriginal[i][j];
            }
        }
    }

    private int puntuacionCasillaSegura(int y, int x) {
        String posicionFinal = "" + y + "" + x;
        String movimientosAtaqueA[] = movimientos.todosLosMovimientosFichasA(tablero);
        for (int i = 0; i < movimientosAtaqueA.length; i++) {
            if (posicionFinal.equals(movimientosAtaqueA[i])) {
                return -8;
            }
        }
        return 3;
    }

    private int puntuacionSiEstaDefendiendo(int y, int x) {
        int puntuacion = 0;
        String[] movimientosDefensora = movimientos.movimientoAtaqueFichaB(tablero, y, x);
        for (int i = 0; i < movimientosDefensora.length; i++) {
            int ejeY = Integer.parseInt(String.valueOf(movimientosDefensora[i].charAt(0)));
            int ejeX = Integer.parseInt(String.valueOf(movimientosDefensora[i].charAt(1)));
            String fichaDefendida = tablero[ejeY][ejeX];
            if (!fichaDefendida.equals("")) {
                if (fichaDefendida.charAt(0) == 'B') {
                    puntuacion -= valorDeLaFicha(fichaDefendida) / 4;
                }
            }
        }
        return puntuacion;
    }

    private int puntuacionEnCasoDeFichaAtaque(int yInicial, int xInicial, int yFinal, int xFinal) {
        String fichaInicial = fichaDeLaCasilla(tablero, yInicial, xInicial);
        if (comprobarSiLaFichaEsBlanca(tablero, yFinal, xFinal)) {

            String fichaFinal = fichaDeLaCasilla(tablero, yFinal, xFinal);
            
            String posicionFinal = "" + yFinal + "" + xFinal;

            if (estaLaFichaDefendidaPorA(tablero, posicionFinal) == false) {
                return valorDeLaFicha(fichaFinal) * 3;
            } else if (valeMasLaPrimera(fichaFinal, fichaInicial)) {
                return valorDeLaFicha(fichaFinal) * 3;
            } else if (valenIgual(fichaFinal, fichaFinal)) {
                return valorDeLaFicha(fichaFinal) * 2;
            } else {
                return 0;
            }
        }

        if (fichaInicial.equals("B_rey")) {
            return -10;
        }

        return 0;
    }

    private String devolverFichaSintetizada(String ficha) {
        String[] array = ficha.split("_");
        return array[1];
    }

    private void examinarLasCasillasAtacadasAlInicio() {
        ajustarPuntuacion0();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (comprobarSiLaFichaEsBlanca(tablero, i, j)) {
                    String posicion = "" + i + "" + j;
                    String fichaAtacante = tablero[i][j];
                    String[] movimientosPosibles = movimientos.movimientosFichas(tablero, posicion);
                    try {
                        for (int k = 0; k < movimientosPosibles.length; k++) {
                            int yM = Character.getNumericValue(movimientosPosibles[k].charAt(0));
                            int xM = Character.getNumericValue(movimientosPosibles[k].charAt(1));
                            String fichaAtacada = tablero[yM][xM];
                            if (comprobarSiLaFichaEsnegra(tablero, yM, xM)) {
                                String posicionFichaAtacada = "" + yM + "" + xM;
                                if (valeMasLaPrimera(fichaAtacada, fichaAtacante)) {
                                    puntuacionesPosiciones[yM][xM] += valorDeLaFichaSinRey(fichaAtacada);
                                } else if (estaLaFichaDefendidaPorB(tablero, posicionFichaAtacada) == false) {
                                    puntuacionesPosiciones[yM][xM] += valorDeLaFichaSinRey(fichaAtacada);
                                }
                            }
                        }
                    } catch (Exception ex) {
                    }
                }
            }
        }
    }

    private boolean estaLaFichaDefendidaPorA(String[][] tableroM, String posicionFicha) {
        String[] movimientosVariables = movimientos.todosLosMovimientosFichasA(tableroM);
        for (int k = 0; k < movimientosVariables.length; k++) {
            if (posicionFicha.equals(movimientosVariables[k])) {
                return true;
            }
        }
        return false;
    }

    private boolean estaLaFichaDefendidaPorB(String[][] tableroM, String posicionFicha) {
        String[] movimientosVariables = movimientos.todosLosMovimientosFichasB(tableroM);
        for (int k = 0; k < movimientosVariables.length; k++) {
            if (posicionFicha.equals(movimientosVariables[k])) {
                return true;
            }
        }
        return false;
    }

    private boolean comprobarSiLaFichaEsBlanca(String[][] tableroM, int y, int x) {
        if (!tableroM[y][x].equals("")) {
            return (tableroM[y][x].charAt(0) == 'A') ? true : false;
        }
        return false;
    }

    private boolean comprobarSiLaFichaEsnegra(String[][] tableroM, int y, int x) {
        if (!tableroM[y][x].equals("")) {
            return (tableroM[y][x].charAt(0) == 'B') ? true : false;
        }
        return false;
    }

    private String fichaDeLaCasilla(String[][] tableroM, int y, int x) {
        String ficha = tableroM[y][x];
        return ficha;
    }

    private String fichaDeLaCasilla(String[][] tableroM, String posicion) {
        char ejeY = posicion.charAt(0);
        char ejeX = posicion.charAt(1);
        int y = Integer.parseInt(String.valueOf(ejeY));
        int x = Integer.parseInt(String.valueOf(ejeX));
        String ficha = tableroM[y][x];
        return ficha;
    }

    private void ajustarPuntuacion0() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                puntuacionesPosiciones[i][j] = 0;
            }
        }
    }

    private boolean valeMasLaPrimera(String ficha1, String ficha2) {
        String[] ficha1Split = ficha1.split("_");
        String[] ficha2Split = ficha2.split("_");

        int valor1 = valorDeLaFicha(ficha1Split[1]);
        int valor2 = valorDeLaFicha(ficha2Split[1]);

        return (valor1 > valor2) ? true : false;
    }

    private boolean valenIgual(String ficha1, String ficha2) {
        String[] ficha1Split = ficha1.split("_");
        String[] ficha2Split = ficha2.split("_");

        int valor1 = valorDeLaFicha(ficha1Split[1]);
        int valor2 = valorDeLaFicha(ficha2Split[1]);

        return (valor1 == valor2) ? true : false;
    }

    private int valorDeLaFicha(String ficha) {
        String ficha1 = ficha;
        if (ficha.charAt(0) == 'A' || ficha.charAt(0) == 'B') {
            ficha1 = devolverFichaSintetizada(ficha);
        }

        int valor = 0;
        if (ficha1.equals("peon")) {
            valor = 1;
        } else if (ficha1.equals("alfil")) {
            valor = 8;
        } else if (ficha1.equals("torre")) {
            valor = 12;
        } else if (ficha1.equals("caballo")) {
            valor = 12;
        } else if (ficha1.equals("reina")) {
            valor = 18;
        } else if (ficha1.equals("rey")) {
            valor = 20;
        }
        return valor;
    }

    private int valorDeLaFichaSinRey(String ficha) {
        String ficha1 = ficha;
        if (ficha.charAt(0) == 'A' || ficha.charAt(0) == 'B') {
            ficha1 = devolverFichaSintetizada(ficha);
        }

        int valor = 0;
        if (ficha1.equals("peon")) {
            valor = 1;
        } else if (ficha1.equals("alfil")) {
            valor = 8;
        } else if (ficha1.equals("torre")) {
            valor = 12;
        } else if (ficha1.equals("caballo")) {
            valor = 12;
        } else if (ficha1.equals("reina")) {
            valor = 18;
        }
        return valor;
    }

    /*
    private String[] pequeñaPruebaDeFuncionamiento(){
        int maximo = 0;
        int yI = 1;
        int xI = 0;
        for(int i = 0;i<8;i++){
            for(int j = 0;j<8;j++){
                if(puntuacionesPosiciones[i][j]>maximo){
                    yI = i;
                    xI = j;
                    maximo = puntuacionesPosiciones[i][j];
                }
            }
        }
        String posicionInicial = ""+yI+""+xI;
        String[] movimientosPos = movimientos.movimientosFichas(tablero,posicionInicial);
        String[] arrayFinal = new String[2];
        arrayFinal[0] = posicionInicial;
        arrayFinal[1] = movimientosPos[0];
        return arrayFinal;
    }
    
        private int comparacionEntreTableros(String[][] tableroReal, String[][] tableroModificado) {
        int puntuacionTotal = 0;
        String[] fichasSiendoAtacadasEnTableroReal = nombresFichasQueEstanSiendoAtacadasEnElTablero(tableroReal);
        String[] fichasSiendoAtacadasEnTableroModificado = nombresFichasQueEstanSiendoAtacadasEnElTablero(tableroModificado);
        int puntuacionAtacadasReal = sumaPuntuacionesFichas(fichasSiendoAtacadasEnTableroReal);
        int puntuacionAtacadasModificado = sumaPuntuacionesFichas(fichasSiendoAtacadasEnTableroModificado);

        puntuacionTotal += (puntuacionAtacadasModificado - puntuacionAtacadasReal);
        puntuacionTotal += metodoDefensaMedianteComer(tableroReal, tableroModificado);
        
        String[] fichasSiendoProtegidasEnTableroReal = nombresFichasSiendoProtegidas(tableroReal);
        String[] fichasSiendoProtegidasEnTableroModificado = nombresFichasSiendoProtegidas(tableroModificado);
        int puntuacionDefendidasReal = sumaPuntuacionesFichas(fichasSiendoProtegidasEnTableroReal);
        int puntuacionDefendidasModificado = sumaPuntuacionesFichas(fichasSiendoProtegidasEnTableroModificado);
        
        puntuacionTotal += (puntuacionDefendidasModificado - puntuacionDefendidasReal);
        
        return puntuacionTotal;
    }
    
    private int sumaPuntuacionesFichas(String[] array) {
        int puntuacion = 0;

        for (int i = 0; i < array.length; i++) {
            if(!array[i].equals("")){
                int valor = valorDeLaFicha(array[i]);
                puntuacion += valor;
            }
        }
        return puntuacion;
    }
    
    private int fichasQueDefiende(String[][] tableroM, int y, int x) {
        String[] movimientosFicha = movimientos.movimientoAtaqueFichaB(tableroM, y, x);
        for (int i = 0; i < movimientosFicha.length; i++) {
            String posicionDefendida = movimientosFicha[i];
            int yN = Integer.parseInt(String.valueOf(posicionDefendida.charAt(0)));
            int xN = Integer.parseInt(String.valueOf(posicionDefendida.charAt(1)));

            if (comprobarSiLaFichaEsnegra(tableroM, yN, xN)) {
                return 2;
            }
        }
        return 0;
    }
    
    private int fichaQueAtaca(String[][] tableroM, int y, int x) {
        int puntuacionTotal = 0;
        String[] movimientosFicha = movimientos.movimientoAtaqueFichaB(tableroM, y, x);
        for (int i = 0; i < movimientosFicha.length; i++) {
            String posicionAtacada = movimientosFicha[i];
            int yN = Integer.parseInt(String.valueOf(posicionAtacada.charAt(0)));
            int xN = Integer.parseInt(String.valueOf(posicionAtacada.charAt(1)));

            if (comprobarSiLaFichaEsBlanca(tableroM, yN, xN)) {
                puntuacionTotal += valorDeLaFicha(posicionAtacada);
            }
        }
        return puntuacionTotal / 2;
    }
    
    private String[] nombresFichasQueEstanSiendoAtacadasEnElTablero(String[][] tableroM) {
        String nombresTotales = "";
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (comprobarSiLaFichaEsBlanca(tableroM, i, j)) {
                    String posicion = "" + i + "" + j;
                    String[] movimientosFicha = movimientos.movimientosAmodificados(tableroM, posicion);
                    for (int k = 0; k < movimientosFicha.length; k++) {
                        if (!movimientosFicha[k].equals("")) {
                            int ejeY = Integer.parseInt(String.valueOf(movimientosFicha[k].charAt(0)));
                            int ejeX = Integer.parseInt(String.valueOf(movimientosFicha[k].charAt(1)));
                            if (comprobarSiLaFichaEsnegra(tableroM, ejeY, ejeX) && estaLaFichaDefendidaPorB(tableroM, movimientosFicha[k]) == false) {
                                nombresTotales += "" + tableroM[ejeY][ejeX] + ":";
                            }
                        }
                    }
                }
            }
        }
        String[] arrayConNombres = nombresTotales.split(":");
        return arrayConNombres;
    }
    
    private String[] nombresFichasSiendoProtegidas(String[][] tableroM){
        String[] movimientosNegras = movimientos.todosLosMovimientosFichasB(tableroM);
        String nombresFichas = "";
        for(int i = 0;i<movimientosNegras.length;i++){
            if(!movimientosNegras[i].equals("")){
                int y = Integer.parseInt(String.valueOf(movimientosNegras[i].charAt(0)));
                int x = Integer.parseInt(String.valueOf(movimientosNegras[i].charAt(1)));
                if(comprobarSiLaFichaEsnegra(tableroM, y, x)){
                    String ficha = tableroM[y][x];
                    nombresFichas = ""+ficha+":";
                }
                
            }
        }
        return nombresFichas.split(":");
    }
    
    private int metodoDefensaMedianteComer(String[][] tableroReal,String[][]tableroM){
        int max = 0;
        String posicionMasValiosa = "Carne de macaco";
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (comprobarSiLaFichaEsBlanca(tableroM, i, j)) {
                    String posicion = "" + i + "" + j;
                    String[] movimientosFicha = movimientos.movimientosAmodificados(tableroReal, posicion);
                    for (int k = 0; k < movimientosFicha.length; k++) {
                        if (!movimientosFicha[k].equals("")) {
                            int y = Integer.parseInt(String.valueOf(movimientosFicha[k].charAt(0)));
                            int x = Integer.parseInt(String.valueOf(movimientosFicha[k].charAt(1)));
                            if (comprobarSiLaFichaEsnegra(tableroM, y, x)) {
                                String ficha = tableroReal[y][x];
                                int valor = valorDeLaFicha(ficha);
                                if(valor > max && valor > 2){
                                    posicionMasValiosa = posicion;
                                    max = valor;
                                }
                                
                            }
                        }
                    }
                }
            }
        }
        System.out.println(posicionMasValiosa + " : "+max);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (comprobarSiLaFichaEsBlanca(tableroM, i, j)) {
                    String posicion = "" + i + "" + j;
                    String[] movimientosFicha = movimientos.movimientosAmodificados(tableroM, posicion);
                    for (int k = 0; k < movimientosFicha.length; k++) {
                        if(movimientosFicha[k].equals(posicionMasValiosa)){
                            return -max;
                        }
                    }
                }
            }
        }
        return max*3;
    }
    
    
    
     */
}
