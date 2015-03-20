$(document).ready(function(){
var idPacienteActual;

$('.divName').hide();
$('.divInfoEpisodio').hide();


//mostrarListaepisodios(episodes);
$('.divEpisodios').hide();

$('.byName').click(function(){
        $('.byIDLi').removeClass('active');
        $('.byNameLi').addClass('active');
        $('.errorBusquedaID' ).empty();
        $('.divID').hide();
        $('.divName').show();
    });

$('.byID').click(function(){
        $('.byNameLi').removeClass('active');
        $('.byIDLi').addClass('active');
        $('.errorBusquedaID').empty();
        $('.divName').hide();
        $('.divID').show();
	});




$('.findByID').click(function(){
        $('.infoPaciente').empty();
        $('.nombrePaciente').empty();
        var idPaciente = parseInt($('.idPacienteTxt').val());
        if (isNaN(idPaciente))
        {
            $('.errorBusquedaID' ).empty();
            $('.errorBusquedaID' ).append("Patient's ID must be a number");
        }
        else
        {
            var path = '/api/patient/'+idPaciente;
            $.get(
                path,
                function(data) {
                    alert(JSON.stringify(data));
                    if (data.name==null)
                    {
                        $('.errorBusquedaID' ).empty();
                        $('.errorBusquedaID' ).append("Patient not found");
                    }
                    else
                    {
                        $('.errorBusquedaID' ).empty();
                        mostrarInfoPaciente(data.name ,data.id, data.email, data.date, data.gender);
                        $('.listaEpisodios').empty();
                        $('.divInfoEpisodio').show();
                        mostrarListaepisodios(data.episodes);
                        $('.divEpisodios').show();
                    }
                }

            );
        }
	});

function mostrarIntensidad(inte)
{
    var colorI = "green";
    if (inte>=4)
        {colorI = "orange";}
    if (inte>=8)
        {colorI = "red";}
    var i = inte*100/10;
    $('.intensity').empty();
    //$('.intensity').append('<div class="progress" style="border-radius:10px"><span class="'+colorI+'" style="width: '+i+'%;"><span>5</span></span></div>');
    $('.intensity').append('<h6>Intensity: </h6><div class="progress" style="border-radius:10px; height:18px;"><span class="'+colorI+'" style="width: '+i+'%;"><span>'+inte+'</span></span></div>');
};

function mostrarHorasSueno(horasP)
{
    var oMas = "";
    var colorI = "red";
    if (horasP>=8)
    {
        horasP=8;
        oMas=" o más";
    }
    if (horasP>=4)
        {colorI = "orange";}
    if (horasP>=6)
        {colorI = "green";}
    var i = horasP*100/8;
    $('.sleepHours').empty();
    //$('.intensity').append('<div class="progress" style="border-radius:10px"><span class="'+colorI+'" style="width: '+i+'%;"><span>5</span></span></div>');
    $('.sleepHours').append('<h6>Sleep Hours: </h6><div class="progress" style="border-radius:10px; height:18px;"><span class="'+colorI+'" style="width: '+i+'%;"><span>'+horasP+oMas+'</span></span></div>');
};

function mostrarSuenoRegular(suenoP)
{
    $('.regularSleep').empty();
    if (suenoP==true)
    {
        $('.regularSleep').append('<h6>Regular Sleep: YES</h6>');
    }
    else
    {
        $('.regularSleep').append('<h6>Regular Sleep: NO</h6>');
    }
};
function mostrarFecha(fechaP)
{
    $('.date').empty();
    $('.date').append('<h6>Date: '+fechaP+'</h6>');
};

function mostrarListaepisodios(episodesP)
{
    $.each(episodesP, function(i,episodioActualI) {
        var select = "";
        if (i==0)
        {
            select ='selected="selected"';
        }
        $('.listaEpisodios').append('<option value="'+episodesP[i].id+'" '+select+'>'+episodesP[i].pubDate+'</option>');
    });
}

function mostrarInfoPaciente(nombre,docID,email,fechaNacimiento,generoN)
{
    idPacienteActual = docID;
    $('.infoPaciente').empty();
    $('.nombrePaciente').empty();
    $('.nombrePaciente').append('<h3>'+nombre+'</h3>');
    $('.infoPaciente').append('<h4>ID: '+docID+'</h4>');
    $('.infoPaciente').append('<h4>Email: '+email+'</h4>');
    $('.infoPaciente').append('<h4>Birth date: '+fechaNacimiento+'</h4>');
    var genero = "M";
    if (generoN == 1)
        genero='F';
    $('.infoPaciente').append('<h4>Gender: '+genero+'</h4>');
};


$('.listaEpisodios').change(function(){
    var episodioActual = $('.listaEpisodios').val();
    var path = '/api/patient/'+idPacienteActual+"/episode/"+episodioActual;
    $.get(
        path,
        function(data) {
            mostrarFecha(data.pubDate);
            mostrarHorasSueno(data.sllepHours);
            mostrarIntensidad(data.intensity);
            mostrarSuenoRegular(data.regularSleep);
        }


    );
});

});