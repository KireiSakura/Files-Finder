import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

class FindFiles {
    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
        } else {
            // extract options from command line
            java.util.HashMap<String, String> options = parse(args);

            // print them to verify that it worked
            if (options.containsKey("help")) {
                printHelp();
            } else if (options.containsValue("error")) {
                printError(options);
                printHelp();
            } else {
                Path currentDirectory = Paths.get(".");
                if (args.length == 1) {
                    System.out.println("finding the file " + args[0] + " in the current directory");
                    System.out.println("");
                    BiPredicate<Path, BasicFileAttributes> fileMatcher = (path, attribute) -> String.valueOf(path)
                            .contains(args[0]);
                    try {
                        if (Files.find(currentDirectory, 1, fileMatcher).count() == 0) {
                            System.out.println("File " + args[0] + " is not present.");
                        } else {
                            System.out.println(Paths.get(args[0]).toAbsolutePath());
                        }
                    } catch (Exception ex) {
                        System.out.print("Error finding file:" + ex.toString());
                    }
                } else if (args.length == 2) {
                    if (options.containsKey("r")) {
                        System.out.println("find " + args[0] + " in the current directory and all its subfolders.");
                        int present = searchRecursive(currentDirectory, args[0]);
                        if (present == 0) {
                            System.out.println("");
                            System.out.println("File " + args[0] + " not present.");
                        }
                    } else {
                        System.out.println(
                                "Looking for files in the current directory that satisfy the given regular expression: "
                                        + args[0]);

                        File files = currentDirectory.toFile();

                        int count = 0;

                        for (File file : files.listFiles()) {
                            if (file.isFile()) {
                                if (Pattern.matches(args[0], file.getName())) {
                                    count++;
                                    System.out.println("");
                                    try {
                                        System.out.println(file.getCanonicalPath());
                                    } catch (Exception ex) {
                                        System.out.print("Error finding file:" + ex.toString());
                                    }
                                }
                            }
                        }
                        if (count == 0) {
                            System.out.println("");
                            System.out.println("No file matches the given regex: " + args[0]);
                        }
                    }
                } else if (args.length == 3) {
                    if (options.containsKey("dir")) {
                        Path myDirectory = Paths.get(args[2]);
                        System.out.println("finding the file " + args[0] + " in " + myDirectory.toFile().getName());
                        System.out.println("");
                        BiPredicate<Path, BasicFileAttributes> fileMatcher = (path, attribute) -> String.valueOf(path)
                                .contains(args[0]);
                        try {
                            if (Files.find(myDirectory, 1, fileMatcher).count() == 0) {
                                System.out.println("File " + args[0] + " is not present.");
                            } else {
                                Files.find(myDirectory, 1, fileMatcher).forEach(element -> {
                                    try {
                                        System.out.println(element.toFile().getCanonicalPath());
                                    } catch (Exception ex) {
                                        System.out.print("Error finding file:" + ex.toString());
                                    }
                                });

                            }
                        } catch (Exception ex) {
                            System.out.print("Error finding file:" + ex.toString());
                        }
                    } else {
                        List<String> extensions = Arrays.asList(args[2].split("\\s*,\\s*"));
                        System.out.println("finding " + args[0] + " with the extensions " + args[2]
                                + " in the current directory.");
                        for (String extension : extensions) {
                            String filename = args[0] + "." + extension;
                            System.out.println("");
                            BiPredicate<Path, BasicFileAttributes> fileMatcher = (path, attribute) -> String
                                    .valueOf(path).endsWith(filename);
                            try {
                                if (Files.find(currentDirectory, 1, fileMatcher).count() == 0) {
                                    System.out.println("File " + filename + " is not present.");
                                } else {
                                    Files.find(currentDirectory, 1, fileMatcher).forEach(element -> {
                                        try {
                                            System.out.println(element.toFile().getCanonicalPath());
                                        } catch (Exception ex) {
                                            System.out.print("Error finding file:" + ex.toString());
                                        }
                                    });

                                }
                            } catch (Exception ex) {
                                System.out.print("Error finding file:" + ex.toString());
                            }
                        }
                    }
                } else if (args.length == 4) {
                    if (options.containsKey("r") && options.containsKey("dir")) {
                        Path myDirectory = Paths.get(options.get("dir"));
                        System.out.println("find " + args[0] + " in " + myDirectory.toFile().getName()
                                + " and all its subfolders.");
                        int present = searchRecursive(myDirectory, args[0]);
                        if (present == 0) {
                            System.out.println("");
                            System.out.println("File " + args[0] + " not present.");
                        }
                    } else if (options.containsKey("reg") && options.containsKey("dir")) {
                        System.out.println("Looking for files in the" + options.get("dir")
                                + " that satisfy the given regular expression: " + args[0]);

                        Path myDirectory = Paths.get(options.get("dir"));
                        File files = myDirectory.toFile();

                        int count = 0;

                        for (File file : files.listFiles()) {
                            if (file.isFile()) {
                                if (Pattern.matches(args[0], file.getName())) {
                                    count++;
                                    System.out.println("");
                                    try {
                                        System.out.println(file.getCanonicalPath());
                                    } catch (Exception ex) {
                                        System.out.print("Error finding file:" + ex.toString());
                                    }
                                }
                            }
                        }
                        if (count == 0) {
                            System.out.println("");
                            System.out.println("No file matches the given regex: " + args[0]);
                        }
                    } else if (options.containsKey("ext") && options.containsKey("r")) {
                        List<String> extensions = Arrays.asList(options.get("ext").split("\\s*,\\s*"));
                        System.out.println("finding " + args[0] + " with the extensions " + options.get("ext")
                                + " in the current directory and all its subfolders.");
                        for (String extension : extensions) {
                            String filename = args[0] + "." + extension;
                            int present = searchExtensionRecursive(currentDirectory, filename);
                            if (present == 0) {
                                System.out.println("");
                                System.out.println("File " + filename + " not present.");
                            }
                        }
                    } else if (options.containsKey("ext") && options.containsKey("reg")) {
                        List<String> extensions = Arrays.asList(options.get("ext").split("\\s*,\\s*"));
                        System.out.println(
                                "Looking for files in the current directory that satisfy the given regular expression "
                                        + args[0] + " and ends with the extensions " + options.get("ext"));
                        File files = currentDirectory.toFile();
                        int count = 0;
                        for (String extension : extensions) {
                            for (File file : files.listFiles()) {
                                if (file.isFile()) {
                                    if (Pattern.matches(args[0], file.getName())
                                            && file.getName().endsWith(extension)) {
                                        count++;
                                        System.out.println("");
                                        try {
                                            System.out.println(file.getCanonicalPath());
                                        } catch (Exception ex) {
                                            System.out.print("Error finding file:" + ex.toString());
                                        }
                                    }
                                }
                            }
                        }
                        if (count == 0) {
                            System.out.println("");
                            System.out.println("No file matches the given regex " + args[0]
                                    + " that ends with the extesions " + options.get("ext"));
                        }
                    }
                } else if (args.length == 5) {
                    if (options.containsKey("dir") && options.containsKey("r") && options.containsKey("reg")) {
                        Path myDirectory = Paths.get(options.get("dir"));
                        System.out.println("Looking for files in the" + options.get("dir")
                                + " and all its subfolders that satisfy the given regular expression: " + args[0]);
                        int present = searchRegexRecursive(myDirectory, args[0]);
                        if (present == 0) {
                            System.out.println("");
                            System.out.println("No file matches the given regex: " + args[0]);
                        }
                    } else if (options.containsKey("ext") && options.containsKey("r") && options.containsKey("reg")) {
                        List<String> extensions = Arrays.asList(options.get("ext").split("\\s*,\\s*"));
                        System.out.println(
                                "Looking for files in the current directory and all its subfolders that satisfy the given regular expression "
                                        + args[0] + " and ends with the extensions " + options.get("ext"));
                        int count = 0;
                        for (String extension : extensions) {
                            count = count + searchRegexExtRecursive(currentDirectory, args[0], extension);
                        }
                        if (count == 0) {
                            System.out.println("");
                            System.out.println("No file matches the given regex " + args[0]
                                    + " that ends with the extesions " + options.get("ext"));
                        }
                    } else if (options.containsKey("ext") && options.containsKey("dir")) {
                        List<String> extensions = Arrays.asList(args[2].split("\\s*,\\s*"));
                        Path myDirectory = Paths.get(options.get("dir"));
                        System.out.println("finding " + args[0] + " with the extensions " + options.get("ext") + " in "
                                + options.get("dir"));
                        for (String extension : extensions) {
                            String filename = args[0] + "." + extension;
                            BiPredicate<Path, BasicFileAttributes> fileMatcher = (path, attribute) -> String
                                    .valueOf(path).endsWith(filename);
                            try {
                                if (Files.find(myDirectory, 1, fileMatcher).count() == 0) {
                                    System.out.println("");
                                    System.out.println("File " + filename + " is not present.");
                                } else {
                                    System.out.println("");
                                    Files.find(myDirectory, 1, fileMatcher).forEach(element -> {
                                        try {
                                            System.out.println(element.toFile().getCanonicalPath());
                                        } catch (Exception ex) {
                                            System.out.print("Error finding file:" + ex.toString());
                                        }
                                    });

                                }
                            } catch (Exception ex) {
                                System.out.print("Error finding file:" + ex.toString());
                            }
                        }
                    }

                } else if (args.length == 6) {
                    if (options.containsKey("r")) {
                        List<String> extensions = Arrays.asList(args[2].split("\\s*,\\s*"));
                        Path myDirectory = Paths.get(options.get("dir"));
                        System.out.println("finding " + args[0] + " with the extensions " + args[2] + " in "
                                + options.get("dir") + " and all its subfolders.");
                        int count = 0;

                        for (String extension : extensions) {
                            String filename = args[0] + "." + extension;
                            count = count + searchExtensionRecursive(myDirectory, filename);
                        }
                        if (count == 0) {
                            System.out.println("");
                            System.out.println(
                                    "File " + args[0] + " with the extensions " + options.get("ext") + " not present.");
                        }
                    } else {
                        List<String> extensions = Arrays.asList(options.get("ext").split("\\s*,\\s*"));
                        Path myDirectory = Paths.get(options.get("dir"));
                        System.out.println("Looking for files in" + options.get("dir")
                                + " that satisfy the given regular expression " + args[0]
                                + " and ends with the extensions " + options.get("ext"));
                        File files = myDirectory.toFile();
                        int count = 0;
                        for (String extension : extensions) {
                            for (File file : files.listFiles()) {
                                if (file.isFile()) {
                                    if (Pattern.matches(args[0], file.getName())
                                            && file.getName().endsWith(extension)) {
                                        count++;
                                        System.out.println("");
                                        try {
                                            System.out.println(file.getCanonicalPath());
                                        } catch (Exception ex) {
                                            System.out.print("Error finding file:" + ex.toString());
                                        }
                                    }
                                }
                            }
                            if (count == 0) {
                                System.out.println("");
                                System.out.println("No file matches the given regex " + args[0]
                                        + " that ends with the extesions " + options.get("ext"));
                            }
                        }
                    }
                } else {
                    List<String> extensions = Arrays.asList(options.get("ext").split("\\s*,\\s*"));
                    Path myDirectory = Paths.get(options.get("dir"));
                    System.out.println("Looking for files in " + options.get("dir")
                            + " and all its subfolders that satisfy the given regular expression " + args[0]
                            + " and ends with the extensions " + options.get("ext"));
                    int count = 0;
                    for (String extension : extensions) {
                        count = count + searchRegexExtRecursive(myDirectory, args[0], extension);
                    }
                    if (count == 0) {
                        System.out.println("");
                        System.out.println("No file matches the given regex " + args[0]
                                + " that ends with the extesions " + options.get("ext"));
                    }
                }
            }
        }
        System.out.println("");

    }

    static int searchRecursive(Path path, String filename) {
        File file = path.toFile();
        int count = 0;
        if (file.toString().contains(filename)) {
            System.out.println("");
            try {
                System.out.println(file.getCanonicalPath());
            } catch (Exception ex) {
                System.out.print("Error finding file:" + ex.toString());
            }
            count++;
        }

        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                count += searchRecursive(subFile.toPath(), filename);
            }

        }

        return count;
    }

    static int searchRegexRecursive(Path path, String regex) {
        File file = path.toFile();
        int count = 0;
        if (Pattern.matches(regex, file.getName())) {
            System.out.println("");
            try {
                System.out.println(file.getCanonicalPath());
            } catch (Exception ex) {
                System.out.print("Error finding file:" + ex.toString());
            }
            count++;
        }

        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                count += searchRegexRecursive(subFile.toPath(), regex);
            }
        }

        return count;
    }

    static int searchRegexExtRecursive(Path path, String regex, String extension) {
        File file = path.toFile();
        int count = 0;
        if (Pattern.matches(regex, file.getName()) && file.getName().endsWith("." + extension)) {
            System.out.println("");
            try {
                System.out.println(file.getCanonicalPath());
            } catch (Exception ex) {
                System.out.print("Error finding file:" + ex.toString());
            }
            count++;
        }

        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                count += searchRegexExtRecursive(subFile.toPath(), regex, extension);
            }
        }

        return count;
    }

    static int searchExtensionRecursive(Path path, String filename) {
        File file = path.toFile();
        int count = 0;
        if (file.getName().endsWith(filename)) {
            System.out.println("");
            try {
                System.out.println(file.getCanonicalPath());
            } catch (Exception ex) {
                System.out.print("Error finding file:" + ex.toString());
            }
            count++;
        }

        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                count += searchExtensionRecursive(subFile.toPath(), filename);
            }
        }

        return count;
    }

    static void printError(HashMap<String, String> options) {
        for (Map.Entry entry : options.entrySet()) {
            if (entry.getValue().equals("error")) {
                if (entry.getKey().equals("dir")) {
                    System.out.println("ERROR: No directory provided for the directory(-dir) option");
                    break;
                } else if (entry.getKey().equals("ext")) {
                    System.out.println("ERROR: No extensions for the filenames for the -ext option provided.");
                    break;
                } else if (entry.getKey().equals("filename")) {
                    System.out.println("ERROR: No filename provided");
                    break;
                } else if (entry.getKey().equals("duplicate")) {
                    System.out.println(
                            "ERROR: Argument provided for option(-r,-reg), where it is not required, or duplicate argument provided");
                    break;
                } else {
                    System.out.println("ERROR: Invalid option provided: -" + entry.getKey()
                            + ", provide options from the list as shown in help");

                    break;
                }
            }
        }
    }

    // Print command-line syntax
    static void printHelp() {
        System.out.println("Usage: java FindFiles filetofind [-option arg]");
        System.out.println("-help                     :: print out a help page and exit the program.");
        System.out.println("-r                        :: execute the command recursively in subfiles.");
        System.out.println("-reg                      :: treat `filetofind` as a regular expression when searching.");
        System.out.println("-dir [directory]          :: find files starting in the specified directory.");
        System.out.println(
                "-ext [ext1,ext2,...]      :: find files matching [filetofind] with extensions [ext1, ext2,...].");
    }

    // Build a dictionary of key:value pairs (without the leading "-")
    static HashMap<String, String> parse(String[] args) {
        HashMap<String, String> arguments = new HashMap<>();
        String key = null;
        String value = null;

        if (args[0].startsWith("-")) {
            arguments.put("filename", "error");
        }
        // process each argument as either a key or value in the pair
        // process each argument as either a key or value in the pair
        for (String entry : args) {
            // assume that keys start with a dash
            if (entry.startsWith("-")) {
                // if we already have a key, and then find a second key
                // before we've found the corresponding value, it's an error.
                if (key != null) {
                    arguments.put(key, "error");
                    key = null;
                }
                if (entry.substring(1).equals("help")) {
                    arguments.put(entry.substring(1), "help");
                } else if (entry.substring(1).equals("reg")) {
                    arguments.put(entry.substring(1), "regex");
                } else if (entry.substring(1).equals("r")) {
                    arguments.put(entry.substring(1), "recurse");
                } else {
                    key = entry.substring(1); // skip leading "-"
                }
                // values start with anything else
            } else {
                // if we already have a key, and then find a second key
                // before we've found the corresponding value, it's an error.
                if (key == null) {
                    if (!arguments.containsKey("filename")) {
                        arguments.put("filename", entry);
                    } else {
                        arguments.put("duplicate", "error");
                    }
                } else {
                    value = entry;
                }
            }
            if (key != null && value != null) {
                if (key.equals("dir") || key.equals("ext")) {
                    arguments.put(key, value);
                } else {
                    arguments.put(key, "error");
                }
                key = null;
                value = null;
            }
        }

        // check final values
        if (key != null)
            arguments.put(key, "error");

        // return dictionary
        return arguments;
    }
}

