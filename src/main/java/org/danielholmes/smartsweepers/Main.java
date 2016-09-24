package org.danielholmes.smartsweepers;

import javax.swing.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    private static String szApplicationName = "Smart Sweepers v1.0";
    private static String szWindowClassName = "sweeper";

    //The controller class for this simulation
    private static CController g_pController = null;

    //create an instance of the parameter class.
    private static CParams g_Params;

    public static void main(String[] args)
    {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        loadInConfigParameters();

        JFrame frame = new JFrame(szApplicationName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(CParams.WindowWidth, CParams.WindowHeight);

        JLabel label = new JLabel("Hello World");
        frame.getContentPane().add(label);

        frame.pack();
        frame.setVisible(true);

        CController g_pController = new CController(/*frame*/);

        long millisPerFrame = 1000 / CParams.iFramesPerSecond;

        Timer timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("Run");
                    }
                },
                millisPerFrame,
                millisPerFrame
        );
        //CTimer timer(CParams.iFramesPerSecond);
        //timer.Start();

        /*while (true)
        {
            if (timer.ReadyForNextFrame() || g_pController.FastRender())
            {
                if (!g_pController.Update())
                {
                    break;
                }

                // TODO: Call repaint() and possibly g_pController->Render(hdcBackBuffer);
            }

        }*/
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
