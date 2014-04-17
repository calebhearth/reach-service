# Geoip Continents Index
** This endpoint is not currently active **

## Description
Returns all allowed values for geoip_content.

## Endpoint
`/geoip_continents`

## HTTP Method
GET

## Arguments
None

## Example Response

```
[
  {
    'id' : 1
    'name' : 'AF',
    'properName': 'Africa'
  },
  {
    'id' : 2
    'name' : 'AS',
    'properName': 'Asia'
  }
  {
    'id' : 3
    'name' : 'EU',
    'properName': 'Europe'
  }
]
```

## Currently Supported Values
While this endpoint is under development, this is the currently supported list
of continents.

* AF
* AS
* EU
* NA
* OC
* SA
