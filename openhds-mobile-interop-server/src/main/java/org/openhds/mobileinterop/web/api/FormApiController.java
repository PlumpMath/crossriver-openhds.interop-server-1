package org.openhds.mobileinterop.web.api;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.openhds.mobileinterop.dao.FormSubmissionDao;
import org.openhds.mobileinterop.model.FormSubmission;
import org.openhds.mobileinterop.model.FormSubmissionSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/form")
public class FormApiController {
	
	private FormSubmissionDao dao;

	@Autowired
	public FormApiController(FormSubmissionDao dao) {
		this.dao = dao;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public void handleFormSubmission(@RequestBody FormSubmission submission, HttpServletResponse response) {
		submission.setFormOwnerId(submission.getFormOwnerId().toUpperCase());
		dao.saveFormSubmission(submission);
	}	
	
	@RequestMapping(value="/download", method=RequestMethod.GET)
	public ResponseEntity<FormSubmissionSet> getFormInstancesForUser() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication auth = context.getAuthentication();
		String user = auth.getName();
		
		List<FormSubmission> submissions = dao.findSubmissionsByOwner(user);
		if (submissions.size() == 0) {
			return new ResponseEntity<FormSubmissionSet>(HttpStatus.NO_CONTENT);
		}
		
		FormSubmissionSet set = new FormSubmissionSet();
		for(FormSubmission submission : submissions) {
			set.addSubmission(submission);
		}
		
		return new ResponseEntity<FormSubmissionSet>(set, HttpStatus.OK);		
	}
}