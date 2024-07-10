# JCleaner
A Java Clean-Code analyse tool.<br>
There are three categories which get tested: Coupling, Cohesion and Clean-Code.<br>
In each category there are different settings to give you fine graind controll over the tests.
> Last Update: 2023-07-01
## Team ("Die müden Cowboys")
🧑‍💻 Alexander Kolenaty<br/>
🧑‍💻 Gabriel Gillmann<br/>
🧑‍💻 Luca Marceca<br/>
🧑‍💻 Tobias Hefti

## Getting Started
> There is sample code for testing under "test project".
> Since our main users are Java beginners according to the project outline, we have compiled 3 initial Java projects: Bank, Kaffee and Tic-Tac-Toe.
> The folder user code contains further testable code, which intentionally has errors.

### 1️⃣ Start the Application
Run the following command in the main project folder: 
```gradle run```   

### 2️⃣ Configure the settings
After starting the app you can adjust three setting types: Coupling, Cohesion and Clean Code. This settings can be enabled/disabled throw a tick box or inserting text. the settings are set to a suited default value.<br>
All changes made to the settings get saved in a config file.

| Coupling Setting's | Description                     |
|-------------|----------------------------------|
| Instantiation Check 1      | Check if there is bidirectional dependency between classes|
| Instantiation Check 2      | Check if each class is only instantiated in one field|
| Access Check 1             | Check public methods|
| Access Check 2             | Check public fields|
| Access Check 3             | Check public inner classes|


| Cohesion Setting's | Description                     |
|-------------|----------------------------------|
| Parameter Check                   | Adjust the maximal amount of parameter and the feedback type|
| Fieldtypes Check 1                | Adjust the maximal amount of different types|
| Fieldtypes Check 2                | Allow primitive/wrapper|
| Codesegment complexity check 1    | Adjust the maximal nested keywords|
| Codesegment complexity check 2    | Change the checked keywords|
| Method constructor length check 1 | Adjust the check type|
| Method constructor length check 2 | Adjust maximal method length|
| Method constructor length check 3 | Adjust maximal constructor length|

| Clean code Setting's | Description                     |
|-------------|----------------------------------|
| Naming check      | Insert Regex for naming check|

### 3️⃣ Open a Folder
Go to Scanner and open a folder with files you would like to analyse.<br>
In this version there are still different limitations to what project can be analysed. See the Limitations section for more info.<br>
There is also "test-project" folder with different project to try out.<br>
The project "user_code" is from us to try out all the different function and error catcher.<br>
All other files are from a first semester coding examples from the programming lecture.<br>

### 4️⃣ Results
After selecting a folder the program starts analysing.<br>
The Resultwindow will tell you in which class and which position it found code not beeing within it's parameters.<br>

### 5️⃣ Further documentation
If further infos are needed (especially to decipher the result categories) there is a "docs" tab in the application.<br>
There all the different test are documented.<br>

## Feature description
## Implemented functional features
Accessing .class files was implemented with java.util.reflect and with the extended Java Reflection standard library. 
But since logical elements in method bodies etc. are not stored in a class file we created small parsers which can read and extract code bodies using adapted pushdown automatons. An alternative would have been parse trees (to define each code segment). But since we only need a small part of the parsing with our analysis tool, this was only partially implemented as described above.

## Ideas for future releases
1️⃣ <b>"Checks for different packages at the same time."</b>
- Description: Currently our testing tool can only test one package at a time.
- Priority: High

2️⃣ <b>"Update for: Dependence on object instance variables."</b>
- Description: Next to instantiation in fields check we can check also if the class is local instantiated in method, static blocks or constructors.
- Priority: High

3️⃣ <b>"Compilation of java files in runtime"</b>
- Description: Now the user has to compile the java files to get .class files for testing. This could be done with the Java compiler class.
- Priority: High

4️⃣ <b>"Setter if statement check."</b>
- Description: If a setter exists, the app should check if there is an if block with an ejected error message exists.
- Priority: Medium

5️⃣ <b>"Check code duplication of code scopes / code indentations."</b>
- Description: If a codescope occurs more than once, that is code duplication.
- Priority: Medium

6️⃣ <b>"Update for: Access modifiers check (securing encapsulation)."</b>
- Description: Currently if there is a local or anonymous class inside a method it gets parsed as a statementblock in this method an handled as a method block.
- Priority: Medium

7️⃣ <b>"Update for: Complexity of a code block."</b>
- Description: Complexity of functional cohesion: How deep the different function calls are allowed to go.
- Priority: Medium

8️⃣ <b>"Update for: Complexity of a code block."</b>
- Description: Calculate timecomplexity (for example: c nested linear "for" keywords => O(n^c)).
- Priority: Medium

9️⃣ <b>"Javadoc check."</b>
- Description: For documentation purposes, Javadocs should be available for all code elements.
- Priority: Low

## Limitations
Limitations to what java projects can be analysed:
- The Project can be split in multiple java files but can't use packages
- There can't be any extern librarys used in the project
- The java files have to be all in the same directory
- The files need to be precompiled to class files with `javac` version 17
- The class files must be in the same directory as the java files
(In future realeases those limitations will be solved)


# UML: Classdiagram
![UML.svg](UML.svg)

## Class Structure and reasoning behind it

The class structure follows the Model-View-Controller (MVC) architectural pattern. This pattern is widely used in software development because it promotes a clear separation of concerns between the components of an application, making it easier to maintain and extend the codebase.

In this specific case, the App.java class would be responsible for starting the application, and it would instantiate the MainWindow and the SplashWindowController. The MainWindow.java class is responsible for the user interface and would interact with the MainWindowController to update its state and respond to user events. Similarly, the SplashWindowController would control the behavior of the splash screen.

The model package contains the core logic of the application. The analyzer subpackage contains classes responsible for analyzing the code. It has several subclasses such as CleanCodeDetector, CohesionDetector and CouplingDetector which all detect different code issues. The boot subpackage contains a Boot class responsible for the boot animation, and the db subpackage contains classes for handling the local database, including SettingsPropertiesHandler and SettingsDataSpecification. The exception subpackage defines custom exception classes, such as FalseCheckedKeywordFormat and TooManyPackagesException, which are used to handle specific error scenarios. The io subpackage contains classes responsible for loading projects. Finally, the util subpackage has various utility classes that support the other parts of the model.

The LogConfiguration class is responsible for logging application events, providing better insight into the application's behavior during runtime.

Where possible abstract classes where used. This makes additions in the future easier. An example for this would be the Detector. If needed new Detector classes can added for deeper analysis.

# Testing

We have tested the analysis models (detectors) using JUnit. For this purpose we have created the class DetectorTest.java. 

Since our analysis program loads Java classes into the program only at runtime, it would be difficult to test using mock testing. 
For future features, which interact with external APIs, Mockito would be ideal, but for the moment JUnit testing is sufficient.

The test classes are predefined java classes, which were specially adapted to the individual tests. The test cases are therefore based on predefined results that the individual methods should return.
