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
  <tr>
    <td>platform</td>
    <td>No</td>
    <td>String</td>
    <td>The platform to target</td>
    <td>[Platforms Index](platforms_index.md)</td>
  </tr>
  <tr>
    <td>device_os_versions</td>
    <td>No</td>
    <td>Array of Strings</td>
    <td>The list of OS versions to target</td>
    <td>[OS Versions Index](os_versions_index.md)</td>
  </tr>
</table>

## Example response

```
{
  'udids_count': 150000,
  'impressions_count': 500000
}
```

geoip_country%5B%5D=USs&geoip_country%5B%5D=CA&platform%5B%5D=iose&apple_product_line%5B%5D=iPhonee&languages%5B%5D=en&os_versions%5B%5D=3&os_versions%5B%5D=4&os_versions%5B%5D=5&sources%5B%5D=offerwall
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
