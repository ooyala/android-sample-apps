package main

import "errors"
import gl "log"
import ol "mobile.ooyala.com/common/log"
import args "mobile.ooyala.com/common/args/parse"
import "log"
import "mobile.ooyala.com/common/util"
import . "mobile.ooyala.com/common/path"
import vc "mobile.ooyala.com/samples/config/vendor_config"
import zc "mobile.ooyala.com/samples/config/zip_config"

func run(fn func() error, l *gl.Logger) {
	err := fn()
	if err != nil {
		util.Die(err, l)
	}
}

func loadFlags(l *gl.Logger) (args.Config, error) {
	c := args.MakeConfig(l)
	err := args.ParseArgs(c, l)
	return c, err
}

func main() {
	l, err := ol.NewFileAndStdoutLoggerNow(MakeFileAbs("/tmp/android-sample-apps.update_from_target_location"))
	ol.ColorizedPrintln(l, "android update from target location")
	util.MaybeDie(err, nil)

	rootDir, err := util.ToDirAbs(MakeDirRel("."))
	util.MaybeDie(err, l)

	config := vc.MakeConfig("Android", rootDir, l);
	zipConfig := zc.MakeConfig("Android", rootDir, l);

	var configArgs args.Config
	run( func() error { var err error; configArgs, err = loadFlags(l); return err }, l)

	sdkFolderPath := *configArgs.Path

	if sdkFolderPath == "" {
 		util.Die(errors.New("ERROR: no path passed in"), l)
 	}
 
	checkSdkExist(sdkFolderPath, zipConfig, l)

	removeOldOoyalaVendorFolders(config, l)

	copyFromTargetFolders(config, zipConfig, l, sdkFolderPath)

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

func copyFromTargetFolders(config vc.Config, zipConfig zc.Config, l *log.Logger, sdkFolderPathString string) {
	ol.ColorizedMethodPrintln(l)

	if sdkFolderPathString == "" {
		util.Die(util.NewErrorWithMessageAndStack("Folder Path String was empty"), l)
	}
	
	sdkFolderPath := MakeDirAbs(sdkFolderPathString)

	_, err := util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "cp " + MakeFileAbs(Join(sdkFolderPath, zipConfig.CoreSDKFileName)).S +  " " + zipConfig.CoreSDKFileName.S, l)
	util.MaybeDie(err, l)

	_, err = util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "cp " + MakeFileAbs(Join(sdkFolderPath, zipConfig.FreewheelSDKFileName)).S +  " " + zipConfig.FreewheelSDKFileName.S, l)
	util.MaybeDie(err, l)

	_, err = util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "cp " + MakeFileAbs(Join(sdkFolderPath, zipConfig.IMASDKFileName)).S + " " + zipConfig.IMASDKFileName.S, l)
	util.MaybeDie(err, l)
}


func unzipNewRCPackages(config vc.Config, zipConfig zc.Config, l *log.Logger) {
	ol.ColorizedMethodPrintln(l)
	_, err := util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "unzip -o " + zipConfig.CoreSDKFileName.S, l)
	util.MaybeDie(err, l)

	_, err = util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "unzip -o " + zipConfig.FreewheelSDKFileName.S, l)
	util.MaybeDie(err, l)

	_, err = util.RunBashCommandInDir(config.VendorOoyalaRootFolderPath, "unzip -o " + zipConfig.IMASDKFileName.S, l)
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

func checkSdkExist(sdkFolderPathString string, zipConfig zc.Config, l *log.Logger) {
	sdkFolderPath := MakeDirAbs(sdkFolderPathString)

     CoreSDKPath := MakeFileAbs(Join(sdkFolderPath, zipConfig.CoreSDKFileName))
     FreewheelSDKPath := MakeFileAbs(Join(sdkFolderPath, zipConfig.FreewheelSDKFileName))
     IMASDKPath := MakeFileAbs(Join(sdkFolderPath, zipConfig.IMASDKFileName))

     sdkPaths := []Pather{CoreSDKPath, FreewheelSDKPath, IMASDKPath}

     run( func() error { var err error; err = util.RequirePaths(sdkPaths, l); return err }, l)
 }
