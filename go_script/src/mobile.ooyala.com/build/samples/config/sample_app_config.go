package config

import "log"
import "strings"
import "mobile.ooyala.com/build/common/util"
import . "mobile.ooyala.com/build/common/path"

/**
 * This config file represents folders to access sample apps.
 * Consistent between Android and iOS sample app
 */
type SampleAppConfig struct {
	RootPath         DirAbs

	CompleteSampleAppName DirName
	CompleteSampleAppPath DirAbs


	MergableSampleAppPaths          []DirAbs
	AllSampleAppsPaths              []DirAbs
	FreewheelEnabledSampleAppsPaths []DirAbs
	IMAEnabledSampleAppPaths        []DirAbs


}

func MakeSampleAppConfig(platformName string, rootPath DirAbs, logger *log.Logger) SampleAppConfig {

	completeSampleAppName := MakeDirName("CompleteSampleApp")
	freewheelSampleAppName := MakeDirName("FreewheelSampleApp")
	imaSampleAppName := MakeDirName("IMASampleApp")
	completeSampleAppPath := MakeDirAbs(Join(rootPath, completeSampleAppName))
	freewheelSampleAppPath := MakeDirAbs(Join(rootPath, freewheelSampleAppName))
	imaSampleAppPath := MakeDirAbs(Join(rootPath, imaSampleAppName))


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

	c := SampleAppConfig{
		RootPath: rootPath,

		CompleteSampleAppName:           completeSampleAppName,
		CompleteSampleAppPath:           completeSampleAppPath,

		MergableSampleAppPaths:          appNamesDirSlice,
		AllSampleAppsPaths:              append(appNamesDirSlice, completeSampleAppPath),
		
		FreewheelEnabledSampleAppsPaths: []DirAbs{freewheelSampleAppPath, completeSampleAppPath},
		IMAEnabledSampleAppPaths:        []DirAbs{imaSampleAppPath, completeSampleAppPath},

	}
	util.RequireFullStructOrDie(c, logger)
	return c
}
