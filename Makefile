export GOPATH = $(shell pwd)/go_script/

install: install-scripts clean
	go_script/bin/copy_from_vendor
	go_script/bin/merge_into_complete_sample_app

get-latest-rc: install-scripts
	go_script/bin/get_latest_rc
	make install

update-vendor-from-target-location:
	go_script/bin/update-vendor-from-target-location /user/yigu/repos/android-sdk
	// get the zip files from the folder

clean-scripts:
	rm -f go_script/bin/*

install-scripts: clean-scripts
	cd go_script/src/mobile.ooyala.com/samples/clean_sample_apps/ && go install
	cd go_script/src/mobile.ooyala.com/samples/get_latest_rc/ && go install
	cd go_script/src/mobile.ooyala.com/samples/get_latest_release/ && go install
	cd go_script/src/mobile.ooyala.com/samples/copy_from_vendor/ && go install
	cd go_script/src/mobile.ooyala.com/samples/merge_into_complete_sample_app/ && go install

clean: install-scripts
	go_script/bin/clean_sample_apps
