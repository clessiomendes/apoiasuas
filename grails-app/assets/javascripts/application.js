// This is a manifest file that'll be compiled into application.js.
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
// You're free to add application-wide JavaScript to this file, but it's generally better 
// to create separate JavaScript files as needed.
//
//= require jquery
//= require jstree/jstree.js
//= require fileuploader.js
//= require js/jquery-ui-1.10.4.custom
//= require notificacoes.js
//
//Manter as instruções abaixo sempre como as últimas:
//
//= require_tree .
//= require_self

if (typeof jQuery !== 'undefined') {
	//huh
	(function($) {
		$('#spinner').ajaxStart(function() {
			$(this).fadeIn();
		}).ajaxStop(function() {
			$(this).fadeOut();
		});
	})(jQuery);
}

/**
 * Funcao auxiliar para imprimir numeros grandes de arquivos
 */
function fileSizeToString(bytes) {
	if(bytes == 0) return '0 Byte';
	var decimals = 0; //máximo de casas decimais
	var k = 1000; // or 1024 for binary
	var dm = decimals + 1 || 3;
	var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
	var i = Math.floor(Math.log(bytes) / Math.log(k));
	return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

