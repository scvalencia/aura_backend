$(document).ready(function(){
var sports = ["No activity","American Football","Baseball","Basketball","Bowling","Football","Dancing","Football","Golf","Hockey","Ping-Pong","Rugby","Running","Swimming","Tennis","Volleyball","Walking","Other"];
var climate = ["sunny.svg","partly_sunny.svg","cloudy.svg","raining.svg","thunderstorm.svg","snowing.svg"];
var locationImg = ["sinus.svg","tension.svg","cluster_left.svg","cluster_right.svg","migraine_left.svg","migraine_right.svg"];
var location = ["Sinus","Tension","Cluster Left","Cluster Right","Migraine Left","Migraine Right"];
var signs =["Aura","Depression, ittitability, or excitement","Lack of restful sleep","Stuffy nose or watery eyes","Cravings","Throbbing pain on one or both sides of the head","Eye pain","Neck pain","Frequent urination","Yawning","Numbness or tingling","Nausea or vomiting","Light, noise, or smells worsen pain"];
$(function() {
    $( ".datepickerFrom" ).datepicker({dateFormat: "yy-mm-dd"});
    $( ".datepickerTo" ).datepicker({dateFormat: "yy-mm-dd"});
    $( ".datepickerFromAnalysis" ).datepicker({dateFormat: "yy-mm-dd"});
    $( ".datepickerToAnalysis" ).datepicker({dateFormat: "yy-mm-dd"});
  });
var idPacienteActual;
var idDoctorActual=window.location.pathname.split("/")[2];
$.get('/api/doctor/'+idDoctorActual,function(data) {

    $('.nameDr').append(data.name);
});


$('.divName').hide();
$('.divInfoEpisodio').hide();
$('.divAnalysisAfuera').hide();
$('.divAnalysis').hide();

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

$('.analysis').click(function(){
        $('.episodeLi').removeClass('active');
        $('.analysisLi').addClass('active');
        $('.divEpisodiosAfuera').hide();
        $('.divAnalysisAfuera').show();
    });

$('.episode').click(function(){
        $('.analysisLi').removeClass('active');
        $('.episodeLi').addClass('active');
        $('.divAnalysisAfuera').hide();
        $('.divEpisodiosAfuera').show();
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
                    //alert(JSON.stringify(data));
                    if (data.name==null)
                    {
                        $('.errorBusquedaID' ).empty();
                        $('.errorBusquedaID' ).append("Patient not found");
                        $('.divEpisodios').hide();
                        $('.divAnalysis').hide();
                    }
                    else
                    {
                        $('.errorBusquedaID' ).empty();
                        mostrarInfoPaciente(data.name ,data.id, data.email, data.date, data.gender);
                        $('.divInfoEpisodio').show();
                        mostrarListaepisodios(data.episodes);
                        $('.divEpisodios').show();
                        $('.divAnalysis').show();
                    }
                }

            );
        }
	});

$('.btnFilter').click(function(){
    var dateFrom = $( ".datepickerFrom" ).datepicker( "getDate" );
    var dateTo = $( ".datepickerTo" ).datepicker( "getDate" );
    if (dateFrom > dateTo)
    {
        $('.errorFechas').empty();
        $('.errorFechas').append("Invalid period");
        vaciarInfoEpisodio();
        $('.listaEpisodios').empty();
    }
    else
    {
        $('.errorFechas').empty();
        var path = '/api/patient/'+idPacienteActual+'/episode/'+$('.datepickerFrom').val()+'/'+$('.datepickerTo').val();
        $.get(
            path,
            function(data) {
                //alert(JSON.stringify(data))
                mostrarListaepisodios(data);
            }
        );
    }

	});

$('.btnFilterAnalysis').click(function(){
    var dateFrom = $( ".datepickerFromAnalysis" ).datepicker( "getDate" );
    var dateTo = $( ".datepickerToAnalysis" ).datepicker( "getDate" );
    if (dateFrom > dateTo)
    {
        $('.errorFechasAnalysis').empty();
        $('.errorFechasAnalysis').append("Invalid period");
        //TODO borrar graficas
    }
    else
    {
        $('.errorFechasAnalysis').empty();
        var path1 = '/api/patient/'+idPacienteActual+'/analysis1/'+$('.datepickerFromAnalysis').val()+'/'+$('.datepickerToAnalysis').val();
        var path2 = '/api/patient/'+idPacienteActual+'/analysis2/'+$('.datepickerFromAnalysis').val()+'/'+$('.datepickerToAnalysis').val();
        var path3 = '/api/patient/'+idPacienteActual+'/analysis3/'+$('.datepickerFromAnalysis').val()+'/'+$('.datepickerToAnalysis').val();
        //alert(path1);
        $.get(
            path2,
            function(data) {
                if (JSON.stringify(data) != '{}')
                {
                    //var meses = JSON.stringify(data).split("},"):
                    //for (var i = 0; i < meses.length; i++)
                    {
                        /*meses[i]=meses[i].split("\"").join("");
                        meses[i]=meses[i].split("{").join("");
                        meses[i]=meses[i].split("}").join("");
                        alert(meses[i]);
                        var datos = meses[i].split(",");*/
                    }
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
    $('.intensity').append('<h5>Intensity: </h5><div class="progress" style="border-radius:10px; height:18px;"><span class="'+colorI+'" style="width: '+i+'%;"><span>'+inte+'</span></span></div>');
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
    $('.sleepHours').append('<h5>Sleep Hours: </h5><div class="progress" style="border-radius:10px; height:18px;"><span class="'+colorI+'" style="width: '+i+'%;"><span>'+horasP+oMas+'</span></span></div>');
};

function mostrarSuenoRegular(suenoP)
{
    $('.regularSleep').empty();
    if (suenoP==true)
    {
        $('.regularSleep').append('<h5>Regular Sleep: </h5><div class="switch" style="margin-top: 0px; margin-bottom: 10px;"><input type="radio" class="switch-input" id="YES"><label class="switch-label switch-label-off" style="color:black;">YES</label><input type="radio" class="switch-input" id="NO"><label for="NO" class="switch-label switch-label-on" style="color:black;">NO</label><span class="switch-selection"></span></div>');
    }
    else
    {
        $('.regularSleep').append('<h5>Regular Sleep: </h5><div class="switch switch-blue" style="margin-top: 0px; margin-bottom: 10px;"><input type="radio" class="switch-input" id="YES" checked><label class="switch-label switch-label-on" style="color:black;">YES</label><input type="radio" class="switch-input" id="NO"><label for="NO" class="switch-label switch-label-off" style="color:black;">NO</label><span class="switch-selection"></span></div>');
    }
};

function mostrarEstres(estresP)
{
    $('.stress').empty();
    if (estresP==true)
    {
        $('.stress').append('<h5>Stress: </h5><div class="switch switch-blue" style="margin-top: 0px; margin-bottom: 10px;"><input type="radio" class="switch-input" id="YES"><label class="switch-label switch-label-off" style="color:black;">YES</label><input type="radio" class="switch-input" id="NO"><label for="NO" class="switch-label switch-label-on" style="color:black;">NO</label><span class="switch-selection"></span></div>');
    }
    else
    {
        $('.stress').append('<h5>Stress: </h5><div class="switch" style=" margin-top: 0px;margin-bottom: 10px;"><input type="radio" class="switch-input" id="YES" checked><label class="switch-label switch-label-on" style="color:black;">YES</label><input type="radio" class="switch-input" id="NO"><label for="NO" class="switch-label switch-label-off" style="color:black;">NO</label><span class="switch-selection"></span></div>');
    }
};

function mostrarLugar(lugarP)
{
    $('.location').empty();
    $('.location').append('<h5>Location: </h5>');
    $('.location').append('<h6>'+location[parseInt(lugarP)]+'</h6>');
    $('.location').append('<img src="/assets/images/'+locationImg[parseInt(lugarP)]+'" height="100" width="100">');
};

function mostrarFecha(fechaP)
{
    $('.date').empty();
    $('.date').append('<h4>Date: '+fechaP+'</h4>');
};

function mostrarComida(foodP)
{
    $('.food').empty();
    var str = '<h5>Food: </h5><ul class="list-group">';
    $.each(foodP, function(i,comidaActual) {
        str = str+'<li class="list-group-item" style="padding-top:0px; padding-bottom:0px;"><span class="badge">'+foodP[i].quantity+'</span>'+foodP[i].name+'</li>';
    });
    str = str+'</ul>';
    $('.food').append(str);
};

function mostrarMedicinas(medicinesP)
{
    $('.medicines').empty();

    var str = '<h5>Medicines: </h5><table class="table"><thead><tr><th style="padding-top: 0px; padding-bottom: 0px;">Medicine</th><th style="padding-top: 0px; padding-bottom: 0px;">Hours Ago</th></tr></thead><tbody>';
    $.each(medicinesP, function(i,medicinaActual) {
            str = str+'<tr><td style="padding-top: 0px; padding-bottom: 0px;">'+medicinesP[i].name+'</th><th style="padding-top: 0px; padding-bottom: 0px;">'+medicinesP[i].hoursAgo+'</td></tr>';
    });
    str = str+'<tbody></table>'

    $('.medicines').append(str);
};

function mostrarDeportes(deportesP)
{
    $('.sports').empty();

    var str = '<h5>Sports and Activities: </h5><table class="table"><thead><tr><th style="padding-top: 0px; padding-bottom: 0px;">Activity</th><th style="padding-top: 0px; padding-bottom: 0px;">Intensity</th><th style="padding-top: 0px; padding-bottom: 0px;">Weather</th><th style="padding-top: 0px; padding-bottom: 0px;">Hydration</th></tr></thead><tbody>';
    $.each(deportesP, function(i,medicinaActual) {
            str = str+'<tr>';
            str = str+'<td style="padding-top: 0px; padding-bottom: 0px;">'+sports[parseInt(deportesP[i].description)]+'</th>';
            //str = str+'<th style="padding-top: 0px; padding-bottom: 0px;">'+deportesP[i].intensity+'</td>';
            var j = deportesP[i].intensity*100/10;
            str = str+'<th style="padding-top: 0px; padding-bottom: 0px;"><div class="progress" style="border-radius:10px; height:18px;"><span class="blue" style="width: '+j+'%;"><span>'+deportesP[i].intensity+'</span></span></div></td>';
            //str = str+'<th style="padding-top: 0px; padding-bottom: 0px;"><img src="@routes.Assets.at('+climate[parseInt(deportesP[i].climate)]+') height="50" width="50"></td>';
            str = str+'<th style="padding-top: 0px; padding-bottom: 0px;"><img src="/assets/images/'+climate[parseInt(deportesP[i].climate-1)]+'" height="40" width="40"></td>';
            if (deportesP[i].hydration)
            {
                str = str+'<th style="padding-top: 0px; padding-bottom: 0px;">YES</td>';
            }
            else
            {
                str = str+'<th style="padding-top: 0px; padding-bottom: 0px;">NO</td>';
            }
            str = str+'</tr>';
    });
    str = str+'<tbody></table>'

    $('.sports').append(str);
};

function mostrarSintomas(sintomasP)
{
    $('.symptoms').empty();
    var str = '<h5>Symptoms: </h5><ul class="list-group">';
        //alert(JSON.stringify(sintomasP));
        //$.each(sintomasP, function(i,sintomaActual) {
            //str = str+'<li class="list-group-item" style="padding-top:0px; padding-bottom:0px;">'+signs[parserInt(sintomasP[i].symptom)]+'</li>';
            //str=str+parserInt(sintomasP[i].symptom);
        //});
    $.each(sintomasP, function(i,sintomaActual) {
           str = str+'<li class="list-group-item" style="padding-top:0px; padding-bottom:0px;">'+signs[parseInt(sintomasP[i].symptom)]+'</li>';
    });
    str = str+'</ul>';
        //alert(str);
    $('.symptoms').append(str);
};

function mostrarListaepisodios(episodesP)
{
    $('.listaEpisodios').empty();
    vaciarInfoEpisodio();
    $.each(episodesP, function(i,episodioActualI) {
        var select = "";
        if (i==0)
        {
            select ='selected="selected"';
            mostrarFecha(episodesP[i].pubDate);
            mostrarIntensidad(episodesP[i].intensity);
            mostrarHorasSueno(episodesP[i].sllepHours);
            mostrarSuenoRegular(episodesP[i].regularSleep);
            mostrarEstres(episodesP[i].stress);
            mostrarLugar(episodesP[i].location);
            mostrarComida(episodesP[i].foods);
            mostrarMedicinas(episodesP[i].medicines);
            mostrarDeportes(episodesP[i].sports);
            mostrarSintomas(episodesP[i].symptoms);

        }
        $('.listaEpisodios').append('<option value="'+episodesP[i].id+'" '+select+'>'+episodesP[i].pubDate+'</option>');
    });
}

function vaciarInfoEpisodio()
{
    $('.date').empty();
    $('.intensity').empty();
    $('.sleepHours').empty();
    $('.regularSleep').empty();
    $('.stress').empty();
    $('.location').empty();
     $('.food').empty();
     $('.medicines').empty();
     $('.sports').empty();
     $('.symptoms').empty();
};

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
            mostrarEstres(data.stress);
            mostrarLugar(data.location);
            mostrarComida(data.foods);
            mostrarMedicinas(data.medicines);
            mostrarDeportes(data.sports);
            mostrarSintomas(data.symptoms);
        }


    );
});

$(function () {

    /*var path = '/api/patient/'+idPacienteActual+'/analysis1/'+:f1/:f2
    '/api/patient/'+idPacienteActual+"/episode/"+episodioActual;
        $.get(
            path,
            function(data) {

            }


        );*/

    $('#container1').highcharts({
        chart: {
            type: 'bar'
        },
        title: {
            text: 'Intensity episodes'
        },
        xAxis: {
            categories: ['January', 'February', 'May', 'June', 'July']
        },
        yAxis: {
            min: 0,
            title: {
                text: 'Total fruit consumption'
            }
        },
        legend: {
            reversed: true
        },
        plotOptions: {
            series: {
                stacking: 'normal'
            }
        },
        series: [{
            name: '10',
            data: [5, 3, 4, 7, 2]
        }, {
            name: '9',
            data: [2, 2, 3, 2, 1]
        }, {
            name: '8',
            data: [2, 2, 3, 2, 1]
        }, {
            name: '7',
            data: [2, 2, 3, 2, 1]
        }, {
            name: '6',
            data: [2, 2, 3, 2, 1]
        }, {
            name: '5',
            data: [2, 2, 3, 2, 1]
        }, {
            name: '4',
            data: [2, 2, 3, 2, 1]
        }, {
            name: '3',
            data: [2, 2, 3, 2, 1]
        }, {
            name: '2',
            data: [2, 2, 3, 2, 1]
        }, {
            name: '1',
            data: [3, 4, 4, 2, 5]
        }]
    });
});

});