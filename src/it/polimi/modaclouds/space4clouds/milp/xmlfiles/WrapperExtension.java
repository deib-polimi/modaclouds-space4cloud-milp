package it.polimi.modaclouds.space4clouds.milp.xmlfiles;

//as it is required to create several extension files (for each provider)
//this class is used as wrapper for all of them
public class WrapperExtension {

	// array of classes, each of which is creator for extension file of its own
	// provider
	public ExtensionXML[] ExtensionsArray = null;
	// amount of providers
	public int countproviders = 0;

	// constructor
	public WrapperExtension(int countprov, String SaveDirect,
			 int counttimesn, int countcontainersn) {
		ExtensionsArray = new ExtensionXML[countprov];
		countproviders = countprov;
		for (int i = 0; i < countproviders; i++) {
			ExtensionsArray[i] = new ExtensionXML(i,
					counttimesn, countcontainersn);
			ExtensionsArray[i].SaveDirectory = SaveDirect;
		}
	}

	// this function prints all extensions
	public void printExtensions() {
		for (int i = 0; i < countproviders; i++)
			ExtensionsArray[i].createExtensions();
	}
}
