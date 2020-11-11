package picocli;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FileManipulator {

    private static ArrayList<String> readFromFile(String path){
        ArrayList<String> contentFromFile = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))){
            String line;
            while ((line = reader.readLine()) != null) {
                contentFromFile.add(line);
            }
            return contentFromFile;
        }
        catch (IOException e){
            System.err.println(new File(path).getName() + " not found!");
            System.out.println("Try another path or file name:");
            Scanner scanner = new Scanner(System.in);
            String newPath = scanner.nextLine();
            scanner.close();
            return readFromFile(newPath);
        }
    }
    private static String readFromFile(String path, ArrayList<String> restOfText, int firstLineIndex, int secondLineIndex){

        try (BufferedReader reader = new BufferedReader(new FileReader(path))){

            String holder;
            String firstLine="";
            String secondLine="";

            int currentLine =1;
            int listLineCounter = 0;
            boolean inside = false;

            while((holder = reader.readLine())!=null){
                if(currentLine == firstLineIndex && firstLine.equals("")) {
                    firstLine = holder;
                    restOfText.add(listLineCounter,null);
                    inside= true;
                }
                if(currentLine == secondLineIndex && secondLine.equals("")) {
                    secondLine = holder;
                    if(!firstLine.equals(secondLine))
                        restOfText.add(listLineCounter, null);
                    inside= true;
                }
                else if(!inside) {
                    restOfText.add(listLineCounter, holder);
                }
                inside = false;
                currentLine++;
                listLineCounter++;
            }
            return firstLine+ "///" + secondLine;
        }
        catch (IOException e){
            System.err.println(new File(path).getName() + " not found!");
            System.out.println("Try another path or file name:");
            Scanner scanner = new Scanner(System.in);
            String newPath = scanner.nextLine();
            scanner.close();
            return readFromFile(newPath,restOfText,firstLineIndex,secondLineIndex);
        }
    }

    private static void writeToFile(String path, ArrayList<String> content){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))){
            for(String s : content) {
                writer.write(s + "\n");
            }
        }
        catch (IOException e){
            System.err.println("Could not write to " + new File(path).getName());
        }
    }
    private static void writeToFile(String path, String content){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))){
            writer.write(content);
        }
        catch (IOException e){
            System.err.println("Could not write to " + new File(path).getName());
        }
    }


    public static void switchTwoLines(String path){
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter the indexes of the lines you want to change: ");
            int indexOfLine1 = scanner.nextInt();
            int indexOfLine2 = scanner.nextInt();

            ArrayList<String> linesFromFile = readFromFile(path);

            String lineAtIndex1 = linesFromFile.get(indexOfLine1 - 1);
            String lineAtIndex2 = linesFromFile.get(indexOfLine2 - 1);

            linesFromFile.set(indexOfLine1 - 1, lineAtIndex2);
            linesFromFile.set(indexOfLine2 - 1, lineAtIndex1);

            writeToFile(path, linesFromFile);

        } catch (IndexOutOfBoundsException e) {
            System.err.println(new File(path).getName() + " does not have these lines! \nTry new lines!");
            switchTwoLines(path);
        } finally {
            scanner.close();
            System.out.println("Changed lines successfully!");
        }
    }

    public static void switchAtIndex(String path){
        Scanner scanner = new Scanner(System.in);

        try{
            System.out.println("Enter <first_line_index> <first_line_word_index> <second_line_index> <second_line_word_index>");

            int firstLineIndex = scanner.nextInt();
            int firstWordIndex = scanner.nextInt();
            int secondLineIndex = scanner.nextInt();
            int secondWordIndex = scanner.nextInt();

            ArrayList<String> restOfText = new ArrayList<>();

            String[] holder = readFromFile(path,restOfText,firstLineIndex,secondLineIndex).split("///");

            String firstLine = holder[0];
            String secondLine = holder[1];

            if(firstLineIndex == secondLineIndex){
                restOfText.set(firstLineIndex-1,swapWordPlaceOnOneLine(firstLine,firstWordIndex,secondLine,secondWordIndex));
            }
            else {
                restOfText.set(firstLineIndex-1,swapWordPlace(firstLine,firstWordIndex,secondLine,secondWordIndex));
                restOfText.set(secondLineIndex-1,swapWordPlace(secondLine,secondWordIndex,firstLine,firstWordIndex));
            }

            String contentToSave = restOfText.toString().replaceAll("[\\[\\]]", "").replaceAll(", ", "\n");
            writeToFile(path, contentToSave.trim());

        }
        catch (ArrayIndexOutOfBoundsException e){
            System.err.println("Some of the indexes do not exist, please enter new ones!");
            switchAtIndex(path);
        }
        finally {
            System.out.println("Done!");
            scanner.close();
        }
    }

    private static String swapWordPlace(String line,int wordIndex,String secondLine, int secondWordIndex){


        String neededWord = line.split("\\s+")[wordIndex-1];

        String[] wordsSeparatedBySpace = line.split(" ");

        String[] wordsSeparatedByTab = null;

        int wordCount = 1;

        for(int i = 0; i<wordsSeparatedBySpace.length; i++){
            if(wordsSeparatedBySpace[i].contains(neededWord)) {
                if (wordsSeparatedBySpace[i].contains("\t")) {
                    wordsSeparatedByTab = wordsSeparatedBySpace[i].split("\\s+");
                    for(int j =0; j<wordsSeparatedByTab.length; j++){
                        if(wordsSeparatedByTab[j].equals(neededWord) && wordIndex==wordCount){
                            wordsSeparatedByTab[j] = secondLine.split("\\s+")[secondWordIndex-1];
                        }
                        wordCount++;
                    }
                    StringBuilder holder = new StringBuilder();
                    for(int j =0; j<wordsSeparatedByTab.length; j++){
                        if(j==0)
                            holder.append(wordsSeparatedByTab[j]);
                        else
                            holder.append("\t"+wordsSeparatedByTab[j]);
                    }
                    wordsSeparatedBySpace[i] = holder.toString();
                }
                else{
                    if(wordCount == wordIndex){
                        wordsSeparatedBySpace[i] = secondLine.split("\\s+")[secondWordIndex-1];
                    }
                }
            }
            wordCount++;
        }

        StringBuilder returnLine = new StringBuilder();
        for(int k = 0 ; k<wordsSeparatedBySpace.length;k++){
            if(k!=0 )
                returnLine.append(" " + wordsSeparatedBySpace[k]);
            else
                returnLine.append(wordsSeparatedBySpace[k]);
        }

        return returnLine.toString();

    }
    private static String swapWordPlaceOnOneLine(String line,int wordIndex,String secondLine, int secondWordIndex){

        String neededWord1 = line.split("\\s+")[wordIndex-1];
        String neededWord2 = secondLine.split("\\s+")[secondWordIndex-1];

        String[] wordsSeparatedBySpace = line.split(" ");


        String[] wordsSeparatedByTab = null;

        int wordCount = 1;

        for(int i = 0; i<wordsSeparatedBySpace.length; i++) {
            if (wordsSeparatedBySpace[i].contains(neededWord1)) {
                if (wordsSeparatedBySpace[i].contains("\t")) {
                    wordsSeparatedByTab = wordsSeparatedBySpace[i].split("\\s+");
                    for (int j = 0; j < wordsSeparatedByTab.length; j++) {
                        if (wordsSeparatedByTab[j].equals(neededWord1) && wordIndex == wordCount) {
                            wordsSeparatedByTab[j] = secondLine.split("\\s+")[secondWordIndex - 1];
                        }
                        wordCount++;
                    }
                    StringBuilder holder = new StringBuilder();
                    for (int j = 0; j < wordsSeparatedByTab.length; j++) {
                        if (j == 0)
                            holder.append(wordsSeparatedByTab[j]);
                        else
                            holder.append("\t" + wordsSeparatedByTab[j]);
                    }
                    wordsSeparatedBySpace[i] = holder.toString();
                } else {
                    if (wordCount == wordIndex) {
                        wordsSeparatedBySpace[i] = secondLine.split("\\s+")[secondWordIndex - 1];
                    }
                }
            }
            wordCount++;
        }


        wordCount = 1;

        for(int i = 0; i<wordsSeparatedBySpace.length; i++) {
            if (wordsSeparatedBySpace[i].contains(neededWord2)) {
                if (wordsSeparatedBySpace[i].contains("\t")) {
                    wordsSeparatedByTab = wordsSeparatedBySpace[i].split("\\s+");
                    for (int j = 0; j < wordsSeparatedByTab.length; j++) {
                        if (wordsSeparatedByTab[j].equals(neededWord2) && secondWordIndex == wordCount) {
                            wordsSeparatedByTab[j] = line.split("\\s+")[wordIndex - 1];
                        }
                        wordCount++;
                    }
                    StringBuilder holder = new StringBuilder();
                    for (int j = 0; j < wordsSeparatedByTab.length; j++) {
                        if (j == 0)
                            holder.append(wordsSeparatedByTab[j]);
                        else
                            holder.append("\t" + wordsSeparatedByTab[j]);
                    }
                    wordsSeparatedBySpace[i] = holder.toString();
                } else {
                    if (wordCount == secondWordIndex) {
                        wordsSeparatedBySpace[i] = line.split("\\s+")[wordIndex - 1];
                    }
                }
            }
            wordCount++;
        }


        StringBuilder returnLine = new StringBuilder();
        for(int k = 0 ; k<wordsSeparatedBySpace.length;k++){
            if(k!=0 )
                returnLine.append(" " + wordsSeparatedBySpace[k]);
            else
                returnLine.append(wordsSeparatedBySpace[k]);
        }

        return returnLine.toString();

    }
}



