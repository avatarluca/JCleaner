package team03.mudecowboys.projekt2.jcleaner.model.io;

import team03.mudecowboys.projekt2.jcleaner.model.exception.TooManyPackagesException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * Class to provide java sourcecode and reflection classes in a given {@link #projectDirectory}
 */
public class ProjectLoader {

    private static final Logger logger = Logger.getLogger(ProjectLoader.class.getCanonicalName());
    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
    private static final int DEFAULT_DEPTH = 0;

    private Path projectDirectory;
    private List<Path> javaPaths;

    public ProjectLoader(final Path projectDirectory) throws FileNotFoundException, IllegalArgumentException {
        discoverProjectFiles(projectDirectory);
    }    

    /**
     * Works like {@link ProjectLoader#discoverProjectFiles(Path, int)} except the depth defaults to DEFAULT_DEPTH
     *
     * @param projectDirectory
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     * @see ProjectLoader#discoverProjectFiles(Path, int)
     */
    public void discoverProjectFiles(Path projectDirectory) throws FileNotFoundException, IllegalArgumentException {
        this.discoverProjectFiles(projectDirectory, DEFAULT_DEPTH);
    }

    /**
     * Finds all *.java files in a given projectDirectory
     *
     * @param projectDirectory location of the Project
     * @param maxDepth         Directory depth to search Files recursively
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     */
    public void discoverProjectFiles(Path projectDirectory, int maxDepth)
            throws FileNotFoundException, IllegalArgumentException {
        this.projectDirectory = projectDirectory.toAbsolutePath();

        if (this.projectDirectory.toFile().isDirectory()) {
            if (this.projectDirectory.toFile().exists()) {
                File dir = projectDirectory.toFile();
                File [] files = dir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".java");
                    }
                });
                javaPaths = new ArrayList<>();
                for (File javaFile : files) {
                    javaPaths.add(javaFile.toPath());
                }   
            } else {
                String message = this.projectDirectory.toString() + " does not exist";
                logger.warning(message);
                throw new FileNotFoundException(message);
            }
        } else {
            String message = this.projectDirectory.toString() + " is not a Directory";
            logger.warning(message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Returns the all files found by {@link ProjectLoader#discoverProjectFiles(Path, int)} as a single String.
     *
     * @return Concatenation of all files
     * @throws IOException
     */
    public String getProjectAsString() throws IOException {
        StringBuilder sb = new StringBuilder();

        logger.info("Starting Concatenation of all Java Files in " + projectDirectory);
        for (Path sourceFile : javaPaths) {
            logger.fine("Reading File: " + javaPaths);
            try (BufferedReader reader = Files.newBufferedReader(sourceFile, DEFAULT_ENCODING)) {
                sb.append(reader.lines().collect(Collectors.joining("\n")));
            }
        }
        logger.info("Finished Concatenation of all Java Files in " + projectDirectory);
        return sb.toString();
    }

    /**
     * Find all classes (*.java files) in {@link #projectDirectory}.
     * @return A {@link Set} of {@link Class} containing all scanned classes.
     */
    public Set<Class<?>> getProjectAsClassSet() throws UnsupportedClassVersionError {
        Set<Class<?>> classes = new HashSet<>();

        try (URLClassLoader classLoader = new URLClassLoader(new URL[] {projectDirectory.toFile().toURI().toURL()})) {
            Package[] packages = classLoader.getDefinedPackages();

            if(packages.length > 1) throw new TooManyPackagesException();
            
            for (File file : projectDirectory.toFile().listFiles()) {
                if (file.getName().endsWith(".class")) {
                    String className = file.getName().replace(".class", "");

                    // Check named packages
                    for(Package pack : packages) {
                        try {
                            classes.add(classLoader.loadClass(String.format("%s.%s", pack.getName(), className)));
                        } catch (ClassNotFoundException | NoClassDefFoundError e) {
                            logger.warning(String.format("No class %s in %s with package name %s found.", className, projectDirectory.toAbsolutePath(), pack.getName()));
                        }
                    }

                    // Check unnamed packages
                    try {
                        classes.add(classLoader.loadClass(className));
                    } catch (ClassNotFoundException | NoClassDefFoundError e) {
                        logger.warning(String.format("No class %s in %s with unnamed package name found.", className, projectDirectory.toAbsolutePath()));
                    }
                }
            }
        } catch (IOException | TooManyPackagesException e) {
            logger.severe(String.format("Couldn't create class loader for directory %s.", projectDirectory.toAbsolutePath()));
        }
         
        return classes;
    }

}
