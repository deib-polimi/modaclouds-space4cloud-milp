package it.polimi.modaclouds.space4clouds.milp;

import java.nio.file.Paths;



public class MainTest {

	public static void main(String[] args) {
		String projectPath = "C:\\Users\\Riccardo\\Desktop\\SPACE4CLOUD\\runtime-New_configuration\\OfBiz-bis\\";
		String workingDirectory = "space4cloud\\";
		String constraintFile    = projectPath + "OfBiz-Constraint.xml";
		String usageModelExtFile = projectPath + "ume-1000.xml";

		Solver s = new Solver(projectPath, workingDirectory, constraintFile, usageModelExtFile);
		
		s.setProviders("Amazon", "Microsoft");
//		s.setProviders("Amazon");
//		s.setProviders("Flexiscale");
//		s.setRegions("us-east");
//		s.setDeleteTempFiles(false);
		s.getOptions().SqlDBUrl="jdbc:mysql://localhost:3306/";
		s.getOptions().DBName = "cloud_full";
		s.getOptions().DBUserName="moda";
		s.getOptions().DBPassword="modaclouds";
		s.setStartingSolution(Paths.get(projectPath, "solution-amamicr-1000-2.xml").toFile());
//		s.setStartingSolution(Paths.get(projectPath, "solution-amazon-1000-2.xml").toFile());
		
		System.out.println(s.getResourceModelExt().getAbsolutePath());
		System.out.println(s.getSolution().getAbsolutePath());
		System.out.println(s.getMultiCloudExt().getAbsolutePath());
	}
	
}
