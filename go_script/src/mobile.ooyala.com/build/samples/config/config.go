package config

import "log"
import "strings"
import "mobile.ooyala.com/build/common/util"
import . "mobile.ooyala.com/build/common/path"

/**
 * This config file represents folders to access sample apps and vendor files.
 * Consistent between Android and iOS sample app
 */
type Config struct {
	RootPath         DirAbs

	CompleteSampleAppName DirName
	CompleteSampleAppPath DirAbs

	VendorFreewheelFolderPath DirAbs
	VendorIMAFolderPath DirAbs

	VendorOoyalaFreewheelFolderPath DirAbs
	VendorOoyalaIMAFolderPath DirAbs
	VendorOoyalaCoreFolderPath DirAbs

	MergableSampleAppPaths []DirAbs
}

func MakeConfig(platformName string, rootPath DirAbs, logger *log.Logger) Config {
	vendorDirName := MakeDirName("vendor")
	vendorPath := MakeDirAbs(Join(rootPath, vendorDirName))

	//Names of all folders in vendor
	freewheelDirName  := MakeDirName("Freewheel")
	googleDirName     := MakeDirName("Google")
	ooyalaDirName     := MakeDirName("ooyala")
	ooyalaIMADirName  := MakeDirName("OoyalaIMASDK-" + platformName)
	ooyalaFWDirName   := MakeDirName("OoyalaFreewheelSDK-" + platformName)
	ooyalaCoreDirName := MakeDirName("OoyalaSDK-" + platformName)

	completeSampleAppName := MakeDirName("CompleteSampleApp")

	// Get All Sample Apps in Repo
    appNamesListString, err := util.RunBashCommandInDir(rootPath, "ls -d *SampleApp", logger)
	util.MaybeDie(err, logger)

    appNamesDirSlice := make ([]DirAbs, 0)
    for _, element := range strings.Split(appNamesListString, "\n") {
    	// Do not include the CompleteSampleApp
    	if (element != completeSampleAppName.String()) {
    		appNamesDirSlice = append(appNamesDirSlice, MakeDirAbs(Join(rootPath, MakeDirName(element))))
    	}
    }

	c := Config{
		RootPath: rootPath,

		CompleteSampleAppName:           completeSampleAppName,
		CompleteSampleAppPath:           MakeDirAbs(Join(rootPath, completeSampleAppName)),

		VendorFreewheelFolderPath:       MakeDirAbs(Join(vendorPath, freewheelDirName)),
		VendorIMAFolderPath:             MakeDirAbs(Join(vendorPath, googleDirName)),
		VendorOoyalaFreewheelFolderPath: MakeDirAbs(Join(vendorPath, ooyalaDirName, ooyalaFWDirName)),
		VendorOoyalaIMAFolderPath:       MakeDirAbs(Join(vendorPath, ooyalaDirName, ooyalaIMADirName)),
		VendorOoyalaCoreFolderPath:      MakeDirAbs(Join(vendorPath, ooyalaDirName, ooyalaCoreDirName)),

		MergableSampleAppPaths:          appNamesDirSlice,

	}
	util.RequireFullStructOrDie(c, logger)
	return c
}
