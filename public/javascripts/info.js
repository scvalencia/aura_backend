$(document).ready(function(){
var especialidades=["Anesthesia","Cardiovascular Disease","Dermatology","Emergency Medicine","Endocrinology and Metabolism","Family Practice","Gastroenterology","General Practice","Geriatric Medicine","Gynecology","Gynecologic Oncology","Hematology","Infectious Diseases","Internal Medicine","Neonatology"];
var letras= {"0":"q", "1":"r", "2":"s", "3":"t", "4":"u", "5":"v", "6":"w", "7":"x", "8":"y", "9":"z", "A":"0", "B":"1", "C":"2", "D":"3", "E":"4", "F":"5", "G":"6", "H":"7", "I":"8", "J":"9", "K":"A", "L":"B", "M":"C", "N":"D", "O":"E", "P":"F", "Q":"G", "R":"H", "S":"I", "T":"J", "U":"K", "V":"L", "W":"M", "X":"N", "Y":"O", "Z":"P", "a":"Q", "b":"R", "c":"S", "d":"T", "e":"U", "f":"V", "g":"W", "h":"X", "i":"Y", "j":"Z", "k":"a", "l":"b", "m":"c", "n":"d", "o":"e", "p":"f", "q":"g", "r":"h", "s":"i", "t":"j", "u":"k", "v":"l", "w":"m", "x":"n", "y":"o", "z":"p"};
var idDoctorActual;
var tokenDoctorActual;
var tokenDoctorActualEncriptado;
$('.error').hide();
$('.hiddenId').hide();

$(function() {
    $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
    var idToken = $('.hiddenId').html().split("--TOKEN--");
        idDoctorActual = idToken[0];
        tokenDoctorActual = idToken[1];
        tokenDoctorActualEncriptado = encriptar(tokenDoctorActual);
        $.ajax({
             url: "/api/doctor/"+idDoctorActual,
             type: "GET",
             beforeSend: function(xhr){xhr.setRequestHeader('auth-token', tokenDoctorActualEncriptado);},
             success: function(data) {
                $('.nameDr').append(data.name);
                var idEsp = parseInt(data.discipline);
                $('.name').append('<h2>'+data.name+'</h2>');
                $('.id').append('<h4>'+data.id+'</h4>');
                $('.email').append('<h4>'+data.email+'</h4>');
                $('.discipline').append('<h4>'+especialidades[idEsp]+'</h4>');
                $('.birthDate').append('<h4>'+data.date+'</h4>');
                if (data.link!=null)
                {
                    $('.link').append('<h4>'+data.link+'</h4>');
                }
                else
                {
                    $('.link').append('<h4>Not defined</h4>');
                }
             }
          });

  });


$('.btn_sign_out').click(function(){
$.ajax({
            url: '/doctor/logout/'+idDoctorActual,
            type: "PUT",
            beforeSend: function(xhr){
                 xhr.setRequestHeader('auth-token', tokenDoctorActualEncriptado);
            },
            success: function(data) {
                 window.location.href="/";
            }
       });
});

$('.findPatientLink').click(function(){
    window.location.href="/";
});


$('.createPatient').click(function(){

    var firstNameP = $('.firstP').val();
    var lastNameP = $('.lastP').val();
    var nameP = firstNameP.concat(" ").concat(lastNameP);
    var emailP = $('.emailP').val();
    var idP = parseInt($('.idP').val());
    var pass1P = $('.idP').val();
    var dateP = $('.datepicker').val();
    var genderP = parseInt($('input[name="sex"]:checked').val());

    var toSend = {name: nameP, gender: genderP, id: idP, date: dateP, password: pass1P, email: emailP};
    //alert(JSON.stringify(toSend));
    var path = "/api/doctor/"+idDoctorActual+"/patient";
    alert(path);
    alert(JSON.stringify(toSend));
    $.ajax({
          type: 'POST',
          url: path,
          data: JSON.stringify (toSend),
          success: function(data) {
          if (data.name == null)
          {
                        $('#id').css("border-color","#FF0000");
                        $('.errorSignUp').append("Patient with id "+idP+" already exists");
          }
          else
          {
                        window.location.href="/";
          }
          },
          contentType: "application/json",
          dataType: 'json'
          });


    window.location.href="/";

});

function  cambiarLetras(w)
	{
		//alert(w);
		var wr="";
		var arr=w.split('');
		var arrresp=[arr.length];
		if(w!=""){
			for( i= 0; i<arr.length;i++)
			{
				var a=letras[arr[i]];
				arrresp[i]=a;
			}

			for( i= 0; i<arrresp.length;i++)
			{
				wr+=arrresp[i];
			}

		}
		return wr;
	}

function moduloPalabra( w)
	{
		var r=w.split('');
		var respuesta= [w.length];
		var resp="";
		if(w!=""){

			for( i= 0; i<w.length-1;i++)
			{
				respuesta[i]=r[i+1];
			}
			respuesta[w.length-1]=r[0];
			for( i= 0; i<respuesta.length;i++)
			{
				resp+=respuesta[i];
			}

		}
		return resp;
	}

function encriptar(w) {
    var r = cambiarLetras(w);
    return moduloPalabra(r);
}


});