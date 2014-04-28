# Platforms Index
** This endpoint is currently under development **

## Description
Returns all allowed values for platform.

## Endpoint
`/platform/:platform_id/device_os_version`

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
    <td>platform_id</td>
    <td>Yes</td>
    <td>Integer</td>
    <td>The platform id</td>
    <td>[Platforms Index](platforms_index.md)</td>
  </tr>
</table>

## Example Response

```
[
  {"id":1,"name":"2.0","dependents":
    [
      {"dependentType":"platform","dependentId":1},
      {"dependentType":"platform","dependentId":2}
    ]
  },
  {"id":2,"name":"2.1","dependents":
    [
      {"dependentType":"platform","dependentId":1}
    ]
  }
]
```
