package unipaintapp;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class UniPaintApp extends JFrame {

    //variables 
    private int ctrlPanelWidth = 300;
    private int canvasDefaultWidth = 400;
    private int canvasDefaultHeight = 400;

    //colour and drawing type
    private Color selectedColour = new Color(0.0F, 0.0F, 0.0F);
    private String drawingTool = "Line";

    //freehand
    private final int maxFreehandPixels = 10000;
    private int freehandPixelCount = 0;
    private Color[] freehandColour = new Color[maxFreehandPixels];
    private int[][] fxy = new int[maxFreehandPixels][3];
    private int freehandThickness = 10;

    //line
    private final int maxLineCount = 10;
    private int currentLineCount = 0;
    private Color[] lineColour = new Color[maxLineCount];
    private int[][] lxy = new int[maxLineCount][4];

    //rectangle
    private final int maxRectangleCount = 10;
    private int currentRectangleCount = 0;
    private Color[] rectangleColour = new Color[maxRectangleCount];
    private int[][] rxy = new int[maxRectangleCount][4];

    //circle
    private final int maxCircleCount = 10;
    private int currentCircleCount = 0;
    private Color[] circleColour = new Color[maxCircleCount];
    private int[][] cxy = new int[maxCircleCount][4];
    private Boolean legal;

    //Animators
    MyAnimationClass animator = new MyAnimationClass();
    Timer animationTimer = new Timer(1, animator);

    class Canvas extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }
    }

    class CanvasMouseMotionListener implements MouseMotionListener {

        @Override

        public void mouseMoved(MouseEvent e) {
            updateMousePosition(e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            switch (drawingTool) {
                case "Line":
                    drawLineDrag(e);
                    break;
                case "Rectangle":
                    drawRectangleDrag(e);
                    break;
                case "Circle":
                    drawCircleDrag(e);
                    break;
                case "Freehand":
                    drawFreehand(e, freehandThickness, selectedColour);
                    break;
            }
            mouseMoved(e);
            canvas.repaint();
        }
    }

    class CanvasMouseListener implements MouseListener {

        @Override
        public void mousePressed(MouseEvent e) {
            switch (drawingTool) {
                case "Line":
                    drawLinePress(e, selectedColour);
                    break;
                case "Rectangle":
                    drawRectanglePress(e, selectedColour);
                case "Circle":
                    drawCirclePress(e, selectedColour);
                    break;
                case "Freehand":
                    drawFreehand(e, freehandThickness, selectedColour);

                    break;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            switch (drawingTool) {
                case "Line":
                    drawLineRelease(e);
                    break;
                case "Rectangle":
                    drawRectangleRelease(e);
                    break;
                case "Circle":
                    drawCircleRelease(e);
                    break;
                case "Freehand":

                    break;
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    class MyCheckBoxListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            canvas.repaint();
        }
    }

    class RadioButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            updateMsgBox();
            if (lineRadioButton.isSelected()) {
                drawingTool = "Line";
                msgBox.append(drawingTool + " Has been selected.");
            } else if (rectangleRadioButton.isSelected()) {
                drawingTool = "Rectangle";
                msgBox.append(drawingTool + " Has been selected.");
            } else if (circleRadioButton.isSelected()) {
                drawingTool = "Circle";
                msgBox.append(drawingTool + " Has been selected.");
            } else {
                drawingTool = "Freehand";
                msgBox.append(drawingTool + " Has been selected.");
            }

        }
    }

    class ColourButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JColorChooser colourPicker = new JColorChooser(selectedColour);
            Color newColour = colourPicker.showDialog(null, "Choose new Drawing colour", selectedColour);
            selectedColour = newColour;

            colourButton.setBackground(newColour);
        }
    }

    class ClearButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (animationTimer.isRunning()) {
                animationTimer.stop();
                animateButton.setText("Animate");
            }

            lxy = null;
            lxy = new int[maxLineCount][4];
            currentLineCount = 0;

            rxy = null;
            rxy = new int[maxLineCount][4];
            currentRectangleCount = 0;

            cxy = null;
            cxy = new int[maxCircleCount][4];
            currentCircleCount = 0;

            fxy = null;
            fxy = new int[maxFreehandPixels][3];
            freehandPixelCount = 0;

            updateMsgBox();
            canvas.repaint();
        }
    }

    class MyAnimationClass implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            updateMsgBox();
            if (currentCircleCount > 0) {

                for (int i = 0; i < maxCircleCount; i++) {
                    if ((cxy[i][1] + cxy[i][3] <= canvas.getHeight())) {
                        cxy[i][1]++;
                    }
//                    } else {
//                        cxy[i][1]-=5;
//                    }
                }
                msgBox.append("Animation Occuring");
                canvas.repaint();
            }

        }

    }

    class AnimateButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (animationTimer.isRunning()) {
                animationTimer.stop();
            } else {
                animationTimer.start();
            }
        }

    }

    class AnimateButtonChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {

            if (animationTimer.isRunning()) {
                animateButton.setText("Stop Animating");
            } else {
                animateButton.setText("Animate");
            }
        }
    }

    //COMPLETE FOR SAVE AND LOAD
    class FreehandSliderListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            freehandThickness = drawSize.getValue();
        }
    }

    class FileSaveListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
//            FileOutputStream fos = new FileOutputStream(UniPaintApp);
//            ObjectOutputStream fh = new ObjectOutputStream(fos);
//            fh.writeInt(currentLineCount);

        }
    }

    class FileLoadListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            updateMsgBox();
            msgBox.append("Load");

        }
    }

    class FileExitListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    class HelpAboutListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(canvas, "A simple Drawing Application\nBy Jake Sumner\n\nVersion: 3  Date created: 2019");
        }
    }

    public void drawFreehand(MouseEvent e, int freehandThickness, Color selectedColour) {
        updateMsgBox();
        if (freehandPixelCount < maxFreehandPixels - 1) {
            freehandColour[freehandPixelCount] = selectedColour;
            fxy[freehandPixelCount][0] = e.getX(); //x coordinate
            fxy[freehandPixelCount][1] = e.getY();// y coordinate
            fxy[freehandPixelCount][2] = freehandThickness; //dimension
            freehandPixelCount++;
            updateMsgBox();
        } else if (freehandPixelCount == maxFreehandPixels - 1) {
            msgBox.setText("You are out of Pixels");
        }

    }

    //line (requires exception handling)
    public void drawLinePress(MouseEvent e, Color selectedColour) {
        updateMsgBox();
        if (currentLineCount < maxLineCount - 1) {

            lineColour[currentLineCount] = selectedColour;
            lxy[currentLineCount][0] = e.getX();
            lxy[currentLineCount][1] = e.getY();
        } else {
            msgBox.append("Max " + drawingTool + " limit reached\n");
        }

    }

    public void drawLineDrag(MouseEvent e) {
        updateMsgBox();
        if (currentLineCount < maxLineCount - 1) {
            lxy[currentLineCount][2] = e.getX();
            lxy[currentLineCount][3] = e.getY();
            canvas.repaint();
        } else {
            msgBox.append("Max " + drawingTool + " limit reached\n");
        }

    }

    public void drawLineRelease(MouseEvent e) {
        updateMsgBox();
        if (currentLineCount < maxLineCount - 1) {
            lxy[currentLineCount][2] = e.getX();
            lxy[currentLineCount][3] = e.getY();
            currentLineCount++;
        } else {
            msgBox.append("Max " + drawingTool + " limit reached\n");
        }

    }

    //rectangle
    public void drawRectanglePress(MouseEvent e, Color selectedColour) {
        updateMsgBox();
        if (currentRectangleCount < maxRectangleCount - 1) {
            rectangleColour[currentRectangleCount] = selectedColour;
            rxy[currentRectangleCount][0] = e.getX();
            rxy[currentRectangleCount][1] = e.getY();
        } else {
            msgBox.append("Max " + drawingTool + " limit reached\n");
        }
    }

    public void drawRectangleDrag(MouseEvent e) {
        updateMsgBox();
        if (currentRectangleCount < maxRectangleCount - 1) {
            rxy[currentRectangleCount][2] = (e.getX() - rxy[currentRectangleCount][0]);
            rxy[currentRectangleCount][3] = (e.getY() - rxy[currentRectangleCount][1]);
        } else {
            msgBox.append("Max " + drawingTool + " limit reached\n");
        }
        canvas.repaint();
    }

    public void drawRectangleRelease(MouseEvent e) {
        updateMsgBox();
        if (currentRectangleCount < maxRectangleCount - 1) {
            if (RectangleInBounds(e)) {
                rxy[currentRectangleCount][2] = (e.getX() - rxy[currentRectangleCount][0]);
                rxy[currentRectangleCount][3] = (e.getY() - rxy[currentRectangleCount][1]);
                currentRectangleCount++;
            } else {
                msgBox.append("Illegal Rectangle\n");
                rxy[currentRectangleCount][2] = 0;
                rxy[currentRectangleCount][3] = 0;
                canvas.repaint();
            }
        } else {
            msgBox.append("Max " + drawingTool + " limit reached\n");
        }
    }

    public Boolean RectangleInBounds(MouseEvent e) {
        return (((e.getX() - rxy[currentRectangleCount][0]) >= 0) && ((e.getY() - rxy[currentRectangleCount][1]) >= 0));
    }

    //circle
    public void drawCirclePress(MouseEvent e, Color selectedColour) {
        updateMsgBox();
        if (currentCircleCount < maxCircleCount - 1) {
            circleColour[currentCircleCount] = selectedColour;
            cxy[currentCircleCount][0] = e.getX();
            cxy[currentCircleCount][1] = e.getY();
        } else {
            msgBox.append("Max " + drawingTool + " limit reached\n");
        }
    }

    public void drawCircleDrag(MouseEvent e) {
        updateMsgBox();
        if (currentCircleCount < maxCircleCount - 1) {
            cxy[currentCircleCount][2] = (e.getX() - cxy[currentCircleCount][0]);
            cxy[currentCircleCount][3] = (e.getY() - cxy[currentCircleCount][1]);
            canvas.repaint();
        } else {
            msgBox.append("Max " + drawingTool + " limit reached\n");
        }

    }

    public void drawCircleRelease(MouseEvent e) {
        updateMsgBox();
        if (currentCircleCount < maxCircleCount - 1) {
            if (CircleInBounds(e)) {
                cxy[currentCircleCount][2] = (e.getX() - cxy[currentCircleCount][0]);
                cxy[currentCircleCount][3] = (e.getY() - cxy[currentCircleCount][1]);
                currentCircleCount++;
            } else {
                msgBox.append("Illegal Circle\n");
                cxy[currentCircleCount][2] = 0;
                cxy[currentCircleCount][3] = 0;
                canvas.repaint();
            }
        } else {
            msgBox.append("Max " + drawingTool + " limit reached\n");
        }

    }

    public Boolean CircleInBounds(MouseEvent e) {
        return (((e.getX() - cxy[currentCircleCount][0]) >= 0) && ((e.getY() - cxy[currentCircleCount][1]) >= 0));
    }

    public void updateMousePosition(MouseEvent e) {
        mousePosition.setText(String.format("%01dpx, %01dpx", e.getX(), e.getY()));
    }

    public void updateMsgBox() {
        msgBox.setText(null);
        switch (drawingTool) {
            case "Line":
                break;
            case "Rectangle":
                break;
            case "Circle":
                break;
            case "Freehand":
                if (freehandRadioButton.isSelected()) {
                    if (maxFreehandPixels > freehandPixelCount) {
                        msgBox.setText(null);
                        msgBox.append("Remaining Pixels: " + (maxFreehandPixels - freehandPixelCount));
                    } else {
                        msgBox.append("You are out of pixels");
                    }
                }
                break;
        }
    }

    public void fineBoxDisplay(Graphics g) {
        int w = canvas.getWidth();
        int h = canvas.getHeight();

        g.setColor(new Color(0.8F, 0.8F, 0.8F));

        for (int i = 0; i < w; i += 10) { //vertical lines
            g.drawLine(i, 0, i, h);
        }
        for (int j = 0; j < h; j += 10) { //horizontal lines 
            g.drawLine(0, j, w, j);
        }

    }

    public void coarseBoxDisplay(Graphics g) {
        int w = canvas.getWidth();
        int h = canvas.getHeight();

        g.setColor(new Color(0.6F, 0.6F, 0.6F));

        for (int i = 0; i < w; i += 50) {
            g.drawLine(i, 0, i, h);
        }
        for (int j = 0; j < h; j += 50) {
            g.drawLine(0, j, w, j);
        }

    }

    //drawingApp variables 
    private Canvas canvas;
    private JPanel ctrlPanel;
    private JTextArea msgBox;
    private JMenuBar menuBar;

    private JButton colourButton, clearButton, animateButton;
    private JRadioButton lineRadioButton, rectangleRadioButton, circleRadioButton, freehandRadioButton;
    private JLabel mousePosition;
    private JCheckBox coarseCheckBox, fineCheckBox;
    private JSlider drawSize;

    public UniPaintApp() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1900, 1000));

        //canvas
        canvas = new Canvas();
        canvas.setBorder(new TitledBorder(new EtchedBorder(), "Canvas"));
        canvas.setPreferredSize(new Dimension(canvasDefaultWidth, canvasDefaultHeight));
        canvas.addMouseMotionListener(new CanvasMouseMotionListener());
        canvas.addMouseListener(new CanvasMouseListener());
        canvas.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        add(canvas, BorderLayout.CENTER);

        //control panel
        ctrlPanel = new JPanel();
        //ctrlPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        ctrlPanel.setBorder(new TitledBorder(new EtchedBorder(), "Control Panel"));
        ctrlPanel.setPreferredSize(new Dimension(ctrlPanelWidth, 100));

        //Drawing position label
        JPanel coordinatesPanel = new JPanel();
        coordinatesPanel.setBorder(new TitledBorder(new EtchedBorder(), "Drawing Position"));
        coordinatesPanel.setPreferredSize(new Dimension(ctrlPanelWidth - 50, 60));
        mousePosition = new JLabel();
        coordinatesPanel.add(mousePosition);
        ctrlPanel.add(coordinatesPanel);

        //drawing tool radio buttons
        JPanel drawingToolsPanel = new JPanel();
        drawingToolsPanel.setBorder(new TitledBorder(new EtchedBorder(), "Drawing Tools"));
        drawingToolsPanel.setPreferredSize(new Dimension(ctrlPanelWidth - 50, 120));
        drawingToolsPanel.setLayout(new BoxLayout(drawingToolsPanel, BoxLayout.PAGE_AXIS));
        lineRadioButton = new JRadioButton("Line");
        rectangleRadioButton = new JRadioButton("Rectangle");
        circleRadioButton = new JRadioButton("Circle");
        freehandRadioButton = new JRadioButton("Freehand");
        //grouping buttons so only one can be selected at a time
        ButtonGroup group = new ButtonGroup();
        group.add(lineRadioButton);
        group.add(rectangleRadioButton);
        group.add(circleRadioButton);
        group.add(freehandRadioButton);
        //button selected
        lineRadioButton.addActionListener(new RadioButtonListener());
        rectangleRadioButton.addActionListener(new RadioButtonListener());
        circleRadioButton.addActionListener(new RadioButtonListener());
        freehandRadioButton.addActionListener(new RadioButtonListener());

        drawingToolsPanel.add(lineRadioButton);
        drawingToolsPanel.add(rectangleRadioButton);
        drawingToolsPanel.add(circleRadioButton);
        drawingToolsPanel.add(freehandRadioButton);
        ctrlPanel.add(drawingToolsPanel);

        //colour selector
        JPanel colourButtonPanel = new JPanel();
        colourButtonPanel.setBorder(new TitledBorder(new EtchedBorder(), "Colour"));
        colourButtonPanel.setPreferredSize(new Dimension(ctrlPanelWidth - 50, 140));
        colourButton = new JButton();
        colourButton.setPreferredSize(new Dimension(ctrlPanelWidth - 200, 100));
        colourButton.addActionListener(new ColourButtonListener());
        colourButtonPanel.add(colourButton);
        ctrlPanel.add(colourButtonPanel);

        //thickness slider
        JPanel thicknessPanel = new JPanel();
        thicknessPanel.setBorder(new TitledBorder(new EtchedBorder(), "Freehand Size"));
        thicknessPanel.setPreferredSize(new Dimension(ctrlPanelWidth - 50, 100));
        drawSize = new JSlider(0, 20);
        drawSize.setMajorTickSpacing(5);
        drawSize.setMinorTickSpacing(1);
        drawSize.setPaintTicks(true);
        drawSize.setPaintLabels(true);
        drawSize.addChangeListener(new FreehandSliderListener());
        thicknessPanel.add(drawSize);

        ctrlPanel.add(thicknessPanel);

        //grid checkbox
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setBorder(new TitledBorder(new EtchedBorder(), "Grid"));
        checkBoxPanel.setPreferredSize(new Dimension(ctrlPanelWidth - 50, 80));
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.PAGE_AXIS));
        coarseCheckBox = new JCheckBox("Coarse");
        fineCheckBox = new JCheckBox("Fine");
        fineCheckBox.addChangeListener(new MyCheckBoxListener());
        coarseCheckBox.addChangeListener(new MyCheckBoxListener());
        checkBoxPanel.add(coarseCheckBox);
        checkBoxPanel.add(fineCheckBox);
        ctrlPanel.add(checkBoxPanel);

        //Main Buttons
        clearButton = new JButton("Clear Canvas");
        animateButton = new JButton("Animate");
        clearButton.setPreferredSize(new Dimension(ctrlPanelWidth - 50, 60));
        animateButton.setPreferredSize(new Dimension(ctrlPanelWidth - 50, 60));
        clearButton.addActionListener(new ClearButtonListener());
        animateButton.addActionListener(new AnimateButtonListener());

        animateButton.addChangeListener(new AnimateButtonChangeListener());
        ctrlPanel.add(clearButton);
        ctrlPanel.add(animateButton);
        add(ctrlPanel, BorderLayout.LINE_START);

        //message area
        msgBox = new JTextArea();
        msgBox.setEditable(false);
        msgBox.setBorder(new TitledBorder(new EtchedBorder(), "Message Box"));
        msgBox.setBackground(null);
        msgBox.setPreferredSize(new Dimension(200, 200));
        add(msgBox, BorderLayout.PAGE_END);

        //JMenuBar
        //save
        menuBar = new JMenuBar();
        JMenu fileBar = new JMenu("File");
        JMenuItem fileSaveMenuItem = new JMenuItem("Save");
        JMenuItem fileLoadMenuItem = new JMenuItem("Load");
        JMenuItem fileExitMenuItem = new JMenuItem("Exit");
        fileSaveMenuItem.addActionListener(new FileSaveListener());
        fileLoadMenuItem.addActionListener(new FileLoadListener());
        fileExitMenuItem.addActionListener(new FileExitListener());
        fileBar.add(fileSaveMenuItem);
        fileBar.add(fileLoadMenuItem);
        fileBar.add(fileExitMenuItem);
        menuBar.add(fileBar);

        //help
        JMenu helpBar = new JMenu("Help");
        JMenuItem helpAboutMenuItem = new JMenuItem("About");
        helpAboutMenuItem.addActionListener(new HelpAboutListener());
        helpBar.add(helpAboutMenuItem);
        menuBar.add(helpBar);

        add(menuBar, BorderLayout.PAGE_START);

        pack();
        updateMsgBox();
        setVisible(true);
    }

    void draw(Graphics g) {
        //grid
        if (fineCheckBox.isSelected()) {
            fineBoxDisplay(g);
        }
        if (coarseCheckBox.isSelected()) {
            coarseBoxDisplay(g);
        }

        //freehand
        for (int i = 0; i < freehandPixelCount; i++) {
            g.setColor(freehandColour[i]);
            g.fillRect(fxy[i][0], fxy[i][1], fxy[i][2], fxy[i][2]);
        }

        //line 
        for (int i = 0; i <= currentLineCount; i++) {
            g.setColor(lineColour[i]);
            g.drawLine(lxy[i][0], lxy[i][1], lxy[i][2], lxy[i][3]);
        }

        //rectangle
        for (int i = 0; i <= currentRectangleCount; i++) {
            g.setColor(rectangleColour[i]);
            g.drawRect(rxy[i][0], rxy[i][1], rxy[i][2], rxy[i][3]);
        }

        //circle
        for (int i = 0; i <= currentCircleCount; i++) {
            g.setColor(circleColour[i]);
            g.drawOval(cxy[i][0], cxy[i][1], cxy[i][2], cxy[i][3]);
        }
    }

    public static void main(String[] args) {
        UniPaintApp UniPaintApplication = new UniPaintApp();

    }

}
