package main
import ol "mobile.ooyala.com/build/common/log"
import "log"
import "mobile.ooyala.com/build/common/util"
import . "mobile.ooyala.com/build/common/path"
import sc "mobile.ooyala.com/build/samples/config/sample_app_config"


func main() {
	l, err := ol.NewFileAndStdoutLoggerNow(MakeFileAbs("/tmp/android-sample-apps.clean-sample-apps"))
	util.MaybeDie(err, nil)

	rootDir, err := util.ToDirAbs(MakeDirRel("."))
	util.MaybeDie(err, l)

	config := sc.MakeConfig("Android", rootDir, l);

	l.Println("We found the following apps:")
    for _, element := range config.MergableSampleAppPaths {
    	l.Println(element.String())
    }

    removeLibraries (config.AllSampleAppsPaths, l)

    //Remove the code that is duplicated in the CompleteSampleApp
    removeCompleteCode(config, l)
}

func removeLibraries (sampleAppPaths []DirAbs, l *log.Logger) {
	l.Println("clean_sample_apps.removeLibraries")
	for _, element := range sampleAppPaths {
		util.DeletePath(MakeDirAbs(Join(element, MakeDirName("libs"))), l);
		util.EnsurePath(MakeDirAbs(Join(element, MakeDirName("libs"))), l);
	}
}

func removeCompleteCode (config sc.Config, l *log.Logger) {
	l.Println("clean_sample_apps.removeCompleteCode")

	playersDirAbs := MakeDirAbs(Join(config.CompleteSampleAppPath, config.PlayersPackageDirRel))
	listsDirAbs := MakeDirAbs(Join(config.CompleteSampleAppPath, config.ListPackageDirRel))
	utilsDirAbs := MakeDirAbs(Join(config.CompleteSampleAppPath, config.UtilsPackageDirRel))
	layoutDirAbs := MakeDirAbs(Join(config.CompleteSampleAppPath, config.LayoutDirRel))

	util.DeletePath(playersDirAbs, l);
	util.EnsurePath(playersDirAbs, l);

	util.DeletePath(listsDirAbs, l);
	util.EnsurePath(listsDirAbs, l);

	util.DeletePath(utilsDirAbs, l);
	util.EnsurePath(utilsDirAbs, l);

	util.DeletePath(layoutDirAbs, l);
	util.EnsurePath(layoutDirAbs, l);
}
