package main
import ol "mobile.ooyala.com/common/log"
import "log"
import "mobile.ooyala.com/common/util"
import . "mobile.ooyala.com/common/path"
import sc "mobile.ooyala.com/samples/config/sample_app_config"


func main() {
	l, err := ol.NewFileAndStdoutLoggerNow(MakeFileAbs("/tmp/android-sample-apps.clean-sample-apps"))
	ol.ColorizedPrintln(l, "CleanSampleApps")
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
	ol.ColorizedMethodPrintln(l)
	for _, element := range sampleAppPaths {
		util.DeletePath(MakeDirAbs(Join(element, MakeDirRel("app/libs"))), l);
		util.EnsurePath(MakeDirAbs(Join(element, MakeDirRel("app/libs"))), l);
	}
}

func removeCompleteCode (config sc.Config, l *log.Logger) {
	ol.ColorizedMethodPrintln(l)

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
