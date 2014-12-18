package main
import ol "mobile.ooyala.com/build/common/log"
import "log"
import "mobile.ooyala.com/build/common/util"
import . "mobile.ooyala.com/build/common/path"
import sc "mobile.ooyala.com/build/samples/config/sample_app_config"


func main() {
	l, err := ol.NewFileAndStdoutLoggerNow(MakeFileAbs("/tmp/android-sample-apps.m"))
	util.MaybeDie(err, nil)

	rootDir, err := util.ToDirAbs(MakeDirRel("."))
	util.MaybeDie(err, l)

	//Make configs
	config := sc.MakeConfig("Android", rootDir, l);

	l.Println("We found the following apps:")
    for _, element := range config.MergableSampleAppPaths {
    	l.Println(element.String())
    	copyFilesFromSampleApp(element, config, l)

    }
}

func copyFilesFromSampleApp(sampleAppRootDirAbs DirAbs, config sc.Config, l *log.Logger) {
	srcPlayersDirAbs := MakeDirAbs(Join(sampleAppRootDirAbs, config.PlayersPackageDirRel))
	srcListsDirAbs := MakeDirAbs(Join(sampleAppRootDirAbs, config.ListPackageDirRel))
	srcUtilsDirAbs := MakeDirAbs(Join(sampleAppRootDirAbs, config.UtilsPackageDirRel))
	srcLayoutDirAbs := MakeDirAbs(Join(sampleAppRootDirAbs, config.LayoutDirRel))

	dstPlayersDirAbs := MakeDirAbs(Join(config.CompleteSampleAppPath, config.PlayersPackageDirRel))
	dstListsDirAbs := MakeDirAbs(Join(config.CompleteSampleAppPath, config.ListPackageDirRel))
	dstUtilsDirAbs := MakeDirAbs(Join(config.CompleteSampleAppPath, config.UtilsPackageDirRel))
	dstLayoutDirAbs := MakeDirAbs(Join(config.CompleteSampleAppPath, config.LayoutDirRel))

	util.CopyPathContents(srcPlayersDirAbs, dstPlayersDirAbs, l)
	util.CopyPathContents(srcListsDirAbs, dstListsDirAbs, l)
	util.CopyPathContents(srcUtilsDirAbs, dstUtilsDirAbs, l)
	util.CopyPathContents(srcLayoutDirAbs, dstLayoutDirAbs, l)
}