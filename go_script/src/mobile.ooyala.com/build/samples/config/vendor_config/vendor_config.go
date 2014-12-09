package vendor_config

import "log"
import "mobile.ooyala.com/build/common/util"
import . "mobile.ooyala.com/build/common/path"

/**
 * This config file represents folders to access vendor files.
 * Consistent between Android and iOS sample app
 */
type Config struct {
	RootPath         DirAbs

	VendorFreewheelFolderPath DirAbs
	VendorIMAFolderPath DirAbs

	VendorOoyalaRootFolderPath DirAbs
	VendorOoyalaFreewheelFolderPath DirAbs
	VendorOoyalaIMAFolderPath DirAbs
	VendorOoyalaCoreFolderPath DirAbs
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

	c := Config {
		RootPath: rootPath,

		VendorFreewheelFolderPath:       MakeDirAbs(Join(vendorPath, freewheelDirName)),
		VendorIMAFolderPath:             MakeDirAbs(Join(vendorPath, googleDirName)),

		VendorOoyalaRootFolderPath:      MakeDirAbs(Join(vendorPath, ooyalaDirName)),	
		VendorOoyalaFreewheelFolderPath: MakeDirAbs(Join(vendorPath, ooyalaDirName, ooyalaFWDirName)),
		VendorOoyalaIMAFolderPath:       MakeDirAbs(Join(vendorPath, ooyalaDirName, ooyalaIMADirName)),
		VendorOoyalaCoreFolderPath:      MakeDirAbs(Join(vendorPath, ooyalaDirName, ooyalaCoreDirName)),
	}
	util.RequireFullStructOrDie(c, logger)
	return c
}
