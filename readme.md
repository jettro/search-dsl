# Welcome
This repository contains sample code that help me get a grasp of what you can do with Antlr. I set out to create my own search DSL. In a number of steps I enhance the DSL. The intention is to get a grasp of my options and get experience in a way to help my customers create their own search DSL. I hope you tag along and learn with me. Tips for improvement are of course also welcome.

# Running the project
The project is created using maven. You can use an IDE to import the project, but you can also run everything with maven. If you want to use maven you need a few steps.

- First build the project: *mvn clean install*
- Second copy the dependencies into a folder: *mvn dependency:copy-dependencies*
- Third run one of the examples: 
```
java -classpath "target/search-dsl-1.0-SNAPSHOT.jar:target/dependency/*"  nl.gridshore.searchdsl.RunStep1
```
# References

https://tomassetti.me/antlr-mega-tutorial/#java-setup

# Blogs

https://amsterdam.luminis.eu/2017/06/28/creating-search-dsl/