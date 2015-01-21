package main
import ol "mobile.ooyala.com/build/common/log"
import "log"
import "mobile.ooyala.com/build/common/util"
import . "mobile.ooyala.com/build/common/path"
import vc "mobile.ooyala.com/build/samples/config/vendor_config"
import zc "mobile.ooyala.com/build/samples/config/zip_config"

func main() {
	l, err := ol.NewFileAndStdoutLoggerNow(MakeFileAbs("/tmp/android-sample-apps.get_latest_rc"))
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
	l.Println("get_latest_rc.removeOldOoyalaVendorFolders")
		util.DeletePath(config.VendorOoyalaFreewheelFolderPath, l);
		util.DeletePath(config.VendorOoyalaCoreFolderPath, l);
		util.DeletePath(config.VendorOoyalaIMAFolderPath, l);
}

func downloadNewRCPackages(config vc.Config, zipConfig zc.Config, l *log.Logger) {
	l.Println("get_latest_rc.downloadNewRCPackages")
	_, err := util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "wget '" + zipConfig.CoreSDKCandidateURL + "' -O " + zipConfig.CoreSDKFileNameStr, l)
	util.MaybeDie(err, l)

	_, err = util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "wget '" + zipConfig.FreewheelSDKCandidateURL + "' -O " + zipConfig.FreewheelSDKFileNameStr, l)
	util.MaybeDie(err, l)

	_, err = util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "wget '" + zipConfig.IMASDKCandidateURL + "' -O " + zipConfig.IMASDKFileNameStr, l)
	util.MaybeDie(err, l)
}


func unzipNewRCPackages(config vc.Config, zipConfig zc.Config, l *log.Logger) {
	l.Println("get_latest_rc.unzipNewRCPackages")
	_, err := util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "unzip " + zipConfig.CoreSDKFileNameStr, l)
	util.MaybeDie(err, l)

	_, err = util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "unzip " + zipConfig.FreewheelSDKFileNameStr, l)
	util.MaybeDie(err, l)

	_, err = util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "unzip " + zipConfig.IMASDKFileNameStr, l)
	util.MaybeDie(err, l)
}

func removeZipFiles(config vc.Config, zipConfig zc.Config, l *log.Logger) {
	l.Println("get_latest_rc.removeZipFiles")
	_, err := util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "rm " + zipConfig.CoreSDKFileNameStr, l)
	util.MaybeDie(err, l)

	_, err = util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "rm " + zipConfig.FreewheelSDKFileNameStr, l)
	util.MaybeDie(err, l)

	_, err = util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "rm " + zipConfig.IMASDKFileNameStr, l)
	util.MaybeDie(err, l)
}