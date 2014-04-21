# Geoip Regions Index
** This endpoint is not currently active **

## Description
Returns all allowed values for regions for a given geoip_country.

## Endpoint
`/geoip_countries/:country_id/geoip_regions`

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
  {
    'id' : 1
    'name' : 'NC',
    'properName' : 'North Carolina'
    'dependentLocationType' : 'geoip_country',
    'dependentLocationId' : 8
  },
  {
    'id' : 2
    'name' : 'Washington',
    'properName' : 'Washington'
    'dependentLocationType' : 'geoip_country',
    'dependentLocationId' : 8
  }
  {
    'id' : 3
    'name' : 'TX',
    'properName' : 'Texas'
    'dependentLocationType' : 'geoip_country',
    'dependentLocationId' : 8
  }
]
```

## Currently Supported Values
While this endpoint is under development, this is the currently supported list
of regions.

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
