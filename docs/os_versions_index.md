# Platforms Index
** This endpoint is not currently active **

## Description
Returns all allowed values for platform.

## Endpoint
`/platforms/:platform_id/os_versions`

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
  {
    'id' : 1
    'name' : '4.1',
  },
  {
    'id' : 3
    'name' : '5.2',
  }
  {
    'id' : 3
    'name' : '5.3',
  }
]
```
