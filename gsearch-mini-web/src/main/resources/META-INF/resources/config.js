Liferay.Loader.define('jquery', function(){
  return window.jQuery;
});

Liferay.Loader.addModule({
  name        : 'devbridge-autocomplete',
  dependencies: ['jquery'],
  anonymous   : true,
  path        : MODULE_PATH + '/js/jquery.autocomplete.js'
});
