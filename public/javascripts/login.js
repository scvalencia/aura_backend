window.my_session = {};

$(document).ready(function(){
    $('.btnLogin').click(function(){
        var idDoctor = parseInt($('.idTxt').val());
        var pswDoctor = $('.passwordTxt').val();
        var toSend = {id : idDoctor, password : pswDoctor};

        var path = '/doctor/authenticate';

        $.ajax({
            type: 'POST',
            url: path,
            data: JSON.stringify (toSend),
            success: function(data) { window.my_session = data; },
            contentType: "application/json",
            dataType: 'json'
        });

        var pathR = "/test/"+toSend.id;
        window.location.href=pathR;
    });

});