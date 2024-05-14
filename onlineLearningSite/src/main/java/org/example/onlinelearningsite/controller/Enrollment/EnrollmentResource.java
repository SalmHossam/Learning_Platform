package org.example.onlinelearningsite.controller.Enrollment;

import org.example.onlinelearningsite.Entity.Enrollment;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/enrollments")
public class EnrollmentResource {

    @EJB
    private EnrollmentController enrollmentService;

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response enrollStudent(Enrollment enrollment) {
        try {
            enrollmentService.enrollStudent(enrollment);
            return Response.ok("Student enrolled successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to enroll student: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{enrollmentId}")
    public Response cancelEnrollment(@PathParam("enrollmentId") Long enrollmentId) {
        try {
            enrollmentService.cancelEnrollment(enrollmentId);
            return Response.ok("Enrollment canceled successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to cancel enrollment: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/student/{studentEmail}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEnrollmentsForStudent(@PathParam("studentEmail") String studentEmail) {
        try {
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsForStudent(studentEmail);
            return Response.ok(enrollments).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve enrollments for student: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{enrollmentId}/approve")
    public Response approveEnrollment(@PathParam("enrollmentId") Long enrollmentId) {
        try {
            enrollmentService.approveEnrollment(enrollmentId);
            return Response.ok("Enrollment approved successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to approve enrollment: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{enrollmentId}/reject")
    public Response rejectEnrollment(@PathParam("enrollmentId") Long enrollmentId) {
        try {
            enrollmentService.rejectEnrollment(enrollmentId);
            return Response.ok("Enrollment rejected successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to reject enrollment: " + e.getMessage()).build();
        }
    }
}
