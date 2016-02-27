package controllers;

import java.util.Date;

import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.lily.models.User;
import com.lily.utils.PasswordHasher;

/**
 * User controller for user operations
 * 
 * @author Mohammad
 */
public class UserController extends Controller {

	/**
	 * Check User login.
	 * 
	 * @return
	 */
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	public Result authenticate() {
		final JsonNode json = request().body().asJson();
		if (json == null)
			return badRequest("Expecting Json request");

		final String email = json.findPath("email").textValue();
		if (email == null)
			return badRequest("Missing parameter [email]");

		final String password = json.findPath("password").textValue();
		if (password == null)
			return badRequest("Missing parameter [password]");

		final User user = Ebean.createQuery(User.class).where()
				.eq("email", email).findUnique();
		if (user == null)
			return badRequest("Invalid User !!");

		boolean isValidUser = PasswordHasher.matches(password, user.password);

		if (isValidUser == false)
			return badRequest("Invalid User !!");

		return ok(Json.toJson(user));
	}

	/**
	 * Create new user.
	 * 
	 * @return
	 */
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	public Result create() {
		final JsonNode json = request().body().asJson();
		if (json == null)
			return badRequest("Expecting Json request");

		final String firstname = json.findPath("firstname").textValue();
		if (firstname == null)
			return badRequest("Missing parameter [firstname]");

		final String lastname = json.findPath("lastname").textValue();
		if (lastname == null)
			return badRequest("Missing parameter [lastname]");

		final String email = json.findPath("email").textValue();
		if (email == null)
			return badRequest("Missing parameter [email]");

		final String password = json.findPath("password").textValue();
		if (password == null)
			return badRequest("Missing parameter [password]");

		User user = Ebean.createQuery(User.class).where().eq("email", email)
				.findUnique();
		if (user != null)
			return badRequest("Email already registered !!.");

		String hashedPassword = PasswordHasher.hash(password);
		user = new User(firstname, lastname, email, hashedPassword, new Date(),
				null);
		Ebean.save(user);
		return ok(Json.toJson(user));
	}
}
