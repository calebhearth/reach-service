## Device manufacturer Index
** This endpoint is currently active **

## Description

Returns all allowed values for device_manufacturer for a given size or a list of all device_manufacturers.

## Endpoint

`/api/v1/params/device_size/:device_size_id/device_manufacturer` (Returns list of device_manufacturer for a device_size)

`/api/v1/params/device_manufacturer` (Returns list of all device_manufacturer)

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
    <td>device_size_id</td>
    <td>Yes</td>
    <td>Integer</td>
    <td>The device_size_id</td>
    <td>[device_size Index](device_size_index.md)</td>
  </tr>
</table>


## Example Response

```
[
  {"id": 1,"name": "Samsung","deviceSizeIds": [1, 2]},
  {"id": 2,"name": "HTC","deviceSizeIds": [1]},
  {"id": 3,"name": "LG","deviceSizeIds": [1]}]
```
