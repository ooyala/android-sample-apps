package zip_config

import "log"
import "mobile.ooyala.com/build/common/util"
import . "mobile.ooyala.com/build/common/path"

/**
 * This config file represents folders to access vendor files.
 * Consistent between Android and iOS sample app
 */
type Config struct {
	RootPath         DirAbs

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



func MakeConfig(platformName string, rootPath DirAbs, logger *log.Logger) Config {
	ooyalaIMADirName  := MakeDirName("OoyalaIMASDK-" + platformName)
	ooyalaFWDirName   := MakeDirName("OoyalaFreewheelSDK-" + platformName)
	ooyalaCoreDirName := MakeDirName("OoyalaSDK-" + platformName)


	c := Config {
		RootPath: rootPath,

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
