package org.bioauth.typeauth.controller;

import org.bioauth.typeauth.config.ClientSecurityUtil;
import org.bioauth.typeauth.domain.Client;
import org.bioauth.typeauth.domain.Person;
import org.bioauth.typeauth.service.ClientServiceDb;
import org.bioauth.typeauth.service.PersonServiceDb;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api")
public class PersonController {

	private PersonServiceDb personServiceDb;
	private ClientServiceDb clientServiceDb;

	private ClientSecurityUtil clientSecurityUtil;

	public PersonController(PersonServiceDb personServiceDb, ClientServiceDb clientServiceDb, ClientSecurityUtil clientSecurityUtil) {
		this.personServiceDb = personServiceDb;
		this.clientServiceDb = clientServiceDb;
		this.clientSecurityUtil = clientSecurityUtil;
	}

	@GetMapping("/check")
	public @ResponseBody Person checkPerson(@RequestParam("name") String name)
	{
		Client authenticatedClient;

		authenticatedClient = clientSecurityUtil.getAuthenticatedClient();
		if (authenticatedClient == null)
			return null;
		return authenticatedClient.personExist(name);
	}

	@PostMapping("/save")
	public @ResponseBody String savePerson(@RequestBody Person person)
	{
		Client authenticatedClient;

		authenticatedClient = clientSecurityUtil.getAuthenticatedClient();
		if (authenticatedClient == null)
			return "Client Not Authenticated !";
		if (authenticatedClient.personExist(person.getName()) != null)
			return "Person Already exist !";
		authenticatedClient.getPersons().add(person);
		clientServiceDb.update(authenticatedClient);
		return "Person Saved !";
	}

	@PostMapping("/update")
	public @ResponseBody String updatePerson(@RequestBody Person person)
	{
		Client authenticatedClient;
		Person oldPerson;

		authenticatedClient = clientSecurityUtil.getAuthenticatedClient();
		if (authenticatedClient == null)
			return "Client Not Authenticated !";
		if ((oldPerson = authenticatedClient.personExist(person.getName())) == null)
			return "Person doesn't exist !";
		person.setId(oldPerson.getId());
		personServiceDb.update(person);
		return "Person Updated Successfully !";
	}

	@DeleteMapping("/delete")
	public @ResponseBody Person deletePerson(@RequestParam("name") String name)
	{
		Client authenticatedClient;
		Person person;

		authenticatedClient = clientSecurityUtil.getAuthenticatedClient();
		if (authenticatedClient == null)
			return null;
		if ((person = authenticatedClient.personExist(name)) == null)
			return null;
		authenticatedClient.getPersons().remove(person);
		personServiceDb.delete(person);
		clientServiceDb.update(authenticatedClient);
		return person;
	}

	@GetMapping("/verify")
	public @ResponseBody String verify(@RequestBody Person newPerson)
	{
		Client authenticatedClient;
		Person person;

		authenticatedClient = clientSecurityUtil.getAuthenticatedClient();
		if (authenticatedClient == null)
			return "Client not authenticated !";
		if ((person = authenticatedClient.personExist(newPerson.getName())) == null)
			return "Person not found !";
		Double score1 = 100
				* Math.min(person.getTotalElapsedTime(), newPerson.getTotalElapsedTime())
				/ Math.max(person.getTotalElapsedTime(), newPerson.getTotalElapsedTime());
		Double score2 = 100
				* Math.min(person.getTotalPressTime(), newPerson.getTotalPressTime())
				/ Math.max(person.getTotalPressTime(), newPerson.getTotalPressTime());
		return String.format("Result:\nSimilarity: %.2f %%\nKey Similarity: %.2f %%\n", score1, score2);
	}
}
