package com.msrit.studentmanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msrit.studentmanagement.controller.StudentController;
import com.msrit.studentmanagement.model.Student;
import com.msrit.studentmanagement.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StudentController.class)
public class StudentmanagementApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StudentService studentService;

	// Helper method for converting objects to JSON strings
	private String toJson(Object obj) throws Exception {
		return new ObjectMapper().writeValueAsString(obj);
	}

	@Test
	public void testCreateStudent() throws Exception {
		Student student = new Student(null, "1ms24cs125", "Sita", "sita@gmail.com", "9876543210", "CS, DS");
		when(studentService.saveStudent(student)).thenReturn(student);

		mockMvc.perform(post("/api/students")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(student)))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.usn", is("1ms24cs1255")));
	}

	@Test
	public void testGetAllStudents() throws Exception {
		Student student1 = new Student(null, "1ms24cs122", "Rohan", "rohan@gmail.com", "123444", "AI,ML");
		Student student2 = new Student(null, "1ms24cs123", "Amit", "amit@gmail.com", "567899", "AI");
		when(studentService.getAllStudents()).thenReturn(Arrays.asList(student1, student2));

		mockMvc.perform(get("/api/students")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].usn", is("1ms24cs122")))
				.andExpect(jsonPath("$[1].usn", is("1ms24cs123")));
	}

	@Test
	public void testGetStudentByUsn() throws Exception {
		Student student = new Student(null, "1ms24cs122", "Rohan", "rohan@gmail.com", "123444", "AI,ML");
		when(studentService.getStudentByUsn("1ms24cs122")).thenReturn(student);

		mockMvc.perform(get("/api/students/usn/1ms24cs122")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.usn", is("1ms24cs122")))
				.andExpect(jsonPath("$.name", is("Rohan")));
	}

	@Test
	public void testGetStudentByUsn_NotFound() throws Exception {
		when(studentService.getStudentByUsn("1ms24cs999")).thenReturn(null);

		mockMvc.perform(get("/api/students/usn/1ms24cs999")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Not found"));
	}

	@Test
	public void testUpdateStudent() throws Exception {
		Student existingStudent = new Student(null, "1ms24cs122", "Rohan", "rohan@gmail.com", "123444", "AI,ML");
		Student updatedStudent = new Student(null, "1ms24cs122", "Rohan Kumar", "rohan.kumar@gmail.com", "123444", "AI,ML");

		when(studentService.getStudentByUsn("1ms24cs122")).thenReturn(existingStudent);
		when(studentService.saveStudent(existingStudent)).thenReturn(updatedStudent);

		mockMvc.perform(put("/api/students/usn/1ms24cs122")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(updatedStudent)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name", is("Rohan Kumar")))
				.andExpect(jsonPath("$.emailId", is("rohan.kumar@gmail.com")));
	}

	@Test
	public void testUpdateStudent_NotFound() throws Exception {
		Student student = new Student(null, "1ms24cs122", "Rohan", "rohan@gmail.com", "123444", "AI,ML");

		when(studentService.getStudentByUsn("1ms24cs122")).thenReturn(null);

		mockMvc.perform(put("/api/students/usn/1ms24cs122")
						.contentType(MediaType.APPLICATION_JSON)
						.content(toJson(student)))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Student with this USN not found"));
	}

	@Test
	public void testDeleteStudent() throws Exception {
		Student student = new Student(null, "1ms24cs122", "Rohan", "rohan@gmail.com", "123444", "AI,ML");

		when(studentService.getStudentByUsn("1ms24cs122")).thenReturn(student);
		doNothing().when(studentService).deleteStudentByUsn("1ms24cs122");

		mockMvc.perform(delete("/api/students/usn/1ms24cs122")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("Student deleted successfully"));
	}

	@Test
	public void testDeleteStudent_NotFound() throws Exception {
		when(studentService.getStudentByUsn("1ms24cs999")).thenReturn(null);

		mockMvc.perform(delete("/api/students/usn/1ms24cs999")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Student with this USN not found"));
	}
}
