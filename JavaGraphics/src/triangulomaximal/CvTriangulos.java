package triangulomaximal;

import javagraphics.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

class CvTriangulos extends Canvas {

    Vector<Point2D> v = new Vector<Point2D>();
    //Vector<Point2D> vBlue = new Vector<Point2D>();
    //float x0, y0, rWidth = 10F, rHeight = 10.0F, pixelSize; //originalmete rHeight = 7.5F
    float x0, y0, rWidth = 10F, rHeight = 10.0F, pixelSize; //originalmete rHeight = 7.5F
    boolean ready = true;
    int centerX, centerY;
    int numerodePuntos = 12;
    float separacionMinima = .5f;
    // declaro como variable de clase todo lo que su use para pintar
    ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
    ArrayList<HalfEdge> edgeList = new ArrayList<HalfEdge>();
    ArrayList<HalfEdge> pruebas = new ArrayList<HalfEdge>();
    ArrayList<Face> faceList = new ArrayList<Face>();
    ArrayList<Linea> rectasDuales = new ArrayList<>();
    ArrayList<HalfEdge> orden = new ArrayList<>();
    ArrayList<HalfEdge> primeras = new ArrayList<HalfEdge>();
    Triangulo minTri = null;
    Triangulo maxTri = null;

    Vector<Point2D> puntosPrimales = generarPuntosAleatorios();

    CvTriangulos() {
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            //71 es la g
            //67 es la c
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                System.out.println(key);
                if (key == 71) {
                    System.out.println("tecla g");
                    puntosPrimales = generarPuntosAleatorios();
                    repaint();
                }

                if (key == 67) {
                    try {
                        System.out.println("tecla c");
                        generarPuntosCalcularTriangulo();
                    } catch (Exception ee) {
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

        });
    }

    void generarPuntosCalcularTriangulo() {
        limpiarVariables();
        puntosPrimales = generarPuntosAleatorios();
        DCEList dcel = new DCEList();
//                Vertex v10 = new Vertex(-5, 5, null);
//                Vertex v20 = new Vertex(7, -4, null);
//                HalfEdge linea = new HalfEdge(null, null, null, null, null);
//                HalfEdge e2010 = new HalfEdge(null, null, null, null, null);
//                linea.origin = v10;
//                linea.next = e2010;
//                linea.prev = e2010;
//                linea.twin = e2010;
//
//                e2010.origin = v20;
//                e2010.next = linea;
//                e2010.prev = linea;
//                e2010.twin = linea;

        ArrayList<Point2D> intersecciones = new ArrayList<Point2D>();
        for (int i = 0; i < puntosPrimales.size(); i++) {
            for (int j = i; j < puntosPrimales.size(); j++) {
                if (i != j) { // no intersecamos rectas que son iguales
                    intersecciones.add(calcularInterseccionDual(puntosPrimales.get(i), puntosPrimales.get(j), null));
                }
            }
        }
        float extD = 0, extI = 0, extS = 0, extIn = 0;
        for (Point2D interseccione : intersecciones) {
            if (Math.abs(interseccione.x) > extD) {
                extD = Math.abs(interseccione.x);
                extI = -(Math.abs(interseccione.x));
                rWidth = (Math.abs(interseccione.x) * 2) + 3;
            }
            if (Math.abs(interseccione.y) > extS) {
                extS = Math.abs(interseccione.y);
                extIn = -(Math.abs(interseccione.y));
                rHeight = (Math.abs(interseccione.y) * 2) + 3;
            }
//                    System.out.println("inter at   " + interseccione.x);
//                    System.out.println("nueva extD " + extD);

            //System.out.println("inter at   " + interseccione.y);
            //System.out.println("nueva extD " + extS);
        }
        extD = extD + 1;
        extS = extS + 1;
        extI = extI - 1;
        extIn = extIn - 1;
        //rWidth = 102;
        //rHeight = 102;
        //dcel.crearBoundingBox(50, -50, 50, -50, edgeList, vertexList, faceList);
        dcel.crearBoundingBox(extD, extI, extS, extIn, edgeList, vertexList, faceList);

        rectasDuales = transformarPuntosaRectas(puntosPrimales);
        //rectasDuales = transformarPuntosaRectas(generarPuntosPrueba());
        //rectasDuales = transformarPuntosaRectas(generarPuntosPrueba2());
        //rectasDuales = transformarPuntosaRectas(generarPuntos());

//                rWidth = 192;
//                rHeight = 192;
//                dcel.crearBoundingBox(90, -90, 90, -90, edgeList, vertexList, faceList);
        for (Linea dual : rectasDuales) {
            dual.primerArista = dcel.agregarLineaArreglo(edgeList, vertexList, faceList, dual);
            //primeras.add(new HalfEdge(dual.primerArista.origin, dual.primerArista.next, dual.primerArista.prev, dual.primerArista.twin, dual.primerArista.face));
            //primeras.add(dual.primerArista);
        }
        //este for debe ir despues porque el dual.arista se puede actualizar por cada linea agregada
        for (Linea dual : rectasDuales) {
            primeras.add(dual.primerArista);
        }
//                ArrayList<HalfEdge> segmentosLinea = new ArrayList<>();
//                segmentosLinea = dcel.recorrerLinea(primeras.get(1), faceList.get(1));

        for (Linea rectasDuale : rectasDuales) {
            System.out.println("v.add(new Point2D(" + rectasDuale.puntoPrimal.x + "f, " + rectasDuale.puntoPrimal.y + "f));");
        }
        crearOrdenArribaPunto(rectasDuales, faceList.get(1), dcel);
        //dcel.imprimirLista(edgeList);
        //orden = dcel.obtenerOrden(primeras.get(0), faceList.get(1));
        //pruebas = segmentosLinea; //para pintar una linea en segmentos 
        //pruebas = primeras;
        //pruebas = conLinea; //para pintar una linea en segmentos 
        BuscadorTrianguloMin poli = new BuscadorTrianguloMin();
        BuscadorTrianguloMax poliMax = new BuscadorTrianguloMax();
        maxTri = poliMax.buscarTrianguloExtremal(rectasDuales.get(0));
        minTri = poli.buscarTrianguloExtremal(rectasDuales.get(0));
        minTri = null;
        for (Linea rectasDuale : rectasDuales) {
            Triangulo nuevoTriangulo;
            BuscadorTrianguloMin poligono = new BuscadorTrianguloMin();
            nuevoTriangulo = poligono.buscarTrianguloExtremal(rectasDuale);
            if (nuevoTriangulo != null) {
                if (minTri == null) {
                    minTri = nuevoTriangulo;
                } else {
                    if (nuevoTriangulo.area < minTri.area) {
                        minTri = nuevoTriangulo;
                    }
                }

            }
        }
        maxTri = null;
        for (Linea rectasDuale : rectasDuales) {
            Triangulo nuevoTriangulo;
            BuscadorTrianguloMax poligono = new BuscadorTrianguloMax();
            nuevoTriangulo = poligono.buscarTrianguloExtremal(rectasDuale);
            if (nuevoTriangulo != null) {
                if (maxTri == null) {
                    maxTri = nuevoTriangulo;
                } else {
                    if (nuevoTriangulo.area > maxTri.area) {
                        maxTri = nuevoTriangulo;
                    }
                }

            }
        }
        pruebas = edgeList;
        repaint();

    }

    void initgr() {
        Dimension d = getSize();
        int maxX = d.width - 1, maxY = d.height - 1;
        pixelSize = Math.max(rWidth / maxX, rHeight / maxY);
        centerX = maxX / 2;
        centerY = maxY / 2;
    }

    int iX(float x) {
        return Math.round(centerX + x / pixelSize);
    }

    int iY(float y) {
        return Math.round(centerY - y / pixelSize);
    }

    float fx(int x) {
        return (x - centerX) * pixelSize;
    }

    float fy(int y) {
        return (centerY - y) * pixelSize;
    }

    public void paint(Graphics g) {
        BufferedImage bufferedImage = new BufferedImage(1366, 768, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();    //para dibujar sobre la imagen que se exporta
        rWidth = 10F;
        rHeight = 10.0F;
        initgr();
        int left = iX(-rWidth / 2), right = iX(rWidth / 2);
        int bottom = iY(-rHeight / 2), top = iY(rHeight / 2);

//        //---------------codigo para dibujar rectas---------------
//        for (Linea recta : rectasDuales) {
//            g.drawLine(iX(recta.origin.x), iY(recta.origin.y), iX(recta.twin.origin.x), iY(recta.twin.origin.y));
//        }
        //-------------------codigo para dibujar aristas--------------------
//        g.drawRect(left, top, right - left, bottom - top); //dibuja el rectangulo grandote
//        g2d.drawRect(left, top, right - left, bottom - top); //dibuja el rectangulo grandote
//        for (HalfEdge next : pruebas) {
//            //for (HalfEdge next : edgeList) {
//            g.setColor(Color.getHSBColor(next.origin.x, next.origin.y * 20, next.twin.origin.y * 30));
//            //g2d.setColor(Color.getHSBColor(next.origin.x, next.origin.y * 20, next.twin.origin.y * 30));
//            g.drawLine(iX(next.origin.x), iY(next.origin.y), iX(next.twin.origin.x), iY(next.twin.origin.y));
//            //g2d.drawLine(iX(next.origin.x), iY(next.origin.y), iX(next.twin.origin.x), iY(next.twin.origin.y));
//        }
        //---------------codigo para dibujar la primeras en el .png----------------------------------
//        int k = 0;
//        for (HalfEdge next : primeras) {
//            g2d.drawString("" + (++k), iX(next.origin.x), iY(next.origin.y));
//            g2d.setColor(Color.getHSBColor(next.origin.x, next.origin.y * 20, next.twin.origin.y * 30));
//            g2d.drawLine(iX(next.origin.x), iY(next.origin.y), iX(next.twin.origin.x), iY(next.twin.origin.y));
//        }
        //---------------------codigo para dibujar los puntos---------------------------
        int i = 0;
        for (Point2D a : v) {
            g.setColor(a.color);
            g2d.setColor(a.color);
            g.fillRect(iX(a.x) - 2, iY(a.y) - 2, 4, 4);
            g2d.fillRect(iX(a.x) - 2, iY(a.y) - 2, 4, 4);
            g.drawString("" + (++i), iX(a.x), iY(a.y));
        }
//        //---------codigo para dibujar un orden------------------------------------------
//        for (HalfEdge halfEdge : orden) {
//            Point2D puntoRef = primeras.get(0).line.puntoPrimal;
//            System.out.println("punto ref" + puntoRef.x + " " + puntoRef.y);
//            System.out.println("" + halfEdge.line.puntoPrimal.x + " " + halfEdge.line.puntoPrimal.y);
//            g.drawLine(iX(puntoRef.x), iY(puntoRef.y), iX(halfEdge.line.puntoPrimal.x), iY(halfEdge.line.puntoPrimal.y));
//            g.drawString("" + (++i), iX(halfEdge.line.puntoPrimal.x), iY(halfEdge.line.puntoPrimal.y));
//        }

        //nuevo codigo para dibujar el orden de un punto
//        if (!rectasDuales.isEmpty()) {
//            for (Point2D point2D : rectasDuales.get(0).puntoPrimal.orden) {
//                Point2D puntoRef = rectasDuales.get(0).puntoPrimal;
//                //System.out.println("punto ref" + puntoRef.x + " " + puntoRef.y);
//                //System.out.println("" + halfEdge.line.puntoPrimal.x + " " + halfEdge.line.puntoPrimal.y);
//                g.drawLine(iX(puntoRef.x), iY(puntoRef.y), iX(point2D.x), iY(point2D.y));
//                g.drawString("" + (++i), iX(point2D.x), iY(point2D.y));
//            }
//        }
        //para dibujar triangulo
        g.setColor(Color.black);
        g2d.setColor(Color.white);
        if (minTri != null) {
            g.drawLine(iX(minTri.a.x), iY(minTri.a.y), iX(minTri.b.x), iY(minTri.b.y));
            g2d.drawLine(iX(minTri.a.x), iY(minTri.a.y), iX(minTri.b.x), iY(minTri.b.y));
            g.drawLine(iX(minTri.b.x), iY(minTri.b.y), iX(minTri.c.x), iY(minTri.c.y));
            g2d.drawLine(iX(minTri.b.x), iY(minTri.b.y), iX(minTri.c.x), iY(minTri.c.y));
            g.drawLine(iX(minTri.c.x), iY(minTri.c.y), iX(minTri.a.x), iY(minTri.a.y));
            g2d.drawLine(iX(minTri.c.x), iY(minTri.c.y), iX(minTri.a.x), iY(minTri.a.y));
        }

        g.setColor(Color.MAGENTA);
        g2d.setColor(Color.white);
        if (maxTri != null) {
            g.drawLine(iX(maxTri.a.x), iY(maxTri.a.y), iX(maxTri.b.x), iY(maxTri.b.y));
            g2d.drawLine(iX(maxTri.a.x), iY(maxTri.a.y), iX(maxTri.b.x), iY(maxTri.b.y));
            g.drawLine(iX(maxTri.b.x), iY(maxTri.b.y), iX(maxTri.c.x), iY(maxTri.c.y));
            g2d.drawLine(iX(maxTri.b.x), iY(maxTri.b.y), iX(maxTri.c.x), iY(maxTri.c.y));
            g.drawLine(iX(maxTri.c.x), iY(maxTri.c.y), iX(maxTri.a.x), iY(maxTri.a.y));
            g2d.drawLine(iX(maxTri.c.x), iY(maxTri.c.y), iX(maxTri.a.x), iY(maxTri.a.y));
        }
        //para exportar imagen
        g2d.dispose();
        File file = new File("myimage.png");
        try {
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException ex) {
            Logger.getLogger(CvTriangulos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean verificarAlineacion(Vector<Point2D> v, Point2D a) {
        for (int i = 0; i <= v.size() - 1; i++) { //primero comparamos cooredanas X de Red
            //System.out.print("Punto " + i + " :  ");
            if (Math.abs(a.x - v.elementAt(i).x) <= separacionMinima) {
                //System.out.println(a.x + " esta cerca de " + v.elementAt(i).x);
                return true;
            } else {
                //System.out.println(a.x + " esta lejos de " + v.elementAt(i).x);
            }
        }
        for (int i = 0; i <= v.size() - 1; i++) { //despues comparamos cooredanas Y de Red
            //System.out.print("Punto " + i + " :  ");
            if (Math.abs(a.y - v.elementAt(i).y) <= separacionMinima) {
                //System.out.println(a.y + " esta cerca de " + v.elementAt(i).y);
                return true;
            } else {
                //System.out.println(a.y + " esta lejos de " + v.elementAt(i).y);
            }
        }
        return false;
    }

    public Linea calcularDual(Point2D punto, Graphics g) {
        float y1 = 0.0F;
        float y2 = 0.0F;
        float x1 = rWidth / 2, x2 = -(rWidth / 2); //porque ninguna interseccion ocurre más lejos que esas coordenadas x

        //y1 = m*x1-b2
        y1 = punto.x * x1 - punto.y;
        //y2 = m*x2-b2
        y2 = punto.x * x2 - punto.y;
        //System.out.println(x1 + " " + y1);
        //System.out.println(x2 + " " + y2);
//        g.setColor(Color.yellow);
//        g.drawLine(iX(x1), iY(y1), iX(x2), iY(y2));
        DCEList dcel = new DCEList();
        return dcel.crearLinea(x1, y1, x2, y2);
    }

    public Point2D calcularInterseccionDual(Point2D punto1, Point2D punto2, Graphics g) {
        float y1 = 0.0F;
        float y2 = 0.0F;
        float m1 = punto1.x;
        float m2 = punto2.x;
        float b1 = punto1.y;
        float b2 = punto2.y;
        float x = 0.0F;
        x = (b1 - b2) / (m1 - m2);
        y1 = (m1 * x) - b1;
        y2 = (m2 * x) - b2;
        //System.out.println("x" + x);
        //System.out.println("interseccion en" + x + " " + y1);
        //g.setColor(Color.blue);
        //g.fillRect(iX(x) - 2, iY(y1) - 2, 4, 4); // Tiny rectangle; 
        return new Point2D(x, y1);
    }

    public Vector generarPuntosAleatorios() {
        v = new Vector<Point2D>();
        Random r;//= new Random();
        int maxAlto = 10;
        int maxAncho = 10;
        Color colores[] = {Color.RED, Color.BLUE, Color.GREEN};
        int contadorColor = 0;
        for (int i = 0; i < numerodePuntos; i++) {
            float minY = (-(maxAlto / 2)) + .1f;
            float maxY = (maxAlto / 2) - .1f;
            r = new Random();
            float randomRedY = minY + r.nextFloat() * (maxY - minY);
            //System.out.println("random red y = " + randomRedY);
            float minX = (-(maxAncho / 2)) + .1f;
            float maxX = (maxAncho / 2) - .1f;
            r = new Random();
            float randomRedX = minX + r.nextFloat() * (maxX - minX);
//            System.out.println("random red x = " + randomRedX);
            Point2D aVerificarRed = new Point2D(randomRedX, randomRedY);
            while (verificarAlineacion(v, aVerificarRed)) {
                //System.out.println("quise poner un rojo pero esta alineado alv");
                r = new Random();
                randomRedX = minX + r.nextFloat() * (maxX - minX);
                r = new Random();
                randomRedY = minY + r.nextFloat() * (maxY - minY);
                aVerificarRed = new Point2D(randomRedX, randomRedY);
            }
            v.add((Point2D) new PuntoPoligono(randomRedX, randomRedY, colores[contadorColor]));
            contadorColor = (contadorColor + 1) % 3;
        }
        return v;
    }

    public Vector generarPuntos() {
        v = new Vector<Point2D>();
        v.add(new Point2D(1.5f, -3.34f));
        v.add(new Point2D(-0.78f, -1.4f));
        v.add(new Point2D(0.24f, 0.74f));
        v.add(new Point2D(-0.44f, -1.44f));
        return v;
    }

    //para generar puntos de prueba
    public Vector generarPuntosPrueba() {
        v = new Vector<Point2D>();
        v.add(new Point2D(4.093657f, -3.7574651f));
        v.add(new Point2D(2.2738342f, 3.3094459f));
        v.add(new Point2D(-3.2783852f, 3.855044f));
        v.add(new Point2D(-2.664117f, 1.297091f));
        v.add(new Point2D(1.0673037f, -2.8855095f));
        v.add(new Point2D(2.8079023f, -0.43232393f));
        v.add(new Point2D(0.4696145f, -1.491689f));
        v.add(new Point2D(-4.5517263f, 2.2202463f));
        v.add(new Point2D(-0.78973055f, -4.7208543f));
        v.add(new Point2D(3.3992982f, 4.780603f));
        v.add(new Point2D(-0.20737553f, 0.5037036f));
        v.add(new Point2D(-3.9037566f, -2.3219142f));
        return v;
    }

    public Vector generarPuntosPrueba2() {
        v = new Vector<Point2D>();
        v.add((Point2D) new PuntoPoligono(-0.974509f, -1.0474205f, Color.GREEN));
        v.add((Point2D) new PuntoPoligono(-3.6944203f, 4.2643304f, Color.RED));
        v.add((Point2D) new PuntoPoligono(-4.209582f, 3.082199f, Color.RED));
        v.add((Point2D) new PuntoPoligono(2.373415f, -3.5292802f, Color.BLUE));
        v.add((Point2D) new PuntoPoligono(-0.08396673f, 0.8875327f, Color.RED));
        v.add((Point2D) new PuntoPoligono(-2.2308152f, -1.8010471f, Color.BLUE));
        v.add((Point2D) new PuntoPoligono(4.748339f, -2.4762938f, Color.RED));
        v.add((Point2D) new PuntoPoligono(1.7718267f, 0.06327295f, Color.RED));
        v.add((Point2D) new PuntoPoligono(3.5944953f, 1.8263621f, Color.RED));
        v.add((Point2D) new PuntoPoligono(0.6526103f, -4.4022f, Color.BLUE));
        v.add((Point2D) new PuntoPoligono(4.2021174f, 2.3445287f, Color.BLUE));
        v.add((Point2D) new PuntoPoligono(-2.7426038f, 3.6003747f, Color.BLUE));
        return v;
    }

    public ArrayList transformarPuntosaRectas(Vector<Point2D> v) {
        ArrayList<Linea> rectasDuales = new ArrayList<Linea>();
        for (Point2D punto : v) {
            //DCEList dcel = new DCEList();
            Linea auxiliar = this.calcularDual(punto, null);
            auxiliar.puntoPrimal = punto;   //para que la linea a que punto pertenece
            punto.linea = auxiliar;         //para que sepa el punto a que linea se mapea
            rectasDuales.add(auxiliar);
        }
        return rectasDuales;
    }

    /**
     * metodo para eliminar del orden los puntos por debajo del punto de
     * referencia y ordenando en sentido horario
     *
     * @param lineas
     * @param unbounded
     * @param dcel
     */
    public void crearOrdenArribaPunto(ArrayList<Linea> lineas, Face unbounded, DCEList dcel) {
        for (Linea linea : lineas) {
            //ArrayList<Point2D> ordenCompleto = new ArrayList<Point2D>();
            for (HalfEdge halfEdge : dcel.obtenerOrden(linea.primerArista, unbounded)) {
                if (halfEdge.line.puntoPrimal.y >= linea.puntoPrimal.y) {
                    linea.puntoPrimal.orden.add(halfEdge.line.puntoPrimal);
                }
            }
            Collections.reverse(linea.puntoPrimal.orden);
        }
    }

    public void limpiarVariables() {
        vertexList = new ArrayList<Vertex>();
        pruebas = new ArrayList<HalfEdge>();
        faceList = new ArrayList<Face>();
        rectasDuales = new ArrayList<>();
        orden = new ArrayList<>();
        primeras = new ArrayList<HalfEdge>();
        minTri = null;
        maxTri = null;
    }
}
