package main
import ol "mobile.ooyala.com/build/common/log"
import "mobile.ooyala.com/build/common/util"
import . "mobile.ooyala.com/build/common/path"
import c "mobile.ooyala.com/build/samples/config"


func main() {
	l, err := ol.NewFileAndStdoutLoggerNow(MakeFileAbs("/tmp/ios-build-log"))
	util.MaybeDie(err, nil)

	rootDir, err := util.ToDirAbs(MakeDirRel("."))
	util.MaybeDie(err, l)

	config := c.MakeConfig("Android", rootDir, l);

    for _,element := range config.MergableSampleAppPaths {
    	l.Println(element.String())
    }
}
