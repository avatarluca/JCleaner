# JCleaner
A Java Clean-Code analyse tool.<br>
There are three categories which get tested: Coupling, Cohesion and Clean-Code.<br>
In each category there are different settings to give you fine graind controll over the tests.
> Last Update: 2023-07-01

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
There is also "test-project" folder with different beginner projects to try out.<br>

### 4️⃣ Results
After selecting a folder the program starts analysing.<br>
The Resultwindow will tell you in which class and which position it found code not beeing within it's parameters.<br>

### 5️⃣ Further documentation
If further infos are needed (especially to decipher the result categories) there is a "docs" tab in the application.<br>
There all the different test are documented.<br>

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
