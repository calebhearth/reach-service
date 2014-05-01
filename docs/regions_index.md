# Regions Index
** This endpoint is currently active **

## Description
Returns all allowed values for regions for a given geoip_country.

## Endpoint
`/api/v1/params/geoip_country/:country_id/regions` (Returns list of regions for a country)

`/api/v1/params/regions` (Returns list of all regions)

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
    <td>country_id</td>
    <td>Yes</td>
    <td>Integer</td>
    <td>The geoip_country_id</td>
    <td>[Geoip Countries Index](geoip_countries_index.md)</td>
  </tr>
</table>

## Example Response

```
[
  {"geoipCountryId": 31, "id": 1, "name": "AL", "properName": "Alabama"},
  {"geoipCountryId": 31, "id": 2, "name": "AK", "properName": "Alaska"}
]
```

## Currently Supported Values
This is the currently supported list of regions.

### US States
* http://en.wikipedia.org/wiki/List_of_states_and_territories_of_the_United_States

### Canadian providences &  territories
* NB
* NU
* NL
* MB
* YT
* BC
* PE
* NT
* QC
* NS
* AB
* SK
* ON
