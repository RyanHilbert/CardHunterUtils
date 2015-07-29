# CardHunterUtils
Deck manager and party builder utility for Card Hunter by Blue Manchu

# Developer Setup

## For Windows:
Follow the steps in [this guide](http://javapapo.blogspot.com/2013/03/setup-your-java-development-environment.html) to get a Java development environment.
    
### Note: 
One small gotcha is that the Maven cmd, `mvn`, gets unhappy if the path you are running from has no spaces or is a drive root.
    
If you're planning to work in such a path as your dev folder, work around this by modifying the `mvn.cmd` file in `%MAVEN_HOME%\bin\mvn.cmd` as follows:
    
1. Find the line that starts with

    `%MAVEN_JAVA_EXE% %JVM_CONFIG_MAVEN_PROPS% %MAVEN_OPTS%`
    
2.    Remove the quotes around

    `"-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%"`
    
    So that it becomes
    
    `-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%`
    
## For Mac OSX:
Install the latest [Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
	
1. Set your `JAVA_HOME` variable in your shell's rc file (e.g. `.zshrc`):

	`export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home`
	
	(Note your Java SDK version may be different)
	
2. Install ant and maven via brew:

	`brew install ant`

	`brew install maven`
	
	(you may need to `brew update` before you do these)
	
3. Set your `MAVEN_HOME` variable in your rc file as well:

	`export MAVEN_HOME=/usr/local/Cellar/maven/3.3.3`

(If you didn't install ant via homebrew, you will also need to set your `ANT_HOME` variable to wherever your manual ant install lives.  For whatever reason, homebrew doesn't set it, because [homebrew doesn't like that](https://github.com/Homebrew/homebrew/issues/32851).

## Confirming Environment
You should now be able to run the following:

`echo %JAVA_HOME%` (Win) or `echo $JAVA_HOME` (OSX)

`ant -version`

`mvn -version`

and see happy responses.  