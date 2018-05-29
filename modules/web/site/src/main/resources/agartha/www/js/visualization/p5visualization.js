// This file produces and injects a visualization as a html canvas into a given container in the component onGoingSession.vue.
// Powered by p5.js library, more about p5: http://p5js.org/
// TODO: The code in this file could be moved and triggered inside onGoingSession-component.

var colorsURL = "./js/visualization/colors.json"; // TODO: Content loaded from this file should be loaded dynamically from DB or other centralized location
var data; // will store json data to draw
var colorsJson; // will store color scheme to use
var nbrOfSessions; // number of ongoing sessions to draw
var longestDistance = 0; // initialize var to compare distance to other sessions
var sessions = []; // array of all ongoing sessions loaded from data
var userPeers = []; // array of sessions picked as user "peers" (calculated from "match point")
var userSession; // session of client user
var sketchState = 0; // state that decides what animation to trigger
var centerY; // coordinate on Y axis to place circle


/*
*   Function to preload session data and colors
 */

function preload() {
    data = getData();
    colorsJson = loadJSON(colorsURL);
}

/*
*   Setup canvas and data to be drawn
 */

function setup() {
    var canvas = createCanvas(windowWidth, windowHeight);
    canvas.parent('visualizationContainer'); // html elemnt where to put canvas
    centerY = height/3;
    rectMode(BOTTOM);
    angleMode(DEGREES); // allows us to make calculations based on 360 deg
    nbrOfSessions = data.sessions.length;
    data.sessions.sort(function(a, b){ // sort sessions based on match, in order to get peers
        return a.match - b.match;
    });
    for (var i = 0; i < nbrOfSessions; i ++) {
        // find longest distance
        if (data.sessions[i].distance >= longestDistance) {
            longestDistance = data.sessions[i].distance;
        }
        // map distance from user
        if (i%2 == 0) {
            var rotateAngle = map(data.sessions[i].distance, 0, longestDistance, 0, 180);
        } else {
            var rotateAngle = map(data.sessions[i].distance, 0, longestDistance, 360, 180);
        }

        // create session object for each session
        var s = new session(
            data.sessions[i].intention,
            data.sessions[i].discipline,
            rotateAngle,
            data.sessions[i].startTime);

        sessions.push(s);

        // get top 10 sessions to be treated as peers
        if (nbrOfSessions - i <= 10) {
            userPeers.push(s);
        }
    }

    // hard coded user session
    // TODO: this should be loaded dynamically
    userSession = new session("harmony", "self-expression", 0, new Date());

}

/*
*   Resize canvas on window resiza
*/

function windowResized() {
  resizeCanvas(windowWidth, windowHeight);
}

/*
*   Loop to draw visualization
*/
function draw() {
// START Static graphics background
    background(44, 53, 82);

    var backgroundFade = new radialGradient(width/2, centerY, 0, height/1.5, 100, color(0), color(44, 53, 82));
    backgroundFade.display();

// END Static graphics background


    var sessionStates = []; //

// ANALYZE STATUS

// IS INTRO ANIMATION DONE?
    if (sketchState == 0) {
        for (var i = 0; i < sessions.length; i ++) {
            sessionStates.push(sessions[i].getState());
        }
        sessionStates.sort();
        sketchState = sessionStates[0];
    }
// Start user session animation
    if (sketchState == 1) {
        userSession.makeMainSession();
    }
    sketchState = userSession.getState();
// IS PEER ANIMATION DONE?
    if (sketchState == 3) {
        sessionStates = [];
        for (var i = 0; i < userPeers.length; i ++) {
            userPeers[i].makePeer();
            sessionStates.push(sessions[i].getState());
        }
        sessionStates.sort();
    }


// RENDER ALL SESSIONS
    for (var i = 0; i < sessions.length; i ++) {
        sessions[i].display();
    }
    // USER SESSION
        userSession.display();




// START static graphics foreground
   push();
        blendMode(ADD);
            var sunGlow = new radialGradient(width/2, centerY, 100, width*0.8, random(110, 120), color(255,255,100,3), color(255,255,255, 0));
            sunGlow.display();
        blendMode(NORMAL);
    pop();


    var centerSun = new radialGradient(width/2, centerY, 10, 100, 20, color(255), color(255, 255, 100, 0));
    centerSun.display();
// END static graphics foreground



    rotate(180); // in order to get user line att bottom

}



/*
*   Function that defines a session, and calculates animation.
*/
function session (intention, discipline, rotation, startTime) {
    this.intention = intention;
    this.discipline = discipline;
    this.rotation = rotation;
    this.currentHeight = 0;
    this.targetHeight = width/2-40;
    this.currentIntentionAlpha = 0;
    this.targetIntentionAlpha = 50;
    this.currentDisciplineAlpha = 0;
    this.targetDisciplineAlpha = 255;
    this.disciplineIndicator = false;

    this.intentionColor;
    this.disciplineColor;
    this.startTime = startTime;

/*
*   Renders session, called once per session per draw loop
*/
    this.display = function () {
        push();
        translate(width/2, centerY);
        rotate(this.rotation);


        // If this is user session use these settings
        if (this.mainSession) {
            noStroke();
            this.currentHeight = getNewHeight(this.targetHeight, this.currentHeight, 20);

            for (var i = 26; i > 0; i --) {
                this.currentIntentionAlpha = random(5, 35);
                this.intentionColor = arrayToColor(colorsJson.intentions[this.intention], this.currentIntentionAlpha);
                fill(this.intentionColor);
                rect(0-(i/2), this.targetHeight, random(i, i+2), -this.currentHeight);
            }
            var color1 = arrayToColor(colorsJson.disciplines[this.discipline], this.currentDisciplineAlpha);
            var color2 = arrayToColor(colorsJson.disciplines[this.discipline], 0);
            var ellipseContent = new radialGradient(0, this.targetHeight, 10, random(72, 80), 15, color1, color2);
            ellipseContent.display();
            stroke(255);
            strokeWeight(2);
            noFill();
            ellipse(0, this.targetHeight ,60, 60);


        // If this is not user session use these settings
        } else {
            noStroke();
            this.currentIntentionAlpha = getNewAlpha(this.targetIntentionAlpha, this.currentIntentionAlpha);
            this.intentionColor = arrayToColor(colorsJson.intentions[this.intention], this.currentIntentionAlpha);
            fill(this.intentionColor);
            this.currentHeight = getNewHeight(this.targetHeight, this.currentHeight, 20);

            rect(0-(durationToStroke(this.startTime)/2), 10, durationToStroke(this.startTime), this.currentHeight-10);
        }

        // if this session should show discipline, that is if this session is treated as a peer
        if (this.disciplineIndicator) {
            noStroke();
            this.currentDisciplineAlpha = getNewAlpha(this.targetDisciplineAlpha, this.currentDisciplineAlpha);
            this.disciplineColor = arrayToColor(colorsJson.disciplines[this.discipline], this.currentDisciplineAlpha);
            fill(this.disciplineColor);
            ellipse(0, this.currentHeight + 16, 16, 16);
        }

        pop();
    }


    /*
    *   Return animation state of this session
     */
    this.getState = function () {
        var state = 0;
        if (this.targetHeight == this.currentHeight) {
            state = 1;
        }
        if (this.mainSession) {
            state = 2;
        }
        if (this.targetHeight == this.currentHeight && this.mainSession) {
            state = 3;
        }
        return state;
    }

    /*
    *   Set this session as a peer session
    */
    this.makePeer = function () {
        this.targetIntentionAlpha = 255;
        this.disciplineIndicator = true;
    }

    /*
    *   Set this session as user session
    */
    this.makeMainSession = function () {
        this.targetIntentionAlpha = 255;
        this.currentDisciplineAlpha = 255;
        this.targetHeight = centerY*2 - 200;
        this.currentHeight = 0;
        this.mainSession = true;
    }

    /*
    *   Calculate new height, for animation
    */
    function getNewHeight (target, current, speed) {
        var difference = target - current;
        if (Math.floor(difference) == 0 || Math.ceil(difference) == 0) {
            var newHeight = target;
        } else {
            var newHeight = current + (difference/speed);
        }
        return newHeight;
    }

    /*
    *   Calculate new alpha, for animation
    */
    function getNewAlpha (target, current) {
        var difference = target - current;
        if (Math.floor(difference) == 0 || Math.ceil(difference) == 0) {
            var newAlpha = target;
        } else {
            var newAlpha = current + (difference/50);
        }
        return newAlpha;
    }


    /*
    *   Turn arrays loaded from color JSON into color object, with calculated alpha value
    */
    function arrayToColor (colorArray, alpha) {
        var red = colorArray[0];
        var green = colorArray[1];
        var blue = colorArray[2];
        return color(red, green, blue, alpha);
    }

    /*
    *   Calculate and return thickness of rectangle according to session duration
    */
    function durationToStroke (start) {
        var duration = new Date() - start;
        var maxTime = 60*60*1000*3;
        return map(duration, 0, maxTime, 0.1, 20);
    }
}

/*
*   Function to setup radial gradients
*/
function radialGradient(x, y, innerDim, outerDim, fadeSteps, colorOuter, colorInner) {
    this.horizontalCenter = x;
    this.verticalCenter = y;
    this.innerDiameter = innerDim;
    this.outerDiameter = outerDim;
    this.granularity = fadeSteps;
    this.outerColor = colorOuter;
    this.innerColor = colorInner;

    /*
    *   Render a series of ellipses to form a gradient based on parameters above
    */
    this.display = function () {
        noStroke();

        push();
        translate(this.horizontalCenter, this.verticalCenter);

        for (var i = 0; i < this.granularity; i ++) {
            var diam = map(i, 0, this.granularity, this.outerDiameter, this.innerDiameter);
            var color = lerpColor(this.innerColor, this.outerColor, map(i, 0.0, this.granularity, 0.0, 1.0));
            fill(color);
            ellipse (0,0,diam,diam);
        }
        pop();
    }
}
