package main
import ol "mobile.ooyala.com/build/common/log"
import "log"
import "mobile.ooyala.com/build/common/util"
import . "mobile.ooyala.com/build/common/path"
import c "mobile.ooyala.com/build/samples/config"


func main() {
	l, err := ol.NewFileAndStdoutLoggerNow(MakeFileAbs("/tmp/ios-build-log"))
	util.MaybeDie(err, nil)

	rootDir, err := util.ToDirAbs(MakeDirRel("."))
	util.MaybeDie(err, l)

	config := c.MakeConfig("Android", rootDir, l);

	removeOldOoyalaVendorFolders(config, l)

	downloadNewRCPackages(config, l)

	unzipNewRCPackages(config, l)

	removeZipFiles(config, l)

	//TEMPORARY: Remove all sample apps provided in the packages, until the sample apps are no longer included
	util.DeletePath(MakeFileAbs(Join(config.VendorOoyalaCoreFolderPath, MakeFileName("SampleApps"))), l);
	util.DeletePath(MakeFileAbs(Join(config.VendorOoyalaCoreFolderPath, MakeFileName("Documentation"))), l);
	util.DeletePath(MakeFileAbs(Join(config.VendorOoyalaCoreFolderPath, MakeFileName("DefaultControlsSource"))), l);
	util.DeletePath(MakeFileAbs(Join(config.VendorOoyalaFreewheelFolderPath, MakeFileName("FreewheelSampleApp"))), l);
	util.DeletePath(MakeFileAbs(Join(config.VendorOoyalaIMAFolderPath,MakeFileName("IMASampleApp"))), l);
}

func removeOldOoyalaVendorFolders(config c.Config, l *log.Logger) {
	l.Println("get_latest_rc.removeOldOoyalaVendorFolders")
		util.DeletePath(config.VendorOoyalaFreewheelFolderPath, l);
		util.DeletePath(config.VendorOoyalaCoreFolderPath, l);
		util.DeletePath(config.VendorOoyalaIMAFolderPath, l);
}

func downloadNewRCPackages(config c.Config, l *log.Logger) {
	l.Println("get_latest_rc.downloadNewRCPackages")
	util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "wget '" + config.CoreSDKURL + "' -O " + config.CoreSDKFileNameStr, l)
	util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "wget '" + config.FreewheelSDKURL + "' -O " + config.FreewheelSDKFileNameStr, l)
	util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "wget '" + config.IMASDKURL + "' -O " + config.IMASDKFileNameStr, l)
}


func unzipNewRCPackages(config c.Config, l *log.Logger) {
	l.Println("get_latest_rc.unzipNewRCPackages")
	util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "unzip " + config.CoreSDKFileNameStr, l)
	util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "unzip " + config.FreewheelSDKFileNameStr, l)
	util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "unzip " + config.IMASDKFileNameStr, l)
}

func removeZipFiles(config c.Config, l *log.Logger) {
	l.Println("get_latest_rc.removeZipFiles")
	util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "rm " + config.CoreSDKFileNameStr, l)
	util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "rm " + config.FreewheelSDKFileNameStr, l)
	util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "rm " + config.IMASDKFileNameStr, l)
}