package org.example.onlinelearningsite.controller.course;

import org.example.onlinelearningsite.Entity.Course;


import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/courses")
public class CourseResource {

    @EJB
    private CourseController courseController;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCourse(Course course) {
        boolean created = courseController.createCourse(course);
        if (created) {
            return Response.status(Response.Status.CREATED).entity("Course created successfully").build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to create course").build();
        }
    }

    @GET
    @Path("/{courseId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourse(@PathParam("courseId") int courseId) {
        Course course = courseController.getCourse(courseId);
        if (course != null) {
            return Response.status(Response.Status.OK).entity(course).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
        }
    }
    @DELETE
    @Path("/delete-course/{courseId}")
    public Response deleteCourse(@PathParam("courseId") int courseId) {
        boolean deletedCourse = courseController.deleteCourse(courseId);
        if (deletedCourse) {
            return Response.status(Response.Status.OK).entity("Course Deleted Successfully").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
        }
    }
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourses(@QueryParam("searchTerm") String searchTerm, @QueryParam("searchCriteria") String searchCriteria) {
        if (searchTerm == null || searchCriteria == null || searchTerm.isEmpty() || searchCriteria.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Search term and criteria are required").build();
        }

        List<Course> courses = courseController.getCourses(searchTerm, searchCriteria);
        if (courses.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No courses found matching the search criteria").build();
        } else {
            return Response.status(Response.Status.OK).entity(courses).build();
        }
    }
    @PUT
    @Path("/update-course/{courseId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCourse(@PathParam("courseId") int courseId, Course updatedCourse) {
        // Extract updated attributes from the provided updatedCourse object
        String updatedName = updatedCourse.getName();
        String updatedCategory = updatedCourse.getCategory();
        double updatedRating = updatedCourse.getRating();
        int updatedCapacity = updatedCourse.getCapacity();
        String updatedEmail=updatedCourse.getInstructorEmail();

        // Perform the update
        boolean updated = courseController.updateCourse(courseId, updatedName, updatedCategory, updatedRating, updatedCapacity,updatedEmail);

        // Return response based on the update result
        if (updated) {
            return Response.status(Response.Status.OK).entity("Course Updated Successfully").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
        }
    }


}
