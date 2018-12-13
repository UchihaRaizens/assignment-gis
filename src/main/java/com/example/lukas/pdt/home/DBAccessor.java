package com.example.lukas.pdt.home;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.lukas.pdt.PdtApplication;

public class DBAccessor {

	private static final String url = "jdbc:postgresql://192.168.99.100:5432/gis";
	private static final String user = "postgres";
	private static final String password = "";
	
	public List<Place> findPlaceInDistance(Integer distance, Boolean pub, Boolean bar, Double lng, Double lat) {
		List<Place> temp = new ArrayList<>();
		String tempPub = "";
		String tempBar = "";
		
		if(pub) {
			tempPub = "pub";
		}
		if(bar) {
			tempBar = "bar";
		}
		String sql = "select r.name as place_name, " +
				"st_asgeojson(ST_Transform(r.way,4326)), r.amenity, round(cast(ST_Distance(ST_MakePoint(%s,%s)::geography, ST_Transform(r.way, 4326)::geography) as numeric),0) as distancee " +
				"from planet_osm_point r where r.amenity in ('%s','%s') and " +
				"ST_DWithin((ST_MakePoint(%s,%s))::geography, ST_Transform(r.way, 4326)::geography, %d);";
		sql = String.format(sql, String.valueOf(lng), String.valueOf(lat), tempBar,tempPub,String.valueOf(lng), String.valueOf(lat), distance);
		System.out.println(sql);
		try (Connection con = DriverManager.getConnection(url, user, password);
                Statement st = con.createStatement();	
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
               rs.getString(1);
               rs.getString(2);
               rs.getString(3);
               rs.getInt(4);
               temp.add(new Place(rs.getString(1),rs.getString(3),rs.getString(2),rs.getInt(4)));
            }

        } catch (SQLException ex) {
        
            Logger lgr = Logger.getLogger(PdtApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
		
		
		return temp;
	}

	public List<Heatmap> createHeatMap(Boolean pub, Boolean bar) {
		List<Heatmap> temp = new ArrayList<>();
		String tempPub = "";
		String tempBar = "";
		
		if(pub) {
			tempPub = "pub";
		}
		if(bar) {
			tempBar = "bar";
		}
		/*String sql = "select r.name as area_name, " +
			       "st_asgeojson(ST_Transform(r.way,4326)) as area_geo, " +
			       "count(p.name)  / (select count(*) from planet_osm_point p where amenity in ('%s','%s'))::numeric *100 as percentage " +
			"from planet_osm_point p " +
			"cross join planet_osm_polygon r " +
			"where p.amenity in ('%s','%s') and r.boundary like 'administrative' and " +
			"r.admin_level like '10'  and st_contains(ST_Transform(r.way,4326), ST_Transform(p.way,4326)) " +
			"group by r.name, r.way";*/
		String sql = "WITH temp1 AS " +
			    "(SELECT distinct b.name " +
			    		"FROM planet_osm_polygon AS a, planet_osm_polygon AS b " +
			    		"where a.boundary like 'administrative' and " +
			    		"      a.admin_level like '10' and b.boundary like 'administrative' and " +
			    		"      b.admin_level like '10' and a.name not like b.name and st_contains(a.way, b.way)) " +
			    		"select r.name as area_name, " +
			    		"       st_asgeojson(ST_Transform(r.way,4326)) as area_geo, " +
			    		"       count(p.name)  / (select count(*) from planet_osm_point p where amenity in ('%s','%s'))::numeric *100 as percentage " +
			    		"from planet_osm_point p " +
			    		"cross join planet_osm_polygon r " +
			    		"where p.amenity in ('%s', '%s') and r.boundary like 'administrative' and " +
			    		"      r.admin_level like '10'  and st_contains(ST_Transform(r.way,4326), ST_Transform(p.way,4326)) and r.name not in (select * from temp1) " +
			    		"group by r.name, r.way";

		sql = String.format(sql, tempBar,tempPub,tempBar,tempPub);
		System.out.println(sql);
		try (Connection con = DriverManager.getConnection(url, user, password);
                Statement st = con.createStatement();	
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
               temp.add(new Heatmap(rs.getString(1),rs.getString(2),rs.getDouble(3)));
            }

        } catch (SQLException ex) {
        
            Logger lgr = Logger.getLogger(PdtApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
		return temp;
	}

	public List<Place> createPointsHeatmap(String name, Boolean pub, Boolean bar) {
		List<Place> temp = new ArrayList<>();
		String tempPub = "";
		String tempBar = "";
		
		if(pub) {
			tempPub = "pub";
		}
		if(bar) {
			tempBar = "bar";
		}
		String sql = "select p.name as name, p.amenity, " + 
				"st_asgeojson(ST_Transform(p.way,4326)) " + 
				"from planet_osm_point p " +
				"cross join planet_osm_polygon r " +
				"where r.name like '%s' and p.amenity in ('%s','%s') " +
				"	and st_contains(ST_Transform(r.way,4326), ST_Transform(p.way,4326))";
		sql = String.format(sql, name, tempBar,tempPub);
		System.out.println(sql);
		try (Connection con = DriverManager.getConnection(url, user, password);
                Statement st = con.createStatement();	
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
               temp.add(new Place(rs.getString(1),rs.getString(2),rs.getString(3),0));
            }

        } catch (SQLException ex) {
        
            Logger lgr = Logger.getLogger(PdtApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
		
		
		return temp;
	}

	public List<Route> createRoute(Boolean pub, Boolean bar, String placeCord, Double lng, Double lat) {
		List<Route> temp = new ArrayList<>();
		String tempPub = "";
		String tempBar = "";
		
		if(pub) {
			tempPub = "pub";
		}
		if(bar) {
			tempBar = "bar";
		}
		String sql = "WITH neareststop AS ( " +
				"select p.way, p.name from planet_osm_point p " +
				"where p.highway like 'bus_stop' and ST_DWithin((ST_MakePoint(%s,%s))::geography, ST_Transform(p.way, 4326)::geography, 150) " +
				"limit 1 " +
				"), neraststopppoint AS ( " +
				"select p.way, p.name from planet_osm_point p " +
				"where p.highway like 'bus_stop' and ST_DWithin((ST_MakePoint(%s))::geography, ST_Transform(p.way, 4326)::geography, 300) " +
				"limit 1 " +
				") " +
				"select l.ref, l.route,s.name, p.name, st_asgeojson(ST_Transform(l.way,4326)) from planet_osm_line l " +
				"cross join neareststop s " +
				"cross join neraststopppoint p " +
				"where l.route like 'bus'  and l.operator like 'DPB' and st_dwithin(ST_Transform(l.way,4326)::geography, ST_Transform(s.way,4326)::geography,50) "+
				"and st_dwithin(ST_Transform(l.way,4326)::geography, ST_Transform(p.way,4326)::geography,50) " +
				"order by l.ref desc " +
				"limit 1";
		sql = String.format(sql, lng, lat, placeCord);
		System.out.println(sql);
		try (Connection con = DriverManager.getConnection(url, user, password);
                Statement st = con.createStatement();	
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
               temp.add(new Route(rs.getString(5)));
            }

        } catch (SQLException ex) {
        
            Logger lgr = Logger.getLogger(PdtApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
		
		
		return temp;
	}

	public List<Route> createCrossRoute(Boolean pub, Boolean bar, String placeCord, Double lng, Double lat) {
		List<Route> temp = new ArrayList<>();
		String tempPub = "";
		String tempBar = "";
		
		if(pub) {
			tempPub = "pub";
		}
		if(bar) {
			tempBar = "bar";
		}
		String sql = "WITH neareststop AS ( select distinct l.ref, l.route,l.way, p.name from planet_osm_point p\r\n" + 
				"cross join  planet_osm_line l \r\n" + 
				"where p.highway like 'bus_stop' and  l.operator like 'DPB'and l.route in ('bus','trolleybus','tram')\r\n" + 
				"and ST_DWithin((ST_MakePoint(%s,%s))::geography, ST_Transform(p.way, 4326)::geography, 150) and  st_dwithin(ST_Transform(l.way,4326)::geography, ST_Transform(p.way,4326)::geography,50) \r\n" + 
				"), neraststopppoint AS ( select distinct l.ref, l.route,l.way, p.name from planet_osm_point p \r\n" + 
				"cross join  planet_osm_line l \r\n" + 
				"where p.highway like 'bus_stop' and l.operator like 'DPB' and l.route in ('bus','trolleybus','tram') and ST_DWithin((ST_MakePoint(%s))::geography, ST_Transform(p.way, 4326)::geography, 300)\r\n" + 
				"and  st_dwithin(ST_Transform(l.way,4326)::geography, ST_Transform(p.way,4326)::geography,50)) \r\n" + 
				"select s.ref,p.ref, s.name, p.name from neareststop s\r\n" + 
				"cross join neraststopppoint p \r\n" + 
				"where st_intersects(ST_Transform(s.way,4326)::geography, ST_Transform(p.way,4326)::geography)\r\n" + 
				"limit 1";
		sql = String.format(sql, lng, lat, placeCord);
		System.out.println(sql);
		try (Connection con = DriverManager.getConnection(url, user, password);
                Statement st = con.createStatement();	
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
               temp.add(new Route(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4)));
            }

        } catch (SQLException ex) {
        
            Logger lgr = Logger.getLogger(PdtApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
				
		return temp;
	}
}
