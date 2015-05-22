$(document).ready(function(){

    var informacionPacientes;
    var idDoctorActual;
    var tokenDoctorActual;
    var tokenDoctorActualEncriptado;
    var letras= {"0":"q", "1":"r", "2":"s", "3":"t", "4":"u", "5":"v", "6":"w", "7":"x", "8":"y", "9":"z", "A":"0", "B":"1", "C":"2", "D":"3", "E":"4", "F":"5", "G":"6", "H":"7", "I":"8", "J":"9", "K":"A", "L":"B", "M":"C", "N":"D", "O":"E", "P":"F", "Q":"G", "R":"H", "S":"I", "T":"J", "U":"K", "V":"L", "W":"M", "X":"N", "Y":"O", "Z":"P", "a":"Q", "b":"R", "c":"S", "d":"T", "e":"U", "f":"V", "g":"W", "h":"X", "i":"Y", "j":"Z", "k":"a", "l":"b", "m":"c", "n":"d", "o":"e", "p":"f", "q":"g", "r":"h", "s":"i", "t":"j", "u":"k", "v":"l", "w":"m", "x":"n", "y":"o", "z":"p"};
    var sports = ["No activity","American Football","Baseball","Basketball","Bowling","Football","Dancing","Football","Golf","Hockey","Ping-Pong","Rugby","Running","Swimming","Tennis","Volleyball","Walking","Other"];
    var climate = ["sunny.svg","partly_sunny.svg","cloudy.svg","raining.svg","thunderstorm.svg","snowing.svg"];
    var locationImg = ["sinus.svg","tension.svg","cluster_left.svg","cluster_right.svg","migraine_left.svg","migraine_right.svg"];
    var location = ["Sinus","Tension","Cluster Left","Cluster Right","Migraine Left","Migraine Right"];
    var signs =["Aura","Depression, ittitability, or excitement","Lack of restful sleep","Stuffy nose or watery eyes","Cravings","Throbbing pain on one or both sides of the head","Eye pain","Neck pain","Frequent urination","Yawning","Numbness or tingling","Nausea or vomiting","Light, noise, or smells worsen pain", "Other"];

    $(function() {

        $( ".datepickerFrom" ).datepicker({dateFormat: "yy-mm-dd"});
        $( ".datepickerTo" ).datepicker({dateFormat: "yy-mm-dd"});
        $( ".datepickerFromAnalysis" ).datepicker({dateFormat: "yy-mm-dd"});
        $( ".datepickerToAnalysis" ).datepicker({dateFormat: "yy-mm-dd"});
        //var path = window.location.hash.substring(1);
        //var d = Base64.decode(path).split('"')[1];
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
            }
        });
        $.ajax({
            url: "/api/doctor/"+idDoctorActual+"/patient",
            type: "GET",
            success: function(data) {
                informacionPacientes = data;
                mostrarListaPacientes();
            }
        });

    });
    var idPacienteActual;


//$('.hiddenId').append($('.hiddenId').html());
    $('.hiddenId').hide();
    $('.divName').hide();
    $('.divInfoEpisodio').hide();
    $('.divAnalysisAfuera').hide();
    $('.divAnalysis').hide();
    $('#container1').hide();
    $('#container2').hide();
    $('#container3').hide();

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

            $.ajax({
                url: "/api/patient/"+idPacienteActual+'/episode/'+$('.datepickerFrom').val()+'/'+$('.datepickerTo').val(),
                type: "GET",
                beforeSend: function(xhr){
                    xhr.setRequestHeader('auth-token', tokenDoctorActualEncriptado);
                    xhr.setRequestHeader('who', 'DOC');
                    xhr.setRequestHeader('id', idDoctorActual);
                },
                success: function(data) {
                    mostrarListaepisodios(data);
                }
            });
        }

    });


    $('.btnFilter').click(function(){
        var intensityFilter = $( ".filterIntensity" ).val();
        var sleepFilter = $( ".filterSleep" ).val();
        var stressFilter = $( ".filterStress" ).val();
        var SymptomFilter = $( ".filterSymptom" ).val();
        var placeFilter = $( ".filterPlace" ).val();

        var jsonFilter = {intensity: intensityFilter, timeslept: sleepFilter, stress: stressFilter, symptom: SymptomFilter, place: placeFilter};
        $('.errorFechas').empty();
            $.ajax({
                url: "/api/doctor/:doctor/patient/"+idPacienteActual+"/filter",
                type: "GET",
                data: JSON.stringify(jsonFilter),
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: function(dataR) {
                    mostrarListaepisodios(dataR);
                }
            });

    });


    $('.btnFilterAnalysis').click(function(){
        var dateFrom = $( ".datepickerFromAnalysis" ).datepicker( "getDate" );
        var dateTo = $( ".datepickerToAnalysis" ).datepicker( "getDate" );
        var chartMeses = [];
        if (dateFrom > dateTo)
        {
            $('.errorFechasAnalysis').empty();
            $('.errorFechasAnalysis').append("Invalid period");
            $('#container1').hide();
            $('#container2').hide();
            $('#container3').hide();
            //TODO borrar graficas
        }
        else
        {
            $('.errorFechasAnalysis').empty();
            var path1 = '/api/patient/'+idPacienteActual+'/analysis1/'+$('.datepickerFromAnalysis').val()+'/'+$('.datepickerToAnalysis').val();
            var path2 = '/api/patient/'+idPacienteActual+'/analysis2/'+$('.datepickerFromAnalysis').val()+'/'+$('.datepickerToAnalysis').val();
            var path3 = '/api/patient/'+idPacienteActual+'/analysis3/'+$('.datepickerFromAnalysis').val()+'/'+$('.datepickerToAnalysis').val();
            $.get(
                path2,
                function(data) {
                    if (JSON.stringify(data) != '{}')
                    {
                        var meses = JSON.stringify(data).split('},');
                        for (var i = 0; i < meses.length; i++)
                        {
                            var chartMes = ['',0,0,0,0,0,0,0,0,0,0];
                            meses[i]=meses[i].split("\"").join("");
                            meses[i]=meses[i].split("{").join("");
                            meses[i]=meses[i].split("}").join("");
                            var datos = meses[i].split(",");
                            var dato = datos[0].split(':');
                            var anio = dato[0].split(" ")[0];
                            var mes = parseInt(dato[0].split(" ")[1])+1;
                            chartMes[0]=anio+"-"+mes;
                            chartMes[dato[1]] = dato[2];
                            for (var j = 1; j < datos.length; j++)
                            {
                                dato = datos[j].split(':');
                                chartMes[dato[0]] = dato[1];
                            }
                            chartMeses.push(chartMes);
                        }
                        mostrarAnalisis1(chartMeses);
                    }
                }
            );
            $.get(
                path1,
                function(data) {
                    if (JSON.stringify(data) != '{}')
                    {
                        var matrizHorasIntensidad = [];
                        for (var i = 0; i < 10; i++)
                        {
                            matrizHorasIntensidad[i]=[];
                            for (var j = 0; j < 13; j++)
                            {
                                matrizHorasIntensidad[i][j] = 0;
                            }
                        }
                        $.each(data, function(i,datoActual) {
                            var x = parseInt(data[i].intensity)-1;
                            var y = parseInt(data[i].hours);
                            matrizHorasIntensidad[x][y]=matrizHorasIntensidad[x][y]+1;
                        });
                        mostrarAnalisis2(matrizHorasIntensidad);
                    }
                }
            );
            $.get(
                path3,
                function(data) {
                    if (JSON.stringify(data) != '{}')
                    {
                        var datos = [0,0,0,0,0,0];
                        $.each(data, function(i,datoActual) {
                            datos[data[i].spot]=data[i].frequency;
                        });
                        mostrarAnalisis3(datos);
                    }
                }
            );

        }

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

    $('.myAccountLink').click(function(){
        window.location.href="/info";
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
        $.each(sintomasP, function(i,sintomaActual) {
            str = str+'<li class="list-group-item" style="padding-top:0px; padding-bottom:0px;">'+signs[parseInt(sintomasP[i].symptom)]+'</li>';
        });
        str = str+'</ul>';
        $('.symptoms').append(str);
    };

    function mostrarAudio(audio)
    {
        $('.audio').empty();
        //var link = 'http://api.soundcloud.com/tracks/197197916/stream?client_id='+audio;
        if (audio != 0)
        {
            $('.audio').append('<audio controls><source src="http://api.soundcloud.com/tracks/'+audio+'/stream?client_id=07fe9e7f76d4ac14db7bed65c2241a9d" type="audio/mpeg"></audio>');
        }
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
                mostrarAudio(episodesP[i].urlId);

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
        $('.audio').empty();
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

        $.ajax({
            url: '/api/patient/'+idPacienteActual+"/episode/"+episodioActual,
            type: "GET",
            beforeSend: function(xhr){
                xhr.setRequestHeader('auth-token', tokenDoctorActualEncriptado);
                xhr.setRequestHeader('who', 'DOC');
                xhr.setRequestHeader('id', idDoctorActual);
            },
            success: function(data) {
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
                mostrarAudio(data.urlId);
            }
        });
    });

    function mostrarAnalisis1(chartMeses)
    {
        var txtCategories = [];
        var datos1 = [];
        var datos2 = [];
        var datos3 = [];
        var datos4 = [];
        var datos5 = [];
        var datos6 = [];
        var datos7 = [];
        var datos8 = [];
        var datos9 = [];
        var datos10 = [];

        for (var i = 0; i < chartMeses.length; i++)
        {
            txtCategories.push(chartMeses[i][0]);
            datos1.push(parseInt(chartMeses[i][1]));
            datos2.push(parseInt(chartMeses[i][2]));
            datos3.push(parseInt(chartMeses[i][3]));
            datos4.push(parseInt(chartMeses[i][4]));
            datos5.push(parseInt(chartMeses[i][5]));
            datos6.push(parseInt(chartMeses[i][6]));
            datos7.push(parseInt(chartMeses[i][7]));
            datos8.push(parseInt(chartMeses[i][8]));
            datos9.push(parseInt(chartMeses[i][9]));
            datos10.push(parseInt(chartMeses[i][10]));
        }

        $('#container1').show();
        $('#container1').highcharts({
            chart: {
                type: 'bar'
            },
            title: {
                text: 'Episodes\' Intensity'
            },
            xAxis: {
                categories: txtCategories
            },
            yAxis: {
                min: 0,
                title: {
                    text: 'Total episodes'
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
                data: [datos10[0],datos10[1]]
            }, {
                name: '9',
                data: datos9
            }, {
                name: '8',
                data: datos8
            }, {
                name: '7',
                data: datos7
            }, {
                name: '6',
                data: datos6
            }, {
                name: '5',
                data: datos5
            }, {
                name: '4',
                data: datos4
            }, {
                name: '3',
                data: datos3
            }, {
                name: '2',
                data: datos2
            }, {
                name: '1',
                data: datos1
            }]
        });
    };

    function mostrarAnalisis2(matrizHorasIntensidad)
    {

        var datos = [];
        for (var i = 0; i < 10; i++)
        {
            for (var j = 0; j < 13; j++)
            {
                var horas = j;
                var intensidad = i+1;
                var cantidad = matrizHorasIntensidad[i][j];
                if (cantidad!=0)
                {
                    datos.push([horas,intensidad,cantidad]);
                }
            }
        }
        $('#container2').show();
        $('#container2').highcharts({

            chart: {
                type: 'bubble',
                zoomType: 'xy'
            },

            title: {
                text: 'Sleep Hours vs Intensity'
            },

            series: [{
                data: datos
            }]
        });

    };

    function mostrarAnalisis3(datos)
    {
        $('#container3').show();
        $('#container3').highcharts({

            chart: {
                polar: true,
                type: 'line'
            },

            title: {
                text: 'Migraine Locations',
                x: -80
            },

            pane: {
                size: '80%'
            },

            xAxis: {
                categories: location,
                tickmarkPlacement: 'on',
                lineWidth: 0
            },

            yAxis: {
                gridLineInterpolation: 'polygon',
                lineWidth: 0,
                min: 0
            },

            tooltip: {
                shared: true,
                pointFormat: '<span style="color:{series.color}">{series.name}: <b>${point.y:,.0f}</b><br/>'
            },

            legend: {
                align: 'right',
                verticalAlign: 'top',
                y: 70,
                layout: 'vertical'
            },

            series: [{
                name: 'Location',
                data: datos,
                pointPlacement: 'on'
            }]

        });
    };

    function mostrarListaPacientes()
    {
        $('.listaPacientes').empty();
        $.each(informacionPacientes, function(i,pacienteActualI){
            var r = pacienteActualI.id + "-" + pacienteActualI.name;
            var select = "";
            if (i==0)
            {
                select ='selected="selected"';
                mostrarListaPaciente(pacienteActualI);
            }
            $('.listaPacientes').append('<option value="'+informacionPacientes[i].id+'" '+select+'>'+r+'</option>');
        });
    };

    function mostrarListaPaciente(pacienteActualI)
        {
            $('.errorBusquedaID' ).empty();
            mostrarInfoPaciente(pacienteActualI.name ,pacienteActualI.id, pacienteActualI.email, pacienteActualI.date, pacienteActualI.gender);
            $('.divInfoEpisodio').show();
            mostrarListaepisodios(pacienteActualI.episodes);
            $('.divEpisodios').show();
            $('.divAnalysis').show();
        };

    $('.listaPacientes').change(function(){
        $('.idPacienteTxt').val('');
        var pacienteActualId = $('.listaPacientes').val();
        idPacienteActual = pacienteActualId;

        $.each(informacionPacientes, function(i,pacienteActualI){
            if(pacienteActualI.id == pacienteActualId)
            {
                $('.errorBusquedaID' ).empty();
                mostrarInfoPaciente(pacienteActualI.name ,pacienteActualI.id, pacienteActualI.email, pacienteActualI.date, pacienteActualI.gender);
                $('.divInfoEpisodio').show();
                mostrarListaepisodios(pacienteActualI.episodes);
                $('.divEpisodios').show();
                $('.divAnalysis').show();
            }
        });
    });

    $('.findByID').click(function(){

        $('.infoPaciente').empty();
        $('.nombrePaciente').empty();
        $('.listaPacientes').empty();

        var pacienteActualId = parseInt($('.idPacienteTxt').val());

        $.each(informacionPacientes, function(i,pacienteActualI){
            var r = pacienteActualI.id + "-" + pacienteActualI.name;
            var select = "";
            if(pacienteActualI.id == pacienteActualId)
            {
                select ='selected="selected"';
                $('.errorBusquedaID' ).empty();
                mostrarInfoPaciente(pacienteActualI.name ,pacienteActualI.id, pacienteActualI.email, pacienteActualI.date, pacienteActualI.gender);
                $('.divInfoEpisodio').show();
                mostrarListaepisodios(pacienteActualI.episodes);
                $('.divEpisodios').show();
                $('.divAnalysis').show();
            }

            $('.listaPacientes').append('<option value="'+pacienteActualI.id+'" '+select+'>'+r+'</option>');
        });

        /*if (isNaN(idPaciente))
        {
            $('.errorBusquedaID' ).empty();
            $('.errorBusquedaID' ).append("Patient's ID must be a number");
        }
        else
        {

            $.ajax({
                url: "/api/patient/"+idPaciente,
                type: "GET",
                beforeSend: function(xhr){
                    xhr.setRequestHeader('auth-token', tokenDoctorActualEncriptado);
                    xhr.setRequestHeader('who', 'DOC');
                    xhr.setRequestHeader('id', idDoctorActual);
                },
                success: function(data) {
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
                    //$('.nameDr').append(data.name);
                }
            });
        }*/
    });



    function  cambiarLetras(w)
    {
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