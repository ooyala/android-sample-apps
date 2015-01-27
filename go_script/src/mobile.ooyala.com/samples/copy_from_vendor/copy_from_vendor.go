package main
import ol "mobile.ooyala.com/common/log"
import "log"
import "mobile.ooyala.com/common/util"
import . "mobile.ooyala.com/common/path"
import vc "mobile.ooyala.com/samples/config/vendor_config"
import sc "mobile.ooyala.com/samples/config/sample_app_config"


func main() {
	l, err := ol.NewFileAndStdoutLoggerNow(MakeFileAbs("/tmp/android-sample-apps.copy_from_vendor"))
	ol.ColorizedPrintln(l, "CopyFromVendor")
	util.MaybeDie(err, nil)

	rootDir, err := util.ToDirAbs(MakeDirRel("."))
	util.MaybeDie(err, l)

	//Make configs
	vendorConfig := vc.MakeConfig("Android", rootDir, l);
	sampleAppConfig := sc.MakeConfig("Android", rootDir, l);

    copyOoyalaCoreSDKFromVendor(sampleAppConfig, vendorConfig, l)
    copyOoyalaFreewheelSDKFromVendor(sampleAppConfig, vendorConfig, l)
    copyOoyalaIMASDKFromVendor(sampleAppConfig, vendorConfig, l)
}

func copyOoyalaCoreSDKFromVendor(sampleAppConfig sc.Config, vendorConfig vc.Config, l *log.Logger) {
	ol.ColorizedMethodPrintln(l)
	copyLibrariesFromVendor(vendorConfig.OoyalaCoreSDKFilePaths, sampleAppConfig.AllSampleAppsPaths, l)
}

func copyOoyalaFreewheelSDKFromVendor(sampleAppConfig sc.Config, vendorConfig vc.Config, l *log.Logger) {
	ol.ColorizedMethodPrintln(l)
	copyLibrariesFromVendor(vendorConfig.FreewheelSDKFilePaths, sampleAppConfig.FreewheelEnabledSampleAppsPaths, l)
}

func copyOoyalaIMASDKFromVendor(sampleAppConfig sc.Config, vendorConfig vc.Config, l *log.Logger) {
	ol.ColorizedMethodPrintln(l)
	copyLibrariesFromVendor(vendorConfig.IMASDKFilePaths, sampleAppConfig.IMAEnabledSampleAppPaths, l)
}

func copyLibrariesFromVendor(libraryPaths []Pather, sampleAppPaths []DirAbs, l *log.Logger) {
	for _, appPath := range sampleAppPaths {
		appLibsDir := MakeDirAbs(Join(appPath, MakeDirRel("app/libs")))
		util.EnsurePath(appLibsDir, l)

		for _, libraryPath := range libraryPaths {
			util.RequirePath(libraryPath, l)
			util.CopyPath(libraryPath, appLibsDir, l)
		} 
	}
}