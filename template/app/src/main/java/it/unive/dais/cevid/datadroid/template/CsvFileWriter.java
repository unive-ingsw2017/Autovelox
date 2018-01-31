package it.unive.dais.cevid.datadroid.template;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvFileWriter {
    private static final String DELIMITER = ";";
    private static final String NEW_LINE_SEPARATOR = "\n";

    //CSV file header
    private static final String FILE_HEADER = "\"Comune\";\"Provincia\";\"Regione\";\"Nome\";\"Anno inserimento\";\"Data e ora" +
            " inserimento\";\"Identificatore in OpenStreetMap\";\"Longitudine\";\"Latitudine\";";

    public static void writeCsvFile(String fileName, Autovelox autovelox) {
        List students = new ArrayList();
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName);
            //Write a new autovelox object list to the CSV file
            fileWriter.append(autovelox.getComune());
            fileWriter.append(DELIMITER);
            fileWriter.append(autovelox.getProvincia());
            fileWriter.append(DELIMITER);
            fileWriter.append(autovelox.getRegione());
            fileWriter.append(DELIMITER);
            fileWriter.append(autovelox.getNome());
            fileWriter.append(DELIMITER);
            fileWriter.append(autovelox.getAnnoinserito());
            fileWriter.append(DELIMITER);
            fileWriter.append(autovelox.getDataeorainserimento());
            fileWriter.append(DELIMITER);
            fileWriter.append(String.valueOf(autovelox.getIdentificatoreinOpenStreetMap()));
            fileWriter.append(DELIMITER);
            fileWriter.append(String.valueOf(autovelox.getLongitudine()));
            fileWriter.append(DELIMITER);
            fileWriter.append(String.valueOf(autovelox.getLatitudine()));
            fileWriter.append(DELIMITER);
            fileWriter.append(NEW_LINE_SEPARATOR);
            System.out.println("CSV file was created successfully !!!");
        }
        catch(Exception e){
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        }
        finally{
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }

        }
    }
}
