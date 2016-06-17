package controllers;

import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;

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
import com.lily.services.FitbitService;
import com.lily.utils.JpaUtils;
import com.lily.utils.LilyConstants;
import com.lily.utils.PasswordHasher;

/**
 * User controller for user operations
 * 
 * @author Mohammad
 */
public class UserController extends Controller {

	@Inject
	private FitbitService fitbitService;

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

		if(user instanceof FitbitUser )
			user.userId = ((FitbitUser)user).encodedId;
		
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

		final String company = json.findPath("company").textValue();
		if (company == null)
			return badRequest("Missing parameter [company]");

		final String department = json.findPath("department").textValue();
		if (department == null)
			return badRequest("Missing parameter [department]");

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
			String userId = json.findPath("userId").textValue();

			FitbitUser existingUser = fitbitService.getFitbitUser(userId);

			if (existingUser != null)
				return badRequest(userId
						+ " is already link with another user !!.");

			// Register new user.
			FitbitUser fitbitUser = fitbitService.getUserFromServer(userId);

			fitbitUser.firstname = firstname;
			fitbitUser.lastname = lastname;
			fitbitUser.email = email;
			fitbitUser.password = hashedPassword;
			fitbitUser.createdAt = new Date();
			fitbitUser.encodedId = userId;
			fitbitUser.company = company;
			fitbitUser.department = department;
			em.persist(fitbitUser);
			user = fitbitUser;
			user.userId = fitbitUser.encodedId;
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
	public Result update() throws Throwable {
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

		final String company = json.findPath("company").textValue();
		if (company == null)
			return badRequest("Missing parameter [company]");

		final String department = json.findPath("department").textValue();
		if (department == null)
			return badRequest("Missing parameter [department]");

		EntityManager em = JPA.em();

		FitbitUser fitbitUser = fitbitService.getFitbitUserByEmail(email);

		if (fitbitUser == null)
			return badRequest("User Id is not registered !!.");

		fitbitUser.firstname = firstname;
		fitbitUser.lastname = lastname;
		fitbitUser.email = email;

		if (password != null && password.trim().length() > 0)
			fitbitUser.password = PasswordHasher.hash(password);

		fitbitUser.company = company;
		fitbitUser.department = department;
		fitbitUser.userId = fitbitUser.encodedId;
		em.merge(fitbitUser);

		return ok(Json.toJson(fitbitUser));
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
			fitbitUser.userId = fitbitUser.encodedId;
			em.remove(user);
			em.persist(fitbitUser);
			response().cookies().clear();
		}

		return ok(Json.toJson(fitbitUser));
	}
}
