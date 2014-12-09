package main
import ol "mobile.ooyala.com/build/common/log"
import "log"
import "mobile.ooyala.com/build/common/util"
import . "mobile.ooyala.com/build/common/path"
import sc "mobile.ooyala.com/build/samples/config/sample_app_config"


func main() {
	l, err := ol.NewFileAndStdoutLoggerNow(MakeFileAbs("/tmp/ios-build-log"))
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
    removeCompleteCode(config.CompleteSampleAppPath, l)
}

func removeLibraries (sampleAppPaths []DirAbs, l *log.Logger) {
	l.Println("clean_sample_apps.removeLibraries")
	for _, element := range sampleAppPaths {
		util.DeletePath(MakeDirAbs(Join(element, MakeDirName("libs"))), l);
		util.EnsurePath(MakeDirAbs(Join(element, MakeDirName("libs"))), l);
	}
}

func removeCompleteCode (completeSampleAppPath DirAbs, l *log.Logger) {
	l.Println("clean_sample_apps.removeCompleteCode")
	samplePackageRelPath  := MakePath("src/com/ooyala/sample")

	util.DeletePath(MakeDirAbs(Join(completeSampleAppPath, samplePackageRelPath, MakeDirName("players"))), l);
	util.EnsurePath(MakeDirAbs(Join(completeSampleAppPath, samplePackageRelPath, MakeDirName("players"))), l);

	util.DeletePath(MakeDirAbs(Join(completeSampleAppPath, samplePackageRelPath, MakeDirName("lists"))), l);
	util.EnsurePath(MakeDirAbs(Join(completeSampleAppPath, samplePackageRelPath, MakeDirName("lists"))), l);
}