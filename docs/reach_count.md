# Reach Count

## Description
Returns Reach information for a submitted set of parameters.

## Endpoint
`/reach_counts`

## HTTP Method
GET

## Arguments
### Any argument marked with a star is not currently supported.
<table>
  <tr>
    <th>Argument</th>
    <th>Required</th>
    <th>Format</th>
    <th>Description</th>
    <th>Allowed Values</th>
    <th>Other Notes</th>
  </tr>
  <tr>
    <td>personas</td>
    <td>No</td>
    <td>Array of strings</td>
    <td>Personas to narrow the reach calculation</td>
    <td>[Personas Index](personas_index.md)</td>
    <td></td>
  </tr>
  <tr>
    <td>platform</td>
    <td>No</td>
    <td>String</td>
    <td>The platform to target</td>
    <td>[Platforms Index](platforms_index.md)</td>
    <td></td>
  </tr>
  <tr>
    <td>device_os_versions</td>
    <td>No</td>
    <td>Array of Strings</td>
    <td>The list of OS versions to target</td>
    <td>[OS Versions Index](os_versions_index.md)</td>
    <td>This can only be used if the platform is specified</td>
  </tr>
  <tr>
    <td>apple_product_line</td>
    <td>No *</td>
    <td>String</td>
    <td>The apple product lines to target</td>
    <td>[Apple Product Lines Index](apple_product_lines_index.md)</td>
    <td>This argument is required if the specificed platform is iOS</td>
  </tr>
  <tr>
    <td>geoip_continent</td>
    <td>No</td>
    <td>String</td>
    <td>The continent to be included in the reach</td>
    <td>[Geoip Continents Index](geoip_continents_index.md)</td>
    <td></td>
  </tr>
  <tr>
    <td>geoip_country</td>
    <td>No</td>
    <td>Array of Strings</td>
    <td>The countries to include in the reach</td>
    <td>[Geoip Countries Index](geoip_countries_index.md)</td>
    <td>In order to use this option, you must set the geoip_continent to a
matching continent</td>
  </tr>
  <tr>
    <td>geoip_region</td>
    <td>No</td>
    <td>Array of Strings</td>
    <td>The regions to include in the reach</td>
    <td>[Geoip Regions Index](geoip_regions_index.md)</td>
    <td>In order to use this option, you must set geoip_continent and
geoip_country to matching values</td>
  </tr>
  <tr>
    <td>languages</td>
    <td>No</td>
    <td>Array of Strings</td>
    <td>The languages to include in the reach</td>
    <td>[Languages Index](languages_index.md)</td>
    <td></td>
  </tr>
  <tr>
    <td>sources</td>
    <td>No</td>
    <td>Array of Strings</td>
    <td>The sources to include in the reach</td>
    <td>[Sources Index](sources_index.md)</td>
    <td></td>
  </tr>
</table>

## Example response

```
{
  'udids_count': 150000,
  'impressions_count': 500000
}
```

## Example Call
```
/reach_counts?geoip_country%5B%5D=USs&geoip_country%5B%5D=CA&platform%5B%5D=iose&apple_product_line%5B%5D=iPhonee&languages%5B%5D=en&os_versions%5B%5D=3&os_versions%5B%5D=4&os_versions%5B%5D=5&sources%5B%5D=offerwall
```
