# Castcle-Android

[![Build status](https://build.appcenter.ms/v0.1/apps/735a0a62-562f-4d59-a626-1a639f4dc656/branches/develop/badge)](https://appcenter.ms)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=castcle_Castcle-iOS&metric=alert_status)](https://sonarcloud.io/dashboard?id=castcle_Castcle-iOS)

## A decentralized social media for everyone

[![N|Solid](https://avatars.githubusercontent.com/u/85831466?s=200&v=4)](https://github.com/castcle)

This is a source code repository of **[Castcle](git@github.com:castcle/Castcle-Android.git) Android
Application**.

All works and source files published in this repository are
under [GNU AGPLv3](https://github.com/castcle/castcle-api/blob/main/LICENSE) license terms.

## How-to setup project

<table cellspacing="0" cellpadding="0" style="border: none; width: 100%;">
<tr>
<td style="min-width: 150px;">
<a href="https://screenshot.castcle.com/features_circles.png" target="_blank">
<img src="https://screenshot.castcle.com/features_circles.png" style="max-height:350px; min-width:150px;">
</a>
</td>
<td valign="top">
<h5 style="font-weight: bold">Features:</h5>
In v3+, we plan to launch <em><strong>Castcle Features</strong></em> in Castcle application, including <em><a href="https://screenshot.castcle.com/features_photo.png" target="_blank">Castcle Photo</a></em> and <em><a href="https://screenshot.castcle.com/features_watch.png" target="_blank">Castcle Watch</a></em>, which will provide the richer user experiences than a simple <em>content feed</em> reading in v1. <i>See examples <a href="#examples">below</a></i>.
<h5 style="font-weight: bold">Circles:<a name="circles"></a></h5>
In v2+, the additional <em><strong>Castcle Circles</strong></em>, not only <em><strong>For You</strong></em> circle, will be available. Each <em>Castcle Circle</em> is designed to represent different social bubble. For exmaple, when user navigates to <em><strong>Friends</strong> circle</em>, he/she will only see contents posted by his/her family and friends. Then, when he/she changes to <em><strong>Following</strong> circle</em>, he/she will see a flood of contents that he/she subscribes to. There are many more circles that we plan to provide such as <em><strong>Now</strong></em>, a circle which prioritize new contents to come first, <em><strong>Topics</strong></em>, a circle that feed contents on the topics that user interests, etc.
</td>
</tr>
</table>

## **About**

For more **about** ***Castcle Platform***, please
visit [Castcle API](https://github.com/castcle/castcle-api) repository to see project *Abstracts,
Key Ideas and also the [Whitepaper](https://documents.castcle.com/castcle-whitepaper-v1_3.pdf)*.

## **Demo**

While **Castcle Application** is still under ***Work in Progress***, you can see the demo of **v3**
which we plan to release in the first quarter of
Y2022 [here](https://xd.adobe.com/view/48edf8d9-690e-4ef1-9ec7-332b98447f98-1dbe/).

#### **The Circle Experience:** *Let's Burst our Social Bubbles*

<table cellspacing="0" cellpadding="0" style="border: none; width: 100%;">
<tr>
<td style="min-width: 150px;">
<a href="https://screenshot.castcle.com/circle_swipe.png" target="_blank">
<img src="https://screenshot.castcle.com/circle_swipe.png" style="max-height:350px; min-width:150px;">
</a>
</td>
<td valign="top">
Since <em><strong>Castcle Circle</strong></em> is designed to represent user's social bubble <em>(see the explanation <a href="#circles">here</a>)</em>, we intentionally design the application UX letting individual user <em>change circle</em>, <em><strong>navigate through social bubbles</strong></em>, by swiping left or right on the screen. This will allow user to jump from one social bubble to other social bubbles easily, resulting in <strong>reducing the <a href="https://en.wikipedia.org/wiki/Echo_chamber_(media)">Echo Chamber</a> effect</strong>, as shown in the image on the left hand side.
</td>
</tr>
</table>

##### Features Examples: Feed, Photo and Watch <a name="examples"></a>

<table cellspacing="0" cellpadding="0" style="border: none;">
<tr>
<th style="text-align: center;">
Feed
</th>
<th style="text-align: center;">
Photo
</th>
<th style="text-align: center;">
Watch
</th>
</tr>
<tr>
<td>
<a href="https://screenshot.castcle.com/features_feed.png" target="_blank">
<img src="https://screenshot.castcle.com/features_feed.png" style="max-height:350px; min-width:150px;">
</a>
</td>
<td>
<a href="https://screenshot.castcle.com/features_photo.png" target="_blank">
<img src="https://screenshot.castcle.com/features_photo.png" style="max-height:350px; min-width:150px;">
</a>
</td>
<td>
<a href="https://screenshot.castcle.com/features_watch.png" target="_blank">
<img src="https://screenshot.castcle.com/features_watch.png" style="max-height:350px; min-width:150px;">
</a>
</td>
</tr>
</table>

## **Getting Started**

- Setup the Codestyle:
    - From Android Studio: AS → Preferences → Code Style → Scheme (Click on the settings icon) →
      Import Scheme -> navigate to `config/codestyle.xml` -> Confirm

- Import macro setting (Reformatting & SaveAll)
    - Go to Android Studio
        - File > Manage IDE Settings > Import Settings > Browse `config/macros-settings.zip`
        - After import > Edit > Macros > See like picture
        - Set hotkey
            - Go to Android Studio > Preferences > Keymap > typing `Reformatting & SaveAll` > Add
              Keyboard shortcut with `⌘S` > OK > Remove > OK


## **Milestones**

#### **V1** (Sep 2021)

The following key features are planned to be released at the end of September, 2021.

- **Castcle Features:** ***Feed*** only
- **Castcle Circles:** ***For You*** only (may be ***Topics*** also)
- **Castcle Services:** ***User Feed***, ***Authentications*** (via email and social accounts), ***Page***, ***User/Page Profile & Timeline***

#### **V2** (Dec 2021)

The following key features are planned to be released at the end of December, 2021.

- **Castcle Features:** -
- **Castcle Circles:** add ***Following***, ***Friends*** and ***Topics***
- **Castcle Services:** ***Friend Request***, ***CAST Token*** (rewarding system), ***Castcle Wallet***, ***Ads***, ***BC Exchange Integration*** (via API)

#### **V3** (Mar 2022)

- **Castcle Features:** ***Photo***, ***Watch*** (and may be ***Features Switcher*** also)
- **Castcle Circles:** add ***Profession***
- **Castcle Services:** -

## **License**