## Device model Index
** This endpoint is currently active **

## Description

Returns all allowed values for device_model for a given manufacturer or a list of all device_models.

## Endpoint

`/api/v1/params/device_manufacturer/:device_manufacturer_id/device_model` (Returns list of device_manufacturer for a device_size)

`/api/v1/params/device_model` (Returns list of all device_model)

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
    <td>device_manufacturer_id</td>
    <td>Yes</td>
    <td>Integer</td>
    <td>The device_manufacturer_id</td>
    <td>[device_manufacturer Index](device_manufacturer_index.md)</td>
  </tr>
</table>


## Example Response

```
[
  {"id":1,"name":"GALAXY S","dependents":
    [
      {"dependentType":"device_manufacturer","dependentId":1},
      {"dependentType":"device_size","dependentId":1}
    ]
  },
  {"id":2,"name":"GALAXY S MINI","dependents":
    [
      {"dependentType":"device_manufacturer","dependentId":1},
      {"dependentType":"device_size","dependentId":1}
    ]
  },
  {"id":3,"name":"GALAXY S DUOS","dependents":
    [
      {"dependentType":"device_manufacturer","dependentId":1},
      {"dependentType":"device_size","dependentId":1}
    ]
  }
]
```
