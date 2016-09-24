package org.danielholmes.smartsweepers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CParams {
    public static void LoadInParameters(Path path) {
        try {
            Files.lines(path)
                    .forEach(line -> {
                        if (line != null && !line.equals("")) {
                            String[] parts = line.split(" ");
                            if (parts.length != 2) {
                                throw new RuntimeException("Invalid line: " + line);
                            }

                            String key = parts[0];
                            String value = parts[1];
                            switch (key) {
                                case "iFramesPerSecond":
                                    iFramesPerSecond = Integer.parseInt(value);
                                    break;
                                case "iNumInputs":
                                    iNumInputs = Integer.parseInt(value);
                                    break;
                                case "iNumHidden":
                                    iNumHidden = Integer.parseInt(value);
                                    break;
                                case "iNeuronsPerHiddenLayer":
                                    iNeuronsPerHiddenLayer = Integer.parseInt(value);
                                    break;
                                case "iNumOutputs":
                                    iNumOutputs = Integer.parseInt(value);
                                    break;
                                case "dActivationResponse":
                                    dActivationResponse = Double.parseDouble(value);
                                    break;
                                case "dBias":
                                    dBias = Double.parseDouble(value);
                                    break;
                                case "dMaxTurnRate":
                                    dMaxTurnRate = Double.parseDouble(value);
                                    break;
                                case "dMaxSpeed":
                                    dMaxSpeed = Double.parseDouble(value);
                                    break;
                                case "iSweeperScale":
                                    iSweeperScale = Integer.parseInt(value);
                                    break;
                                case "iNumMines":
                                    iNumMines = Integer.parseInt(value);
                                    break;
                                case "iNumSweepers":
                                    iNumSweepers = Integer.parseInt(value);
                                    break;
                                case "iNumTicks":
                                    iNumTicks = Integer.parseInt(value);
                                    break;
                                case "dMineScale":
                                    dMineScale = Double.parseDouble(value);
                                    break;
                                case "dCrossoverRate":
                                    dCrossoverRate = Double.parseDouble(value);
                                    break;
                                case "dMutationRate":
                                    dMutationRate = Double.parseDouble(value);
                                    break;
                                case "dMaxPerturbation":
                                    dMaxPerturbation = Double.parseDouble(value);
                                    break;
                                case "iNumElite":
                                    iNumElite = Integer.parseInt(value);
                                    break;
                                case "iNumCopiesElite":
                                    iNumCopiesElite = Integer.parseInt(value);
                                    break;
                                default:
                                    throw new RuntimeException("Unexpected key " + key);
                            }
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Error getting ini file", e);
        }
    }

    public static double dPi                 = 3.14159265358979;
    public static double dHalfPi             = dPi / 2;
    public static double dTwoPi              = dPi * 2;
    public static int WindowWidth            = 400;
    public static int WindowHeight           = 400;
    public static int iFramesPerSecond       = 0;
    public static int iNumInputs             = 0;
    public static int iNumHidden             = 0;
    public static int iNeuronsPerHiddenLayer = 0;
    public static int iNumOutputs            = 0;
    public static double dActivationResponse = 0;
    public static double dBias               = 0;
    public static double dMaxTurnRate        = 0;
    public static double dMaxSpeed           = 0;
    public static int iSweeperScale          = 0;
    public static int iNumSweepers           = 0;
    public static int iNumMines              = 0;
    public static int iNumTicks              = 0;
    public static double dMineScale          = 0;
    public static double dCrossoverRate      = 0;
    public static double dMutationRate       = 0;
    public static double dMaxPerturbation    = 0;
    public static int iNumElite              = 0;
    public static int iNumCopiesElite        = 0;
}
