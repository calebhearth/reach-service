# Geoip Countries Index
** This endpoint is not currently active **

## Description
Returns all allowed values for geoip_contries for a given continent.

## Endpoint
`/geoip_continents/:continent_id/geoip_contries`

## HTTP Method
GET

## Arguments
<table>
  <tr>
    <th>Argument</th>
    <th>Required</th>
    <th>Format</th>
    <th>Description</th>
    <th>Allowed Values</th>
  </tr>
  <tr>
    <td>continent_id</td>
    <td>Yes</td>
    <td>Integer</td>
    <td>The geoip_continent_id</td>
    <td>[Geoip Continents Index](geoip_continents_index.md)</td>
  </tr>
</table>

## Example Response

```
[
  {
    'id' : 1
    'name' : 'AR',
    'properName' : 'Argentina'
  },
  {
    'id' : 2
    'name' : 'AU',
    'properName' : 'Austrailia'
  }
  {
    'id' : 3
    'name' : 'BR',
    'properName' : 'Brazil'
  }
]
```

## Currently Supported Values
While this endpoint is under development, this is the currently supported list
of continents.

* AR
* AU
* BR
* CA
* CL
* CN
* CO
* DE
* ES
* FR
* GB
* GR
* HK
* ID
* IL
* IN
* IT
* JP
* KR
* KW
* MX
* MY
* NL
* RU
* SA
* SE
* SG
* TH
* TR
* TW
* US
