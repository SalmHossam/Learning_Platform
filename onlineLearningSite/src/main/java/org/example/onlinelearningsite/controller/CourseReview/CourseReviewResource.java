package org.example.onlinelearningsite.controller.CourseReview;

import org.example.onlinelearningsite.Entity.CourseReview;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/reviews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CourseReviewResource {

    @Inject
    private CourseReviewController reviewController;

    @Path("/create")
    @POST
    public Response createReview(CourseReview courseReview) {
        boolean success = reviewController.createReview(courseReview);
        if (success) {
            return Response.status(Response.Status.CREATED).entity("Review created successfully").build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to create Review").build();
        }
    }
    @DELETE
    @Path("/delete-course-review/{courseId}")
    public Response deleteCourse(@PathParam("courseId") int courseId) {

        boolean deletedCourse =reviewController.deleteCourseReview(courseId);
        if (deletedCourse) {
            return Response.status(Response.Status.OK).entity("Review Deleted Successfully").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Review not found").build();
        }
    }

    // Enable CORS for all methods in this resource
    @OPTIONS
    public Response options() {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE,OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .build();
    }
}
