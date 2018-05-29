function getRandomInt(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min)) + min; //The maximum is exclusive and the minimum is inclusive
}


function getData () {
    var intentions = ["wellbeing","harmony","freedom","empowerment","resolution","empathy","abundance","love","celebration","transformation"];
    var disciplines = ["meditation","yoga","physical wellness","divination","martial and internal arts","self-expression","transformative technology","psychic realm"];

    var userIntention = "harmony";
    var userDiscipline = "self-expression";

    var threeHours = 60*60*1000*3;
    //        var nbrOfContributions = getRandomInt(20,200);
    var nbrOfContributions = 100;
    var data = new Object();
    data.sessions = new Array();

    for (var i = 0; i < nbrOfContributions; i ++) {
        var distance = getRandomInt(100,10000);
        var intention = intentions[getRandomInt(0, intentions.length)];
        var discipline = disciplines[getRandomInt(0, disciplines.length)];
        var startTime = getRandomInt(new Date()-threeHours, new Date());
        var match = 0;
        if (intention === userIntention) {
            match++;
        }

        if (discipline === userDiscipline) {
            match++;
        }

        data.sessions.push({ index : i,
            distance: distance,
            match: match,
            intention: intention,
            discipline: discipline,
            startTime: startTime
        });
    }
    return data;
}