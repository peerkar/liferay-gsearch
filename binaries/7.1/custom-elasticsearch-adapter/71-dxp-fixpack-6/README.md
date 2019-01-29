# 71-DXP-FIXPACK-6

This custom Elasticsearch adapter version has been tested with following portal versions:

* DXP 7.1 FIXPACK 6

## Installation Instructions

1. Download and deploy the binaries in this folder
1. Restart portal.
1. Check from logs or Gogo shell that it was installed correctly and the default adapter is not active.
1. Do full reindex to update index settings and mappings.

If you have problems to get keyword suggestions to work, try updating the adapter bundle from Gogo shell. This is a known issue in some scenarios, portal standard adapter not releasing the standard suggester reference.



 