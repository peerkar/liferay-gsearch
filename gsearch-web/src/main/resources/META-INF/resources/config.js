Liferay.Loader.define('jquery', function(){
	return window.jQuery;
});

Liferay.Loader.addModule(
  {
    name: 'google-maps-loader',
    dependencies: [],
    anonymous: true,
    path: MODULE_PATH + '/js/Google.js',
    exports: 'GoogleMapsLoader'
  }
);

Liferay.Loader.addModule({
  name        : 'bootstrap-datepicker',
  dependencies: ['jquery'],
  anonymous   : true,
  path        : MODULE_PATH + '/js/bootstrap-datepicker/js/bootstrap-datepicker.js'
});

