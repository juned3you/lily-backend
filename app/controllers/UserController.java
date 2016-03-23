package controllers;

import java.util.Date;

import javax.persistence.EntityManager;

import org.apache.commons.beanutils.BeanUtils;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.lily.models.FitbitUser;
import com.lily.models.LilyUser;
import com.lily.models.User;
import com.lily.utils.JpaUtils;
import com.lily.utils.LilyConstants;
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
	 * @throws Throwable
	 */
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	@Transactional
	public Result authenticate() throws Throwable {
		final JsonNode json = request().body().asJson();
		if (json == null)
			return badRequest("Expecting Json request");

		final String email = json.findPath("email").textValue();
		if (email == null)
			return badRequest("Missing parameter [email]");

		final String password = json.findPath("password").textValue();
		if (password == null)
			return badRequest("Missing parameter [password]");

		EntityManager em = JPA.em();

		final User user = JPA.withTransaction(() -> {
			return JpaUtils.getSingleResultOrElseNull(
					em.createQuery("FROM User where email = '" + email + "'"),
					User.class);
		});
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
	 * @throws Throwable
	 */
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	@Transactional
	public Result create() throws Throwable {
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

		EntityManager em = JPA.em();

		User user = JPA.withTransaction(() -> {
			return JpaUtils.getSingleResultOrElseNull(
					em.createQuery("FROM User where email = '" + email + "'"),
					User.class);
		});
		if (user != null)
			return badRequest("Email already registered !!.");

		String hashedPassword = PasswordHasher.hash(password);

		final String userType = json.findPath(LilyConstants.USER_TYPE)
				.textValue();

		// Plain user
		if (userType == null || userType.trim().length() == 0) {
			user = new LilyUser(firstname, lastname, email, hashedPassword,
					new Date(), null);
			em.persist(user);

			// Fitbit User
		} else if (LilyConstants.Fitbit.CLIENT_NAME.equals(userType)) {
			FitbitUser fitbitUser = new FitbitUser();
			fitbitUser.firstname = firstname;
			fitbitUser.lastname = lastname;
			fitbitUser.email = email;
			fitbitUser.password = hashedPassword;
			fitbitUser.createdAt = new Date();
			fitbitUser.encodedId = json.findPath("userId").textValue();
			em.persist(fitbitUser);
			user = fitbitUser;
			response().cookies().clear();
		}

		return ok(Json.toJson(user));
	}

	/**
	 * Create new user.
	 * 
	 * @return
	 * @throws Throwable
	 */
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	@Transactional
	public Result linktoWearableForExistingUser() throws Throwable {
		final JsonNode json = request().body().asJson();
		final String email = json.findPath("email").textValue();
		
		if (email == null)
			return badRequest("Missing parameter [email]");

		EntityManager em = JPA.em();

		User user = JPA.withTransaction(() -> {
			return JpaUtils.getSingleResultOrElseNull(
					em.createQuery("FROM User where email = '" + email + "'"),
					User.class);
		});

		final String userType = json.findPath(LilyConstants.USER_TYPE)
				.textValue();

		FitbitUser fitbitUser = null;
		if (userType == null || userType.trim().length() == 0) 
			return badRequest("User type is missing in request..");
			// Fitbit User
		 if (LilyConstants.Fitbit.CLIENT_NAME.equals(userType)) {
			fitbitUser = new FitbitUser();
			fitbitUser.firstname = user.firstname;
			fitbitUser.lastname = user.lastname;
			fitbitUser.email = email;
			fitbitUser.password = user.password;
			fitbitUser.createdAt = new Date();
			fitbitUser.encodedId = json.findPath("userId").textValue();
			em.remove(user);
			em.persist(fitbitUser);
			response().cookies().clear();
		}

		return ok(Json.toJson(fitbitUser));
	}
}
