(function() {
	function getData() {
		return {
			nombre: $("#nombre").val(),
			apellidoPaterno: $("#apellidoPaterno").val(),
			apellidoMaterno: $("#apellidoMaterno").val(),
			email: $("#email").val(),
			telefono: $("#telefono").val(),
			fecha: $("#dia").val() + "/" + $("#mes").val() + "/" + $("#year").val()
		}
	}
	
function disableButtons() {
	$("#modificar").addClass("disabled");
	$("#borrar").addClass("disabled");
}
	
//	$('#nombre').on('input', function() {
//    	enableButtons();
//	});
//	$('#apellidoPaterno').on('input', function() {
//    	enableButtons();
//	});
//	$('#apellidoMaterno').on('input', function() {
//    	enableButtons();
//	});
//	$('#email').on('input', function() {
//    	enableButtons();
//	});
//	$('#telefono').on('input', function() {
//    	enableButtons();
//	});
//	$('#dia').on('input', function() {
//    	enableButtons();
//	});
//	$('#mes').change(function() {
//    	enableButtons();
//	});
//	$('#year').on('input', function() {
//    	enableButtons();
//	});

	$("#crear").click(function(e) {
		e.preventDefault();
		data = getData();
		//console.log(data.nombre);
		//console.log(data.fecha);
		$.getJSON('_crear', {
			nombre: data.nombre,
			apellidoPaterno: data.apellidoPaterno,
			apellidoMaterno: data.apellidoMaterno,
			email: data.email,
			telefono: data.telefono,
			fecha: data.fecha
		}, function(data) {
			console.log(data);
			$("#modificar").removeClass("disabled");
			$("#borrar").removeClass("disabled");
		})
	})
	
	$("#consultar").click(function(e) {
		e.preventDefault();
		data = getData();
		//console.log(data.nombre);
		//console.log(data.fecha);
		$.getJSON('_consultar', {
			email: data.email,
			telefono: data.telefono
		}, function(data) {
			fecha = data.fecha.split("/");
			$("#nombre").val(data.nombre);
			$("#apellidoPaterno").val(data.apellidoPaterno);
			$("#apellidoMaterno").val(data.apellidoMaterno);
			$("#email").val(data.email);
			$("#telefono").val(data.telefono);
			$("#dia").val(fecha[0]);
			$("#mes").val(fecha[1]);
			$("#year").val(fecha[2]);
			$("#modificar").removeClass("disabled");
			$("#borrar").removeClass("disabled");
		})
	})
	
	$("#modificar").click(function(e) {
		e.preventDefault();
		data = getData();
		//console.log(data.nombre);
		//console.log(data.fecha);
		$.getJSON('_modificar', {
			nombre: data.nombre,
			apellidoPaterno: data.apellidoPaterno,
			apellidoMaterno: data.apellidoMaterno,
			email: data.email,
			telefono: data.telefono,
			fecha: data.fecha
		}, function(data) {
			fecha = data.fecha.split("/");
			$("#nombre").val(data.nombre);
			$("#apellidoPaterno").val(data.apellidoPaterno);
			$("#apellidoMaterno").val(data.apellidoMaterno);
			$("#email").val(data.email);
			$("#telefono").val(data.telefono);
			$("#dia").val(fecha[0]);
			$("#mes").val(fecha[1]);
			$("#year").val(fecha[2]);
		})
	})
	
	$("#borrar").click(function(e) {
		e.preventDefault();
		data = getData();
		//console.log(data.nombre);
		//console.log(data.fecha);
		$.getJSON('_borrar', {
			nombre: data.nombre,
			apellidoPaterno: data.apellidoPaterno,
			apellidoMaterno: data.apellidoMaterno,
			email: data.email,
			telefono: data.telefono,
			fecha: data.fecha
		}, function() {
			$("#nombre").val('');
			$("#apellidoPaterno").val('');
			$("#apellidoMaterno").val('');
			$("#email").val('');
			$("#telefono").val('');
			$("#dia").val('');
			$("#mes").val('');
			$("#year").val('');
			disableButtons();
		})
	})
})();
