$(document).ready(function(){
var especialidades=["Anesthesia","Cardiovascular Disease","Dermatology","Emergency Medicine","Endocrinology and Metabolism","Family Practice","Gastroenterology","General Practice","Geriatric Medicine","Gynecology","Gynecologic Oncology","Hematology","Infectious Diseases","Internal Medicine","Neonatology"];
$('.error').hide();

$(function() {
    $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
  });


var idDoctorActual=window.location.pathname.split("/")[2];
$.get('/api/doctor/'+idDoctorActual,function(data) {
    //alert(JSON.stringify(data));
    var idEsp = parseInt(data.discipline);
    $('.nameDr').append(data.name);
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

});


$('.btn_sign_out').click(function(){
    window.location.href="/doctor/login";
});

$('.findPatientLink').click(function(){
    window.location.href="/search/"+idDoctorActual;
});



});