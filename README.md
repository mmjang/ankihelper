# AnkiHelper

## What is this? 

Ankihelper is an Android application for adding language-learning cards to Ankidroid. 

## What is Ankidroid?

[Ankidroid](https://play.google.com/store/apps/details?id=com.ichi2.anki&hl=en) is the Android version of [Anki](https://apps.ankiweb.net/).

Anki is a [spaced repetition](https://en.wikipedia.org/wiki/Spaced_repetition) flashcard program. Basically, you add two-sided(question side and answer side) flashcards to this program. During a study session, the program shows you the question side of one card and let you think about the answer. Then you flip the card to the answer side and check how well you memorized it. The answer side will show several buttons ("agian", "hard", "good", "easy") for you to evaluate this review. According to your evaluation, Anki uses the [SM2 algorithm] to decide when will it quiz you this card again (aka spaced repetition).

## Why Ankihelper?

A large portion of anki users use this program to learn languages. In the desktop version(Windows, Linux, and Mac), you can create cards manually or import them from text data. There're also several add-ons to simplify the process of making language cards. For example, the [WordQuery](https://github.com/finalion/WordQuery) add-on let you add dictionary definitions for a list of vocabularies with one click. Also, chrome extension [Yomichan](https://chrome.google.com/webstore/detail/yomichan/ogmnaimimemjmbakcfefmnahgdfhfami?hl=en-US) and [Anki Card Helper](https://chrome.google.com/webstore/detail/anki-%E5%88%92%E8%AF%8D%E5%88%B6%E5%8D%A1%E5%8A%A9%E6%89%8B/ajencmdaamfnkgilhpgkepfhfgjfplnn?hl=zh-CN) make it possible to add definitions of words on any website, together with the context, direcly to Anki.

For a long time, tools for making language cards on the mobile platforms (Ankidroid on Android and Ankimobile on IOS) have been scarce. Part of the reasion is that add-ons for the desktop version cannot be used on the mobile platforms. Recently, Ankidroid introduced [a set of APIs](https://github.com/ankidroid/Anki-Android/wiki/AnkiDroid-API) for communicating with external apps. While these APIs are not as powerful as the functions add-ons can call on the desktop version, they still gives us a change to stay away from the tedious and awkward process of adding cards on Android. So, here comes Ankihelper.

## How to use?

