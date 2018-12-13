# Overview

Táto aplikácia zobrazuje pub a reštaurácie v Bratislave na mape. Niektoré z obsahujúcich funkcií sú:
- vyhľadanie podniku (typ si môže vybrať používateľ) v zadanoom rozsahu od mojej aktuálnej pozície
- zobrazenie heatmapy podnikov Bratislave nad jednotlivými oblasťami Bratislavy 
- vyhľadanie podnikov (typ si môže vybrať používateľ) nad oblasťou v Bratislave
- zobrazenie priameho spoju mestskej hromadnej dopravy od mojej aktuálnej polohy k podniku
- vyhľadanie liniek s jedným prestupom od mojej akutálnej polohy k podniku
- jednoduché resetovania mapy kliknutím na tlačidlo
- zobrazenie súradníc podniku

Vyhľadanie podnikov v zadanom radiuse:

![Screenshot](findpubs.PNG)

```
select r.name as place_name, st_asgeojson(ST_Transform(r.way,4326)), r.amenity, 
	round(cast(ST_Distance(ST_MakePoint(17.067675558809526,48.15798850101939)::geography,
	ST_Transform(r.way, 4326)::geography) as numeric),0) as distancee 
from planet_osm_point r where r.amenity in ('bar','pub') and ST_DWithin((ST_MakePoint(17.067675558809526,48.15798850101939))::geography, ST_Transform(r.way, 4326)::geography, 50);
```

Zobrazenie heatmapy podnikov  v jednotlivých oblastiach Bratislavy:

![Screenshot](heatmap.PNG)

```
WITH temp1 AS (
	--najdenie casti bratislavy, ktore su castou inych oblasti
	SELECT distinct b.name FROM planet_osm_polygon AS a, planet_osm_polygon AS b 
	where a.boundary like 'administrative' and a.admin_level like '10' and b.boundary like 'administrative' and 
	b.admin_level like '10' and a.name not like b.name and st_contains(a.way, b.way)
) 
select r.name as area_name, 
st_asgeojson(ST_Transform(r.way,4326)) as area_geo, 
count(p.name)  / (select count(*) from planet_osm_point p where amenity in ('pub','bar'))::numeric *100 as percentage 
from planet_osm_point p 
cross join planet_osm_polygon r 
where p.amenity in ('pub','bar') and r.boundary like 'administrative' and 
r.admin_level like '10'  and st_contains(ST_Transform(r.way,4326), ST_Transform(p.way,4326)) and r.name not in (select * from temp1) 
group by r.name, r.way;
```

Vyhľadanie podnikov v oblasti Bratislavy:

![Screenshot](findheatmap.PNG)

Zobrazenie trasy priameho spoju:

![Screenshot](direct.PNG)

```
 WITH neareststop AS ( 
	--najdem najblizsiu zastavku od podniku
	select p.way, p.name from planet_osm_point p 
	where p.highway like 'bus_stop' and ST_DWithin((ST_MakePoint(17.083993642113825,48.14306355542479))::geography, ST_Transform(p.way, 4326)::geography, 150) 
	limit 1
), neraststopppoint AS ( 
	--najdem najblisiu zastavku od seba
	select p.way, p.name from planet_osm_point p where p.highway like 'bus_stop' and 
	ST_DWithin((ST_MakePoint(17.0674386,48.1583543))::geography, ST_Transform(p.way, 4326)::geography, 300) 
	limit 1
) 
--najdem cestu autobusu ktora ide okolo oboch zastavok
select l.ref, l.route,s.name, p.name, st_asgeojson(ST_Transform(l.way,4326)) from planet_osm_line l 
cross join neareststop s 
cross join neraststopppoint p where l.route like 'bus'  and l.operator like 'DPB' 
and st_dwithin(ST_Transform(l.way,4326)::geography, ST_Transform(s.way,4326)::geography,50) 
and st_dwithin(ST_Transform(l.way,4326)::geography, ST_Transform(p.way,4326)::geography,50) 
limit 1;
```

Vyhľadanie liniek s prestupom v okne Traffic links:

![Screenshot](transfer.PNG)

```
WITH neareststop AS ( 
	--najdem vsetky spoje iduce okolo mojej zastavky
	select distinct l.ref, l.route,l.way, p.name from planet_osm_point p
	cross join  planet_osm_line l 
	where p.highway like 'bus_stop' and l.route in ('bus','trolleybus','tram') and l.operator like 'DPB'
	and ST_DWithin((ST_MakePoint(17.052506636358714,48.149964326624485))::geography, ST_Transform(p.way, 4326)::geography, 150) and  st_dwithin(ST_Transform(l.way,4326)::geography, ST_Transform(p.way,4326)::geography,150) 
), neraststopppoint AS ( 
	--najdem vsetky spoje iduce okolo cielovej zastavky
	select distinct l.ref, l.route,l.way, p.name from planet_osm_point p 
	cross join  planet_osm_line l 
	where p.highway like 'bus_stop' and l.route in ('bus','trolleybus','tram') and l.operator like 'DPB'
	 and ST_DWithin((ST_MakePoint(17.0674386,48.1583543))::geography, ST_Transform(p.way, 4326)::geography, 150) and  st_dwithin(ST_Transform(l.way,4326)::geography, ST_Transform(p.way,4326)::geography,50)
) 
select s.ref,p.ref, s.name, p.name from neareststop s
cross join neraststopppoint p 
where st_intersects(ST_Transform(s.way,4326)::geography, ST_Transform(p.way,4326)::geography)
limit 1;
```

Aplikácia pozostáva z dvoch častí, z frontendu a backendu. Frontend využíva  mapbox API a mapbox.js a AJAXové volania na backend. Backend (java, spring) spracuje tieto volania, komunikuje a získava dáta z databázy, kde následne získané informácie trasfurnuje do použiteľného geojson formátu.

# Frontend

Frontendová časť aplikácie je HTML stránka (`index.html`), zobrazujúca mapu pomocou mapbox-gl.js. Súčasťou stránky je filter (Bootstrap) umožňujúci filtrovať typy zobrazených podnikov,zadávať vzdialenosť do ktorej má vyhľadávať podniky a aký typ spojenia má vyhľadať pri hľadaní trasy (priamy, s prestupom). Súčasťou filtra sú funkcie na spustenie vyhľadávania podnikov, zobrazenie heatmapy podnikov v oblastiach Bratislavy ,vyčistenie mapy a zobrazenie vyhľadaných liniek spojov v časti Traffic linksT. Samotná mapa podporuje interakciu formou popupov ako hľadanie podnikov v danej oblasti Bratislavy, vybratie podniku ku ktorej sa má hľadať trasa.

# Backend

Backendová časť aplikácie je nakódená v jave s podporou frameworku Spring. Jej hlavnými úlohami sú obstáravanie requestov z frontedu, dopytovanie sa databázy a následné dopyty transformovať do funkčného formátu geojson. Prístp do databázy sa vykonáva v triede (`DBAccessor.java`) a úprava na podporovaný goejson formát v triede (`GJFormattter.java`).

## Data

Dáta o podnikoch sa získavajú z tabuľky planet_osm_point na základe stĺpcu amenity. Dáta o oblastiach z Bratislavy sa získavajú z tabuľky planet_osm_polygon zo stĺpcov boundary a admin_level. Jednotlivé trasy a informácie o linkách sa získavajú z tabuľky planet_osm_line zo stĺpcov route a operátor. Kvôli optimalizácii selectov sme vytvorili niekoľko indexov

```
create index index_points_amenity ON planet_osm_point (amenity);
create index index_points_name ON planet_osm_point (name);
create index index_points_higheay ON planet_osm_point (highway);

create index index_polygon_level_boundary ON planet_osm_polygon (admin_level, boundary);

create index index_line_route ON planet_osm_line (route);
create index index_line_operator ON planet_osm_line (operator);
```
Kompletné query s porovnaním costov pred a po optimalizácii môžete nájsť v časti documentation.
## Api

**Nájdi všetky bary a pub v rozsahu 50 metrov**

`GET /search?radiusDistance=50&bar=true&pub=true&lng=17.067675558809526&lat=48.15798850101939`



### Response


```
{
  "id": "1",
  "type": "circle",
  "source": {
    "type": "geojson",
    "data": {
      "type": "FeatureCollection",
      "features": [
        {
          "type": "Feature",
          "geometry": {
            "type": "Point",
            "coordinates": [
              17.0674386,
              48.1583543
            ]
          },
          "properties": {
            "description": "Sole",
            "marker-color": "#3bb2d0",
            "marker-size": "large",
            "marker-symbol": "rocket"
          }
        }
      ]
    }
  }
}
```

