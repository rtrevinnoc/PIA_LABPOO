/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller
@SpringBootApplication
public class Main {

	@Value("${spring.datasource.url}")
	private String dbUrl;
	MongoClient mongoClient = new MongoClient("localhost", 27017);
	MongoDatabase db = mongoClient.getDatabase("db");
	MongoCollection<Document> citas = db.getCollection("citas");

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Main.class, args);
	}

	@RequestMapping("/")
	String index() {
		return "index";
	}

	@RequestMapping(value="/_crear", produces="application/json")
	@ResponseBody
	Map<String, String> crear(
			HttpServletRequest request,
			@RequestParam("nombre") String nombre,
			@RequestParam("apellidoPaterno") String apellidoPaterno,
			@RequestParam("apellidoMaterno") String apellidoMaterno,
			@RequestParam("email") String email,
			@RequestParam("telefono") String telefono,
			@RequestParam("fecha") String fecha
	) {
		Map<String, String> map = new HashMap<String, String>(); // use new HashMap<String, Object>(); for single result
		HttpSession session = request.getSession();
		
//		Document document = new Document("nombre", nombre)
//					.append("apellidoPaterno", apellidoPaterno)
//					.append("apellidoMaterno", apellidoMaterno)
//					.append("email", email)
//					.append("telefono", telefono)
//					.append("fecha", fecha);
//		UpdateOptions options = new UpdateOptions().upsert(true);
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER).upsert(true);
		Bson filter = or(eq("telefono", telefono), eq("email", email));
		Bson update = combine(set("nombre", nombre), set("apellidoPaterno", apellidoPaterno), set("apellidoMaterno", apellidoMaterno), set("telefono", telefono), set("email", email), set("fecha", fecha));
		Document document = citas.findOneAndUpdate(filter, update, options);
		
//		request.getSession().setAttribute("hello", "hello");
		session.setAttribute("nombre", document.get("nombre"));
		session.setAttribute("apellidoPaterno", document.get("apellidoPaterno"));
		session.setAttribute("apellidoMaterno", document.get("apellidoMaterno"));
		session.setAttribute("email", document.get("email"));
		session.setAttribute("telefono", document.get("telefono"));
		session.setAttribute("fecha", document.get("fecha"));

		map.put("nombre", nombre);
		map.put("apellidoPaterno", apellidoPaterno);
		map.put("apellidoMaterno", apellidoMaterno);
		map.put("email", email);
		map.put("telefono", telefono);
		map.put("fecha", fecha);

		return map;
	}
	
	@RequestMapping(value="/_consultar", produces="application/json")
	@ResponseBody
	String consultar(
			HttpServletRequest request,
			@RequestParam("email") String email,
			@RequestParam("telefono") String telefono
	) {
		HttpSession session = request.getSession();
		Bson filter = or(eq("telefono", telefono), eq("email", email));
		Document document = citas.find(filter).first();
		
		session.setAttribute("nombre", document.get("nombre"));
		session.setAttribute("apellidoPaterno", document.get("apellidoPaterno"));
		session.setAttribute("apellidoMaterno", document.get("apellidoMaterno"));
		session.setAttribute("email", document.get("email"));
		session.setAttribute("telefono", document.get("telefono"));
		session.setAttribute("fecha", document.get("fecha"));
		
		return document.toJson();
	}

	@RequestMapping(value="/_modificar", produces="application/json")
	@ResponseBody
	Map<String, String> modificar(
			HttpServletRequest request,
			@RequestParam("nombre") String nombre,
			@RequestParam("apellidoPaterno") String apellidoPaterno,
			@RequestParam("apellidoMaterno") String apellidoMaterno,
			@RequestParam("email") String email,
			@RequestParam("telefono") String telefono,
			@RequestParam("fecha") String fecha
	) {
		HttpSession session = request.getSession();
		Map<String, String> map = new HashMap<String, String>(); // use new HashMap<String, Object>(); for single result

		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER).upsert(true);
		Bson filter = or(eq("nombre", session.getAttribute("nombre")), eq("apellidoPaterno", session.getAttribute("apellidoPaterno")), eq("apellidoMaterno", session.getAttribute("apellidoMaterno")), eq("telefono", session.getAttribute("telefono")), eq("email", session.getAttribute("email")), eq("fecha", session.getAttribute("fecha")));
		Bson update = combine(set("nombre", nombre), set("apellidoPaterno", apellidoPaterno), set("apellidoMaterno", apellidoMaterno), set("telefono", telefono), set("email", email), set("fecha", fecha));
		//citas.updateOne(filter, update);
		Document document = citas.findOneAndUpdate(filter, update, options);
		
		map.put("nombre", nombre);
		map.put("apellidoPaterno", apellidoPaterno);
		map.put("apellidoMaterno", apellidoMaterno);
		map.put("email", email);
		map.put("telefono", telefono);
		map.put("fecha", fecha);

		return map;
	}
	
	@RequestMapping(value="/_borrar", produces="application/json")
	@ResponseBody
	String borrar(
			HttpServletRequest request,
			@RequestParam("nombre") String nombre,
			@RequestParam("apellidoPaterno") String apellidoPaterno,
			@RequestParam("apellidoMaterno") String apellidoMaterno,
			@RequestParam("email") String email,
			@RequestParam("telefono") String telefono,
			@RequestParam("fecha") String fecha
	) {
		HttpSession session = request.getSession();
		Bson filter = and(eq("nombre", nombre), eq("apellidoPaterno", apellidoPaterno), eq("apellidoMaterno", apellidoMaterno), eq("telefono", telefono), eq("email", email), eq("fecha", fecha));
		String map = citas.findOneAndDelete(filter).toJson();
		session.invalidate();
		
		return map;
	}

}
