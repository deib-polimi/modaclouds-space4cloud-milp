# Readme
This is an adaptation of the project originally developed by **Alexander Lavrentev** and then adapted by [**Riccardo B. Desantis**](mailto:effetti@gmail.com) to work with the [Space4Cloud](http://www.modaclouds.eu/software/space4cloud/) project.

## Usage
To use this tool, you can import it via maven adding this dependency:

* Group Id: it.polimi.modaclouds.space4cloud
* Artifact Id: milp
* Version: 0.1.2
* Type: jar
* Scope: compile.

You must add then the reference to the downloaded jar to the manifest of the project. Then, you can simply use the class `Solver` to perform the evaluation. Here is an example:

```java
import it.polimi.modaclouds.space4clouds.milp.Solver;
public class Example {
    public static void main(String[] args) {
        String projectPath       = "C:\\Users\\Riccardo\\Desktop\\SPACE4CLOUD\\runtime-New_configuration\\OfBiz-bis\\";
        String workingDirectory  = "space4cloud\\";
        String constraintFile    = projectPath + "OfBiz-Constraint.xml";
        String usageModelExtFile = projectPath + "ume-1000.xml";

        Solver s = new Solver(projectPath, workingDirectory, constraintFile, usageModelExtFile);

        // If we want to restrict the research to only some of the providers:
        s.setProviders("Amazon", "Microsoft");

        // We then ask for the files resulting from the computation
        File resourceEnvExt = s.getResourceModelExt();
        File solution = s.getSolution();
        File multiCloudExt = s.getMultiCloudExt();
    }
}
```
