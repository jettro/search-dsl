# Welcome
This repository contains sample code that helped me get a grasp of what you can do with Antlr. I set out to create my own search DSL. In a number of steps I enhance the DSL. The intention is to get a grasp of my options and get experience in a way to help my customers create their own search DSL. I hope you tag along and learn with me. Tips for improvement are of course also welcome.

# Running the project
The project is created using maven. You can use an IDE to import the project, but you can also run everything with maven. If you want to use maven you need a few steps.

- First build the project: *mvn clean install*
- Second copy the dependencies into a folder: *mvn dependency:copy-dependencies*
- Third run one of the examples: 
```
java -classpath "target/search-dsl-1.0-SNAPSHOT.jar:target/dependency/*"  nl.gridshore.searchdsl.RunStep1
```
# What is in the project
The project contains a number of steps. Each step becomes a bit more complicated. The end result is in Searchdsl.g4, CommonLexerRules.g4, RunJacksonVisitor and RunJsonVisitor.

The final classes JsonQueryVisitor and JacksonQueryVisitor are also tested using junit and json-path from jayway. Some example queries that we support:



# References

https://tomassetti.me/antlr-mega-tutorial/#java-setup

# Blogs

- Step 1-4: https://www.luminis.eu/blog/search-en/creating-a-search-dsl
- Step 5 - Visitor Pattern: https://www.luminis.eu/blog/part-2-of-creating-a-search-dsl/
