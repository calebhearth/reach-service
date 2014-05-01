# Geoip Countries Index
** This endpoint is currently active **

## Description
Returns all allowed values for geoip_contries for a given continent.

## Endpoint
`/api/v1/params/geoip_continent/:continent_id/geoip_country`

`/api/v1/params/geoip_country` (Returns list of all countries)

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
    <td>[Geoip Continents Index](geoip_continent_index.md)</td>
  </tr>
</table>

## Example Response

```
[
  {"geoipContinentId": 1, "id": 1, "name": "AR", "properName": "Argentina"},
  {"geoipContinentId": 1, "id": 3, "name": "BR", "properName": "Brazil"}
]
```

## Currently Supported Values
The currently supported list of countries.

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
