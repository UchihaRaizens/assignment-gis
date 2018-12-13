
--najdenie barov a podnikov 300 metrov od aktualnej pozicie
create index index_points_amenity ON planet_osm_point (amenity);
create index index_points_name ON planet_osm_point (name);
create index index_points_higheay ON planet_osm_point (highway);

create index index_polygon_level_boundary ON planet_osm_polygon (admin_level, boundary);

create index index_line_route ON planet_osm_line (route);
create index index_line_operator ON planet_osm_line (operator);

select r.name as place_name, st_asgeojson(ST_Transform(r.way,4326)), r.amenity, 
	round(cast(ST_Distance(ST_MakePoint(17.067675558809526,48.15798850101939)::geography,
	ST_Transform(r.way, 4326)::geography) as numeric),0) as distancee 
from planet_osm_point r where r.amenity in ('bar','pub') and ST_DWithin((ST_MakePoint(17.067675558809526,48.15798850101939))::geography, ST_Transform(r.way, 4326)::geography, 50);


--vytvorenie heatmapy bratislavy podnikov tak aby sa neprekryvali oblasti bratislavy
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

--najdenie priamej trasy
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
