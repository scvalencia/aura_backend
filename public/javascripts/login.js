$(document).ready(function(){

var especialidades=["Anesthesia","Cardiovascular Disease","Dermatology","Emergency Medicine","Endocrinology and Metabolism","Family Practice","Gastroenterology","General Practice","Geriatric Medicine","Gynecology","Gynecologic Oncology","Hematology","Infectious Diseases","Internal Medicine","Neonatology"];
$('.errorLogin').hide();
for (var i = 0; i < especialidades.length; i++)
{
    $('#discipline').append('<option value="'+i+'">'+especialidades[i]+'</option>');
}



$(function() {
    $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
  });

    $('.btnLogin').click(function(){
        var idDoctor = parseInt($('.idTxt').val());
        var pswDoctor = $('.passwordTxt').val();
        var toSend = {id : idDoctor, password : pswDoctor};

        var path = '/doctor/authenticate';
        var request;
        request = $.ajax({
            type: 'POST',
            url: path,
            data: JSON.stringify (toSend),
            success: function(data) {
            if (JSON.stringify(data)=="{}")
            {
                $('.errorLogin').show();
            }
            else
            {
                //window.location.href="/search/"+data.id;
                window.location.href="/";
            }
             },
            contentType: "application/json",
            dataType: 'json'
        });

    });

    $('.btnCreate').click(function(){
            $('.errorSignUp').empty();
            $('#firstName').css("border-color","#CCCCCC");
            $('#firstName').css("background-color","#FFFFFF");
            $('#lastName').css("border-color","#CCCCCC");
            $('#lastName').css("background-color","#FFFFFF");
            $('#email').css("border-color","#CCCCCC");
            $('#email').css("background-color","#FFFFFF");
            $('#id').css("border-color","#CCCCCC");
            $('#id').css("background-color","#FFFFFF");
            $('#password1').css("border-color","#CCCCCC");
            $('#password1').css("background-color","#FFFFFF");
            $('#password2').css("border-color","#CCCCCC");
            $('#password2').css("background-color","#FFFFFF");
            $('.datepicker').css("border-color","");
            $('.datepicker').css("background-color","#FFFFFF");
            var firstNameP = $('#firstName').val();
            var lastNameP = $('#lastName').val();
            var nameP = firstNameP.concat(" ").concat(lastNameP);
            var emailP = $('#email').val();
            var idP = parseInt($('#id').val());
            var pass1P = $('#password1').val();
            var pass2P = $('#password2').val();
            var linkedIn = $('#linkedin').val();
            var dateP = $('.datepicker').val();
            var disciplineP = parseInt($('#discipline').val());
            var genderP = parseInt($('input[name="sex"]:checked').val());

            if (isNaN(parseInt(idP)))
            {
                $('#id').css("border-color","#FF0000");
                $('#id').css("background-color","#FFE5E0");
                $('.errorSignUp').append("ID must me a number");
            }
            else if (firstNameP=="")
            {
                $('#firstName').css("border-color","#FF0000");
                $('#firstName').css("background-color","#FFE5E0");
                $('.errorSignUp').append("First name must not be empty");
            }
            else if (lastNameP=="")
            {
                $('#lastName').css("border-color","#FF0000");
                $('#lastName').css("background-color","#FFE5E0");
                $('.errorSignUp').append("Last name must not be empty");
            }
            else if (emailP=="")
            {
                $('#email').css("border-color","#FF0000");
                $('#email').css("background-color","#FFE5E0");
                $('.errorSignUp').append("Email must not be empty");
            }
            else if (dateP=="")
            {
                $('.datepicker').css("border-color","#FF0000");
                $('.datepicker').css("background-color","#FFE5E0");
                $('.errorSignUp').append("Birth date must not be empty");
            }
            else if (pass1P=="")
            {
                $('#password1').css("border-color","#FF0000");
                $('#password1').css("background-color","#FFE5E0");
                $('#password2').css("border-color","#FF0000");
                $('#password2').css("background-color","#FFE5E0");
                $('.errorSignUp').append("Password must not be empty");
            }
            else if (!(pass1P==pass2P))
            {
                $('#password1').css("border-color","#FF0000");
                $('#password1').css("background-color","#FFE5E0");
                $('#password2').css("border-color","#FF0000");
                $('#password2').css("background-color","#FFE5E0");
                $('.errorSignUp').append("Passwords are not equal");
            }
            else
            {
                var toSend = {discipline: disciplineP, name: nameP, gender: genderP, id: idP, date: dateP, password: pass1P, email: emailP, link:linkedIn};
                alert(JSON.stringify(toSend));
                var path = "/api/doctor";
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
            }

        });

});
