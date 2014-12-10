package main
import ol "mobile.ooyala.com/build/common/log"
import "log"
import "mobile.ooyala.com/build/common/util"
import . "mobile.ooyala.com/build/common/path"
import vc "mobile.ooyala.com/build/samples/config/vendor_config"
import sc "mobile.ooyala.com/build/samples/config/sample_app_config"


func main() {
	l, err := ol.NewFileAndStdoutLoggerNow(MakeFileAbs("/tmp/android-sample-apps.copy_from_vendor"))
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
	l.Println("copy_from_vendor.copyOoyalaCoreSDKFromVendor")
	copyLibrariesFromVendor(vendorConfig.OoyalaCoreSDKFilePaths, sampleAppConfig.AllSampleAppsPaths, l)
}

func copyOoyalaFreewheelSDKFromVendor(sampleAppConfig sc.Config, vendorConfig vc.Config, l *log.Logger) {
	l.Println("copy_from_vendor.copyOoyalaFreewheelSDKFromVendor")
	copyLibrariesFromVendor(vendorConfig.FreewheelSDKFilePaths, sampleAppConfig.FreewheelEnabledSampleAppsPaths, l)
}

func copyOoyalaIMASDKFromVendor(sampleAppConfig sc.Config, vendorConfig vc.Config, l *log.Logger) {
	l.Println("copy_from_vendor.copyOoyalaIMASDKFromVendor")
	copyLibrariesFromVendor(vendorConfig.IMASDKFilePaths, sampleAppConfig.IMAEnabledSampleAppPaths, l)
}

func copyLibrariesFromVendor(libraryPaths []Pather, sampleAppPaths []DirAbs, l *log.Logger) {
	for _, appPath := range sampleAppPaths {
		appLibsDir := MakeDirAbs(Join(appPath, MakeDirName("libs")))
		util.EnsurePath(appLibsDir, l)

		for _, libraryPath := range libraryPaths {
			util.RequirePath(libraryPath, l)
			util.CopyPath(libraryPath, appLibsDir, l)
		} 
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