var cursorXXX;
var cursorYYY;
document.addEventListener("touchstart", function(e){
    cursorXXX = e.touches[0].clientX;
    cursorYYY = e.touches[0].clientY;
    //cursorXXX = e.clientX;
    //cursorYYY = e.clientY;
    console.log([cursorXXX,cursorYYY]);
});

var highlighter;
    
window.onload = function(){
        rangy.init();
        applier = rangy.createClassApplier('highlight');
        highlighter = rangy.createHighlighter();
        highlighter.addClassApplier(applier);   

        wrappingSentencesWithinPTags()
}

function GetAllCreatedElements(selection) {
    var nodes = selection.getRangeAt(0).getNodes(false, function (el) {
        return el.parentNode && el.parentNode.className == "highlight";
    });

    var spans = [];

    for (var i = 0; i < nodes.length; i++) {
        spans.push(nodes[i].parentNode);
    }
    return spans;
}

function setUpEventListeners(){

}

document.onclick = function(e) {
    e = e || window.event;
    var target = e.target || e.srcElement;
    var highlight = highlighter.getHighlightForElement(target);
    if (highlight) {
        highlighter.removeHighlights([highlight]);
        saveHighlights();
    }
};

var showPopup = function(){
    var selection = getSelectionText();
    console.log("getSelectionText: " + selection);
    if(selection)
    {
        var hl = highlighter.highlightSelection('highlight');
        saveHighlights();
        console.log(hl);
        //var hlSpans = GetAllCreatedElements(window.getSelection());
        selection = selection.trim();
        if(selection.indexOf(" ") > -1)
        {
            var senAndIndex = getSentenceWithSelection();
            var sentence = senAndIndex['sentence'];
            var index = senAndIndex['sen_index'];
            console.log("选择:" + selection);
            sentence = sentence.replace(/(\r\n|\n|\r)/gm,"");
            console.log("句子: " + sentence);
            reader.invokePopup(selection, "", index);
            //android.showToast(selection + getTitle(), "", window.location.href);
        }
        else{
            // var textSource = textSourceFromPoint({x:cursorXXX, y:cursorYYY});
            //     	var sentence = extractSentence(textSource, 400);
            //     	console.log("句子:" + sentence);
            var senAndIndex = getSentenceWithSelection();
            var sentence = senAndIndex['sentence'];
            var index = senAndIndex['sen_index'];
            sentence = sentence.replace(/(\r\n|\n|\r)/gm,"");
            reader.invokePopup(sentence, selection, index);
            console.log("句子: " + sentence);
                	//android.showToast(sentence + getTitle(), selection, window.location.href);
        }

    }
    window.getSelection().removeAllRanges();
}

var timeout;

document.addEventListener("selectionchange", function(e) {
    console.log('selection changed');
    if(timeout){
        clearTimeout(timeout);
    }
    timeout = setTimeout(showPopup, 500);
    //window.alert("touchend");
});


function onRestoreHighlights(serial){
    highlighter.deserialize(serial);
}

function saveHighlights(){
    reader.onSaveHightlights(highlighter.serialize())
}

function jumpToSentence(senIndex){
    var sen_node = document.querySelectorAll(".sentence")[senIndex];
    sen_node.scrollIntoView();
}

