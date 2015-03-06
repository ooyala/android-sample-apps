package main
import ol "mobile.ooyala.com/common/log"
import "log"
import "mobile.ooyala.com/common/util"
import . "mobile.ooyala.com/common/path"
import vc "mobile.ooyala.com/samples/config/vendor_config"
import zc "mobile.ooyala.com/samples/config/zip_config"

func main() {
	l, err := ol.NewFileAndStdoutLoggerNow(MakeFileAbs("/tmp/android-sample-apps.get_latest_rc"))
	ol.ColorizedPrintln(l, "GetLatestRc")
	util.MaybeDie(err, nil)

	rootDir, err := util.ToDirAbs(MakeDirRel("."))
	util.MaybeDie(err, l)

	config := vc.MakeConfig("Android", rootDir, l);
	zipConfig := zc.MakeConfig("Android", rootDir, l);

	removeOldOoyalaVendorFolders(config, l)

	downloadNewRCPackages(config, zipConfig, l)

	unzipNewRCPackages(config, zipConfig, l)

	removeZipFiles(config, zipConfig, l)

	//TEMPORARY: Remove all sample apps provided in the packages, until the sample apps are no longer included
	util.DeletePath(MakeFileAbs(Join(config.VendorOoyalaCoreFolderPath, MakeFileName("SampleApps"))), l);
	util.DeletePath(MakeFileAbs(Join(config.VendorOoyalaCoreFolderPath, MakeFileName("APIDocs"))), l);
	util.DeletePath(MakeFileAbs(Join(config.VendorOoyalaCoreFolderPath, MakeFileName("DefaultControlsSource"))), l);
	util.DeletePath(MakeFileAbs(Join(config.VendorOoyalaFreewheelFolderPath, MakeFileName("FreewheelSampleApp"))), l);
	util.DeletePath(MakeFileAbs(Join(config.VendorOoyalaFreewheelFolderPath, MakeFileName("APIDocs"))), l);
	util.DeletePath(MakeFileAbs(Join(config.VendorOoyalaIMAFolderPath,MakeFileName("IMASampleApp"))), l);
	util.DeletePath(MakeFileAbs(Join(config.VendorOoyalaIMAFolderPath, MakeFileName("APIDocs"))), l);
}

func removeOldOoyalaVendorFolders(config vc.Config, l *log.Logger) {
	ol.ColorizedMethodPrintln(l)
		util.DeletePath(config.VendorOoyalaFreewheelFolderPath, l);
		util.DeletePath(config.VendorOoyalaCoreFolderPath, l);
		util.DeletePath(config.VendorOoyalaIMAFolderPath, l);
}

func downloadNewRCPackages(config vc.Config, zipConfig zc.Config, l *log.Logger) {
	ol.ColorizedMethodPrintln(l)
	_, err := util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "wget '" + zipConfig.CoreSDKCandidateURL + "' -O " + zipConfig.CoreSDKFileName.S, l)
	util.MaybeDie(err, l)

	_, err = util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "wget '" + zipConfig.FreewheelSDKCandidateURL + "' -O " + zipConfig.FreewheelSDKFileName.S, l)
	util.MaybeDie(err, l)

	_, err = util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "wget '" + zipConfig.IMASDKCandidateURL + "' -O " + zipConfig.IMASDKFileName.S, l)
	util.MaybeDie(err, l)
}

func unzipNewRCPackages(config vc.Config, zipConfig zc.Config, l *log.Logger) {
	ol.ColorizedMethodPrintln(l)
	_, err := util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "unzip " + zipConfig.CoreSDKFileName.S, l)
	util.MaybeDie(err, l)

	_, err = util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "unzip " + zipConfig.FreewheelSDKFileName.S, l)
	util.MaybeDie(err, l)

	_, err = util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "unzip " + zipConfig.IMASDKFileName.S, l)
	util.MaybeDie(err, l)
}

func removeZipFiles(config vc.Config, zipConfig zc.Config, l *log.Logger) {
	ol.ColorizedMethodPrintln(l)
	_, err := util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "rm " + zipConfig.CoreSDKFileName.S, l)
	util.MaybeDie(err, l)

	_, err = util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "rm " + zipConfig.FreewheelSDKFileName.S, l)
	util.MaybeDie(err, l)

	_, err = util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "rm " + zipConfig.IMASDKFileName.S, l)
	util.MaybeDie(err, l)
}