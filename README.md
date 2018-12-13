# General course assignment

Build a map-based application, which lets the user see geo-based data on a map and filter/search through it in a meaningfull way. Specify the details and build it in your language of choice. The application should have 3 components:

1. Custom-styled background map, ideally built with [mapbox](http://mapbox.com). Hard-core mode: you can also serve the map tiles yourself using [mapnik](http://mapnik.org/) or similar tool.
2. Local server with [PostGIS](http://postgis.net/) and an API layer that exposes data in a [geojson format](http://geojson.org/).
3. The user-facing application (web, android, ios, your choice..) which calls the API and lets the user see and navigate in the map and shows the geodata. You can (and should) use existing components, such as the Mapbox SDK, or [Leaflet](http://leafletjs.com/).

## Example projects

- Showing nearby landmarks as colored circles, each type of landmark has different circle color and the more interesting the landmark is, the bigger the circle. Landmarks are sorted in a sidebar by distance to the user. It is possible to filter only certain landmark types (e.g., castles).

- Showing bicykle roads on a map. The roads are color-coded based on the road difficulty. The user can see various lists which help her choose an appropriate road, e.g. roads that cross a river, roads that are nearby lakes, roads that pass through multiple countries, etc.

## Data sources

- [Open Street Maps](https://www.openstreetmap.org/)

## My project

Fill in (either in English, or in Slovak):

**Application description**: FriendsTime je aplikácia, ktorá umožní priateľom organizovať, plánovať spoločné akcie v Bratislave. V dannom rozsahu vzhľadá podniky na základe filtračnźch podmienok (napr. bary v dosahu 500m a rôznymi farbarmi zvýrazní ich podľa odporúčania). Druhou možnosťou je zobrazenie heatmapy podnikov v jednotlivých oblastiach Bratislavy. V zobrazených oblastiach heatmapy umožňuje nájsť všetky podniky podľa vybraného typu filtra. Tretia možnosť umožňuje vyhladať spôsob hromadnej dopravy k podniku. Používateľ si môže vybrať priamy spoj alebo spoj s jedným prestupm. V prípade nájdenia priameho prestupu, mu cestu daného autobusu zobrazí na mape. Ak ide možnosť s prestupom v okne mu zobrazí linky autobusov, ktoré môže použiť.

**Data source**: Dodaný dataset Bratislava

**Technologies used**: java, spring, postgis, hibernate, mapbox.
