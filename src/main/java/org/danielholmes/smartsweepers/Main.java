package org.danielholmes.smartsweepers;

import javafx.scene.input.KeyCode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    private static String szApplicationName = "Smart Sweepers v1.0";
    private static String szWindowClassName = "sweeper";

    //The controller class for this simulation
    private static CController g_pController;

    public static void main(String[] args)
    {
        loadInConfigParameters();

        g_pController = new CController(/*frame*/);

        JPanel mainPanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);

                g_pController.Render((Graphics2D) g);
            }
        };
        mainPanel.setSize(CParams.WindowWidth, CParams.WindowHeight);

        JFrame frame = new JFrame(szApplicationName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(CParams.WindowWidth, CParams.WindowHeight);
        frame.getContentPane().add(mainPanel);
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent e) { }

            @Override
            public void keyTyped(KeyEvent e) { }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'f') {
                    g_pController.FastRenderToggle();
                } else if (e.getKeyChar() == 'r') {
                    g_pController = new CController();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
            }
        });
        frame.setVisible(true);

        long millisPerFrame = 1000 / CParams.iFramesPerSecond;

        while (true)
        {
            long frameStart = System.currentTimeMillis();
            if (!g_pController.Update())
            {
                // Done
                break;
            }
            mainPanel.repaint();

            if (!g_pController.FastRender())
            {
                long timeToNextFrameStart = (frameStart + millisPerFrame) - System.currentTimeMillis();
                if (timeToNextFrameStart > 0) {
                    try {
                        Thread.sleep(timeToNextFrameStart);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }
    }

    private static void loadInConfigParameters() {
        try {
            CParams.LoadInParameters(Paths.get(Thread.currentThread().getContextClassLoader().getResource("params.ini").toURI()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    case WM_KEYUP:
        case VK_ESCAPE:
            PostQuitMessage(0);
        case 'F':
            g_pController.FastRenderToggle();
        //reset the demo
        case 'R':
            g_pController = new CController(hwnd);
    }*/
}
