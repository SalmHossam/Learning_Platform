package org.example.onlinelearningsite.controller.CourseReview;

import org.example.onlinelearningsite.Entity.CourseReview;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
}
