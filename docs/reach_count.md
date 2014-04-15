# Reach Count

## Description
Returns Reach information for a submitted set of parameters.


## Endpoint
`/reach_count`

## HTTP Method
GET

## Arguments
### Any argument marked with a star is not currently supported.
<table>
  <tr>
    <th>Argument</th>
    <th>Required</th>
    <th>Format</th>
    <th>Description</th>
    <th>Allowed Values</th>
  </tr>
  <tr>
    <td>personas</td>
    <td>No</td>
    <td>Array of strings</td>
    <td>Personas to narrow the reach calculation</td>
    <td>[Personas Index](personas_index.md)</td>
  </tr>
</table>

geoip_country%5B%5D=USs&geoip_country%5B%5D=CA&platform%5B%5D=iose&apple_product_line%5B%5D=iPhonee&languages%5B%5D=en&os_versions%5B%5D=3&os_versions%5B%5D=4&os_versions%5B%5D=5&sources%5B%5D=offerwall
platform
android, ios, windows
device_os_version
android - major version.minor version(eg 4.1)
ios - Major version (eg 5)
apple_product_line
iPhone, iPod, iPad
language
EN, ES, FR, DE, NL, JA, KO, ZH, zh-Hans, zh-Hant, RU
geoip_continent
AF, AS, EU, NA, OC, SA
geoip_country
AR, AU, BR, CA, CL, CN, CO, DE, ES, FR, GB, GR, HK, ID, IL, IN, IT, JP, KR, KW, MX, MY, NL, RU, SA, SE, SG, TH, TR, TW, US
regions
US states and territories (54): http://en.wikipedia.org/wiki/List_of_states_and_territories_of_the_United_States
Canadian prov + territories: NB, NU, NL, MB, YT, BC, PE, NT, QC, NS, AB, SK, ON
personas
persona name like "Fashionista", "Financial", refer to persona list below.
sources
premium, secondary, direct play, offerwall, featured, display ad, video carouse, tapjoy.com, publisher message
Dependencies between parameters:

device_os_version can be targeted only if platform is present.
geoip_region can be targeted only if geoip_country, geoip_continent is present.
geoip_country can be targeted only if geoip_continent is present.
apple_product_line is required whenever you choose ios as a platform
Choosing a device type of iPhone, iTouch or iPad rquires both apple_product_line and platform be set
Currently choosing platform of Android requires no additional param be sent
The service calculates counts which are a union within fields, and intersection across fields.

Example response:

{
  'udids_count': 150000,
  'impressions_count': 500000
}
Personas:

Action Game
Adventure Game
Angry Bird
Animal Enthusiast
Arcade Game
Auto Intender
Baseball Game
Battle Gamer
Boxing Game
Brain Challenging Game
Breeding Game
Bubble Game
Business
Caller using Data Plan
candy clush
Casino Game
Character Building Game
Chinese Style Game
clash of clans
Coin Dozer
Cosmic
Coupon clipper
Crime City
Deer Hunter
Despicable Me
Dice Rolling Game
Drawing Game
e-reader
Education
Endless Runner
Entertainment
Event Intender
Fantasy Sports
Fashionista
Financial
Fishing Sport
Fitness & Health
Flight Intender
Food & Dining
Football Game
Foreign language interest
Fruit Ninja
Gamble
Girl Gamer
Golfer
Gun Bross
Health/Medical-related
Home & Garden
Japanese Style Games
Job Seeker
Jumping Game
Jungle Heat
kid learn game
Kids Game
Kingdoms of Camelot
Korean Style Game
Learning Game
LINE
m-commercer
Magazine
Martial Art Game
Match Game
Mega Jump
Movie Enthusiasts
Music lover
My Singing Monsters
New Mom
New parents
News Reader
Outdoor Camper
Parent/Educator
Pet Game
photo & video
Physics-based game player
Player vs Player
Poker Gamer
public transportation
Puzzle Game - Pictures
Puzzle Game -Word
Quiz Game
Racing Game
Real Estate Follower
Resource Management
Romance
RPG
Sci-fi Game
Shooting Game
Shopper
Simulation Game
Single
Slot Machine Game
Smurf Village
Soccer Fan
Social Sharing
Solitaire Game
Spiritual or Religious user
Sport Gamer
Sport Management Game
Sports Fan
Strategy Game
Student
Subway Surfers
Survey-taker
Tax-related
tech savvy user
Tech/Innovation
Texting app
Tiny Farm
Trade Nation
Traveler
Utilities
Video Content Consumer
Weather
WhatsApp
Wine afficionado
Zombie Game
