package config

import "log"
import "mobile.ooyala.com/build/common/util"
import . "mobile.ooyala.com/build/common/path"

/**
 * This config file represents folders to access vendor files.
 * Consistent between Android and iOS sample app
 */
type VendorConfig struct {
	RootPath         DirAbs

	VendorFreewheelFolderPath DirAbs
	VendorIMAFolderPath DirAbs

	VendorOoyalaRootFolderPath DirAbs
	VendorOoyalaFreewheelFolderPath DirAbs
	VendorOoyalaIMAFolderPath DirAbs
	VendorOoyalaCoreFolderPath DirAbs

	CoreSDKFileNameStr      string
	FreewheelSDKFileNameStr string
	IMASDKFileNameStr       string

	IMASDKCandidateURL       string
	FreewheelSDKCandidateURL string
	CoreSDKCandidateURL      string
	CoreSDKURL      string
	FreewheelSDKURL string
	IMASDKURL       string
}



func MakeVendorConfig(platformName string, rootPath DirAbs, logger *log.Logger) VendorConfig {
	vendorDirName := MakeDirName("vendor")
	vendorPath := MakeDirAbs(Join(rootPath, vendorDirName))

	//Names of all folders in vendor
	freewheelDirName  := MakeDirName("Freewheel")
	googleDirName     := MakeDirName("Google")
	ooyalaDirName     := MakeDirName("Ooyala")
	ooyalaIMADirName  := MakeDirName("OoyalaIMASDK-" + platformName)
	ooyalaFWDirName   := MakeDirName("OoyalaFreewheelSDK-" + platformName)
	ooyalaCoreDirName := MakeDirName("OoyalaSDK-" + platformName)


	c := VendorConfig{
		RootPath: rootPath,

		VendorFreewheelFolderPath:       MakeDirAbs(Join(vendorPath, freewheelDirName)),
		VendorIMAFolderPath:             MakeDirAbs(Join(vendorPath, googleDirName)),

		VendorOoyalaRootFolderPath:      MakeDirAbs(Join(vendorPath, ooyalaDirName)),	
		VendorOoyalaFreewheelFolderPath: MakeDirAbs(Join(vendorPath, ooyalaDirName, ooyalaFWDirName)),
		VendorOoyalaIMAFolderPath:       MakeDirAbs(Join(vendorPath, ooyalaDirName, ooyalaIMADirName)),
		VendorOoyalaCoreFolderPath:      MakeDirAbs(Join(vendorPath, ooyalaDirName, ooyalaCoreDirName)),

		CoreSDKFileNameStr:              ooyalaCoreDirName.S + ".zip",
		FreewheelSDKFileNameStr:         ooyalaFWDirName.S + ".zip",
		IMASDKFileNameStr:               ooyalaIMADirName.S + ".zip",

		IMASDKCandidateURL:              "https://ooyala.box.com/shared/static/rludn8jljngyph7t3ukp.zip",
		FreewheelSDKCandidateURL:        "https://ooyala.box.com/shared/static/cmbyzhg8gxh3mqhaiv5c.zip",
		CoreSDKCandidateURL:             "https://ooyala.box.com/shared/static/inodnnnxaq3fwnzhid44.zip",

		CoreSDKURL:                      "https://ooyala.box.com/shared/static/90wup42cbi7ywel2all2.zip",
		FreewheelSDKURL:                 "https://ooyala.box.com/shared/static/i17ps4vjmne3bsnnc9sz.zip",
		IMASDKURL:                       "https://ooyala.box.com/shared/static/j1189d1o59t3sdaony7l.zip",

	}
	util.RequireFullStructOrDie(c, logger)
	return c
}
