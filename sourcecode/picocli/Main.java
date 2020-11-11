package picocli;
import picocli.CommandLine.*;

import java.io.File;
import java.util.Scanner;



@Command(name = "textmanipulator",  mixinStandardHelpOptions = true, version = "textmanipulator 1.0",description = "Performs text file manipulation operations")
public class Main implements Runnable {

    @Option(names = {"-gp", "--getpath"}, description = "Gets the file's path")
    private boolean pathBoolean;

    @Option(names = {"-sl", "--switchlines"},description = "Switches the places of two lines")
    private boolean switchLanes;

    @Option(names = {"-sw","--switchwords"}, description = "Switches the places of two words")
    private  boolean switchTwoWords;


    public static void main(String[] args) {

            int exitCode = new CommandLine(new Main()).execute(args);
            System.exit(exitCode);

    }

    @Override
    public void run()  {

        if((switchLanes ||  switchTwoWords)&& !pathBoolean)
            System.out.println("You need to specify a path first!(-h or --help for more information)");

        if(pathBoolean){
            System.out.print("Enter path: ");
            String path = new Scanner(System.in).nextLine();
            if (new File(path).isFile()) {
                if (switchLanes) {
                    FileManipulator.switchTwoLines(path);
                }
                if (switchTwoWords) {
                    FileManipulator.switchAtIndex(path);
                }
            } else {
                System.err.println("File path is incorrect!");
            }
        }
    }
}
