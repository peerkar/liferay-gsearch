# Liferay GSearch Wikipedia Article Import

This is a simplistic module for importing Wikipedia articles, meant for search demonstration and testing purposes.

The base import URL is hardcoded in the `ImportMVCActionCommand` class: `https://en.wikipedia.org/w/api.php?action=parse&page=`

## Usage

Deploy the module and place the GSearch WikiPedia Article Import portet on a portal page.

Required import options are explained on the import form.

Please notice that some Wikipedia articles are too large to fit into default size content field.

## Changelog

(Major changes only)

### 2019-10-01 (Version 1.0.0)

* Initial version