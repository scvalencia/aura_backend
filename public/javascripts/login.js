$(document).ready(function(){
$('.errorLogin').hide();

    $('.btnLogin').click(function(){
        my_session={};
        var idDoctor = parseInt($('.idTxt').val());
        var pswDoctor = $('.passwordTxt').val();
        var toSend = {id : idDoctor, password : pswDoctor};

        var path = '/doctor/authenticate';

        $.ajax({
            type: 'POST',
            url: path,
            data: JSON.stringify (toSend),
            success: function(data) { my_session = data;
            if (JSON.stringify(data)=="{}")
            {
                $('.errorLogin').show();
            }
            else
            {
                window.location.href="/test/"+data.id;
            }
             },
            contentType: "application/json",
            dataType: 'json'
        });

    });

});