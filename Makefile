
install: clean
	script/copy_ooyala_core_sdk_from_vendor.sh
	script/copy_freewheel_jars_from_vendor.sh
	script/copy_ima_jars_from_vendor.sh
	script/copy_code_into_complete_sample_app.sh

clean:
	script/clean_all_sample_apps.sh
