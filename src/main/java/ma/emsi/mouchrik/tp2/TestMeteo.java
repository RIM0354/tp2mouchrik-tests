package ma.emsi.mouchrik.tp2;

import ma.emsi.mouchrik.tp2.outil.meteo.MeteoTool;

/**
 * Teste l'outil météo MeteoTool sans LLM, pour Paris (latitude 48.85, longitude 2.35).
 */
public class TestMeteo {

    public static void main(String[] args) {
        MeteoTool meteoTool = new MeteoTool();
        System.out.println("Pluie prévue à Paris pendant 3 jours à partir d'aujourd'hui : "
                + meteoTool.isPluiePrevue(48.85, 2.35, 0, 3));
        System.out.println("Pluie prévue à Paris pendant 3 jours à partir de demain : "
                + meteoTool.isPluiePrevue(48.85, 2.35, 1, 3));
    }
}
