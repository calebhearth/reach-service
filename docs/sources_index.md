# Sources Index
** This endpoint is currently active **

## Description
Returns all allowed values for source.

## Endpoint
`/api/v1/params/sources`

## HTTP Method
GET

## Arguments
None

## Example Response

```
[
  {
    'id' : 1
    'name' : 'premium',
  },
  {
    'id' : 2
    'name' : 'direct_play',
  }
  {
    'id' : 3
    'name' : 'offerwall',
  }
]
```

## Currently Supported Values
This is the currently supported list of sources.

* premium
* direct_play
* offerwall
* featured
* display_ad
* video_carousel
* tj_games
* publisher_message
