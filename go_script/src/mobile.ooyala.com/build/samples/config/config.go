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

	VendorOoyalaRootFolderPath DirAbs
	VendorOoyalaFreewheelFolderPath DirAbs
	VendorOoyalaIMAFolderPath DirAbs
	VendorOoyalaCoreFolderPath DirAbs

	MergableSampleAppPaths []DirAbs
	AllSampleAppsPaths     []DirAbs


	CoreSDKFileNameStr      string
	FreewheelSDKFileNameStr string
	IMASDKFileNameStr       string

	CoreSDKURL      string
	FreewheelSDKURL string
	IMASDKURL       string
}

func MakeConfig(platformName string, rootPath DirAbs, logger *log.Logger) Config {
	vendorDirName := MakeDirName("vendor")
	vendorPath := MakeDirAbs(Join(rootPath, vendorDirName))

	//Names of all folders in vendor
	freewheelDirName  := MakeDirName("Freewheel")
	googleDirName     := MakeDirName("Google")
	ooyalaDirName     := MakeDirName("Ooyala")
	ooyalaIMADirName  := MakeDirName("OoyalaIMASDK-" + platformName)
	ooyalaFWDirName   := MakeDirName("OoyalaFreewheelSDK-" + platformName)
	ooyalaCoreDirName := MakeDirName("OoyalaSDK-" + platformName)

	completeSampleAppName := MakeDirName("CompleteSampleApp")
	completeSampleAppPath := MakeDirAbs(Join(rootPath, completeSampleAppName))
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
		CompleteSampleAppPath:           completeSampleAppPath,

		VendorFreewheelFolderPath:       MakeDirAbs(Join(vendorPath, freewheelDirName)),
		VendorIMAFolderPath:             MakeDirAbs(Join(vendorPath, googleDirName)),

		VendorOoyalaRootFolderPath:      MakeDirAbs(Join(vendorPath, ooyalaDirName)),	
		VendorOoyalaFreewheelFolderPath: MakeDirAbs(Join(vendorPath, ooyalaDirName, ooyalaFWDirName)),
		VendorOoyalaIMAFolderPath:       MakeDirAbs(Join(vendorPath, ooyalaDirName, ooyalaIMADirName)),
		VendorOoyalaCoreFolderPath:      MakeDirAbs(Join(vendorPath, ooyalaDirName, ooyalaCoreDirName)),

		MergableSampleAppPaths:          appNamesDirSlice,
		AllSampleAppsPaths:              append(appNamesDirSlice, completeSampleAppPath),

		CoreSDKFileNameStr:              ooyalaCoreDirName.S + ".zip",
		FreewheelSDKFileNameStr:         ooyalaFWDirName.S + ".zip",
		IMASDKFileNameStr:               ooyalaIMADirName.S + ".zip",

		CoreSDKURL:                      "https://ooyala.box.com/shared/static/90wup42cbi7ywel2all2.zip",
		FreewheelSDKURL:                 "https://ooyala.box.com/shared/static/i17ps4vjmne3bsnnc9sz.zip",
		IMASDKURL:                       "https://ooyala.box.com/shared/static/j1189d1o59t3sdaony7l.zip",

	}
	util.RequireFullStructOrDie(c, logger)
	return c
}
