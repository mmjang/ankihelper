// Polyfill caretRangeFromPoint() using the newer caretPositionFromPoint()
// if (!document.caretRangeFromPoint){
//     document.caretRangeFromPoint = function polyfillcaretRangeFromPoint(x,y){
//         let range = document.createRange();
//         let position = document.caretPositionFromPoint(x,y);
//         if (!position) {
//             return null;
//         }
//         range.setStart(position.offsetNode, position.offset);
//         range.setEnd(position.offsetNode, position.offset);
//         return range;
//     };
// }

// class TextSourceElement {
//     constructor(element, length=-1) {
//         this.element = element;
//         this.length = length;
//     }

//     clone() {
//         return new TextSourceElement(this.element, this.length);
//     }

//     text() {
//         const text = this.textRaw();
//         return this.length < 0 ? text : text.substring(0, this.length);
//     }

//     textRaw() {
//         switch (this.element.nodeName) {
//             case 'BUTTON':
//                 return this.element.innerHTML;
//             case 'IMG':
//                 return this.element.getAttribute('alt');
//             default:
//                 return this.element.value;
//         }
//     }

//     setStartOffset(length) {
//         // NOP
//         return 0;
//     }

//     setEndOffset(length) {
//         this.length = length;
//         return length;
//     }

//     containsPoint(point) {
//         const rect = this.getRect();
//         return point.x >= rect.left && point.x <= rect.right;
//     }

//     getRect() {
//         return this.element.getBoundingClientRect();
//     }

//     select() {
//         // NOP
//     }

//     deselect() {
//         // NOP
//     }

//     equals(other) {
//         return other.element && other.textRaw() == this.textRaw();
//     }
// }

// class TextSourceRange {
//     constructor(range) {
//         this.rng = range;
//     }

//     clone() {
//         return new TextSourceRange(this.rng.cloneRange());
//     }

//     text() {
//         return this.rng.toString();
//     }

//     //set English words offset by words count (not character count)
//     setWordsOffset(){
//         var a=this.rng;
//         do if(a){
//             var g=a.cloneRange();
//             if(a.startContainer.data){

//                 function isAlpha(a){
//                     return /[\u0030-\u024F]/.test(a);
//                 }

//                 function getStartPos(backward_count){
//                     var count=0, b='', pos=a.startOffset;
//                     for(;pos>=1;){
//                         g.setStart(a.startContainer,--pos);
//                         b=g.toString();
//                         if(!isAlpha(b.charAt(0))){
//                             count++;
//                             if(count==backward_count){
//                                 break
//                             }
//                         }
//                     }

//                     return pos;
//                 }

//                 function getEndPos(forward_count){
//                     var count=0, b='', pos=a.endOffset;
//                     for(;pos<a.endContainer.data.length;){
//                         g.setEnd(a.endContainer,++pos);
//                         b=g.toString();
//                         if(!isAlpha(b.charAt(b.length-1))){
//                             count++;
//                             if(count==forward_count){
//                                 break
//                             }
//                         }
//                     }

//                     return pos;
//                 }

//                 var startPos = getStartPos(1);
//                 var endPos = getEndPos(2);

//                 this.rng.setStart(a.startContainer,startPos==0?0:startPos+1);
//                 this.rng.setEnd(a.endContainer,endPos==a.endContainer.data.length?endPos:endPos-1);
//             }

//         }while(0);

//         return null;
//     }

//     setEndOffset(length) {
//         const lengthAdj = length + this.rng.startOffset;
//         const state = TextSourceRange.seekForward(this.rng.startContainer, lengthAdj);
//         this.rng.setEnd(state.node, state.offset);
//         return length - state.length;
//     }

//     setStartOffset(length) {
//         const lengthAdj = length + (this.rng.startContainer.length - this.rng.startOffset);
//         const state = TextSourceRange.seekBackward(this.rng.startContainer, lengthAdj);
//         this.rng.setStart(state.node, state.offset);
//         return length - state.length;
//     }

//     containsPoint(point) {
//         const rect = this.getPaddedRect();
//         return point.x >= rect.left && point.x <= rect.right;
//     }

//     getRect() {
//         return this.rng.getBoundingClientRect();
//     }

//     getPaddedRect() {
//         const range = this.rng.cloneRange();
//         const startOffset = range.startOffset;
//         const endOffset = range.endOffset;
//         const node = range.startContainer;

//         range.setStart(node, Math.max(0, startOffset - 1));
//         range.setEnd(node, Math.min(node.length, endOffset + 1));

//         return range.getBoundingClientRect();
//     }

//     select() {
//         const selection = window.getSelection();
//         selection.removeAllRanges();
//         selection.addRange(this.rng);
//     }

//     deselect() {
//         const selection = window.getSelection();
//         selection.removeAllRanges();
//     }

//     equals(other) {
//         return other.rng && other.rng.compareBoundaryPoints(Range.START_TO_START, this.rng) == 0;
//     }

//     static seekForward(node, length) {
//         const state = {node, offset: 0, length};
//         if (!TextSourceRange.seekForwardHelper(node, state)) {
//             return state;
//         }

//         for (let current = node; current !== null; current = current.parentElement) {
//             for (let sibling = current.nextSibling; sibling !== null; sibling = sibling.nextSibling) {
//                 if (!TextSourceRange.seekForwardHelper(sibling, state)) {
//                     return state;
//                 }
//             }
//         }

//         return state;
//     }

//     static seekForwardHelper(node, state) {
//         if (node.nodeType === 3) {
//             const consumed = Math.min(node.length, state.length);
//             state.node = node;
//             state.offset = consumed;
//             state.length -= consumed;
//         } else {
//             for (let i = 0; i < node.childNodes.length; ++i) {
//                 if (!TextSourceRange.seekForwardHelper(node.childNodes[i], state)) {
//                     break;
//                 }
//             }
//         }

//         return state.length > 0;
//     }

//     static seekBackward(node, length) {
//         const state = {node, offset: node.length, length};
//         if (!TextSourceRange.seekBackwardHelper(node, state)) {
//             return state;
//         }

//         for (let current = node; current !== null; current = current.parentElement) {
//             for (let sibling = current.previousSibling; sibling !== null; sibling = sibling.previousSibling) {
//                 if (!TextSourceRange.seekBackwardHelper(sibling, state)) {
//                     return state;
//                 }
//             }
//         }

//         return state;
//     }

//     static seekBackwardHelper(node, state) {
//         if (node.nodeType === 3) {
//             const consumed = Math.min(node.length, state.length);
//             state.node = node;
//             state.offset = node.length - consumed;
//             state.length -= consumed;
//         } else {
//             for (let i = node.childNodes.length - 1; i >= 0; --i) {
//                 if (!TextSourceRange.seekBackwardHelper(node.childNodes[i], state)) {
//                     break;
//                 }
//             }
//         }

//         return state.length > 0;
//     }
// }

// function textSourceFromPoint(point) {
//         const element = document.elementFromPoint(point.x, point.y);
//         if (element !== null) {
//             const names = ['IMG', 'INPUT', 'BUTTON', 'TEXTAREA'];
//             if (names.indexOf(element.nodeName) !== -1) {
//                 return new TextSourceElement(element);
//             }
//         }

//         const range = document.caretRangeFromPoint(point.x, point.y);
//         if (range !== null) {
//             return new TextSourceRange(range);
//         }

//         return null;
//     }

function getSelectionText() {
    var text = "";
    if (window.getSelection) {
        text = window.getSelection().toString();
    } else if (document.selection && document.selection.type != "Control") {
        text = document.selection.createRange().text;
    }
    return text;
}

// function extractSentence(source, extent) {
//         //const quotesFwd = {'「': '」', '『': '』', "'": "'", '"': '"'};
//         //const quotesBwd = {'」': '「', '』': '『', "'": "'", '"': '"'};
//         const quotesFwd = {'「': '」', '『': '』'};
//         const quotesBwd = {'」': '「', '』': '『'};
//         const terminators = '…。．.？?！!\n';

//         const sourceLocal = source.clone();
//         const position = sourceLocal.setStartOffset(extent);
//         sourceLocal.setEndOffset(position + extent);
//         const content = sourceLocal.text();

//         let quoteStack = [];

//         let startPos = 0;
//         for (let i = position; i >= startPos; --i) {
//             const c = content[i];

//             if (quoteStack.length === 0 && (terminators.indexOf(c) !== -1 || c in quotesFwd)) {
//                 startPos = i + 1;
//                 break;
//             }

//             if (quoteStack.length > 0 && c === quoteStack[0]) {
//                 quoteStack.pop();
//             } else if (c in quotesBwd) {
//                 quoteStack = [quotesBwd[c]].concat(quoteStack);
//             }
//         }

//         quoteStack = [];

//         let endPos = content.length;
//         for (let i = position; i < endPos; ++i) {
//             const c = content[i];

//             if (quoteStack.length === 0) {
//                 if (terminators.indexOf(c) !== -1) {
//                     endPos = i + 1;
//                     break;
//                 }
//                 else if (c in quotesBwd) {
//                     endPos = i;
//                     break;
//                 }
//             }

//             if (quoteStack.length > 0 && c === quoteStack[0]) {
//                 quoteStack.pop();
//             } else if (c in quotesFwd) {
//                 quoteStack = [quotesFwd[c]].concat(quoteStack);
//             }
//         }

//         return content.substring(startPos, endPos).trim();
//     }

function wrappingSentencesWithinPTags(){
    currentIndex = -1;
    "use strict";

    var rxOpen = new RegExp("<[^\\/].+?>"),
    rxClose = new RegExp("<\\/.+?>"),
    rxSupStart = new RegExp("^<sup\\b[^>]*>"),
    rxSupEnd = new RegExp("<\/sup>"),
    sentenceEnd = [],
    rxIndex;

    sentenceEnd.push(new RegExp("[^\\d][\\.!\\?]+"));
    sentenceEnd.push(new RegExp("(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*?$)"));
    sentenceEnd.push(new RegExp("(?![^\\(]*?\\))"));
    sentenceEnd.push(new RegExp("(?![^\\[]*?\\])"));
    sentenceEnd.push(new RegExp("(?![^\\{]*?\\})"));
    sentenceEnd.push(new RegExp("(?![^\\|]*?\\|)"));
    sentenceEnd.push(new RegExp("(?![^\\\\]*?\\\\)"));
    //sentenceEnd.push(new RegExp("(?![^\\/.]*\\/)")); // all could be a problem, but this one is problematic

    rxIndex = new RegExp(sentenceEnd.reduce(function (previousValue, currentValue) {
                                            return previousValue + currentValue.source;
                                            }, ""));

    function indexSentenceEnd(html) {
        var index = html.search(rxIndex);

        if (index !== -1) {
            index += html.match(rxIndex)[0].length - 1;
        }

        return index;
    }

    function pushSpan(array, className, string, classNameOpt) {
        if (!string.match('[a-zA-Z0-9]+')) {
            array.push(string);
        } else {
            array.push('<span class="' + className + '">' + string + '</span>');
        }
    }

    function addSupToPrevious(html, array) {
        var sup = html.search(rxSupStart),
        end = 0,
        last;

        if (sup !== -1) {
            end = html.search(rxSupEnd);
            if (end !== -1) {
                last = array.pop();
                end = end + 6;
                array.push(last.slice(0, -7) + html.slice(0, end) + last.slice(-7));
            }
        }

        return html.slice(end);
    }

    function paragraphIsSentence(html, array) {
        var index = indexSentenceEnd(html);

        if (index === -1 || index === html.length) {
            pushSpan(array, "sentence", html, "paragraphIsSentence");
            html = "";
        }

        return html;
    }

    function paragraphNoMarkup(html, array) {
        var open = html.search(rxOpen),
        index = 0;

        if (open === -1) {
            index = indexSentenceEnd(html);
            if (index === -1) {
                index = html.length;
            }

            pushSpan(array, "sentence", html.slice(0, index += 1), "paragraphNoMarkup");
        }

        return html.slice(index);
    }

    function sentenceUncontained(html, array) {
        var open = html.search(rxOpen),
        index = 0,
        close;

        if (open !== -1) {
            index = indexSentenceEnd(html);
            if (index === -1) {
                index = html.length;
            }

            close = html.search(rxClose);
            if (index < open || index > close) {
                pushSpan(array, "sentence", html.slice(0, index += 1), "sentenceUncontained");
            } else {
                index = 0;
            }
        }

        return html.slice(index);
    }

    function sentenceContained(html, array) {
        var open = html.search(rxOpen),
        index = 0,
        close,
        count;

        if (open !== -1) {
            index = indexSentenceEnd(html);
            if (index === -1) {
                index = html.length;
            }

            close = html.search(rxClose);
            if (index > open && index < close) {
                count = html.match(rxClose)[0].length;
                pushSpan(array, "sentence", html.slice(0, close + count), "sentenceContained");
                index = close + count;
            } else {
                index = 0;
            }
        }

        return html.slice(index);
    }

    function anythingElse(html, array) {
        pushSpan(array, "sentence", html, "anythingElse");

        return "";
    }

    function guessSenetences() {
        var paragraphs = document.querySelectorAll("p, #title");

        Array.prototype.forEach.call(paragraphs, function (paragraph) {
            var html = paragraph.innerHTML,
                length = html.length,
                array = [],
                safety = 100;

            while (length && safety) {
                html = addSupToPrevious(html, array);
                if (html.length === length) {
                    if (html.length === length) {
                        html = paragraphIsSentence(html, array);
                        if (html.length === length) {
                            html = paragraphNoMarkup(html, array);
                            if (html.length === length) {
                                html = sentenceUncontained(html, array);
                                if (html.length === length) {
                                    html = sentenceContained(html, array);
                                    if (html.length === length) {
                                        html = anythingElse(html, array);
                                    }
                                }
                            }
                        }
                    }
                }

                length = html.length;
                safety -= 1;
            }

            try {
                paragraph.innerHTML = array.join("");
            } catch(err) {
                console.error(err);
                console.error("-> " + err.message);
            }
        });
    }

    guessSenetences();
}


function getSentenceWithSelection(){
    var sentence;
    var sel = getSelection();
    var node = null;
    var elements = document.querySelectorAll("span.sentence");

    // Check for a selected text, if found start reading from it
    var text;
    var senIndex = 0;
    if (sel.toString() != "") {
        console.log("anchornode:")
        console.log(sel.anchorNode);
        node = sel.anchorNode;
        while(node.className != "sentence"){
            node = node.parentNode;
            if(!node){
                break;
            }
        }
    
        if (node) {
            sentence = node

            for(var i = 0, len = elements.length; i < len; i++) {
                if (elements[i] === sentence) {
                    //currentIndex = i; //comment this line to make sure there's no global side effect
                    senIndex = i;
                    break;
                }
            }
            text = sentence.innerText || sentence.textContent;
        } else {
            text = "";
            //sentence = findSentenceWithIDInView(elements);
        }
    }else{
        text = "";
    }
    return {
        "sentence":text,
        "sen_index":senIndex
        };
}

// var getTitle = function(){
//     var host = "";
//     if(location.hostname){
//         host = location.hostname;
//     }

//     var title = "";

//     var titleNode = document.querySelector("title");
//     if(titleNode){
//         title = titleNode.text;
//     }

//     return " ( " + title + " )";
// }

// var showPopup = function(){
//     var selection = getSelectionText();
//     if(selection)
//     {
//         if(selection.indexOf(" ") > -1)
//         {
//             selection = selection.replace(/(\r\n|\n|\r)/gm,"");
//             android.showToast(selection + getTitle(), "", window.location.href);
//         }
//         else{
//             var textSource = textSourceFromPoint({x:cursorXXX, y:cursorYYY});
//                 	var sentence = extractSentence(textSource, 400);
//                 	console.log(sentence);
//                 	sentence = sentence.replace(/(\r\n|\n|\r)/gm,"");
//                 	android.showToast(sentence + getTitle(), selection, window.location.href);
//         }

//     }
// }

// var timeout;

// document.addEventListener("selectionchange", function(e) {
//     if(timeout){
//         clearTimeout(timeout);
//     }
//     timeout = setTimeout(showPopup, 500);
//     //window.alert("touchend");
//     //showPopup();
// });



// console.log("javascript injected");

// //for webnovel

// var makeSelectable = function(){
// if(location.hostname == "m.webnovel.com"){
//     document.querySelectorAll("div").forEach( p => p.style.userSelect="text");
//     document.querySelectorAll("p").forEach( p => p.style.userSelect="text");

// }
// }

// makeSelectable();

